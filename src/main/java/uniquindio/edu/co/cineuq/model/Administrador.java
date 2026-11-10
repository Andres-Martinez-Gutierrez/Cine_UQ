package uniquindio.edu.co.cineuq.model;

import uniquindio.edu.co.cineuq.exception.CinemaUQException;
import uniquindio.edu.co.cineuq.exception.PeliculaDuplicadaException;
import uniquindio.edu.co.cineuq.exception.SalaDuplicadaException;
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
     * @param usuario    nombre de usuario con el que inicia sesión
     * @param contrasena contraseña del administrador
     * @param nombre     nombre completo del administrador
     * @param cinema     cine que el administrador gestiona
     */
    public Administrador(String usuario, String contrasena, String nombre) {
        super(usuario, contrasena, nombre);
    }

    /**
     * Registra una película en el cine administrado.
     */
    public void gestionarPeliculas(Pelicula pelicula) throws PeliculaDuplicadaException {
        cinema.registrarPelicula(pelicula);
    }

    /**
     * Registra una sala en el cine administrado.
     */
    public void gestionarSalas(Sala sala) throws SalaDuplicadaException {
        cinema.registrarSala(sala);
    }

    /**
     * Programa una función en el cine, validando que no se traslape con otra de la misma sala.
     */
    public void programarFuncion(Funcion funcion) throws CinemaUQException {
        cinema.programarFuncion(funcion);
    }

    /**
     * Actualiza el valor de la tarifa del tipo de cliente indicado.
     */
    public void cambiarTarifa(TipoCliente tipo, double valor) {
        cinema.buscarTarifa(tipo).actualizarValor(valor);
    }


    @Override
    public String toString() {
        return "Administrador{usuario='" + getUsuario() + "', nombre='" + getNombre() + "'}";
    }
}
