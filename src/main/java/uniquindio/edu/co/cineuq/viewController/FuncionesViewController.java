package uniquindio.edu.co.cineuq.viewController;

import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import uniquindio.edu.co.cineuq.controller.FuncionController;
import uniquindio.edu.co.cineuq.model.Funcion;
import uniquindio.edu.co.cineuq.model.Pelicula;
import uniquindio.edu.co.cineuq.viewController.componentes.TablaFunciones;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static uniquindio.edu.co.cineuq.viewController.componentes.Componentes.*;

/**
 * Pantallas de consulta de funciones: agenda por fecha y funciones de una película.
 */
public class FuncionesViewController {
    private final FuncionController funciones;
    private final Navegador navegador;

    public FuncionesViewController(FuncionController funciones, Navegador navegador) {
        this.funciones = Objects.requireNonNull(funciones);
        this.navegador = Objects.requireNonNull(navegador);
    }

    public Pagina crearPaginaFunciones() {
        return new Pagina("Funciones", "Horarios programados para hoy", Seccion.FUNCIONES, crearAgenda());
    }

    public Pagina crearPaginaVentas() {
        return new Pagina("Venta y cobro", "Selecciona una función para iniciar una compra", Seccion.VENTAS, crearAgenda());
    }

    public Pagina crearPaginaPelicula(Pelicula pelicula) {
        VBox panel = panel();
        panel.getChildren().addAll(
                etiqueta(pelicula.getNombre(), "section-title"),
                crearTabla(funciones.vendiblesDe(pelicula)),
                botonVolver("Volver a cartelera", navegador::mostrarCartelera));
        return new Pagina("Funciones · " + pelicula.getNombre(), "Elige el horario y los asientos",
                Seccion.CARTELERA, desplazable(panel));
    }

    private Node crearAgenda() {
        DatePicker fecha = new DatePicker(LocalDate.now());
        VBox resultado = new VBox();
        Runnable refrescar = () -> {
            if (fecha.getValue() == null) return;
            List<Funcion> delDia = funciones.funcionesDia(fecha.getValue());
            resultado.getChildren().setAll(delDia.isEmpty()
                    ? etiqueta("No hay funciones para esta fecha.", "empty-state")
                    : crearTabla(delDia));
        };
        fecha.setOnAction(event -> refrescar.run());
        refrescar.run();

        HBox encabezado = new HBox(10, etiqueta("Funciones", "section-title"), espaciador(),
                etiqueta("Fecha", "muted-text"), fecha);
        VBox panel = panel();
        panel.getChildren().addAll(encabezado, resultado);
        return desplazable(panel);
    }

    private GridPane crearTabla(List<Funcion> lista) {
        return TablaFunciones.crear(lista, funciones::esVendible, navegador::mostrarMapaAsientos);
    }
}
