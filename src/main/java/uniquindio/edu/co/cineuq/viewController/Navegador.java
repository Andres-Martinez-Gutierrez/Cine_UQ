package uniquindio.edu.co.cineuq.viewController;

import uniquindio.edu.co.cineuq.controller.CompraEnCurso;
import uniquindio.edu.co.cineuq.model.Funcion;
import uniquindio.edu.co.cineuq.model.Pelicula;
import uniquindio.edu.co.cineuq.model.Venta;

/**
 * Navegación entre pantallas. Las vistas dependen de esta abstracción y no de la ventana principal.
 */
public interface Navegador {
    void mostrarCartelera();

    /** Venta y cobro de confitería, sin entradas. */
    void mostrarVentas();

    /** Agenda de funciones para elegir a cuál se le venden entradas. */
    void mostrarAgendaVentas();

    void mostrarFuncionesDe(Pelicula pelicula);

    void mostrarMapaAsientos(Funcion funcion);

    void mostrarCobro(CompraEnCurso compra);

    void mostrarBoleta(Venta venta, CompraEnCurso compra);
}
