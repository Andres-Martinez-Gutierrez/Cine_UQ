package uniquindio.edu.co.cineuq.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uniquindio.edu.co.cineuq.exception.AsientoOcupadoException;
import uniquindio.edu.co.cineuq.model.enumeraciones.Clasificacion;
import uniquindio.edu.co.cineuq.model.enumeraciones.Genero;
import uniquindio.edu.co.cineuq.model.enumeraciones.TipoSala;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class FuncionTest {
    private static final double DELTA = 0.001;

    private Pelicula pelicula;
    private Funcion funcion;

    @BeforeEach
    void setUp() {
        pelicula = new Pelicula("P1", "Pelicula", Genero.ACCION, 120, Clasificacion.TODOS);
        Sala sala = new Sala("S1", "Sala 1", 20, TipoSala.TRADICIONAL);
        funcion = new Funcion("F1", pelicula, sala, LocalDate.of(2026, 11, 10), LocalTime.of(14, 0), 10000);
    }

    @Test
    void alCrearLaFuncionTodosLosAsientosEstanDisponibles() {
        assertEquals(20, funcion.asientosDisponibles());
        assertEquals(0, funcion.asientosOcupados());
    }

    @Test
    void asientosOcupadosYDisponiblesSeActualizanAlOcuparAsientos() throws AsientoOcupadoException {
        funcion.buscarAsiento("A1").ocupar();
        funcion.buscarAsiento("A2").ocupar();

        assertEquals(2, funcion.asientosOcupados());
        assertEquals(18, funcion.asientosDisponibles());
    }

    @Test
    void porcentajeOcupacionCalculaElPorcentajeDeAsientosOcupados() throws AsientoOcupadoException {
        for (int i = 1; i <= 5; i++) {
            funcion.buscarAsiento("A" + i).ocupar();
        }

        assertEquals(25.0, funcion.porcentajeOcupacion(), DELTA);
    }

    @Test
    void porcentajeOcupacionDeUnaSalaSinAsientosEsCero() {
        Sala sinAsientos = new Sala("S2", "Sin asientos", 0, TipoSala.TRADICIONAL);
        Funcion vacia = new Funcion("F2", pelicula, sinAsientos, LocalDate.of(2026, 11, 10), LocalTime.of(14, 0), 10000);

        assertEquals(0, vacia.porcentajeOcupacion(), DELTA);
    }

    @Test
    void buscarAsientoDevuelveElAsientoConEseCodigo() {
        AsientoFuncion asiento = funcion.buscarAsiento("B5");

        assertNotNull(asiento);
        assertEquals("B5", asiento.getAsiento().getCodigo());
    }

    @Test
    void buscarAsientoInexistenteDevuelveNull() {
        assertNull(funcion.buscarAsiento("Z99"));
    }
}
