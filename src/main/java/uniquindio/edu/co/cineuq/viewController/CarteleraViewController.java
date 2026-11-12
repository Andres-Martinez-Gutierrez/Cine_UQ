package uniquindio.edu.co.cineuq.viewController;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import uniquindio.edu.co.cineuq.controller.PeliculaController;
import uniquindio.edu.co.cineuq.model.Pelicula;
import uniquindio.edu.co.cineuq.model.enumeraciones.Genero;
import uniquindio.edu.co.cineuq.viewController.componentes.Formatos;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static uniquindio.edu.co.cineuq.viewController.componentes.Componentes.*;

/**
 * Pantalla de cartelera: catálogo de películas disponibles con filtro por género.
 */
public class CarteleraViewController {
    private static final String[] COLORES_POSTER = {"#d9cdbd", "#c6d3db", "#ead5c3", "#c9c8d0", "#c9d9c8", "#d2d7e8"};

    private final PeliculaController peliculas;
    private final Navegador navegador;

    public CarteleraViewController(PeliculaController peliculas, Navegador navegador) {
        this.peliculas = Objects.requireNonNull(peliculas);
        this.navegador = Objects.requireNonNull(navegador);
    }

    public Pagina crearPagina() {
        GridPane grilla = new GridPane();
        grilla.setHgap(12);
        grilla.setVgap(12);

        HBox filtros = new HBox(7);
        Button todas = crearFiltro("Todas", null, filtros, grilla);
        filtros.getChildren().add(todas);
        for (Genero genero : Genero.values()) {
            filtros.getChildren().add(crearFiltro(Formatos.genero(genero), genero, filtros, grilla));
        }
        marcarActivo(filtros, todas);
        refrescarGrilla(grilla, null);

        Label conteo = etiqueta(peliculas.disponibles().size() + " películas en exhibición", "muted-text");
        VBox contenido = new VBox(16, conteo, filtros, grilla);
        return new Pagina("Cartelera", "Películas disponibles y próximas funciones", Seccion.CARTELERA, desplazable(contenido));
    }

    private Button crearFiltro(String texto, Genero genero, HBox filtros, GridPane grilla) {
        Button filtro = new Button(texto);
        filtro.setOnAction(event -> {
            marcarActivo(filtros, filtro);
            refrescarGrilla(grilla, genero);
        });
        return filtro;
    }

    private void marcarActivo(HBox filtros, Button activo) {
        for (Node filtro : filtros.getChildren()) {
            filtro.getStyleClass().removeAll("filter-chip", "filter-chip-active");
            filtro.getStyleClass().add(filtro == activo ? "filter-chip-active" : "filter-chip");
        }
    }

    private void refrescarGrilla(GridPane grilla, Genero genero) {
        grilla.getChildren().clear();
        List<Pelicula> lista = peliculas.disponiblesPorGenero(genero);
        if (lista.isEmpty()) {
            grilla.add(etiqueta("No hay películas en esta categoría.", "empty-state"), 0, 0);
            return;
        }
        for (int i = 0; i < lista.size(); i++) {
            grilla.add(crearTarjeta(lista.get(i), i), i % 3, i / 3);
        }
    }

    private VBox crearTarjeta(Pelicula pelicula, int indice) {
        VBox poster = new VBox();
        poster.setMinHeight(130);
        poster.setStyle("-fx-background-color: " + COLORES_POSTER[indice % COLORES_POSTER.length] + ";");
        Region espacio = new Region();
        VBox.setVgrow(espacio, Priority.ALWAYS);
        poster.getChildren().addAll(
                etiqueta("CINEUQ  ·  EN CARTELERA", "poster-kicker"),
                espacio,
                etiqueta(Formatos.genero(pelicula.getGenero()).toUpperCase(Locale.ROOT), "poster-genre"),
                etiqueta(Formatos.clasificacion(pelicula.getClasificacion()).toUpperCase(Locale.ROOT), "rating-pill"));

        VBox detalles = new VBox(6);
        detalles.getStyleClass().add("movie-details");
        detalles.getChildren().addAll(
                etiqueta(pelicula.getNombre(), "movie-title"),
                etiqueta(Formatos.genero(pelicula.getGenero()) + " · " + pelicula.getDuracionMinutos() + " min · Disponible", "movie-meta"),
                boton("Ver funciones", "primary-button", () -> navegador.mostrarFuncionesDe(pelicula)));

        VBox tarjeta = new VBox(0, poster, detalles);
        tarjeta.getStyleClass().add("movie-card");
        tarjeta.setPrefWidth(270);
        return tarjeta;
    }
}
