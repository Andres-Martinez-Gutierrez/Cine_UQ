package uniquindio.edu.co.cineuq.viewController.administracion;

import javafx.scene.Node;

/**
 * Pestaña del módulo de administración. Para agregar una pestaña basta con implementar esta interfaz.
 */
public interface SeccionAdministracion {
    String getTitulo();

    Node crearContenido();

    /** Se invoca cada vez que la pestaña queda seleccionada, para refrescar datos que otras pestañas cambian. */
    default void alMostrar() {
    }
}
