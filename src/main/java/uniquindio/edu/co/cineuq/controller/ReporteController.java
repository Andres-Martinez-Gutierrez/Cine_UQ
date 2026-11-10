package uniquindio.edu.co.cineuq.controller;

import uniquindio.edu.co.cineuq.model.CinemaUQ;
import uniquindio.edu.co.cineuq.model.Funcion;
import uniquindio.edu.co.cineuq.model.Pelicula;
import uniquindio.edu.co.cineuq.model.Venta;
import uniquindio.edu.co.cineuq.model.enumeraciones.EstadoVenta;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Indicadores del resumen administrativo.
 */
public class ReporteController {
    private final CinemaUQ cinema;

    public ReporteController(CinemaUQ cinema) {
        this.cinema = Objects.requireNonNull(cinema);
    }

    public int entradasVendidas() {
        return cinema.totalEntradasVendidas();
    }

    public double ingresos() {
        return cinema.getListCinemaVentas().stream()
                .filter(venta -> venta.getEstado() == EstadoVenta.ACTIVA)
                .mapToDouble(Venta::getTotal)
                .sum();
    }

    /** Película con más entradas vendidas, o {@code null} si aún no hay ventas. */
    public Pelicula peliculaMasVendida() {
        return cinema.peliculaMasVendida();
    }

    public double ocupacionPromedio(LocalDate fecha) {
        return cinema.funcionesDia(fecha).stream()
                .mapToDouble(Funcion::porcentajeOcupacion)
                .average()
                .orElse(0);
    }
}
