package uniquindio.edu.co.cineuq.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Venta {
    private String codigo;
    private LocalDateTime fechaHora;
    private Cliente cliente;
    private Funcion funcion;
    private Cobro cobro;
    private List<AsientoFuncion> asientos;
    private List<Producto> productos;
    private EstadoVenta estado;

    /**
     * Constructor vacío de la clase Venta.
     * Deja las listas de asientos y productos vacías y el estado en ACTIVA.
     */
    public Venta() {
        this.asientos = new ArrayList<>();
        this.productos = new ArrayList<>();
        this.estado = EstadoVenta.ACTIVA;
    }

    /**
     * Constructor de la clase Venta.
     *
     * @param codigo código único de la venta
     * @param fechaHora fecha y hora en que se realiza la venta
     * @param cliente cliente que realiza la compra
     * @param funcion función para la que se venden los asientos
     * @param cobro cobro que detalla el total a pagar
     */
    public Venta(String codigo, LocalDateTime fechaHora, Cliente cliente, Funcion funcion, Cobro cobro) {
        this();
        this.codigo = codigo;
        this.fechaHora = fechaHora;
        this.cliente = cliente;
        this.funcion = funcion;
        this.cobro = cobro;
    }

    void agregarAsiento(AsientoFuncion asiento) {
        asientos.add(asiento);
    }

    public void agregarProducto(Producto producto) {
        productos.add(producto);
        cobro.agregarValorProductos(producto.getPrecio());
    }

    public void cancelar() {
        if (estado == EstadoVenta.CANCELADA) return;
        estado = EstadoVenta.CANCELADA;
        asientos.forEach(AsientoFuncion::liberar);
    }

    public double getTotal() {
        return cobro.calcularTotal();
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Funcion getFuncion() {
        return funcion;
    }

    public void setFuncion(Funcion funcion) {
        this.funcion = funcion;
    }

    public Cobro getCobro() {
        return cobro;
    }

    public void setCobro(Cobro cobro) {
        this.cobro = cobro;
    }

    public List<AsientoFuncion> getAsientos() {
        return Collections.unmodifiableList(asientos);
    }

    public void setAsientos(List<AsientoFuncion> asientos) {
        this.asientos = new ArrayList<>(asientos);
    }

    public List<Producto> getProductos() {
        return Collections.unmodifiableList(productos);
    }

    public void setProductos(List<Producto> productos) {
        this.productos = new ArrayList<>(productos);
    }

    public EstadoVenta getEstado() {
        return estado;
    }

    public void setEstado(EstadoVenta estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Venta{codigo='" + codigo + "', fechaHora=" + fechaHora
                + ", cliente=" + (cliente == null ? null : cliente.getIdentificacion())
                + ", funcion=" + (funcion == null ? null : funcion.getCodigo())
                + ", asientos=" + asientos.size() + ", productos=" + productos.size()
                + ", total=" + getTotal() + ", estado=" + estado + "}";
    }
}
