package uniquindio.edu.co.cineuq.viewController.administracion;

import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import uniquindio.edu.co.cineuq.controller.FuncionController;
import uniquindio.edu.co.cineuq.controller.ReporteController;
import uniquindio.edu.co.cineuq.model.Pelicula;
import uniquindio.edu.co.cineuq.viewController.Navegador;
import uniquindio.edu.co.cineuq.viewController.componentes.Formatos;
import uniquindio.edu.co.cineuq.viewController.componentes.TablaFunciones;

import java.time.LocalDate;
import java.util.Objects;

import static uniquindio.edu.co.cineuq.viewController.componentes.Componentes.*;

/**
 * Pestaña con los indicadores del cine y las funciones del día.
 */
public class ResumenAdminViewController implements SeccionAdministracion {
    private final ReporteController reportes;
    private final FuncionController funciones;
    private final Navegador navegador;

    public ResumenAdminViewController(ReporteController reportes, FuncionController funciones, Navegador navegador) {
        this.reportes = Objects.requireNonNull(reportes);
        this.funciones = Objects.requireNonNull(funciones);
        this.navegador = Objects.requireNonNull(navegador);
    }

    @Override
    public String getTitulo() {
        return "Resumen";
    }

    @Override
    public Node crearContenido() {
        LocalDate hoy = LocalDate.now();
        Pelicula masVendida = reportes.peliculaMasVendida();
        HBox metricas = new HBox(12,
                metrica("Entradas vendidas", String.valueOf(reportes.entradasVendidas()), "Solo ventas activas"),
                metrica("Ingresos", Formatos.dinero(reportes.ingresos()), "Total de ventas activas"),
                metrica("Más vendida", masVendida == null ? "Sin ventas" : masVendida.getNombre(),
                        masVendida == null ? "Se actualiza con las ventas" : "Por entradas vendidas"),
                metrica("Ocupación promedio", Math.round(reportes.ocupacionPromedio(hoy)) + "%", "Funciones del día"));
        VBox tabla = panel();
        tabla.getChildren().addAll(etiqueta("Funciones del día", "section-title"),
                TablaFunciones.crear(funciones.funcionesDia(hoy), funciones::esVendible, navegador::mostrarMapaAsientos));
        return desplazable(new VBox(16, metricas, tabla));
    }
}
