package uniquindio.edu.co.cineuq.model;

import java.util.ArrayList;
import java.util.List;

public class Cliente {

    //Atributos de la clase Cliente
    private String identificacion;
    private String nombre;

    //Relaciones de la Clase Cliente
    private TipoCliente tipo;
    private List<Venta> listClientesVenta;


    /**
     * Constructor vacío de la clase Cliente.
     * Los atributos se asignan luego con los setters.
     */
    public Cliente() {
    }

    /**
     * Constructor de la clase Cliente.
     *
     * @param identificacion número de identificación del cliente
     * @param nombre nombre completo del cliente
     * @param tipo tipo de cliente, que determina la tarifa que se le aplica
     */
    public Cliente(String identificacion, String nombre, TipoCliente tipo) {
        this.identificacion = identificacion;
        this.nombre = nombre;
        this.tipo = tipo;
        this.listClientesVenta = new ArrayList<>();
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoCliente getTipo() {
        return tipo;
    }

    public void setTipo(TipoCliente tipo) {
        this.tipo = tipo;
    }

    public List<Venta> getListClientesVenta() {
        return listClientesVenta;
    }

    public void setListClientesVenta(List<Venta> listClientesVenta) {
        this.listClientesVenta = listClientesVenta;
    }

    @Override
    public String toString() {
        return "Cliente{identificacion='" + identificacion + "', nombre='" + nombre + "', tipo=" + tipo + "}";
    }
}
