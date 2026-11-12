package uniquindio.edu.co.cineuq.viewController;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import uniquindio.edu.co.cineuq.controller.CompraEnCurso;
import uniquindio.edu.co.cineuq.controller.VentaController;
import uniquindio.edu.co.cineuq.exception.CinemaUQException;
import uniquindio.edu.co.cineuq.model.Funcion;
import uniquindio.edu.co.cineuq.model.Producto;
import uniquindio.edu.co.cineuq.model.Venta;
import uniquindio.edu.co.cineuq.model.enumeraciones.TipoCliente;
import uniquindio.edu.co.cineuq.viewController.componentes.Formatos;

import java.util.Objects;

import static uniquindio.edu.co.cineuq.viewController.componentes.Componentes.*;

/**
 * Pantalla de venta y cobro. Sirve para vender confitería sola o entradas de una función con sus productos.
 */
public class CobroViewController {
    private final VentaController ventas;
    private final Navegador navegador;

    public CobroViewController(VentaController ventas, Navegador navegador) {
        this.ventas = Objects.requireNonNull(ventas);
        this.navegador = Objects.requireNonNull(navegador);
    }

    /** Venta de mostrador: productos de confitería, con opción de pasar a vender entradas. */
    public Pagina crearPaginaConfiteria() {
        return crearPagina(ventas.iniciarCompraConfiteria());
    }

    public Pagina crearPagina(CompraEnCurso compra) {
        Funcion funcion = compra.getFuncion();

        Label conceptoEntradas = etiqueta("", "summary-label");
        Label valorEntradas = etiqueta("", "summary-value");
        Label valorRecargo = etiqueta("", "summary-value");
        Label valorProductos = etiqueta("", "summary-value");
        Label total = etiqueta("", "total-value");
        Runnable actualizarCobro = () -> {
            conceptoEntradas.setText("Entradas (" + compra.getAsientos().size() + " × "
                    + Formatos.dinero(ventas.tarifa(compra)) + ")");
            valorEntradas.setText(Formatos.dinero(ventas.totalTarifas(compra)));
            valorRecargo.setText(Formatos.dinero(ventas.totalRecargo(compra)));
            valorProductos.setText(Formatos.dinero(ventas.totalProductos(compra)));
            total.setText(Formatos.dinero(ventas.total(compra)));
        };

        TextField nombre = new TextField();
        nombre.setPromptText("Nombre del cliente");
        TextField identificacion = new TextField();
        identificacion.setPromptText("Identificación (opcional)");

        VBox formulario = new VBox(16);
        if (!compra.incluyeEntradas()) {
            formulario.getChildren().add(crearPanelSinEntradas());
        }
        formulario.getChildren().addAll(crearPanelCliente(compra, actualizarCobro, nombre, identificacion),
                crearPanelProductos(compra, actualizarCobro));

        Button confirmar = boton("Confirmar venta", "primary-button",
                () -> confirmar(compra, nombre.getText(), identificacion.getText()));
        confirmar.setMaxWidth(Double.MAX_VALUE);

        HBox lineaTotal = linea(etiqueta("Total", "section-title"), total);
        lineaTotal.getStyleClass().add("summary-total");

        VBox cobro = panel();
        cobro.getStyleClass().add("checkout-summary");
        cobro.setPrefWidth(360);
        cobro.getChildren().add(etiqueta("Cobro", "section-title"));
        if (compra.incluyeEntradas()) {
            cobro.getChildren().addAll(linea(conceptoEntradas, valorEntradas),
                    linea(etiqueta("Recargo sala " + Formatos.tipoSala(funcion.getSala().getTipo()), "summary-label"),
                            valorRecargo));
        }
        cobro.getChildren().addAll(linea(etiqueta("Productos", "summary-label"), valorProductos), lineaTotal, confirmar);
        if (compra.incluyeEntradas()) {
            cobro.getChildren().add(botonVolver("Cambiar asientos", () -> navegador.mostrarMapaAsientos(funcion)));
        }

        HBox layout = new HBox(24, formulario, cobro);
        layout.setFillHeight(false);
        layout.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(formulario, Priority.ALWAYS);
        actualizarCobro.run();

        String descripcion = compra.incluyeEntradas()
                ? funcion.getPelicula().getNombre() + " · " + funcion.getSala().getNombre()
                  + " · Asientos " + compra.descripcionAsientos()
                : "Confitería · agrega productos o vende entradas para una función";
        return new Pagina("Venta y cobro", descripcion, Seccion.VENTAS, desplazable(layout));
    }

