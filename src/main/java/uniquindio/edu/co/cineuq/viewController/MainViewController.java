package uniquindio.edu.co.cineuq.viewController;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import uniquindio.edu.co.cineuq.controller.CompraEnCurso;
import uniquindio.edu.co.cineuq.controller.ModelFactory;
import uniquindio.edu.co.cineuq.model.Funcion;
import uniquindio.edu.co.cineuq.model.Pelicula;
import uniquindio.edu.co.cineuq.model.Venta;
import uniquindio.edu.co.cineuq.viewController.administracion.*;

import java.util.List;
import java.util.Map;

import static uniquindio.edu.co.cineuq.viewController.componentes.Componentes.mostrarMensaje;

/**
 * Ventana principal: menú lateral, encabezado y área de trabajo.
 * Solo navega entre pantallas; cada pantalla tiene su propio view controller.
 */
public class MainViewController implements Navegador {
    @FXML
    private VBox pageContent;
    @FXML
    private Label pageTitle;
    @FXML
    private Label pageDescription;
    @FXML
    private Button carteleraButton;
    @FXML
    private Button funcionesButton;
    @FXML
    private Button ventasButton;
    @FXML
    private Button administracionButton;

    private Map<Seccion, Button> botonesMenu;
    private CarteleraViewController cartelera;
    private FuncionesViewController funciones;
    private MapaAsientosViewController mapaAsientos;
    private CobroViewController cobro;
    private BoletaViewController boleta;
    private AdministracionViewController administracion;

    @FXML
    private void initialize() {
        botonesMenu = Map.of(Seccion.CARTELERA, carteleraButton, Seccion.FUNCIONES, funcionesButton,
                Seccion.VENTAS, ventasButton, Seccion.ADMINISTRACION, administracionButton);

        ModelFactory fabrica = ModelFactory.getInstance();
        cartelera = new CarteleraViewController(fabrica.getPeliculaController(), this);
        funciones = new FuncionesViewController(fabrica.getFuncionController(), this);
        mapaAsientos = new MapaAsientosViewController(fabrica.getVentaController(), this);
        cobro = new CobroViewController(fabrica.getVentaController(), this);
        boleta = new BoletaViewController(this);
        administracion = new AdministracionViewController(List.of(
                new ResumenAdminViewController(fabrica.getReporteController(), fabrica.getFuncionController(), this),
                new PeliculasAdminViewController(fabrica.getPeliculaController()),
                new SalasAdminViewController(fabrica.getSalaController()),
                new FuncionesAdminViewController(fabrica.getFuncionController(), fabrica.getPeliculaController(),
                        fabrica.getSalaController()),
                new TarifasAdminViewController(fabrica.getTarifaController())));

        mostrarCartelera();
    }

    @FXML
    private void showCartelera() {
        mostrarCartelera();
    }

    @FXML
    private void showFunciones() {
        mostrar(funciones.crearPaginaFunciones());
    }

    @FXML
    private void showVentas() {
        mostrar(funciones.crearPaginaVentas());
    }

    @FXML
    private void showAdministracion() {
        mostrar(administracion.crearPagina());
    }

    @Override
    public void mostrarCartelera() {
        mostrar(cartelera.crearPagina());
    }

    @Override
    public void mostrarFuncionesDe(Pelicula pelicula) {
        mostrar(funciones.crearPaginaPelicula(pelicula));
    }

    @Override
    public void mostrarMapaAsientos(Funcion funcion) {
        try {
            mostrar(mapaAsientos.crearPagina(funcion));
        } catch (IllegalStateException exception) {
            mostrarMensaje(Alert.AlertType.WARNING, "Función no disponible", exception.getMessage());
        }
    }

    @Override
    public void mostrarCobro(CompraEnCurso compra) {
        mostrar(cobro.crearPagina(compra));
    }

    @Override
    public void mostrarBoleta(Venta venta, CompraEnCurso compra) {
        mostrar(boleta.crearPagina(venta, compra));
    }

    private void mostrar(Pagina pagina) {
        pageTitle.setText(pagina.titulo());
        pageDescription.setText(pagina.descripcion());
        botonesMenu.forEach((seccion, boton) -> {
            boton.getStyleClass().remove("nav-button-active");
            if (seccion == pagina.seccion()) boton.getStyleClass().add("nav-button-active");
        });
        pageContent.getChildren().setAll(pagina.contenido());
    }
}
