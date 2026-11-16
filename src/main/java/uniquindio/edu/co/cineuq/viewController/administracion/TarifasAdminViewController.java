package uniquindio.edu.co.cineuq.viewController.administracion;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import uniquindio.edu.co.cineuq.controller.TarifaController;
import uniquindio.edu.co.cineuq.model.enumeraciones.TipoCliente;
import uniquindio.edu.co.cineuq.viewController.componentes.Formatos;

import java.util.Objects;

import static uniquindio.edu.co.cineuq.viewController.componentes.Componentes.*;

/**
 * Pestaña para ajustar el valor de la entrada por tipo de cliente.
 */
public class TarifasAdminViewController implements SeccionAdministracion {
    private final TarifaController tarifas;

    public TarifasAdminViewController(TarifaController tarifas) {
        this.tarifas = Objects.requireNonNull(tarifas);
    }

    @Override
    public String getTitulo() {
        return "Tarifas";
    }

    @Override
    public Node crearContenido() {
        ComboBox<TipoCliente> tipo = new ComboBox<>();
        tipo.getItems().setAll(TipoCliente.values());
        tipo.setValue(TipoCliente.GENERAL);
        tipo.setConverter(Formatos.convertidor(Formatos::tipoCliente));
        Label valorActual = etiqueta("", "muted-text");
        TextField nuevoValor = new TextField();
        nuevoValor.setPromptText("Nuevo valor en pesos");
        Label mensaje = etiqueta("Ajusta el valor de entrada para cada tipo de cliente.", "muted-text");

        Runnable refrescar = () -> {
            double valor = tarifas.valor(tipo.getValue());
            valorActual.setText("Tarifa actual: " + Formatos.dinero(valor));
            nuevoValor.setText(String.valueOf(valor));
        };
        tipo.setOnAction(event -> refrescar.run());

        Button actualizar = boton("Actualizar tarifa", "primary-button", () -> {
            try {
                tarifas.actualizar(tipo.getValue(), Double.parseDouble(nuevoValor.getText().trim()));
                refrescar.run();
                mensaje.setText("Tarifa actualizada correctamente.");
            } catch (NumberFormatException exception) {
                mensaje.setText("Ingresa un valor numérico válido.");
            } catch (IllegalArgumentException exception) {
                mensaje.setText(exception.getMessage());
            }
        });

        VBox formulario = panel();
        formulario.getChildren().addAll(etiqueta("Tarifas de entrada", "section-title"), tipo, valorActual,
                nuevoValor, actualizar, mensaje);
        refrescar.run();
        return new VBox(14, formulario);
    }
}
