package uniquindio.edu.co.cineuq.viewController.administracion;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import uniquindio.edu.co.cineuq.controller.SalaController;
import uniquindio.edu.co.cineuq.exception.CinemaUQException;
import uniquindio.edu.co.cineuq.model.Sala;
import uniquindio.edu.co.cineuq.model.enumeraciones.EstadoSala;
import uniquindio.edu.co.cineuq.model.enumeraciones.TipoSala;
import uniquindio.edu.co.cineuq.viewController.componentes.Formatos;

import java.util.Objects;

import static uniquindio.edu.co.cineuq.viewController.componentes.Componentes.*;

/**
 * Pestaña de registro, edición y habilitación de salas.
 */
public class SalasAdminViewController implements SeccionAdministracion {
    private final SalaController salas;

    public SalasAdminViewController(SalaController salas) {
        this.salas = Objects.requireNonNull(salas);
    }

    @Override
    public String getTitulo() {
        return "Salas";
    }

    @Override
    public Node crearContenido() {
        ListView<Sala> lista = new ListView<>();
        lista.getItems().setAll(salas.listar());
        lista.setCellFactory(view -> new ListCell<>() {
            @Override
            protected void updateItem(Sala sala, boolean empty) {
                super.updateItem(sala, empty);
                setText(empty || sala == null ? null : sala.getNombre() + " · " + Formatos.tipoSala(sala.getTipo())
                        + " · " + sala.getCapacidad() + " puestos"
                        + (sala.getEstado() == EstadoSala.DISPONIBLE ? "" : " · Fuera de servicio"));
            }
        });

        TextField codigo = new TextField();
        codigo.setPromptText("Código");
        TextField nombre = new TextField();
        nombre.setPromptText("Nombre de la sala");
        TextField capacidad = new TextField();
        capacidad.setPromptText("Capacidad");
        ComboBox<TipoSala> tipo = new ComboBox<>();
        tipo.getItems().setAll(TipoSala.values());
        tipo.setValue(TipoSala.TRADICIONAL);
        tipo.setConverter(Formatos.convertidor(Formatos::tipoSala));
        Label mensaje = etiqueta("Registra salas o selecciona una para editarla.", "muted-text");

        Button nueva = boton("Nueva sala", "secondary-button", () -> {
            lista.getSelectionModel().clearSelection();
            codigo.clear();
            codigo.setDisable(false);
            nombre.clear();
            capacidad.clear();
            tipo.setValue(TipoSala.TRADICIONAL);
        });
        Button guardar = boton("Guardar sala", "primary-button", () -> {
            try {
                int puestos = Integer.parseInt(capacidad.getText().trim());
                Sala seleccionada = lista.getSelectionModel().getSelectedItem();
                if (seleccionada == null) {
                    seleccionada = salas.registrar(codigo.getText(), nombre.getText(), puestos, tipo.getValue());
                } else {
                    salas.actualizar(seleccionada, nombre.getText(), puestos, tipo.getValue());
                }
                lista.getItems().setAll(salas.listar());
                lista.getSelectionModel().select(seleccionada);
                mensaje.setText("Sala guardada correctamente.");
            } catch (NumberFormatException exception) {
                mensaje.setText("La capacidad debe ser un número entero.");
            } catch (CinemaUQException | IllegalArgumentException exception) {
                mensaje.setText(exception.getMessage());
            }
        });
        Button alternarEstado = boton("Habilitar / deshabilitar", "secondary-button", () -> {
            try {
                salas.alternarEstado(lista.getSelectionModel().getSelectedItem());
                lista.refresh();
                mensaje.setText("Estado de sala actualizado.");
            } catch (IllegalArgumentException exception) {
                mensaje.setText(exception.getMessage());
            }
        });

        lista.getSelectionModel().selectedItemProperty().addListener((observable, anterior, seleccionada) -> {
            if (seleccionada == null) return;
            codigo.setText(seleccionada.getCodigo());
            codigo.setDisable(true);
            nombre.setText(seleccionada.getNombre());
            capacidad.setText(String.valueOf(seleccionada.getCapacidad()));
            tipo.setValue(seleccionada.getTipo());
        });

        VBox formulario = panel();
        formulario.setPrefWidth(390);
        formulario.getChildren().addAll(etiqueta("Datos de la sala", "section-title"), codigo, nombre, capacidad, tipo,
                new HBox(8, nueva, guardar), alternarEstado, mensaje);
        lista.setPrefWidth(390);
        HBox layout = new HBox(14, lista, formulario);
        HBox.setHgrow(lista, Priority.ALWAYS);
        VBox contenido = new VBox(14, etiqueta("Salas", "section-title"), layout);
        VBox.setVgrow(layout, Priority.ALWAYS);
        return contenido;
    }
}
