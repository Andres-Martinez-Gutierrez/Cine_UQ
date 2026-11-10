package uniquindio.edu.co.cineuq.viewController;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import uniquindio.edu.co.cineuq.controller.CompraEnCurso;
import uniquindio.edu.co.cineuq.model.Cobro;
import uniquindio.edu.co.cineuq.model.Funcion;
import uniquindio.edu.co.cineuq.model.Producto;
import uniquindio.edu.co.cineuq.model.Venta;
import uniquindio.edu.co.cineuq.viewController.componentes.Formatos;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static uniquindio.edu.co.cineuq.viewController.componentes.Componentes.*;

/**
 * Pantalla con la boleta de una venta confirmada.
 */
public class BoletaViewController {
    private final Navegador navegador;

    public BoletaViewController(Navegador navegador) {
        this.navegador = Objects.requireNonNull(navegador);
    }

    /**
     * @param venta  venta registrada
     * @param compra selección con la que se hizo la venta (la venta solo conserva el último producto)
     */
    public Pagina crearPagina(Venta venta, CompraEnCurso compra) {
        VBox boleta = panel();
        boleta.getStyleClass().add("receipt-card");
        boleta.getChildren().addAll(etiqueta("CineUQ", "receipt-brand"), etiqueta("Boleta de venta", "muted-text"),
                linea("Boleta", venta.getCodigo()),
                linea("Fecha", venta.getFechaHora().format(Formatos.FECHA_HORA)),
                linea("Cliente", venta.getCliente().getNombre()));

        Funcion funcion = venta.getFuncion();
        Cobro cobroEntradas = null;
        if (funcion != null) {
            cobroEntradas = venta.getListVentaCobros().getFirst();
            String asientos = venta.getListVentaAsientofun().stream()
                    .map(asiento -> asiento.getAsiento().getCodigo())
                    .collect(Collectors.joining(", "));
            boleta.getChildren().addAll(
                    linea("Función", funcion.getPelicula().getNombre()),
                    linea("Sala / hora", funcion.getSala().getNombre() + " · " + funcion.getHoraInicio().format(Formatos.HORA)),
                    linea("Asientos", asientos),
                    linea("Entradas", Formatos.dinero(cobroEntradas.getValorEntrada())),
                    linea("Recargo de sala", Formatos.dinero(cobroEntradas.getRecargoSala())));
        }
        for (Map.Entry<Producto, Integer> entrada : compra.getProductos().entrySet()) {
            Producto producto = entrada.getKey();
            boleta.getChildren().add(linea("Producto", entrada.getValue() + " × " + producto.getNombre()
                    + " · " + Formatos.dinero(producto.getPrecio() * entrada.getValue())));
        }
        if (cobroEntradas != null && cobroEntradas.getDescuentoCliente() > 0) {
            boleta.getChildren().add(linea("Descuento", "− " + Formatos.dinero(cobroEntradas.getDescuentoCliente())));
        }
        boleta.getChildren().addAll(linea("Total pagado", Formatos.dinero(venta.getTotal())),
                new HBox(16, botonVolver("Nueva venta", navegador::mostrarVentas),
                        botonVolver("Volver a cartelera", navegador::mostrarCartelera)));
        return new Pagina("Boleta", "Venta registrada correctamente", Seccion.VENTAS, desplazable(boleta));
    }

    private HBox linea(String concepto, String valor) {
        HBox fila = new HBox(10, etiqueta(concepto, "muted-text"), espaciador(), etiqueta(valor, "table-cell"));
        fila.getStyleClass().add("receipt-line");
        return fila;
    }
}
