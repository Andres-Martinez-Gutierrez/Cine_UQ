package uniquindio.edu.co.cineuq.model;

import org.junit.jupiter.api.Test;
import uniquindio.edu.co.cineuq.model.enumeraciones.CategoriaProducto;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductoTest {
    private static final double DELTA = 0.001;

    @Test
    void actualizarPrecioCambiaElPrecioDelProducto() {
        Producto producto = new Producto("B1", "Gaseosa", 3000, CategoriaProducto.BEBIDA, null);

        producto.actualizarPrecio(3500);

        assertEquals(3500, producto.getPrecio(), DELTA);
    }
}
