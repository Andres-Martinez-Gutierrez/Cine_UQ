package uniquindio.edu.co.cineuq.viewController.administracion;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import uniquindio.edu.co.cineuq.controller.PeliculaController;
import uniquindio.edu.co.cineuq.exception.CinemaUQException;
import uniquindio.edu.co.cineuq.model.Pelicula;
import uniquindio.edu.co.cineuq.model.enumeraciones.Clasificacion;
import uniquindio.edu.co.cineuq.model.enumeraciones.EstadoPelicula;
import uniquindio.edu.co.cineuq.model.enumeraciones.Genero;
import uniquindio.edu.co.cineuq.viewController.componentes.Formatos;

import java.util.Objects;

import static uniquindio.edu.co.cineuq.viewController.componentes.Componentes.*;

/**
 * Pestaña de registro, edición y habilitación de películas.
 */
public class PeliculasAdminViewController implements SeccionAdministracion {
    private final PeliculaController peliculas;

    public PeliculasAdminViewController(PeliculaController peliculas) {
        this.peliculas = Objects.requireNonNull(peliculas);
    }

    @Override
    public String getTitulo() {
        return "Películas";
    }

    @Override
    public Node crearContenido() {
        ListView<Pelicula> lista = new ListView<>();
        lista.getItems().setAll(peliculas.listar());
        lista.setCellFactory(view -> new ListCell<>() {
            @Override
            protected void updateItem(Pelicula pelicula, boolean empty) {
                super.updateItem(pelicula, empty);
                setText(empty || pelicula == null ? null : pelicula.getNombre() + " · " + Formatos.genero(pelicula.getGenero())
                        + " · " + Formatos.clasificacion(pelicula.getClasificacion())
                        + (pelicula.getEstado() == EstadoPelicula.DISPONIBLE ? "" : " · No disponible"));
            }
        });

        TextField codigo = new TextField();
        codigo.setPromptText("Código");
        TextField nombre = new TextField();
        nombre.setPromptText("Nombre de la película");
        TextField duracion = new TextField();
        duracion.setPromptText("Duración en minutos");
        ComboBox<Genero> genero = new ComboBox<>();
        genero.getItems().setAll(Genero.values());
        genero.setValue(Genero.ACCION);
        genero.setConverter(Formatos.convertidor(Formatos::genero));
        ComboBox<Clasificacion> clasificacion = new ComboBox<>();
        clasificacion.getItems().setAll(Clasificacion.values());
        clasificacion.setValue(Clasificacion.TODOS);
        clasificacion.setConverter(Formatos.convertidor(Formatos::clasificacion));
        Label mensaje = etiqueta("Registra películas o selecciona una para editarla.", "muted-text");

        Button nueva = boton("Nueva película", "secondary-button", () -> {
            lista.getSelectionModel().clearSelection();
            codigo.clear();
            codigo.setDisable(false);
            nombre.clear();
            duracion.clear();
            genero.setValue(Genero.ACCION);
            clasificacion.setValue(Clasificacion.TODOS);
            mensaje.setText("Completa los datos de la película.");
        });
        Button guardar = boton("Guardar película", "primary-button", () -> {
            try {
                int minutos = Integer.parseInt(duracion.getText().trim());
                Pelicula seleccionada = lista.getSelectionModel().getSelectedItem();
                if (seleccionada == null) {
                    seleccionada = peliculas.registrar(codigo.getText(), nombre.getText(), genero.getValue(),
                            clasificacion.getValue(), minutos);
                } else {
                    peliculas.actualizar(seleccionada, nombre.getText(), genero.getValue(), clasificacion.getValue(), minutos);
                }
                lista.getItems().setAll(peliculas.listar());
                lista.getSelectionModel().select(seleccionada);
                mensaje.setText("Película guardada correctamente.");
            } catch (NumberFormatException exception) {
                mensaje.setText("La duración debe ser un número entero de minutos.");
            } catch (CinemaUQException | IllegalArgumentException exception) {
                mensaje.setText(exception.getMessage());
            }
        });
        Button alternarEstado = boton("Habilitar / deshabilitar", "secondary-button", () -> {
            Pelicula seleccionada = lista.getSelectionModel().getSelectedItem();
            try {
                peliculas.alternarEstado(seleccionada);
                lista.refresh();
                mensaje.setText("Estado actualizado: " + (seleccionada.getEstado() == EstadoPelicula.DISPONIBLE
                        ? "disponible" : "no disponible") + ".");
            } catch (IllegalArgumentException exception) {
                mensaje.setText(exception.getMessage());
            }
        });

        lista.getSelectionModel().selectedItemProperty().addListener((observable, anterior, seleccionada) -> {
            if (seleccionada == null) return;
            codigo.setText(seleccionada.getCodigo());
            codigo.setDisable(true);
            nombre.setText(seleccionada.getNombre());
            duracion.setText(String.valueOf(seleccionada.getDuracionMinutos()));
            genero.setValue(seleccionada.getGenero());
            clasificacion.setValue(seleccionada.getClasificacion() == null ? Clasificacion.TODOS : seleccionada.getClasificacion());
        });

        VBox formulario = panel();
        formulario.setPrefWidth(390);
        formulario.getChildren().addAll(etiqueta("Datos de la película", "section-title"), codigo, nombre, duracion,
                genero, clasificacion, new HBox(8, nueva, guardar), alternarEstado, mensaje);
        lista.setPrefWidth(390);
        HBox layout = new HBox(14, lista, formulario);
        HBox.setHgrow(lista, Priority.ALWAYS);
        VBox contenido = new VBox(14, etiqueta("Películas", "section-title"), layout);
        VBox.setVgrow(layout, Priority.ALWAYS);
        return contenido;
    }
}