    private Node crearPanelSinEntradas() {
        VBox panel = panel();
        Button elegirFuncion = boton("Elegir función y asientos", "secondary-button", navegador::mostrarAgendaVentas);
        HBox fila = new HBox(12, new VBox(4, etiqueta("Entradas", "section-title"),
                etiqueta("Esta venta no incluye entradas.", "muted-text")), espaciador(), elegirFuncion);
        fila.setAlignment(Pos.CENTER_LEFT);
        panel.getChildren().add(fila);
        return panel;
    }

    private Node crearPanelCliente(CompraEnCurso compra, Runnable alCambiar, TextField nombre, TextField identificacion) {
        VBox panel = panel();
        if (compra.incluyeEntradas()) {
            panel.getChildren().addAll(etiqueta("Tipo de cliente", "section-title"), crearSelectorTipoCliente(compra, alCambiar));
        } else {
            panel.getChildren().add(etiqueta("Cliente", "section-title"));
        }
        panel.getChildren().addAll(nombre, identificacion);
        return panel;
    }

    private Node crearPanelProductos(CompraEnCurso compra, Runnable alCambiar) {
        VBox panel = panel();
        panel.getChildren().add(etiqueta("Productos", "section-title"));
        for (Producto producto : ventas.productos()) {
            panel.getChildren().add(crearFilaProducto(producto, compra, alCambiar));
        }
        return panel;
    }

    /** Opciones tipo píldora; siempre queda exactamente una seleccionada. */
    private Node crearSelectorTipoCliente(CompraEnCurso compra, Runnable alCambiar) {
        ToggleGroup grupo = new ToggleGroup();
        FlowPane opciones = new FlowPane(8, 8);
        for (TipoCliente tipo : TipoCliente.values()) {
            ToggleButton opcion = new ToggleButton(Formatos.tipoCliente(tipo));
            opcion.getStyleClass().add("client-chip");
            opcion.setUserData(tipo);
            opcion.setToggleGroup(grupo);
            opcion.setSelected(tipo == compra.getTipoCliente());
            opciones.getChildren().add(opcion);
        }
        grupo.selectedToggleProperty().addListener((observable, anterior, actual) -> {
            if (actual == null) {
                grupo.selectToggle(anterior);
                return;
            }
            compra.setTipoCliente((TipoCliente) actual.getUserData());
            alCambiar.run();
        });
        return opciones;
    }

    private HBox crearFilaProducto(Producto producto, CompraEnCurso compra, Runnable alCambiar) {
        Label cantidad = etiqueta(String.valueOf(compra.cantidad(producto)), "product-quantity");
        Runnable refrescar = () -> {
            cantidad.setText(String.valueOf(compra.cantidad(producto)));
            alCambiar.run();
        };
        Button quitar = boton("−", "quantity-button", () -> {
            compra.quitarProducto(producto);
            refrescar.run();
        });
        quitar.setAccessibleText("Quitar " + producto.getNombre());
        Button agregar = boton("+", "quantity-button", () -> {
            compra.agregarProducto(producto);
            refrescar.run();
        });
        agregar.setAccessibleText("Agregar " + producto.getNombre());

        VBox informacion = new VBox(2, etiqueta(producto.getNombre(), "product-name"),
                etiqueta(Formatos.dinero(producto.getPrecio()) + " · " + Formatos.categoria(producto), "muted-text"));
        HBox fila = new HBox(12, informacion, espaciador(), quitar, cantidad, agregar);
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.getStyleClass().add("product-row");
        return fila;
    }

    private HBox linea(Label concepto, Label valor) {
        HBox fila = new HBox(10, concepto, espaciador(), valor);
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.getStyleClass().add("summary-line");
        return fila;
    }

    private void confirmar(CompraEnCurso compra, String nombre, String identificacion) {
        try {
            Venta venta = ventas.vender(compra, nombre, identificacion);
            navegador.mostrarBoleta(venta, compra);
        } catch (CinemaUQException | IllegalArgumentException | IllegalStateException exception) {
            mostrarMensaje(Alert.AlertType.ERROR, "No se pudo completar la venta", exception.getMessage());
        }
    }
}
