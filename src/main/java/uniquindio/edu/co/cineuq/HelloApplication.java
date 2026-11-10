package uniquindio.edu.co.cineuq;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Punto de entrada y configuración inicial de la aplicación CineUQ.
 */
public class HelloApplication extends Application {
    /**
     * Carga la vista principal y aplica sus estilos antes de mostrar la ventana.
     *
     * @param stage ventana principal proporcionada por JavaFX
     * @throws IOException si no se puede cargar la vista o la hoja de estilos
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 800);
        URL stylesheet = HelloApplication.class.getResource("styles.css");
        if (stylesheet == null) {
            throw new IOException("No se encontró la hoja de estilos de CineUQ");
        }
        scene.getStylesheets().add(stylesheet.toExternalForm());
        stage.setTitle("CineUQ");
        stage.setMinWidth(1024);
        stage.setMinHeight(680);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
