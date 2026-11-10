package uniquindio.edu.co.cineuq.controller;

import uniquindio.edu.co.cineuq.exception.CinemaUQException;
import uniquindio.edu.co.cineuq.model.*;
import uniquindio.edu.co.cineuq.model.enumeraciones.EstadoFuncion;
import uniquindio.edu.co.cineuq.model.enumeraciones.EstadoPelicula;
import uniquindio.edu.co.cineuq.model.enumeraciones.EstadoSala;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * Casos de uso de la programación y consulta de funciones.
 */
public class FuncionController {
    private static final double VALOR_BASE = 15000;
    private static final Comparator<Funcion> POR_FECHA_Y_HORA =
            Comparator.comparing(Funcion::getFecha).thenComparing(Funcion::getHoraInicio);

    private final CinemaUQ cinema;
    private final Administrador administrador;

    public FuncionController(CinemaUQ cinema, Administrador administrador) {
        this.cinema = Objects.requireNonNull(cinema);
        this.administrador = Objects.requireNonNull(administrador);
    }

    public List<Funcion> listar() {
        return cinema.getListCinemaFunciones().stream().sorted(POR_FECHA_Y_HORA).toList();
    }

    public List<Funcion> funcionesDia(LocalDate fecha) {
        return cinema.funcionesDia(fecha).stream().sorted(POR_FECHA_Y_HORA).toList();
    }

    /** Funciones de la película a las que todavía se les pueden vender entradas. */
    public List<Funcion> vendiblesDe(Pelicula pelicula) {
        return cinema.getListCinemaFunciones().stream()
                .filter(funcion -> funcion.getPelicula().equals(pelicula) && esVendible(funcion))
                .sorted(POR_FECHA_Y_HORA)
                .toList();
    }

    /** Una función es vendible si está programada a futuro, con sala y película disponibles y cupo libre. */
    public boolean esVendible(Funcion funcion) {
        return funcion != null
                && funcion.getEstado() == EstadoFuncion.PROGRAMADA
                && funcion.getFecha() != null
                && funcion.getHoraInicio() != null
                && LocalDateTime.of(funcion.getFecha(), funcion.getHoraInicio()).isAfter(LocalDateTime.now())
                && funcion.getSala() != null
                && funcion.getSala().getEstado() == EstadoSala.DISPONIBLE
                && funcion.getPelicula() != null
                && funcion.getPelicula().getEstado() == EstadoPelicula.DISPONIBLE
                && funcion.asientosDisponibles() > 0;
    }

    public Funcion programar(Pelicula pelicula, Sala sala, LocalDate fecha, LocalTime horaInicio) throws CinemaUQException {
        if (pelicula == null || sala == null || fecha == null || horaInicio == null) {
            throw new IllegalArgumentException("Completa todos los datos de la función.");
        }
        if (!LocalDateTime.of(fecha, horaInicio).isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("La función debe programarse en una fecha y hora futuras.");
        }
        Funcion funcion = new Funcion(nuevoCodigo(), pelicula, sala, fecha, horaInicio, VALOR_BASE);
        administrador.programarFuncion(cinema, funcion);
        return funcion;
    }

    public void cancelar(Funcion funcion) {
        if (funcion == null) {
            throw new IllegalArgumentException("Selecciona una función primero.");
        }
        if (funcion.getEstado() != EstadoFuncion.PROGRAMADA) {
            throw new IllegalStateException("Solo se pueden cancelar funciones programadas.");
        }
        funcion.setEstado(EstadoFuncion.CANCELADA);
    }

    private static String nuevoCodigo() {
        return "F" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
    }
}
