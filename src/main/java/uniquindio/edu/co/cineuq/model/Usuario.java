package uniquindio.edu.co.cineuq.model;

public abstract class Usuario {
    private String usuario;
    private String contrasena;
    private String nombre;

    /**
     * Constructor vacío de la clase Usuario.
     * Los atributos se asignan luego con los setters.
     */
    protected Usuario() {
    }

    /**
     * Constructor de la clase Usuario.
     *
     * @param usuario nombre de usuario con el que inicia sesión
     * @param contrasena contraseña del usuario
     * @param nombre nombre completo del usuario
     */
    protected Usuario(String usuario, String contrasena, String nombre) {
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.nombre = nombre;
    }

    public boolean autenticar(String usuario, String contrasena) {
        return this.usuario.equals(usuario) && this.contrasena.equals(contrasena);
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{usuario='" + usuario + "', nombre='" + nombre + "'}";
    }
}
