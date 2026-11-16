package uniquindio.edu.co.cineuq.viewController.componentes;

import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import uniquindio.edu.co.cineuq.model.Funcion;
import uniquindio.edu.co.cineuq.model.enumeraciones.EstadoFuncion;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static uniquindio.edu.co.cineuq.viewController.componentes.Componentes.boton;
import static uniquindio.edu.co.cineuq.viewController.componentes.Componentes.etiqueta;

/**
 * Tabla de funciones reutilizada en la agenda, en la cartelera y en el resumen administrativo.
 */
public final class TablaFunciones {
    private static final double[] ANCHOS = {8, 21, 16, 14, 11, 13, 17};
    private static final String[] ENCABEZADOS = {"Hora", "Película", "Sala", "Disponibles", "Ocupación", "Estado", ""};

    private TablaFunciones() {
    }

    /**
     * @param funciones   funciones a mostrar, en el orden deseado
     * @param esVendible  indica si una función admite la venta de entradas
     * @param alElegir    acción al pulsar "Elegir asientos" en una función vendible
     */
    public static GridPane crear(List<Funcion> funciones, Predicate<Funcion> esVendible, Consumer<Funcion> alElegir) {
        GridPane tabla = new GridPane();
        tabla.getStyleClass().add("data-table");
        tabla.setHgap(12);
        tabla.setMaxWidth(Double.MAX_VALUE);
        for (double ancho : ANCHOS) {
            ColumnConstraints columna = new ColumnConstraints();
            columna.setPercentWidth(ancho);
            columna.setHgrow(Priority.ALWAYS);
            tabla.getColumnConstraints().add(columna);
        }
        for (int columna = 0; columna < ENCABEZADOS.length; columna++) {
            tabla.add(etiqueta(ENCABEZADOS[columna], "table-header"), columna, 0);
        }
        for (int i = 0; i < funciones.size(); i++) {
            Funcion funcion = funciones.get(i);
            int fila = i + 1;
            tabla.add(etiqueta(funcion.getHoraInicio().format(Formatos.HORA), "table-cell"), 0, fila);
            tabla.add(etiqueta(funcion.getPelicula().getNombre(), "table-cell"), 1, fila);
            tabla.add(etiqueta(funcion.getSala().getNombre(), "table-cell"), 2, fila);
            tabla.add(etiqueta(funcion.asientosDisponibles() + " de " + funcion.getSala().getCapacidad(), "table-cell"), 3, fila);
            tabla.add(etiqueta(Math.round(funcion.porcentajeOcupacion()) + "%", "table-cell"), 4, fila);
            tabla.add(etiqueta(Formatos.estadoFuncion(funcion.getEstado()),
                    funcion.getEstado() == EstadoFuncion.PROGRAMADA ? "status-scheduled" : "status-inactive"), 5, fila);
            if (esVendible.test(funcion)) {
                Button elegir = boton("Elegir asientos", "text-button", () -> alElegir.accept(funcion));
                tabla.add(elegir, 6, fila);
            } else {
                tabla.add(etiqueta("No disponible", "muted-text"), 6, fila);
            }
        }
        return tabla;
    }
}
