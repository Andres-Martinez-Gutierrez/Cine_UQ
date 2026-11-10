package uniquindio.edu.co.cineuq.model;

import uniquindio.edu.co.cineuq.model.enumeraciones.CategoriaProducto;

import java.util.ArrayList;
import java.util.List;

public class Producto {

    //Atributos de la clase producto
    private String codigo;
    private String nombre;
    private double precio;

    //Relaciones de la clase producto
    private CategoriaProducto categoria;
    private Combo combo;
    private List<Venta> listProductoVentas;

    /**
     * Constructor vacío de la clase Producto.
     * Los atributos se asignan luego con los setters.
     */
    public Producto() {
    }

    /**
     * Constructor de la clase Producto.
     *
     * @param codigo código único del producto
     * @param nombre nombre del producto
     * @param precio precio unitario del producto
     * @param categoria categoría del producto (comida, bebida o postre)
     */
    public Producto(String codigo, String nombre, double precio, CategoriaProducto categoria, Combo combo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
        this.combo = combo;
        this.listProductoVentas = new ArrayList<>();
    }

    /** Actualiza el precio unitario del producto. */
    public void actualizarPrecio(double nuevoPrecio) {
        this.precio = nuevoPrecio;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public CategoriaProducto getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaProducto categoria) {
        this.categoria = categoria;
    }

    public Combo getCombo() {
        return combo;
    }

    public void setCombo(Combo combo) {
        this.combo = combo;
    }

    public List<Venta> getListProductoVentas() {
        return listProductoVentas;
    }

    public void setListProductoVentas(List<Venta> listProductoVentas) {
        this.listProductoVentas = listProductoVentas;
    }

    @Override
    public String toString() {
        return "Producto{codigo='" + codigo + "', nombre='" + nombre + "', precio=" + precio
                + ", categoria=" + categoria + "}";
    }
}
