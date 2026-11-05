package uniquindio.edu.co.cineuq.model;

import uniquindio.edu.co.cineuq.exception.CinemaUQException;

public class Administrador extends Usuario {
    private CinemaUQ cinema;

    /**
     * Constructor vacío de la clase Administrador.
     * Los atributos se asignan luego con los setters.
     */
    public Administrador() {
    }

    /**
     * Constructor de la clase Administrador.
     *
     * @param usuario nombre de usuario con el que inicia sesión
     * @param contrasena contraseña del administrador
     * @param nombre nombre completo del administrador
     * @param cinema cine que el administrador gestiona
     */
    public Administrador(String usuario, String contrasena, String nombre, CinemaUQ cinema) {
        super(usuario, contrasena, nombre);
        this.cinema = cinema;
    }

    /** Operación de interfaz: se implementa en la capa de controladores. */
    public void gestionarPeliculas() {
    }

    /** Operación de interfaz: se implementa en la capa de controladores. */
    public void gestionarSalas() {
    }

    public void programarFuncion(Funcion funcion) throws CinemaUQException {
        cinema.programarFuncion(funcion);
    }

    public void cambiarTarifa(TipoCliente tipo, double valor) {
        cinema.buscarTarifa(tipo).actualizarValor(valor);
    }

    public CinemaUQ getCinema() {
        return cinema;
    }

    public void setCinema(CinemaUQ cinema) {
        this.cinema = cinema;
    }

    @Override
    public String toString() {
        return "Administrador{usuario='" + getUsuario() + "', nombre='" + getNombre() + "'}";
    }
}
