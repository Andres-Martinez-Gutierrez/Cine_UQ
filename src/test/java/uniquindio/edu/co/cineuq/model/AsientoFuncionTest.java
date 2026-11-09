package uniquindio.edu.co.cineuq.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uniquindio.edu.co.cineuq.exception.AsientoOcupadoException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AsientoFuncionTest {
    private AsientoFuncion asientoFuncion;

    @BeforeEach
    void setUp() {
        asientoFuncion = new AsientoFuncion(null, new Asiento("A1", 'A', 1));
    }

    @Test
    void unAsientoNuevoEstaDisponible() {
        assertTrue(asientoFuncion.estaDisponible());
    }

    @Test
    void ocuparMarcaElAsientoComoNoDisponible() throws AsientoOcupadoException {
        asientoFuncion.ocupar();

        assertFalse(asientoFuncion.estaDisponible());
    }

    @Test
    void ocuparUnAsientoYaOcupadoLanzaExcepcion() throws AsientoOcupadoException {
        asientoFuncion.ocupar();

        assertThrows(AsientoOcupadoException.class, asientoFuncion::ocupar);
    }

    @Test
    void liberarDejaElAsientoDisponibleDeNuevo() throws AsientoOcupadoException {
        asientoFuncion.ocupar();

        asientoFuncion.liberar();

        assertTrue(asientoFuncion.estaDisponible());
    }

    @Test
    void unAsientoLiberadoSePuedeOcuparDeNuevo() throws AsientoOcupadoException {
        asientoFuncion.ocupar();
        asientoFuncion.liberar();

        assertDoesNotThrow(asientoFuncion::ocupar);
    }
}
