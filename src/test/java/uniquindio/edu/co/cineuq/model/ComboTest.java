package uniquindio.edu.co.cineuq.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ComboTest {
    private static final double DELTA = 0.001;

    private Producto cafe;
    private Producto torta;

    @BeforeEach
    void setUp() {
        cafe = new Producto("B1", "Café", 3000, CategoriaProducto.BEBIDA);
        torta = new Producto("P1", "Torta", 7000, CategoriaProducto.POSTRE);
    }

    @Test
    void calcularPrecioAplicaElDescuentoSobreLaSumaDeLosProductos() {
        Combo combo = new Combo("C1", "Combo dulce", 0.1, List.of(cafe, torta));

        assertEquals(9000, combo.calcularPrecio(), DELTA);
    }

    @Test
    void calcularPrecioSinDescuentoEsLaSumaDeLosProductos() {
        Combo combo = new Combo("C1", "Combo", 0, List.of(cafe, torta));

        assertEquals(10000, combo.calcularPrecio(), DELTA);
    }

    @Test
    void calcularPrecioDeUnComboSinProductosEsCero() {
        Combo combo = new Combo("C2", "Vacío", 0.1, List.of());

        assertEquals(0, combo.calcularPrecio(), DELTA);
    }

    @Test
    void calcularPrecioReflejaLosCambiosDePrecioDeLosProductos() {
        Combo combo = new Combo("C1", "Combo", 0.1, List.of(cafe, torta));

        cafe.actualizarPrecio(4000);

        assertEquals(9900, combo.calcularPrecio(), DELTA);
    }
}
