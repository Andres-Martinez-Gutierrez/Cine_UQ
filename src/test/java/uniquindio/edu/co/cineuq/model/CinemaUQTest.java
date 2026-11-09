package uniquindio.edu.co.cineuq.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uniquindio.edu.co.cineuq.exception.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CinemaUQTest {
    private static final double DELTA = 0.001;
    private static final LocalDate FECHA = LocalDate.of(2026, 11, 10);

    private CinemaUQ cinema;
    private Pelicula pelicula;
    private Sala sala;

    @BeforeEach
    void setUp() {
        cinema = new CinemaUQ("CineUQ", "Armenia", "900123456");
        pelicula = new Pelicula("P1", "Interestelar", Genero.DRAMA, 120, Clasificacion.TODOS);
        sala = new Sala("S1", "Sala 1", 20, TipoSala.TRADICIONAL);
    }

    private Funcion funcion(String codigo, Sala sala, LocalDate fecha, LocalTime hora) {
        return new Funcion(codigo, pelicula, sala, fecha, hora, 10000);
    }

    private Venta venta(String codigo, Funcion funcion, Cobro cobro) {
        return new Venta(codigo, LocalDateTime.now(), new Cliente("1", "Ana", TipoCliente.GENERAL), funcion, cobro);
    }

    @Test
    void registrarPeliculaAgregaLaPelicula() throws Exception {
        cinema.registrarPelicula(pelicula);

        assertEquals(1, cinema.getListCinemaPeliculas().size());
    }

    @Test
    void registrarPeliculaDuplicadaLanzaExcepcion() throws Exception {
        cinema.registrarPelicula(pelicula);

        assertThrows(PeliculaDuplicadaException.class,
                () -> cinema.registrarPelicula(new Pelicula("P1", "Otra", Genero.COMEDIA, 90, Clasificacion.TODOS)));
    }

    @Test
    void registrarSalaAgregaLaSala() throws Exception {
        cinema.registrarSala(sala);

        assertEquals(1, cinema.getListCinemaSalas().size());
    }

    @Test
    void registrarSalaDuplicadaLanzaExcepcion() throws Exception {
        cinema.registrarSala(sala);

        assertThrows(SalaDuplicadaException.class,
                () -> cinema.registrarSala(new Sala("S1", "Otra", 10, TipoSala.IMAX)));
    }

    @Test
    void programarFuncionEnSalaFueraDeServicioLanzaExcepcion() {
        sala.deshabilitar();

        assertThrows(SalaNoDisponibleException.class,
                () -> cinema.programarFuncion(funcion("F1", sala, FECHA, LocalTime.of(14, 0))));
    }

    @Test
    void programarFuncionQueSeTraslapaEnLaMismaSalaLanzaExcepcion() throws Exception {
        cinema.programarFuncion(funcion("F1", sala, FECHA, LocalTime.of(14, 0)));

        assertThrows(SalaNoDisponibleException.class,
                () -> cinema.programarFuncion(funcion("F2", sala, FECHA, LocalTime.of(15, 0))));
    }

    @Test
    void programarFuncionQueEmpiezaCuandoTerminaLaAnteriorNoSeTraslapa() throws Exception {
        cinema.programarFuncion(funcion("F1", sala, FECHA, LocalTime.of(14, 0)));

        cinema.programarFuncion(funcion("F2", sala, FECHA, LocalTime.of(16, 0)));

        assertEquals(2, cinema.getListCinemaFunciones().size());
    }

    @Test
    void programarFuncionEnOtraSalaOOtroDiaNoSeTraslapa() throws Exception {
        Sala otraSala = new Sala("S2", "Sala 2", 20, TipoSala.VIP);
        cinema.programarFuncion(funcion("F1", sala, FECHA, LocalTime.of(14, 0)));

        cinema.programarFuncion(funcion("F2", otraSala, FECHA, LocalTime.of(14, 0)));
        cinema.programarFuncion(funcion("F3", sala, FECHA.plusDays(1), LocalTime.of(14, 0)));

        assertEquals(3, cinema.getListCinemaFunciones().size());
    }

    @Test
    void programarFuncionIgnoraLasFuncionesCanceladas() throws Exception {
        Funcion cancelada = funcion("F1", sala, FECHA, LocalTime.of(14, 0));
        cinema.programarFuncion(cancelada);
        cancelada.setEstado(EstadoFuncion.CANCELADA);

        cinema.programarFuncion(funcion("F2", sala, FECHA, LocalTime.of(15, 0)));

        assertEquals(2, cinema.getListCinemaFunciones().size());
    }

    @Test
    void registrarVentaAgregaLaVenta() {
        cinema.registrarVenta(venta("V1", funcion("F1", sala, FECHA, LocalTime.of(14, 0)),
                new Cobro(10000, 0, 0)));

        assertEquals(1, cinema.getListCinemaVentas().size());
    }

    @Test
    void buscarVentaDevuelveLaVentaExistente() throws Exception {
        Venta venta = venta("V1", funcion("F1", sala, FECHA, LocalTime.of(14, 0)), new Cobro(10000, 0, 0));
        cinema.registrarVenta(venta);

        assertSame(venta, cinema.buscarVenta("V1"));
    }

    @Test
    void buscarVentaInexistenteLanzaExcepcion() {
        assertThrows(VentaNoEncontradaException.class, () -> cinema.buscarVenta("X"));
    }

    @Test
    void totalEntradasVendidasCuentaSoloAsientosDeVentasActivas() {
        Funcion funcion = funcion("F1", sala, FECHA, LocalTime.of(14, 0));
        Venta activa = venta("V1", funcion, new Cobro(10000, 0, 0));
        activa.agregarAsiento(new AsientoFuncion(null, new Asiento("A1", 'A', 1)));
        activa.agregarAsiento(new AsientoFuncion(null, new Asiento("A2", 'A', 2)));
        Venta cancelada = venta("V2", funcion, new Cobro(10000, 0, 0));
        cancelada.agregarAsiento(new AsientoFuncion(null, new Asiento("A3", 'A', 3)));
        cancelada.cancelar();
        cinema.registrarVenta(activa);
        cinema.registrarVenta(cancelada);

        assertEquals(2, cinema.totalEntradasVendidas());
    }

    @Test
    void totalIngresosSumaSoloVentasActivas() {
        Funcion funcion = funcion("F1", sala, FECHA, LocalTime.of(14, 0));
        Venta primera = venta("V1", funcion, new Cobro(10000, 0, 0));
        Venta cancelada = venta("V2", funcion, new Cobro(15000, 0, 0));
        cancelada.cancelar();
        Venta segunda = venta("V3", funcion, new Cobro(8000, 0, 0));
        cinema.registrarVenta(primera);
        cinema.registrarVenta(cancelada);
        cinema.registrarVenta(segunda);

        assertEquals(18000, cinema.totalIngresos(), DELTA);
    }

    @Test
    void peliculaMasVendidaSinVentasDevuelveNull() {
        assertNull(cinema.peliculaMasVendida());
    }

    @Test
    void peliculaMasVendidaDevuelveLaPeliculaConMasEntradas() {
        Pelicula otra = new Pelicula("P2", "Otra", Genero.COMEDIA, 90, Clasificacion.TODOS);
        Funcion funcionPrincipal = funcion("F1", sala, FECHA, LocalTime.of(14, 0));
        Funcion funcionOtra = new Funcion("F2", otra, sala, FECHA, LocalTime.of(18, 0), 10000);
        Venta v1 = venta("V1", funcionPrincipal, new Cobro(10000, 0, 0));
        v1.agregarAsiento(new AsientoFuncion(null, new Asiento("A1", 'A', 1)));
        Venta v2 = venta("V2", funcionOtra, new Cobro(10000, 0, 0));
        v2.agregarAsiento(new AsientoFuncion(null, new Asiento("A1", 'A', 1)));
        v2.agregarAsiento(new AsientoFuncion(null, new Asiento("A2", 'A', 2)));
        cinema.registrarVenta(v1);
        cinema.registrarVenta(v2);

        assertSame(otra, cinema.peliculaMasVendida());
    }

    @Test
    void porcentajeOcupacionDevuelveElPorcentajeDeLaFuncion() throws Exception {
        Funcion funcion = funcion("F1", sala, FECHA, LocalTime.of(14, 0));
        for (int i = 1; i <= 5; i++) {
            funcion.buscarAsiento("A" + i).ocupar();
        }

        assertEquals(25.0, cinema.porcentajeOcupacion(funcion), DELTA);
    }

    @Test
    void funcionesDiaDevuelveSoloLasFuncionesDeLaFecha() throws Exception {
        Funcion primera = funcion("F1", sala, FECHA, LocalTime.of(14, 0));
        Funcion segunda = funcion("F2", sala, FECHA, LocalTime.of(18, 0));
        cinema.programarFuncion(primera);
        cinema.programarFuncion(segunda);
        cinema.programarFuncion(funcion("F3", sala, FECHA.plusDays(1), LocalTime.of(14, 0)));

        assertEquals(List.of(primera, segunda), cinema.funcionesDia(FECHA));
    }

    @Test
    void buscarFuncionDevuelveLaFuncionExistente() throws Exception {
        Funcion funcion = funcion("F1", sala, FECHA, LocalTime.of(14, 0));
        cinema.programarFuncion(funcion);

        assertSame(funcion, cinema.buscarFuncion("F1"));
    }

    @Test
    void buscarFuncionInexistenteLanzaExcepcion() {
        assertThrows(FuncionNoEncontradaException.class, () -> cinema.buscarFuncion("X"));
    }

    @Test
    void buscarTarifaDevuelveUnaTarifaPorCadaTipoDeClienteEnCero() {
        for (TipoCliente tipo : TipoCliente.values()) {
            assertEquals(0, cinema.buscarTarifa(tipo).getValor(), DELTA);
        }
    }

    @Test
    void buscarTarifaReflejaElNuevoValorAsignado() {
        cinema.buscarTarifa(TipoCliente.GENERAL).actualizarValor(12000);

        assertEquals(12000, cinema.buscarTarifa(TipoCliente.GENERAL).getValor(), DELTA);
        assertTrue(cinema.buscarTarifa(TipoCliente.GENERAL).getValor() > 0);
    }
}
