package uniquindio.edu.co.cineuq.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uniquindio.edu.co.cineuq.exception.AsientoOcupadoException;
import uniquindio.edu.co.cineuq.exception.SinAsientosDisponiblesException;
import uniquindio.edu.co.cineuq.model.enumeraciones.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OperadorTest {
    private static final double DELTA = 0.001;
    private static final LocalDate FECHA = LocalDate.of(2026, 11, 10);

    private CinemaUQ cinema;
    private Operador operador;
    private Pelicula pelicula;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cinema = new CinemaUQ("CineUQ", "Armenia", "900123456");
        cinema.buscarTarifa(TipoCliente.GENERAL).actualizarValor(10000);
        operador = new Operador("operador", "clave", "Operador", cinema);
        pelicula = new Pelicula("P1", "Pelicula", Genero.ACCION, 120, Clasificacion.TODOS);
        cliente = new Cliente("1", "Ana", TipoCliente.GENERAL);
    }

    private Funcion funcion(TipoSala tipo, int capacidad) {
        Sala sala = new Sala("S1", "Sala 1", capacidad, tipo);
        return new Funcion("F1", pelicula, sala, FECHA, LocalTime.of(14, 0), 10000);
    }

    @Test
    void venderCalculaElValorConLaTarifaYElRecargoDeLaSala() throws Exception {
        Funcion funcion = funcion(TipoSala.VIP, 20);

        Venta venta = operador.vender(funcion, cliente, "A1");

        assertEquals(15000, venta.getTotal(), DELTA);
        assertEquals(EstadoVenta.ACTIVA, venta.getEstado());
        assertEquals(1, venta.getAsientos().size());
    }

    @Test
    void venderRegistraLaVentaEnElCine() throws Exception {
        Venta venta = operador.vender(funcion(TipoSala.TRADICIONAL, 20), cliente, "A1");

        assertTrue(cinema.getListCinemaVentas().contains(venta));
    }

    @Test
    void venderOcupaElAsientoVendido() throws Exception {
        Funcion funcion = funcion(TipoSala.TRADICIONAL, 20);

        operador.vender(funcion, cliente, "A1");

        assertFalse(funcion.buscarAsiento("A1").estaDisponible());
    }

    @Test
    void venderUnAsientoYaOcupadoLanzaExcepcion() throws Exception {
        Funcion funcion = funcion(TipoSala.TRADICIONAL, 20);
        operador.vender(funcion, cliente, "A1");

        assertThrows(AsientoOcupadoException.class, () -> operador.vender(funcion, cliente, "A1"));
    }

    @Test
    void venderUnAsientoInexistenteLanzaExcepcion() {
        Funcion funcion = funcion(TipoSala.TRADICIONAL, 20);

        assertThrows(IllegalArgumentException.class, () -> operador.vender(funcion, cliente, "Z99"));
    }

    @Test
    void venderEnUnaFuncionCanceladaLanzaExcepcion() {
        Funcion funcion = funcion(TipoSala.TRADICIONAL, 20);
        funcion.setEstado(EstadoFuncion.CANCELADA);

        assertThrows(IllegalStateException.class, () -> operador.vender(funcion, cliente, "A1"));
    }

    @Test
    void venderSinAsientosDisponiblesLanzaExcepcion() throws Exception {
        Funcion funcion = funcion(TipoSala.TRADICIONAL, 1);
        operador.vender(funcion, cliente, "A1");

        assertThrows(SinAsientosDisponiblesException.class, () -> operador.vender(funcion, cliente, "A1"));
    }

    @Test
    void cancelarVentaCambiaSuEstadoYLiberaSusAsientos() throws Exception {
        Funcion funcion = funcion(TipoSala.TRADICIONAL, 20);
        Venta venta = operador.vender(funcion, cliente, "A1");

        operador.cancelarVenta(venta);

        assertEquals(EstadoVenta.CANCELADA, venta.getEstado());
        assertTrue(funcion.buscarAsiento("A1").estaDisponible());
    }

    @Test
    void funcionesDiaDelegaEnLasFuncionesDelCine() throws Exception {
        Funcion funcion = funcion(TipoSala.TRADICIONAL, 20);
        cinema.programarFuncion(funcion);

        assertEquals(List.of(funcion), operador.funcionesDia(FECHA));
    }
}
