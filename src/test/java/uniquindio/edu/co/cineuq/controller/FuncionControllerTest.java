package uniquindio.edu.co.cineuq.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uniquindio.edu.co.cineuq.model.*;
import uniquindio.edu.co.cineuq.model.enumeraciones.*;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class FuncionControllerTest {
    private CinemaUQ cinema;
    private Pelicula pelicula;
    private Sala sala;
    private FuncionController funciones;

    @BeforeEach
    void setUp() {
        cinema = new CinemaUQ("CineUQ", "Armenia", "900123456");
        pelicula = new Pelicula("P1", "Pelicula", Genero.ACCION, Clasificacion.TODOS, 120);
        sala = new Sala("S1", "Sala 1", 20, TipoSala.TRADICIONAL);
        funciones = new FuncionController(cinema, new Administrador("admin", "clave", "Administrador"));
    }

    @Test
    void programarUnaFuncionFuturaLaAgregaAlCine() throws Exception {
        Funcion funcion = funciones.programar(pelicula, sala, LocalDate.now().plusDays(1), LocalTime.of(14, 0));

        assertTrue(funciones.listar().contains(funcion));
        assertTrue(funciones.esVendible(funcion));
    }

    @Test
    void programarUnaFuncionEnElPasadoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> funciones.programar(pelicula, sala, LocalDate.now().minusDays(1), LocalTime.of(14, 0)));
    }

    @Test
    void programarSinPeliculaLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> funciones.programar(null, sala, LocalDate.now().plusDays(1), LocalTime.of(14, 0)));
    }

    @Test
    void cancelarUnaFuncionYaCanceladaLanzaExcepcion() throws Exception {
        Funcion funcion = funciones.programar(pelicula, sala, LocalDate.now().plusDays(1), LocalTime.of(14, 0));
        funciones.cancelar(funcion);

        assertEquals(EstadoFuncion.CANCELADA, funcion.getEstado());
        assertFalse(funciones.esVendible(funcion));
        assertThrows(IllegalStateException.class, () -> funciones.cancelar(funcion));
    }

    @Test
    void unaFuncionConLaPeliculaDeshabilitadaNoEsVendible() throws Exception {
        Funcion funcion = funciones.programar(pelicula, sala, LocalDate.now().plusDays(1), LocalTime.of(14, 0));
        pelicula.setEstado(EstadoPelicula.NO_DISPONIBLE);

        assertFalse(funciones.esVendible(funcion));
    }
}
