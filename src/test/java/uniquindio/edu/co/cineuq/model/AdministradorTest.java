package uniquindio.edu.co.cineuq.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uniquindio.edu.co.cineuq.model.enumeraciones.Clasificacion;
import uniquindio.edu.co.cineuq.model.enumeraciones.Genero;
import uniquindio.edu.co.cineuq.model.enumeraciones.TipoCliente;
import uniquindio.edu.co.cineuq.model.enumeraciones.TipoSala;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class AdministradorTest {
    private CinemaUQ cinema;
    private Administrador administrador;

    @BeforeEach
    void setUp() {
        cinema = new CinemaUQ("CineUQ", "Armenia", "900123456");
        administrador = new Administrador("admin", "clave", "Administrador");
    }

    @Test
    void programarFuncionAgregaLaFuncionAlCine() throws Exception {
        Pelicula pelicula = new Pelicula("P1", "Pelicula", Genero.ACCION, 120, Clasificacion.TODOS);
        Sala sala = new Sala("S1", "Sala 1", 20, TipoSala.TRADICIONAL);
        Funcion funcion = new Funcion("F1", pelicula, sala, LocalDate.of(2026, 11, 10), LocalTime.of(14, 0), 10000);

        administrador.programarFuncion(cinema, funcion);

        assertEquals(1, cinema.getListCinemaFunciones().size());
        assertSame(funcion, cinema.buscarFuncion("F1"));
    }

    @Test
    void cambiarTarifaActualizaElValorDelTipoDeCliente() {
        administrador.cambiarTarifa(cinema, TipoCliente.ESTUDIANTE, 8000);

        assertEquals(8000, cinema.buscarTarifa(TipoCliente.ESTUDIANTE).getValor(), 0.001);
    }
}
