package uniquindio.edu.co.cineuq.viewController;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
import java.util.stream.Collectors;

import static uniquindio.edu.co.cineuq.viewController.componentes.Componentes.*;

/**
 * Pantalla de cobro: datos del cliente, productos y confirmación de la venta.
 */
public class CobroViewController {
    private static final String SIN_PRODUCTOS = "Sin productos";

    private final VentaController ventas;
    private final Navegador navegador;

    public CobroViewController(VentaController ventas, Navegador navegador) {
        this.ventas = Objects.requireNonNull(ventas);
        this.navegador = Objects.requireNonNull(navegador);
    }

    public Pagina crearPagina(CompraEnCurso compra) {
        Funcion funcion = compra.getFuncion();
        Label total = etiqueta("", "total-value");
        Label resumenProductos = etiqueta(SIN_PRODUCTOS, "muted-text");
        Label listaProductos = etiqueta(SIN_PRODUCTOS, "muted-text");
        Runnable actualizarTotales = () -> {
            total.setText(Formatos.dinero(ventas.total(compra)));
            String productos = describirProductos(compra);
            resumenProductos.setText(productos);
            listaProductos.setText(productos);
        };

        ComboBox<TipoCliente> tipo = new ComboBox<>();
        tipo.getItems().setAll(TipoCliente.values());
        tipo.setValue(compra.getTipoCliente());
        tipo.setConverter(Formatos.convertidor(Formatos::tipoCliente));
        tipo.getStyleClass().add("customer-type-select");
        tipo.setOnAction(event -> {
            compra.setTipoCliente(tipo.getValue());
            actualizarTotales.run();
        });
        TextField nombre = new TextField();
        nombre.setPromptText("Nombre del cliente");
        TextField identificacion = new TextField();
        identificacion.setPromptText("Identificación (opcional)");
        VBox cliente = panel();
        cliente.getChildren().addAll(etiqueta("Tipo de cliente", "section-title"), tipo, nombre, identificacion);

        VBox productos = panel();
        productos.getChildren().add(etiqueta("Productos", "section-title"));
        for (Producto producto : ventas.productos()) {
            productos.getChildren().add(crearFilaProducto(producto, compra, actualizarTotales));
        }
        productos.getChildren().add(listaProductos);

        Button confirmar = boton("Confirmar venta", "primary-button",
                () -> confirmar(compra, nombre.getText(), identificacion.getText()));
        confirmar.setMaxWidth(Double.MAX_VALUE);

        VBox resumen = panel();
        resumen.getStyleClass().add("purchase-summary");
        resumen.setPrefWidth(270);
        resumen.getChildren().addAll(etiqueta("Cobro", "section-title"),
                etiqueta(compra.getAsientos().size() + " entrada(s) · " + compra.descripcionAsientos(), "table-cell"),
                etiqueta("Sala " + Formatos.tipoSala(funcion.getSala().getTipo()), "muted-text"),
                etiqueta("Productos", "muted-text"), resumenProductos,
                etiqueta("Total", "section-title"), total, confirmar,
                botonVolver("Cambiar asientos", () -> navegador.mostrarMapaAsientos(funcion)));

        VBox formulario = new VBox(16, cliente, productos);
        HBox layout = new HBox(18, formulario, resumen);
        HBox.setHgrow(formulario, Priority.ALWAYS);
        actualizarTotales.run();

        String descripcion = funcion.getPelicula().getNombre() + " · " + funcion.getSala().getNombre()
                + " · " + compra.descripcionAsientos();
        return new Pagina("Venta y cobro", descripcion, Seccion.VENTAS, desplazable(layout));
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
        Button agregar = boton("+", "quantity-button", () -> {
            compra.agregarProducto(producto);
            refrescar.run();
        });
        HBox fila = new HBox(8, etiqueta(producto.getNombre() + "\n" + Formatos.dinero(producto.getPrecio()), "product-name"),
                espaciador(), quitar, cantidad, agregar);
        fila.getStyleClass().add("product-row");
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

    private static String describirProductos(CompraEnCurso compra) {
        if (compra.getProductos().isEmpty()) return SIN_PRODUCTOS;
        return compra.getProductos().entrySet().stream()
                .map(entrada -> entrada.getValue() + " × " + entrada.getKey().getNombre())
                .collect(Collectors.joining(", "));
    }
}
