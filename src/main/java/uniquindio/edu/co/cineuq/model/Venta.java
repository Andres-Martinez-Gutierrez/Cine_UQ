package uniquindio.edu.co.cineuq.model;

import uniquindio.edu.co.cineuq.model.enumeraciones.EstadoVenta;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Venta {

    //Atributos de la clase Venta
    private String codigo;
    private LocalDateTime fechaHora;


    private Cliente cliente;
    private Funcion funcion;
    private List<Cobro> listVentaCobros;
    private List<AsientoFuncion> listVentaAsientofun;
    private Producto producto;
    private List<Boleta> listVentaBoletas;
    private EstadoVenta estado= EstadoVenta.ACTIVA;

    /**
     * Constructor vacío de la clase Venta.
     */
    public Venta() {
    }

    /**
     * Constructor de la clase Venta.
     *
     * @param codigo código único de la venta
     * @param fechaHora fecha y hora en que se realiza la venta
     * @param cliente cliente que realiza la compra
     * @param funcion función para la que se venden los asientos
     * @param estado venya que detalla si la venta fue ACTIVA o CANCELADA
     */
    public Venta(EstadoVenta estado, Funcion funcion, Cliente cliente, LocalDateTime fechaHora, String codigo, Producto producto) {
        this.estado = estado;
        this.listVentaBoletas = new ArrayList<>();
        this.producto = producto;
        this.listVentaAsientofun = new ArrayList<>();
        this.funcion = funcion;
        this.cliente = cliente;
        this.fechaHora = fechaHora;
        this.codigo = codigo;
        this.listVentaCobros = new ArrayList<>();
    }

    /** Agrega un asiento vendido a la venta. */
    void agregarAsiento(AsientoFuncion asiento) {
        listVentaAsientofun.add(asiento);
    }

    /** Agrega un producto a la venta y suma su precio al cobro. */
    public void agregarProducto(Producto producto) {
        this.producto = producto; // Asigna el producto en singular

        // Creamos un cobro por el valor del producto y lo agregamos a la lista
        Cobro cobroProducto = new Cobro(0, 0, 0);
        cobroProducto.agregarValorProductos(producto.getPrecio());
        this.listVentaCobros.add(cobroProducto);
    }


    /** Cancela la venta y libera sus asientos. No hace nada si ya estaba cancelada. */
    public void cancelar() {
        if (estado == EstadoVenta.CANCELADA) return;
        estado = EstadoVenta.CANCELADA;
        listVentaAsientofun.forEach(AsientoFuncion::liberar);
    }

    public double getTotal() {
        // Recorre la lista de cobros y suma el cálculo total de cada uno
        return listVentaCobros.stream()
                .mapToDouble(Cobro::calcularTotal)
                .sum();
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

    public List<AsientoFuncion> getListVentaAsientofun() {
        return Collections.unmodifiableList(listVentaAsientofun);
    }

    public void setListVentaAsientofun(List<AsientoFuncion> listVentaAsientofun) {
        this.listVentaAsientofun = new ArrayList<>(listVentaAsientofun);
    }

    public List<Cobro> getListVentaCobros() {
        return listVentaCobros;
    }

    public void setListVentaCobros(List<Cobro> listVentaCobros) {
        this.listVentaCobros = listVentaCobros;
    }

    public List<Boleta> getListVentaBoletas() {
        return listVentaBoletas;
    }

    public void setListVentaBoletas(List<Boleta> listVentaBoletas) {
        this.listVentaBoletas = listVentaBoletas;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public EstadoVenta getEstado() {
        return estado;
    }

    public void setEstado(EstadoVenta estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Venta{" +
                "codigo='" + codigo + '\'' +
                ", fechaHora=" + fechaHora +
                ", cliente=" + cliente +
                ", funcion=" + funcion +
                ", producto=" + producto +
                ", estado=" + estado +
                '}';
    }
}
