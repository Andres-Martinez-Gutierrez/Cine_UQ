package uniquindio.edu.co.cineuq.controller;

import uniquindio.edu.co.cineuq.model.Administrador;
import uniquindio.edu.co.cineuq.model.CinemaUQ;
import uniquindio.edu.co.cineuq.model.Operador;

/**
 * Punto único de composición de la aplicación: crea el modelo y los controladores
 * una sola vez y los entrega a la capa de vista.
 */
public final class ModelFactory {
    private static ModelFactory instancia;

    private final PeliculaController peliculaController;
    private final SalaController salaController;
    private final FuncionController funcionController;
    private final TarifaController tarifaController;
    private final VentaController ventaController;
    private final ReporteController reporteController;

    private ModelFactory() {
        CinemaUQ cinema = DatosDemo.crearCine();
        Administrador administrador = new Administrador("admin", "", "Administrador CineUQ");
        Operador operador = new Operador("operador", "", "Operador CineUQ", cinema);

        peliculaController = new PeliculaController(cinema, administrador);
        salaController = new SalaController(cinema, administrador);
        funcionController = new FuncionController(cinema, administrador);
        tarifaController = new TarifaController(cinema, administrador);
        ventaController = new VentaController(cinema, operador, DatosDemo.crearProductos(), funcionController);
        reporteController = new ReporteController(cinema);
    }

    public static synchronized ModelFactory getInstance() {
        if (instancia == null) {
            instancia = new ModelFactory();
        }
        return instancia;
    }

    public PeliculaController getPeliculaController() {
        return peliculaController;
    }

    public SalaController getSalaController() {
        return salaController;
    }

    public FuncionController getFuncionController() {
        return funcionController;
    }

    public TarifaController getTarifaController() {
        return tarifaController;
    }

    public VentaController getVentaController() {
        return ventaController;
    }

    public ReporteController getReporteController() {
        return reporteController;
    }
}
