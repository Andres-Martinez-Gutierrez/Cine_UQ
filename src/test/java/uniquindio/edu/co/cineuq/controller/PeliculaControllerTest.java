package uniquindio.edu.co.cineuq.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uniquindio.edu.co.cineuq.exception.PeliculaDuplicadaException;
import uniquindio.edu.co.cineuq.model.Administrador;
import uniquindio.edu.co.cineuq.model.CinemaUQ;
import uniquindio.edu.co.cineuq.model.Pelicula;
import uniquindio.edu.co.cineuq.model.enumeraciones.Clasificacion;
import uniquindio.edu.co.cineuq.model.enumeraciones.Genero;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PeliculaControllerTest {
    private PeliculaController peliculas;

    @BeforeEach
    void setUp() {
        peliculas = new PeliculaController(new CinemaUQ("CineUQ", "Armenia", "900123456"),
                new Administrador("admin", "clave", "Administrador"));
    }

    @Test
    void registrarRecortaLosEspaciosYAgregaLaPelicula() throws Exception {
        Pelicula pelicula = peliculas.registrar(" P1 ", " Interestelar ", Genero.DRAMA, Clasificacion.TODOS, 120);

        assertEquals("P1", pelicula.getCodigo());
        assertEquals("Interestelar", pelicula.getNombre());
        assertEquals(List.of(pelicula), peliculas.listar());
    }

    @Test
    void registrarSinNombreLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> peliculas.registrar("P1", " ", Genero.DRAMA, Clasificacion.TODOS, 120));
    }

    @Test
    void registrarConDuracionNoPositivaLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> peliculas.registrar("P1", "Pelicula", Genero.DRAMA, Clasificacion.TODOS, 0));
    }

    @Test
    void registrarUnCodigoRepetidoLanzaExcepcion() throws Exception {
        peliculas.registrar("P1", "Pelicula", Genero.DRAMA, Clasificacion.TODOS, 120);

        assertThrows(PeliculaDuplicadaException.class,
                () -> peliculas.registrar("P1", "Otra", Genero.COMEDIA, Clasificacion.TODOS, 90));
    }

    @Test
    void disponiblesPorGeneroFiltraYExcluyeLasDeshabilitadas() throws Exception {
        Pelicula drama = peliculas.registrar("P1", "Drama", Genero.DRAMA, Clasificacion.TODOS, 120);
        Pelicula comedia = peliculas.registrar("P2", "Comedia", Genero.COMEDIA, Clasificacion.TODOS, 90);
        Pelicula otroDrama = peliculas.registrar("P3", "Otro drama", Genero.DRAMA, Clasificacion.TODOS, 100);
        peliculas.alternarEstado(otroDrama);

        assertEquals(List.of(drama), peliculas.disponiblesPorGenero(Genero.DRAMA));
        assertEquals(List.of(drama, comedia), peliculas.disponiblesPorGenero(null));
    }
}
