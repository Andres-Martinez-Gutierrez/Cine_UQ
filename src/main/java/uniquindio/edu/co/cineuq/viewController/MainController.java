package uniquindio.edu.co.cineuq.viewController;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/** Gestiona la navegación y el contenido de la vista principal. */
public class MainController {
    @FXML
    private VBox pageContent;
    @FXML
    private Label pageTitle;
    @FXML
    private Label pageDescription;
    @FXML
    private Button carteleraButton;
    @FXML
    private Button funcionesButton;
    @FXML
    private Button ventasButton;
    @FXML
    private Button administracionButton;

    @FXML
    private void initialize() {
        showCartelera();
    }

    @FXML
    private void showCartelera() {
        showPage("Cartelera", "Consulta las películas disponibles y sus próximas funciones.",
                carteleraButton, "cartelera");
    }

    @FXML
    private void showFunciones() {
        showPage("Funciones", "Programa y consulta las funciones del cine.",
                funcionesButton, "funciones");
    }

    @FXML
    private void showVentas() {
        showPage("Ventas", "Selecciona una función, sus asientos y los productos de la compra.",
                ventasButton, "ventas");
    }

    @FXML
    private void showAdministracion() {
        showPage("Administración", "Gestiona películas, salas, tarifas y consulta los resultados del cine.",
                administracionButton, "administracion");
    }

    /** Actualiza el encabezado, la opción seleccionada y el contenido del módulo activo. */
    private void showPage(String title, String description, Button selectedButton, String module) {
        pageTitle.setText(title);
        pageDescription.setText(description);

        carteleraButton.getStyleClass().remove("nav-button-active");
        funcionesButton.getStyleClass().remove("nav-button-active");
        ventasButton.getStyleClass().remove("nav-button-active");
        administracionButton.getStyleClass().remove("nav-button-active");
        selectedButton.getStyleClass().add("nav-button-active");

        pageContent.getChildren().setAll(createModuleCard(title, module));
    }

    private VBox createModuleCard(String title, String module) {
        Label eyebrow = new Label("MÓDULO");
        eyebrow.getStyleClass().add("card-eyebrow");

        Label cardTitle = new Label(title);
        cardTitle.getStyleClass().add("card-title");

        Label message = new Label("La estructura de navegación está lista. "
                + "El siguiente paso es conectar este módulo con el modelo de CineUQ.");
        message.getStyleClass().add("card-message");
        message.setWrapText(true);

        Label nextStep = new Label(getNextStep(module));
        nextStep.getStyleClass().add("next-step");
        nextStep.setWrapText(true);

        VBox card = new VBox(14, eyebrow, cardTitle, message, nextStep);
        card.getStyleClass().add("module-card");
        card.setMaxWidth(620);
        return card;
    }

    /** Devuelve el texto de orientación asociado al identificador de cada módulo de navegación. */
    private String getNextStep(String module) {
        return switch (module) {
            case "cartelera" -> "Próximo paso: mostrar películas y funciones registradas.";
            case "funciones" -> "Próximo paso: listar funciones por fecha y sala.";
            case "ventas" -> "Próximo paso: conectar la selección de asientos con el flujo de venta.";
            case "administracion" -> "Próximo paso: habilitar la gestión de películas, salas y tarifas.";
            default -> throw new IllegalArgumentException("Módulo desconocido: " + module);
        };
    }
}
