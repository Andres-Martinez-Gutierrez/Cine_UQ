package uniquindio.edu.co.cineuq.controller;

import uniquindio.edu.co.cineuq.model.Administrador;
import uniquindio.edu.co.cineuq.model.CinemaUQ;
import uniquindio.edu.co.cineuq.model.enumeraciones.TipoCliente;

import java.util.Objects;

/**
 * Casos de uso de la gestión de tarifas por tipo de cliente.
 */
public class TarifaController {
    private final CinemaUQ cinema;
    private final Administrador administrador;

    public TarifaController(CinemaUQ cinema, Administrador administrador) {
        this.cinema = Objects.requireNonNull(cinema);
        this.administrador = Objects.requireNonNull(administrador);
    }

    public double valor(TipoCliente tipo) {
        return cinema.buscarTarifa(tipo).getValor();
    }

    public void actualizar(TipoCliente tipo, double valor) {
        if (tipo == null) {
            throw new IllegalArgumentException("Selecciona un tipo de cliente.");
        }
        if (!Double.isFinite(valor) || valor < 0) {
            throw new IllegalArgumentException("La tarifa debe ser un valor válido mayor o igual que cero.");
        }
        administrador.cambiarTarifa(cinema, tipo, valor);
    }
}
