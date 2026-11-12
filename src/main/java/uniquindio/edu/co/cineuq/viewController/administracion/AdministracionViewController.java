package uniquindio.edu.co.cineuq.viewController.administracion;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import uniquindio.edu.co.cineuq.viewController.Pagina;
import uniquindio.edu.co.cineuq.viewController.Seccion;
import uniquindio.edu.co.cineuq.viewController.componentes.Formatos;

import java.time.LocalDate;
import java.util.List;

/**
 * Módulo de administración: agrupa en pestañas las secciones que recibe.
 */
public class AdministracionViewController {
    private final List<SeccionAdministracion> secciones;

    public AdministracionViewController(List<SeccionAdministracion> secciones) {
        this.secciones = List.copyOf(secciones);
    }

    public Pagina crearPagina() {
        TabPane pestanas = new TabPane();
        pestanas.getStyleClass().add("admin-tabs");
        for (SeccionAdministracion seccion : secciones) {
            Tab pestana = new Tab(seccion.getTitulo(), seccion.crearContenido());
            pestana.setClosable(false);
            pestana.setOnSelectionChanged(event -> {
                if (pestana.isSelected()) seccion.alMostrar();
            });
            pestanas.getTabs().add(pestana);
        }
        return new Pagina("Administración", "Resumen del cine · " + LocalDate.now().format(Formatos.FECHA),
                Seccion.ADMINISTRACION, pestanas);
    }
}
