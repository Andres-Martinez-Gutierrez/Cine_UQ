package uniquindio.edu.co.cineuq.controller;

import uniquindio.edu.co.cineuq.exception.PeliculaDuplicadaException;
import uniquindio.edu.co.cineuq.model.Administrador;
import uniquindio.edu.co.cineuq.model.CinemaUQ;
import uniquindio.edu.co.cineuq.model.Pelicula;
import uniquindio.edu.co.cineuq.model.enumeraciones.Clasificacion;
import uniquindio.edu.co.cineuq.model.enumeraciones.EstadoPelicula;
import uniquindio.edu.co.cineuq.model.enumeraciones.Genero;

import java.util.List;
import java.util.Objects;

/**
 * Casos de uso del catálogo de películas.
 */
public class PeliculaController {
    private final CinemaUQ cinema;
    private final Administrador administrador;

    public PeliculaController(CinemaUQ cinema, Administrador administrador) {
        this.cinema = Objects.requireNonNull(cinema);
        this.administrador = Objects.requireNonNull(administrador);
    }

    public List<Pelicula> listar() {
        return List.copyOf(cinema.getListCinemaPeliculas());
    }

    public List<Pelicula> disponibles() {
        return cinema.getListCinemaPeliculas().stream()
                .filter(pelicula -> pelicula.getEstado() == EstadoPelicula.DISPONIBLE)
                .toList();
    }

    /** Películas disponibles del género indicado; con {@code null} devuelve todas las disponibles. */
    public List<Pelicula> disponiblesPorGenero(Genero genero) {
        return disponibles().stream()
                .filter(pelicula -> genero == null || pelicula.getGenero() == genero)
                .toList();
    }

    public Pelicula registrar(String codigo, String nombre, Genero genero, Clasificacion clasificacion,
                              int duracionMinutos) throws PeliculaDuplicadaException {
        validar(codigo, nombre, genero, clasificacion, duracionMinutos);
        Pelicula pelicula = new Pelicula(codigo.trim(), nombre.trim(), genero, clasificacion, duracionMinutos);
        administrador.gestionarPeliculas(cinema, pelicula);
        return pelicula;
    }

    public void actualizar(Pelicula pelicula, String nombre, Genero genero, Clasificacion clasificacion,
                           int duracionMinutos) {
        requerirSeleccion(pelicula);
        validar(pelicula.getCodigo(), nombre, genero, clasificacion, duracionMinutos);
        pelicula.setNombre(nombre.trim());
        pelicula.setGenero(genero);
        pelicula.setClasificacion(clasificacion);
        pelicula.setDuracionMinutos(duracionMinutos);
    }

    public void alternarEstado(Pelicula pelicula) {
        requerirSeleccion(pelicula);
        pelicula.setEstado(pelicula.getEstado() == EstadoPelicula.DISPONIBLE
                ? EstadoPelicula.NO_DISPONIBLE : EstadoPelicula.DISPONIBLE);
    }

    private static void requerirSeleccion(Pelicula pelicula) {
        if (pelicula == null) throw new IllegalArgumentException("Selecciona una película primero.");
    }

    private static void validar(String codigo, String nombre, Genero genero, Clasificacion clasificacion,
                                int duracionMinutos) {
        if (codigo == null || codigo.isBlank() || nombre == null || nombre.isBlank()
                || genero == null || clasificacion == null) {
            throw new IllegalArgumentException("Completa todos los campos de la película.");
        }
        if (duracionMinutos <= 0) {
            throw new IllegalArgumentException("La duración debe ser mayor que cero.");
        }
    }
}
