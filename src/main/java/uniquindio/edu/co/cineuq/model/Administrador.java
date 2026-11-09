package uniquindio.edu.co.cineuq.model;

import uniquindio.edu.co.cineuq.exception.CinemaUQException;
import uniquindio.edu.co.cineuq.model.enumeraciones.TipoCliente;

public class Administrador extends Usuario {

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
     */
    public Administrador(String usuario, String contrasena, String nombre) {
        super(usuario, contrasena, nombre);
    }

    /** Operación de interfaz: se implementa en la capa de controladores. */
    public void gestionarPeliculas() {
    }

    /** Operación de interfaz: se implementa en la capa de controladores. */
    public void gestionarSalas() {
    }

    /** Programa una función en el cine, validando que no se traslape con otra de la misma sala. */
    public void programarFuncion(CinemaUQ cinema, Funcion funcion) throws CinemaUQException {
        cinema.programarFuncion(funcion);
    }

    /** Actualiza el valor de la tarifa del tipo de cliente indicado. */
    public void cambiarTarifa(CinemaUQ cinema, TipoCliente tipo, double valor) {
        cinema.buscarTarifa(tipo).actualizarValor(valor);
    }


    @Override
    public String toString() {
        return "Administrador{usuario='" + getUsuario() + "', nombre='" + getNombre() + "'}";
    }
}
