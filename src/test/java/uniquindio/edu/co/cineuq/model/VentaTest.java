package uniquindio.edu.co.cineuq.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uniquindio.edu.co.cineuq.model.enumeraciones.CategoriaProducto;
import uniquindio.edu.co.cineuq.model.enumeraciones.EstadoVenta;
import uniquindio.edu.co.cineuq.model.enumeraciones.TipoCliente;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VentaTest {
    private static final double DELTA = 0.001;

    private Venta venta;

    @BeforeEach
    void setUp() {
        Cobro cobro = new Cobro(10000, 0, 0);
        Cliente cliente = new Cliente("1", "Ana", TipoCliente.GENERAL);
        venta = new Venta(EstadoVenta.ACTIVA, cobro, null, cliente, LocalDateTime.now(), "V1");
    }

    @Test
    void agregarProductoSumaSuPrecioAlCobro() {
        venta.agregarProducto(new Producto("C1", "Crispetas", 3000, CategoriaProducto.COMIDA));

        assertEquals(13000, venta.getTotal(), DELTA);
        assertEquals(1, venta.getProductos().size());
    }

    @Test
    void agregarAsientoAgregaElAsientoALaVenta() {
        venta.agregarAsiento(new AsientoFuncion(null, new Asiento("A1", 'A', 1, null)));

        assertEquals(1, venta.getListVentaAsientofun().size());
    }

    @Test
    void cancelarMarcaLaVentaComoCancelada() {
        venta.cancelar();

        assertEquals(EstadoVenta.CANCELADA, venta.getEstado());
    }

    @Test
    void cancelarLiberaLosAsientosDeLaVenta() throws Exception {
        AsientoFuncion asiento = new AsientoFuncion(null, new Asiento("A1", 'A', 1, null));
        asiento.ocupar();
        venta.agregarAsiento(asiento);

        venta.cancelar();

        assertTrue(asiento.estaDisponible());
    }

    @Test
    void cancelarUnaVentaYaCanceladaNoTieneEfecto() throws Exception {
        AsientoFuncion asiento = new AsientoFuncion(null, new Asiento("A1", 'A', 1, null));
        venta.agregarAsiento(asiento);
        venta.cancelar();
        asiento.ocupar();

        venta.cancelar();

        assertFalse(asiento.estaDisponible());
    }
}
