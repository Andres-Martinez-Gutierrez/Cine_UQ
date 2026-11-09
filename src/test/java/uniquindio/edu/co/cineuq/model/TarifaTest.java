package uniquindio.edu.co.cineuq.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TarifaTest {
    private static final double DELTA = 0.001;

    @Test
    void actualizarValorCambiaElValorDeLaTarifa() {
        Tarifa tarifa = new Tarifa(TipoCliente.GENERAL, 0);

        tarifa.actualizarValor(12000);

        assertEquals(12000, tarifa.getValor(), DELTA);
    }
}
