package uniquindio.edu.co.cineuq.viewController.administracion;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import uniquindio.edu.co.cineuq.controller.FuncionController;
import uniquindio.edu.co.cineuq.controller.PeliculaController;
import uniquindio.edu.co.cineuq.controller.SalaController;
import uniquindio.edu.co.cineuq.exception.CinemaUQException;
import uniquindio.edu.co.cineuq.model.Funcion;
import uniquindio.edu.co.cineuq.model.Pelicula;
import uniquindio.edu.co.cineuq.model.Sala;
import uniquindio.edu.co.cineuq.viewController.componentes.Formatos;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;

import static uniquindio.edu.co.cineuq.viewController.componentes.Componentes.*;

/**
 * Pestaña de programación y cancelación de funciones.
 */
public class FuncionesAdminViewController implements SeccionAdministracion {
    private final FuncionController funciones;
    private final PeliculaController peliculas;
    private final SalaController salas;

    private ComboBox<Pelicula> selectorPelicula;
    private ComboBox<Sala> selectorSala;

    public FuncionesAdminViewController(FuncionController funciones, PeliculaController peliculas, SalaController salas) {
        this.funciones = Objects.requireNonNull(funciones);
        this.peliculas = Objects.requireNonNull(peliculas);
        this.salas = Objects.requireNonNull(salas);
    }

    @Override
    public String getTitulo() {
        return "Funciones";
    }

    /** Las películas y salas pueden cambiar en otras pestañas; se recargan al volver a esta. */
    @Override
    public void alMostrar() {
        recargar(selectorPelicula, peliculas.disponibles());
        recargar(selectorSala, salas.disponibles());
    }

    @Override
    public Node crearContenido() {
        selectorPelicula = new ComboBox<>();
        selectorPelicula.setConverter(Formatos.convertidor(Pelicula::getNombre));
        selectorSala = new ComboBox<>();
        selectorSala.setConverter(Formatos.convertidor(Sala::getNombre));
        alMostrar();

        LocalDateTime sugerida = LocalDateTime.now().plusHours(1).withSecond(0).withNano(0);
        DatePicker fecha = new DatePicker(sugerida.toLocalDate());
        TextField hora = new TextField(sugerida.toLocalTime().format(Formatos.HORA));
        hora.setPromptText("HH:mm");
        Label mensaje = etiqueta("Selecciona película, sala, fecha y hora para programar una función.", "muted-text");

        ListView<Funcion> lista = new ListView<>();
        lista.getItems().setAll(funciones.listar());
        lista.setCellFactory(view -> new ListCell<>() {
            @Override
            protected void updateItem(Funcion funcion, boolean empty) {
                super.updateItem(funcion, empty);
                setText(empty || funcion == null ? null : funcion.getFecha() + " · "
                        + funcion.getHoraInicio().format(Formatos.HORA) + " · " + funcion.getPelicula().getNombre()
                        + " · " + funcion.getSala().getNombre() + " · " + Formatos.estadoFuncion(funcion.getEstado()));
            }
        });

        Button programar = boton("Programar función", "primary-button", () -> {
            try {
                if (hora.getText().isBlank()) {
                    throw new IllegalArgumentException("Completa todos los datos de la función.");
                }
                LocalTime inicio = LocalTime.parse(hora.getText().trim(), Formatos.HORA);
                funciones.programar(selectorPelicula.getValue(), selectorSala.getValue(), fecha.getValue(), inicio);
                lista.getItems().setAll(funciones.listar());
                mensaje.setText("Función programada correctamente.");
            } catch (DateTimeParseException exception) {
                mensaje.setText("La hora debe tener formato HH:mm, por ejemplo 19:30.");
            } catch (CinemaUQException | IllegalArgumentException exception) {
                mensaje.setText(exception.getMessage());
            }
        });
        Button cancelar = boton("Cancelar función seleccionada", "secondary-button", () -> {
            try {
                funciones.cancelar(lista.getSelectionModel().getSelectedItem());
                lista.refresh();
                mensaje.setText("Función cancelada.");
            } catch (IllegalArgumentException | IllegalStateException exception) {
                mensaje.setText(exception.getMessage());
            }
        });

        VBox formulario = panel();
        formulario.setPrefWidth(350);
        formulario.getChildren().addAll(etiqueta("Nueva función", "section-title"), selectorPelicula, selectorSala,
                fecha, hora, programar, cancelar, mensaje);
        VBox listado = panel();
        listado.getChildren().addAll(etiqueta("Funciones programadas", "section-title"), lista);
        VBox.setVgrow(lista, Priority.ALWAYS);
        HBox layout = new HBox(14, formulario, listado);
        HBox.setHgrow(listado, Priority.ALWAYS);
        VBox contenido = new VBox(14, etiqueta("Programación de funciones", "section-title"), layout);
        VBox.setVgrow(layout, Priority.ALWAYS);
        return contenido;
    }

    private static <T> void recargar(ComboBox<T> selector, List<T> opciones) {
        if (selector == null) return;
        T actual = selector.getValue();
        selector.getItems().setAll(opciones);
        if (actual != null && opciones.contains(actual)) {
            selector.setValue(actual);
        } else {
            selector.setValue(opciones.isEmpty() ? null : opciones.getFirst());
        }
    }
}
