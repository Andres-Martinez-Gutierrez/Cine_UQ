package uniquindio.edu.co.cineuq.model;

public class Cliente {
    private String identificacion;
    private String nombre;
    private TipoCliente tipo;

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

    @Override
    public String toString() {
        return "Cliente{identificacion='" + identificacion + "', nombre='" + nombre + "', tipo=" + tipo + "}";
    }
}
