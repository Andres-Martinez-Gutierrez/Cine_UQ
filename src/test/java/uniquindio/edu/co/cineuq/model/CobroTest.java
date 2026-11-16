package uniquindio.edu.co.cineuq.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CobroTest {
    private static final double DELTA = 0.001;

    @Test
    void calcularTotalSumaEntradaRecargoYProductosYRestaElDescuento() {
        Cobro cobro = new Cobro(10000, 5000, 2000);
        cobro.agregarValorProductos(3000);

        assertEquals(16000, cobro.calcularTotal(), DELTA);
    }

    @Test
    void calcularTotalDeUnCobroVacioEsCero() {
        assertEquals(0, new Cobro().calcularTotal(), DELTA);
    }

    @Test
    void agregarValorProductosAcumulaLosValoresAgregados() {
        Cobro cobro = new Cobro(10000, 0, 0);

        cobro.agregarValorProductos(3000);
        cobro.agregarValorProductos(2000);

        assertEquals(15000, cobro.calcularTotal(), DELTA);
    }
}
