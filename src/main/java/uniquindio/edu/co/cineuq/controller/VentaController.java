package uniquindio.edu.co.cineuq.controller;

import uniquindio.edu.co.cineuq.exception.CinemaUQException;
import uniquindio.edu.co.cineuq.model.*;

import java.util.List;
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

    /** Inicia una compra de solo confitería; se le pueden vender productos sin entradas. */
    public CompraEnCurso iniciarCompraConfiteria() {
        return new CompraEnCurso(null);
    }

    /** Tarifa de una entrada para el tipo de cliente de la compra, sin recargo de sala. */
    public double tarifa(CompraEnCurso compra) {
        return cinema.buscarTarifa(compra.getTipoCliente()).getValor();
    }

    /** Recargo por asiento según el tipo de sala de la función; 0 en una compra de confitería. */
    public double recargoSala(CompraEnCurso compra) {
        return compra.incluyeEntradas() ? compra.getFuncion().getSala().recargo() : 0;
    }

    /** Valor de una entrada según el tipo de cliente y el recargo de la sala. */
    public double precioEntrada(CompraEnCurso compra) {
        return tarifa(compra) + recargoSala(compra);
    }

    public double totalTarifas(CompraEnCurso compra) {
        return compra.getAsientos().size() * tarifa(compra);
    }

    public double totalRecargo(CompraEnCurso compra) {
        return compra.getAsientos().size() * recargoSala(compra);
    }

    public double totalEntradas(CompraEnCurso compra) {
        return totalTarifas(compra) + totalRecargo(compra);
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
     * Registra la venta. Con función vende los asientos seleccionados y les suma los productos;
     * sin función vende solo los productos de confitería.
     *
     * @throws CinemaUQException si algún asiento ya no está disponible
     * @throws IllegalArgumentException si faltan datos del cliente o la compra está vacía
     */
    public Venta vender(CompraEnCurso compra, String nombreCliente, String identificacion) throws CinemaUQException {
        if (nombreCliente == null || nombreCliente.isBlank()) {
            throw new IllegalArgumentException("Ingresa el nombre del cliente para continuar.");
        }
        String id = identificacion == null || identificacion.isBlank() ? "SIN-ID" : identificacion.trim();
        Cliente cliente = new Cliente(id, nombreCliente.trim(), compra.getTipoCliente());

        if (!compra.incluyeEntradas()) {
            if (!compra.tieneProductos()) {
                throw new IllegalArgumentException("Agrega al menos un producto para continuar.");
            }
            return operador.venderProductos(cinema, cliente, compra.unidadesProductos());
        }
        if (!compra.tieneAsientos()) {
            throw new IllegalArgumentException("Selecciona al menos un asiento para continuar.");
        }
        Venta venta = operador.vender(cinema, compra.getFuncion(), cliente, compra.codigosAsientos());
        compra.unidadesProductos().forEach(venta::agregarProducto);
        return venta;
    }
}
