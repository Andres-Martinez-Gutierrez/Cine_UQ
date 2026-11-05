package uniquindio.edu.co.cineuq.model;

import java.util.ArrayList;
import java.util.List;

public class Combo extends Producto {
    /** Descuento como fracción: 0.1 equivale al 10 %. */
    private double descuento;
    private List<Producto> productos;

    /**
     * Constructor vacío de la clase Combo.
     * Deja la lista de productos vacía; los demás atributos se asignan con los setters.
     */
    public Combo() {
        super();
        this.productos = new ArrayList<>();
    }

    /**
     * Constructor de la clase Combo. Su precio se calcula a partir de los productos que lo componen.
     *
     * @param codigo código único del combo
     * @param nombre nombre del combo
     * @param descuento descuento como fracción (0.1 equivale al 10 %)
     * @param productos productos que componen el combo
     */
    public Combo(String codigo, String nombre, double descuento, List<Producto> productos) {
        super(codigo, nombre, 0, CategoriaProducto.COMIDA);
        this.descuento = descuento;
        this.productos = new ArrayList<>(productos);
    }

    /** Calcula el precio del combo: la suma de sus productos menos el descuento. */
    public double calcularPrecio() {
        double suma = productos.stream().mapToDouble(Producto::getPrecio).sum();
        return suma * (1 - descuento);
    }

    /** Devuelve el precio del combo, calculado a partir de sus productos y el descuento. */
    @Override
    public double getPrecio() {
        return calcularPrecio();
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public List<Producto> getProductos() {
        return List.copyOf(productos);
    }

    public void setProductos(List<Producto> productos) {
        this.productos = new ArrayList<>(productos);
    }

    @Override
    public String toString() {
        return "Combo{codigo='" + getCodigo() + "', nombre='" + getNombre() + "', descuento=" + descuento
                + ", productos=" + productos.size() + ", precio=" + calcularPrecio() + "}";
    }
}
