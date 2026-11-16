package uniquindio.edu.co.cineuq.model;

import org.junit.jupiter.api.Test;
import uniquindio.edu.co.cineuq.model.enumeraciones.Clasificacion;
import uniquindio.edu.co.cineuq.model.enumeraciones.EstadoPelicula;
import uniquindio.edu.co.cineuq.model.enumeraciones.Genero;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PeliculaTest {

    @Test
    void unaPeliculaNuevaEstaDisponible() {
        assertEquals(EstadoPelicula.DISPONIBLE, new Pelicula().getEstado());
    }

    @Test
    void deshabilitarCambiaElEstadoANoDisponible() {
        Pelicula pelicula = new Pelicula("P1", "Pelicula", Genero.TERROR, Clasificacion.TODOS, 100);

        pelicula.deshabilitar();

        assertEquals(EstadoPelicula.NO_DISPONIBLE, pelicula.getEstado());
    }
}
