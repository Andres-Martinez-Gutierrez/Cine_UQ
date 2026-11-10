package uniquindio.edu.co.cineuq.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uniquindio.edu.co.cineuq.model.*;
import uniquindio.edu.co.cineuq.model.enumeraciones.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VentaControllerTest {
    private static final double DELTA = 0.001;

    private CinemaUQ cinema;
    private Funcion funcion;
    private Producto crispetas;
    private VentaController ventas;

    @BeforeEach
    void setUp() throws Exception {
        cinema = new CinemaUQ("CineUQ", "Armenia", "900123456");
        cinema.buscarTarifa(TipoCliente.GENERAL).actualizarValor(10000);
        Administrador administrador = new Administrador("admin", "clave", "Administrador");
        Pelicula pelicula = new Pelicula("P1", "Pelicula", Genero.ACCION, Clasificacion.TODOS, 120);
        Sala sala = new Sala("S1", "Sala 1", 20, TipoSala.TRADICIONAL);
        cinema.registrarPelicula(pelicula);
        cinema.registrarSala(sala);
        funcion = new Funcion("F1", pelicula, sala, LocalDate.now().plusDays(1), LocalTime.of(14, 0), 10000);
        cinema.programarFuncion(funcion);
        crispetas = new Producto("C1", "Crispetas", 3000, CategoriaProducto.COMIDA, null);

        ventas = new VentaController(cinema, new Operador("op", "clave", "Operador", cinema), List.of(crispetas),
                new FuncionController(cinema, administrador));
    }

    @Test
    void iniciarCompraDeUnaFuncionCanceladaLanzaExcepcion() {
        funcion.setEstado(EstadoFuncion.CANCELADA);

        assertThrows(IllegalStateException.class, () -> ventas.iniciarCompra(funcion));
    }

    @Test
    void totalSumaEntradasYProductos() {
        CompraEnCurso compra = ventas.iniciarCompra(funcion);
        compra.alternarAsiento(funcion.buscarAsiento("A1"));
        compra.alternarAsiento(funcion.buscarAsiento("A2"));
        compra.agregarProducto(crispetas);

        assertEquals(2 * ventas.precioEntrada(compra) + 3000, ventas.total(compra), DELTA);
    }

    @Test
    void venderCobraLoMismoQueMuestraElResumen() throws Exception {
        CompraEnCurso compra = ventas.iniciarCompra(funcion);
        compra.alternarAsiento(funcion.buscarAsiento("A1"));
        compra.alternarAsiento(funcion.buscarAsiento("A2"));
        compra.agregarProducto(crispetas);
        compra.agregarProducto(crispetas);

        Venta venta = ventas.vender(compra, "Ana", "");

        assertEquals(ventas.total(compra), venta.getTotal(), DELTA);
        assertEquals(2, venta.getListVentaAsientofun().size());
        assertEquals("SIN-ID", venta.getCliente().getIdentificacion());
        assertTrue(cinema.getListCinemaVentas().contains(venta));
    }

    @Test
    void elDesgloseDelResumenCoincideConElCobroDeLaVenta() throws Exception {
        Sala vip = new Sala("S2", "Sala VIP", 20, TipoSala.VIP);
        cinema.registrarSala(vip);
        Funcion funcionVip = new Funcion("F2", funcion.getPelicula(), vip, LocalDate.now().plusDays(1),
                LocalTime.of(18, 0), 10000);
        cinema.programarFuncion(funcionVip);
        CompraEnCurso compra = ventas.iniciarCompra(funcionVip);
        compra.alternarAsiento(funcionVip.buscarAsiento("A1"));
        compra.alternarAsiento(funcionVip.buscarAsiento("A2"));

        double tarifas = ventas.totalTarifas(compra);
        double recargo = ventas.totalRecargo(compra);
        Cobro cobro = ventas.vender(compra, "Ana", "1").getListVentaCobros().getFirst();

        assertEquals(20000, tarifas, DELTA);
        assertEquals(10000, recargo, DELTA);
        assertEquals(cobro.getValorEntrada(), tarifas, DELTA);
        assertEquals(cobro.getRecargoSala(), recargo, DELTA);
    }

    @Test
    void venderSoloConfiteriaCobraLosProductosSinEntradas() throws Exception {
        CompraEnCurso compra = ventas.iniciarCompraConfiteria();
        compra.agregarProducto(crispetas);
        compra.agregarProducto(crispetas);

        Venta venta = ventas.vender(compra, "Ana", "1");

        assertFalse(compra.incluyeEntradas());
        assertEquals(6000, ventas.total(compra), DELTA);
        assertEquals(6000, venta.getTotal(), DELTA);
        assertNull(venta.getFuncion());
        assertTrue(venta.getListVentaAsientofun().isEmpty());
        assertTrue(cinema.getListCinemaVentas().contains(venta));
        assertEquals(0, cinema.totalEntradasVendidas());
        assertNull(cinema.peliculaMasVendida());
    }

    @Test
    void venderConfiteriaSinProductosLanzaExcepcion() {
        CompraEnCurso compra = ventas.iniciarCompraConfiteria();

        assertThrows(IllegalArgumentException.class, () -> ventas.vender(compra, "Ana", "1"));
    }

    @Test
    void unaCompraDeConfiteriaNoAdmiteAsientos() {
        CompraEnCurso compra = ventas.iniciarCompraConfiteria();

        assertThrows(IllegalStateException.class, () -> compra.alternarAsiento(funcion.buscarAsiento("A1")));
    }

    @Test
    void venderSinNombreDeClienteLanzaExcepcion() {
        CompraEnCurso compra = ventas.iniciarCompra(funcion);
        compra.alternarAsiento(funcion.buscarAsiento("A1"));

        assertThrows(IllegalArgumentException.class, () -> ventas.vender(compra, "  ", null));
    }

    @Test
    void alternarAsientoDosVecesLoDeselecciona() {
        CompraEnCurso compra = ventas.iniciarCompra(funcion);
        AsientoFuncion asiento = funcion.buscarAsiento("A1");

        assertTrue(compra.alternarAsiento(asiento));
        assertFalse(compra.alternarAsiento(asiento));
        assertFalse(compra.tieneAsientos());
    }
}
