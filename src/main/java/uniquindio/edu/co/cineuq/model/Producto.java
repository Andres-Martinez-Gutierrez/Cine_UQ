package uniquindio.edu.co.cineuq.model;

import uniquindio.edu.co.cineuq.model.enumeraciones.CategoriaProducto;

public class Producto {
    private String codigo;
    private String nombre;
    private double precio;
    private CategoriaProducto categoria;

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
    public Producto(String codigo, String nombre, double precio, CategoriaProducto categoria) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
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

    @Override
    public String toString() {
        return "Producto{codigo='" + codigo + "', nombre='" + nombre + "', precio=" + precio
                + ", categoria=" + categoria + "}";
    }
}
