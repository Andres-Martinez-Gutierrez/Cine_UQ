package uniquindio.edu.co.cineuq.viewController.componentes;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Fábrica de los elementos visuales que comparten todas las pantallas.
 */
public final class Componentes {

    private Componentes() {
    }

    public static Label etiqueta(String texto, String estilo) {
        Label etiqueta = new Label(texto);
        etiqueta.getStyleClass().add(estilo);
        etiqueta.setWrapText(true);
        return etiqueta;
    }

    public static Button boton(String texto, String estilo, Runnable accion) {
        Button boton = new Button(texto);
        boton.getStyleClass().add(estilo);
        boton.setOnAction(event -> accion.run());
        return boton;
    }

    public static HBox botonVolver(String texto, Runnable accion) {
        return new HBox(boton(texto, "text-button", accion));
    }

    public static VBox panel() {
        VBox panel = new VBox(12);
        panel.getStyleClass().add("module-card");
        return panel;
    }

    public static ScrollPane desplazable(Node contenido) {
        ScrollPane scroll = new ScrollPane(contenido);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("content-scroll");
        VBox.setVgrow(scroll, Priority.ALWAYS);
        return scroll;
    }

    public static Region espaciador() {
        Region espaciador = new Region();
        HBox.setHgrow(espaciador, Priority.ALWAYS);
        return espaciador;
    }

    public static VBox metrica(String titulo, String valor, String pie) {
        VBox tarjeta = panel();
        tarjeta.getStyleClass().add("metric-card");
        tarjeta.getChildren().addAll(etiqueta(titulo, "muted-text"), etiqueta(valor, "metric-value"), etiqueta(pie, "movie-meta"));
        HBox.setHgrow(tarjeta, Priority.ALWAYS);
        tarjeta.setPrefWidth(180);
        tarjeta.setMaxWidth(Double.MAX_VALUE);
        return tarjeta;
    }

    public static void mostrarMensaje(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
