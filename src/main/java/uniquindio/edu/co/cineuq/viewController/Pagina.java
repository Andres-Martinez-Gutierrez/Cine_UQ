package uniquindio.edu.co.cineuq.viewController;

import javafx.scene.Node;

/**
 * Contenido que una vista entrega a la ventana principal para mostrarlo.
 *
 * @param titulo      título del encabezado
 * @param descripcion texto bajo el título
 * @param seccion     opción del menú lateral que queda resaltada
 * @param contenido   nodo que ocupa el área de trabajo
 */
public record Pagina(String titulo, String descripcion, Seccion seccion, Node contenido) {
}
