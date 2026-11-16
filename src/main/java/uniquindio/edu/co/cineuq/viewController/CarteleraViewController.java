package uniquindio.edu.co.cineuq.viewController;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
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
    private static final String[] COLORES_POSTER = {
            "#54204c, #e74774", "#74361e, #f28a2f", "#fff06a, #ff835f",
            "#10251f, #bfc877", "#17465c, #57b1c7", "#39234e, #7884cb"
    };

    private final PeliculaController peliculas;
    private final Navegador navegador;

    public CarteleraViewController(PeliculaController peliculas, Navegador navegador) {
        this.peliculas = Objects.requireNonNull(peliculas);
        this.navegador = Objects.requireNonNull(navegador);
    }

    public Pagina crearPagina() {
        TilePane grilla = new TilePane();
        grilla.setHgap(18);
        grilla.setVgap(18);
        grilla.setPrefColumns(4);
        grilla.setPrefTileWidth(250);
        grilla.setTileAlignment(Pos.TOP_LEFT);

        HBox filtros = new HBox(7);
        Button todas = crearFiltro("Todas (" + peliculas.disponibles().size() + ")", null, filtros, grilla);
        filtros.getChildren().add(todas);
        for (Genero genero : Genero.values()) {
            int cantidad = peliculas.disponiblesPorGenero(genero).size();
            if (cantidad > 0) {
                filtros.getChildren().add(crearFiltro(
                        Formatos.genero(genero) + " (" + cantidad + ")", genero, filtros, grilla));
            }
        }
        marcarActivo(filtros, todas);
        refrescarGrilla(grilla, null);

        HBox tituloSeccion = new HBox(12,
                etiqueta("En cartelera", "section-title"),
                etiqueta(peliculas.disponibles().size() + " películas", "muted-text"));
        tituloSeccion.setAlignment(Pos.CENTER_LEFT);
        VBox contenido = new VBox(18, crearDestacada(), tituloSeccion, filtros, grilla);
        return new Pagina("Cartelera", "Películas disponibles y próximas funciones", Seccion.CARTELERA, desplazable(contenido));
    }

    private Button crearFiltro(String texto, Genero genero, HBox filtros, TilePane grilla) {
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

    private void refrescarGrilla(TilePane grilla, Genero genero) {
        grilla.getChildren().clear();
        List<Pelicula> lista = peliculas.disponiblesPorGenero(genero);
        if (lista.isEmpty()) {
            grilla.getChildren().add(etiqueta("No hay películas en esta categoría.", "empty-state"));
            return;
        }
        for (int i = 0; i < lista.size(); i++) {
            grilla.getChildren().add(crearTarjeta(lista.get(i), i));
        }
    }

    private HBox crearDestacada() {
        List<Pelicula> lista = peliculas.disponibles();
        if (lista.isEmpty()) {
            HBox vacio = new HBox(etiqueta("No hay películas disponibles por el momento.", "empty-state"));
            vacio.getStyleClass().add("feature-banner");
            return vacio;
        }

        Pelicula[] destacadas = lista.toArray(Pelicula[]::new);
        int[] indice = {0};
        for (int i = 0; i < destacadas.length; i++) {
            if (destacadas[i].getGenero() == Genero.DRAMA) {
                indice[0] = i;
                break;
            }
        }

        Label genero = etiqueta("", "feature-genre");
        Label titulo = etiqueta("", "feature-title");
        Label descripcion = etiqueta("", "feature-description");
        Label duracion = etiqueta("", "feature-pill");
        Label clasificacion = etiqueta("", "feature-pill");
        Label arteTitulo = etiqueta("", "featured-art-title");
        VBox arte = new VBox(arteTitulo);
        arte.setAlignment(Pos.CENTER);
        arte.getStyleClass().add("featured-art");

        Button verFunciones = boton("Ver funciones", "primary-button",
                () -> navegador.mostrarFuncionesDe(destacadas[indice[0]]));
        Button siguiente = boton("Siguiente >", "feature-next", () -> {
            indice[0] = (indice[0] + 1) % destacadas.length;
            actualizarDestacada(destacadas[indice[0]], genero, titulo, descripcion,
                    duracion, clasificacion, arteTitulo);
        });
        HBox acciones = new HBox(10, verFunciones, siguiente);
        VBox textos = new VBox(12);
        textos.getStyleClass().add("feature-copy");
        Region espacio = new Region();
        VBox.setVgrow(espacio, Priority.ALWAYS);
        textos.getChildren().addAll(genero, titulo, descripcion,
                new HBox(8, duracion, clasificacion, etiqueta("Disponible", "feature-pill")),
                espacio, acciones);
        HBox banner = new HBox(24, textos, arte);
        banner.setAlignment(Pos.CENTER_LEFT);
        banner.getStyleClass().add("feature-banner");
        HBox.setHgrow(textos, Priority.ALWAYS);
        actualizarDestacada(destacadas[indice[0]], genero, titulo, descripcion,
                duracion, clasificacion, arteTitulo);
        return banner;
    }

    private void actualizarDestacada(Pelicula pelicula, Label genero, Label titulo, Label descripcion,
                                     Label duracion, Label clasificacion, Label arteTitulo) {
        genero.setText(Formatos.genero(pelicula.getGenero()).toUpperCase(Locale.ROOT) + "  ·  PELÍCULA DESTACADA");
        titulo.setText(pelicula.getNombre());
        descripcion.setText("Una historia que llega al corazón");
        duracion.setText(pelicula.getDuracionMinutos() + " min");
        clasificacion.setText(Formatos.clasificacion(pelicula.getClasificacion()));
        arteTitulo.setText(pelicula.getNombre());
    }

    private VBox crearTarjeta(Pelicula pelicula, int indice) {
        VBox poster = new VBox();
        poster.setMinHeight(250);
        poster.setMaxHeight(250);
        poster.setStyle("-fx-background-color: linear-gradient(to bottom right, "
                + COLORES_POSTER[indice % COLORES_POSTER.length] + ");");
        Region espacio = new Region();
        VBox.setVgrow(espacio, Priority.ALWAYS);
        poster.getChildren().addAll(
                etiqueta("CINEUQ  ·  EN CARTELERA", "poster-kicker"),
                espacio,
                etiqueta(pelicula.getNombre(), "poster-title"),
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
        tarjeta.setPrefWidth(250);
        return tarjeta;
    }
}
