package uniquindio.edu.co.cineuq.controller;

import uniquindio.edu.co.cineuq.model.AsientoFuncion;
import uniquindio.edu.co.cineuq.model.Funcion;
import uniquindio.edu.co.cineuq.model.Producto;
import uniquindio.edu.co.cineuq.model.enumeraciones.TipoCliente;

import java.util.*;

/**
 * Selección que el operador va armando antes de confirmar una venta:
 * función, asientos, productos y tipo de cliente.
 */
public class CompraEnCurso {
    private final Funcion funcion;
    private final List<AsientoFuncion> asientos = new ArrayList<>();
    private final Map<Producto, Integer> productos = new LinkedHashMap<>();
    private TipoCliente tipoCliente = TipoCliente.GENERAL;

    CompraEnCurso(Funcion funcion) {
        this.funcion = Objects.requireNonNull(funcion);
    }

    public Funcion getFuncion() {
        return funcion;
    }

    /**
     * Selecciona el asiento si no lo estaba, o lo quita si ya estaba seleccionado.
     *
     * @return {@code true} si el asiento quedó seleccionado
     */
    public boolean alternarAsiento(AsientoFuncion asiento) {
        if (asientos.remove(asiento)) return false;
        asientos.add(asiento);
        return true;
    }

    public List<AsientoFuncion> getAsientos() {
        return Collections.unmodifiableList(asientos);
    }

    public boolean tieneAsientos() {
        return !asientos.isEmpty();
    }

    public List<String> codigosAsientos() {
        return asientos.stream().map(asiento -> asiento.getAsiento().getCodigo()).toList();
    }

    public String descripcionAsientos() {
        return String.join(", ", codigosAsientos());
    }

    public void agregarProducto(Producto producto) {
        productos.merge(producto, 1, Integer::sum);
    }

    public void quitarProducto(Producto producto) {
        productos.computeIfPresent(producto, (item, cantidad) -> cantidad <= 1 ? null : cantidad - 1);
    }

    public int cantidad(Producto producto) {
        return productos.getOrDefault(producto, 0);
    }

    public Map<Producto, Integer> getProductos() {
        return Collections.unmodifiableMap(productos);
    }

    public TipoCliente getTipoCliente() {
        return tipoCliente;
    }

    public void setTipoCliente(TipoCliente tipoCliente) {
        this.tipoCliente = Objects.requireNonNull(tipoCliente);
    }
}
