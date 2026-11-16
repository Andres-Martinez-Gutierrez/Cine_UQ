package uniquindio.edu.co.cineuq.model;

import org.junit.jupiter.api.Test;
import uniquindio.edu.co.cineuq.model.enumeraciones.EstadoSala;
import uniquindio.edu.co.cineuq.model.enumeraciones.TipoSala;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SalaTest {
    private static final double DELTA = 0.001;

    @Test
    void deshabilitarPonLaSalaFueraDeServicio() {
        Sala sala = new Sala("S1", "Sala 1", 20, TipoSala.TRADICIONAL);

        sala.deshabilitar();

        assertEquals(EstadoSala.FUERA_DE_SERVICIO, sala.getEstado());
    }

    @Test
    void unaSalaNuevaEstaDisponible() {
        assertEquals(EstadoSala.DISPONIBLE, new Sala("S1", "Sala 1", 20, TipoSala.VIP).getEstado());
    }

    @Test
    void recargoDependeDelTipoDeSala() {
        assertEquals(0, recargo(TipoSala.TRADICIONAL), DELTA);
        assertEquals(5000, recargo(TipoSala.VIP), DELTA);
        assertEquals(3000, recargo(TipoSala.SALA_3D), DELTA);
        assertEquals(8000, recargo(TipoSala.IMAX), DELTA);
    }

    private double recargo(TipoSala tipo) {
        return new Sala("S1", "Sala 1", 20, tipo).recargo();
    }
}
