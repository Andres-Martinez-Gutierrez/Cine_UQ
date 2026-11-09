package uniquindio.edu.co.cineuq.model;

public class Pelicula {
    private String codigo;
    private String nombre;
    private Genero genero;
    private int duracionMinutos;
    private EstadoPelicula estado;
    private Clasificacion clasificacion;

    /**
     * Constructor vacío de la clase Pelicula.
     * Deja el estado de la película en DISPONIBLE; los demás atributos se asignan con los setters.
     */
    public Pelicula() {
        this.estado = EstadoPelicula.DISPONIBLE;
    }

    /**
     * Constructor de la clase Pelicula.
     *
     * @param codigo código de la película
     * @param nombre título de la película
     * @param genero género cinematográfico de la película
     * @param duracionMinutos duración de la película en minutos
     */
    public Pelicula(String codigo, String nombre, Genero genero, int duracionMinutos, Clasificacion clasificacion) {
        this();
        this.codigo = codigo;
        this.nombre = nombre;
        this.genero = genero;
        this.duracionMinutos = duracionMinutos;
        this.clasificacion = clasificacion;
    }

    /** Marca la película como no disponible. */
    public void deshabilitar() {
        estado = EstadoPelicula.NO_DISPONIBLE;
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

    public Genero getGenero() {
        return genero;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(int duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public EstadoPelicula getEstado() {
        return estado;
    }

    public void setEstado(EstadoPelicula estado) {
        this.estado = estado;
    }

    public Clasificacion getClasificacion() {
        return clasificacion;
    }

    public void setClasificacion(Clasificacion clasificacion) {
        this.clasificacion = clasificacion;
    }

    @Override
    public String toString() {
        return "Pelicula{codigo='" + codigo + "', nombre='" + nombre + "', genero=" + genero
                + ", duracionMinutos=" + duracionMinutos + ", estado=" + estado + "}";
    }
}
