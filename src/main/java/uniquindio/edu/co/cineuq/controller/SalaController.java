package uniquindio.edu.co.cineuq.controller;

import uniquindio.edu.co.cineuq.exception.SalaDuplicadaException;
import uniquindio.edu.co.cineuq.model.Administrador;
import uniquindio.edu.co.cineuq.model.CinemaUQ;
import uniquindio.edu.co.cineuq.model.Sala;
import uniquindio.edu.co.cineuq.model.enumeraciones.EstadoSala;
import uniquindio.edu.co.cineuq.model.enumeraciones.TipoSala;

import java.util.List;
import java.util.Objects;

/**
 * Casos de uso de la gestión de salas.
 */
public class SalaController {
    private final CinemaUQ cinema;
    private final Administrador administrador;

    public SalaController(CinemaUQ cinema, Administrador administrador) {
        this.cinema = Objects.requireNonNull(cinema);
        this.administrador = Objects.requireNonNull(administrador);
    }

    public List<Sala> listar() {
        return List.copyOf(cinema.getListCinemaSalas());
    }

    public List<Sala> disponibles() {
        return cinema.getListCinemaSalas().stream()
                .filter(sala -> sala.getEstado() == EstadoSala.DISPONIBLE)
                .toList();
    }

    public Sala registrar(String codigo, String nombre, int capacidad, TipoSala tipo) throws SalaDuplicadaException {
        validar(codigo, nombre, capacidad, tipo);
        Sala sala = new Sala(codigo.trim(), nombre.trim(), capacidad, tipo);
        administrador.gestionarSalas(cinema, sala);
        return sala;
    }

    public void actualizar(Sala sala, String nombre, int capacidad, TipoSala tipo) {
        requerirSeleccion(sala);
        validar(sala.getCodigo(), nombre, capacidad, tipo);
        if (capacidad != sala.getCapacidad() && tieneFunciones(sala)) {
            throw new IllegalArgumentException("No se puede cambiar la capacidad de una sala que ya tiene funciones programadas.");
        }
        sala.setNombre(nombre.trim());
        sala.setCapacidad(capacidad);
        sala.setTipo(tipo);
    }

    public void alternarEstado(Sala sala) {
        requerirSeleccion(sala);
        sala.setEstado(sala.getEstado() == EstadoSala.DISPONIBLE ? EstadoSala.FUERA_DE_SERVICIO : EstadoSala.DISPONIBLE);
    }

    private boolean tieneFunciones(Sala sala) {
        return cinema.getListCinemaFunciones().stream().anyMatch(funcion -> funcion.getSala() == sala);
    }

    private static void requerirSeleccion(Sala sala) {
        if (sala == null) throw new IllegalArgumentException("Selecciona una sala primero.");
    }

    private static void validar(String codigo, String nombre, int capacidad, TipoSala tipo) {
        if (codigo == null || codigo.isBlank() || nombre == null || nombre.isBlank() || tipo == null) {
            throw new IllegalArgumentException("Completa todos los campos de la sala.");
        }
        if (capacidad <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor que cero.");
        }
    }
}
