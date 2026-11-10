package uniquindio.edu.co.cineuq.controller;

import uniquindio.edu.co.cineuq.exception.CinemaUQException;
import uniquindio.edu.co.cineuq.model.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Casos de uso de la venta de entradas y productos.
 */
public class VentaController {
    private final CinemaUQ cinema;
    private final Operador operador;
    private final List<Producto> productos;
    private final FuncionController funciones;

    public VentaController(CinemaUQ cinema, Operador operador, List<Producto> productos, FuncionController funciones) {
        this.cinema = Objects.requireNonNull(cinema);
        this.operador = Objects.requireNonNull(operador);
        this.productos = List.copyOf(productos);
        this.funciones = Objects.requireNonNull(funciones);
    }

    public List<Producto> productos() {
        return productos;
    }

    /**
     * Inicia una compra para la función indicada.
     *
     * @throws IllegalStateException si la función ya no admite ventas
     */
    public CompraEnCurso iniciarCompra(Funcion funcion) {
        if (!funciones.esVendible(funcion)) {
            throw new IllegalStateException("Esta función ya inició o no está programada para la venta.");
        }
        return new CompraEnCurso(funcion);
    }

    /** Valor de una entrada según el tipo de cliente y el recargo de la sala. */
    public double precioEntrada(CompraEnCurso compra) {
        return cinema.buscarTarifa(compra.getTipoCliente()).getValor() + compra.getFuncion().getSala().recargo();
    }

    public double totalEntradas(CompraEnCurso compra) {
        return compra.getAsientos().size() * precioEntrada(compra);
    }

    public double totalProductos(CompraEnCurso compra) {
        return compra.getProductos().entrySet().stream()
                .mapToDouble(entrada -> entrada.getKey().getPrecio() * entrada.getValue())
                .sum();
    }

    public double total(CompraEnCurso compra) {
        return totalEntradas(compra) + totalProductos(compra);
    }

    /**
     * Registra la venta de los asientos seleccionados y le agrega los productos de la compra.
     *
     * @throws CinemaUQException si algún asiento ya no está disponible
     * @throws IllegalArgumentException si faltan datos del cliente o de la selección
     */
    public Venta vender(CompraEnCurso compra, String nombreCliente, String identificacion) throws CinemaUQException {
        if (nombreCliente == null || nombreCliente.isBlank()) {
            throw new IllegalArgumentException("Ingresa el nombre del cliente para continuar.");
        }
        if (!compra.tieneAsientos()) {
            throw new IllegalArgumentException("Selecciona al menos un asiento para continuar.");
        }
        String id = identificacion == null || identificacion.isBlank() ? "SIN-ID" : identificacion.trim();
        Cliente cliente = new Cliente(id, nombreCliente.trim(), compra.getTipoCliente());
        Venta venta = operador.vender(cinema, compra.getFuncion(), cliente, compra.codigosAsientos());
        for (Map.Entry<Producto, Integer> entrada : compra.getProductos().entrySet()) {
            for (int i = 0; i < entrada.getValue(); i++) venta.agregarProducto(entrada.getKey());
        }
        return venta;
    }
}
