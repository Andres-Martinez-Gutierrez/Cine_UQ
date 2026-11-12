package uniquindio.edu.co.cineuq.viewController;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import uniquindio.edu.co.cineuq.controller.CompraEnCurso;
import uniquindio.edu.co.cineuq.controller.VentaController;
import uniquindio.edu.co.cineuq.model.AsientoFuncion;
import uniquindio.edu.co.cineuq.model.Funcion;
import uniquindio.edu.co.cineuq.viewController.componentes.Formatos;

import java.util.List;
import java.util.Objects;

import static uniquindio.edu.co.cineuq.viewController.componentes.Componentes.*;

/**
 * Pantalla de selección de asientos de una función.
 */
public class MapaAsientosViewController {
    private static final int ASIENTOS_POR_FILA = 10;
    private static final String SIN_SELECCION = "Ningún asiento seleccionado";

    private final VentaController ventas;
    private final Navegador navegador;

    public MapaAsientosViewController(VentaController ventas, Navegador navegador) {
        this.ventas = Objects.requireNonNull(ventas);
        this.navegador = Objects.requireNonNull(navegador);
    }

    /**
     * @throws IllegalStateException si la función ya no admite ventas
     */
    public Pagina crearPagina(Funcion funcion) {
        CompraEnCurso compra = ventas.iniciarCompra(funcion);

        Label seleccion = etiqueta(SIN_SELECCION, "movie-meta");
        Label total = etiqueta(Formatos.dinero(0), "total-value");
        Runnable actualizarResumen = () -> {
            seleccion.setText(compra.tieneAsientos() ? compra.descripcionAsientos() : SIN_SELECCION);
            total.setText(Formatos.dinero(ventas.totalEntradas(compra)));
        };

        VBox asientos = panel();
        asientos.getChildren().addAll(etiqueta("PANTALLA", "screen-label"),
                crearGrilla(compra, actualizarResumen), crearLeyenda());

        Button continuar = boton("Continuar al cobro", "primary-button", () -> {
            if (!compra.tieneAsientos()) {
                seleccion.setText("Selecciona al menos un asiento para continuar.");
                return;
            }
            navegador.mostrarCobro(compra);
        });
        continuar.setMaxWidth(Double.MAX_VALUE);

        VBox resumen = panel();
        resumen.getStyleClass().add("purchase-summary");
        resumen.setPrefWidth(260);
        resumen.getChildren().addAll(etiqueta("Tu selección", "section-title"), seleccion,
                etiqueta("Entrada por asiento", "muted-text"),
                etiqueta(Formatos.dinero(ventas.precioEntrada(compra)), "table-cell"),
                etiqueta("Total", "section-title"), total, continuar);

        HBox layout = new HBox(20, asientos, resumen);
        HBox.setHgrow(asientos, Priority.ALWAYS);
        String descripcion = funcion.getPelicula().getNombre() + " · " + funcion.getSala().getNombre()
                + " · " + funcion.getHoraInicio().format(Formatos.HORA);
        return new Pagina("Mapa de asientos", descripcion, Seccion.FUNCIONES, desplazable(layout));
    }

    private Node crearGrilla(CompraEnCurso compra, Runnable alCambiar) {
        GridPane grilla = new GridPane();
        grilla.setHgap(7);
        grilla.setVgap(7);
        List<AsientoFuncion> asientos = compra.getFuncion().getListFuncionAsientofun();
        for (int i = 0; i < asientos.size(); i++) {
            AsientoFuncion asiento = asientos.get(i);
            int fila = i / ASIENTOS_POR_FILA;
            int columna = i % ASIENTOS_POR_FILA;
            Button boton = new Button(asiento.getAsiento().getCodigo());
            boton.getStyleClass().add(asiento.estaDisponible() ? "seat-available" : "seat-occupied");
            boton.setDisable(!asiento.estaDisponible());
            boton.setOnAction(event -> {
                boolean seleccionado = compra.alternarAsiento(asiento);
                boton.getStyleClass().removeAll("seat-available", "seat-selected");
                boton.getStyleClass().add(seleccionado ? "seat-selected" : "seat-available");
                alCambiar.run();
            });
            if (columna == 0) grilla.add(etiqueta(String.valueOf((char) ('A' + fila)), "seat-row-label"), 0, fila);
            grilla.add(boton, columna + 1, fila);
        }
        return grilla;
    }

    private Node crearLeyenda() {
        HBox leyenda = new HBox(14, elementoLeyenda("Disponible", "seat-available"),
                elementoLeyenda("Seleccionado", "seat-selected"), elementoLeyenda("Ocupado", "seat-occupied"));
        leyenda.getStyleClass().add("legend-row");
        return leyenda;
    }

    private HBox elementoLeyenda(String texto, String estilo) {
        return new HBox(6, etiqueta(" ", estilo), etiqueta(texto, "muted-text"));
    }
}
