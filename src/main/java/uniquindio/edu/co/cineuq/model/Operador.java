package uniquindio.edu.co.cineuq.model;

import uniquindio.edu.co.cineuq.exception.AsientoOcupadoException;
import uniquindio.edu.co.cineuq.exception.CinemaUQException;
import uniquindio.edu.co.cineuq.exception.SinAsientosDisponiblesException;
import uniquindio.edu.co.cineuq.model.enumeraciones.EstadoFuncion;
import uniquindio.edu.co.cineuq.model.enumeraciones.EstadoVenta;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Operador extends Usuario {
    private CinemaUQ cinema;

    /**
     * Constructor vacío de la clase Operador.
     * Los atributos se asignan luego con los setters.
     */
    public Operador() {
    }

    /**
     * Constructor de la clase Operador.
     *
     * @param usuario    nombre de usuario con el que inicia sesión
     * @param contrasena contraseña del operador
     * @param nombre     nombre completo del operador
     * @param cinema     cine en el que el operador realiza las ventas
     */
    public Operador(String usuario, String contrasena, String nombre, CinemaUQ cinema) {
        super(usuario, contrasena, nombre);
        this.cinema = cinema;
    }

    /**
     * Vende un único asiento, manteniendo compatibilidad con la operación anterior.
     *
     * @param funcion función para la que se vende el asiento
     * @param cliente cliente que realiza la compra
     * @param codigoAsiento código del asiento seleccionado
     * @return venta registrada
     * @throws CinemaUQException si el asiento no está disponible
     * @throws IllegalArgumentException si algún dato de entrada no es válido
     * @throws IllegalStateException si la función no permite ventas
     */
    public Venta vender(Funcion funcion, Cliente cliente, String codigoAsiento) throws CinemaUQException {
        return vender(funcion, cliente, Collections.singletonList(codigoAsiento));
    }

    /**
     * Vende varios asientos como una sola operación. Cada asiento suma una entrada y el recargo de sala;
     * los productos se agregan después con {@link Venta#agregarProducto(Producto)}.
     *
     * @param funcion función para la que se venden los asientos
     * @param cliente cliente que realiza la compra
     * @param codigosAsientos códigos de asientos distintos que se van a vender
     * @return venta registrada con todos los asientos seleccionados
     * @throws CinemaUQException si algún asiento seleccionado ya no está disponible
     * @throws IllegalArgumentException si los datos son inválidos o no pertenecen a este cine
     * @throws IllegalStateException si la función, la sala, la película o sus valores no permiten la venta
     */
    public Venta vender(Funcion funcion, Cliente cliente, List<String> codigosAsientos) throws CinemaUQException {
        if (cinema == null) {
            throw new IllegalStateException("El operador no tiene un cine asignado");
        }
        if (funcion == null || cliente == null || codigosAsientos == null) {
            throw new IllegalArgumentException("La función, el cliente y la selección de asientos son obligatorios");
        }
        if (codigosAsientos.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un asiento");
        }
        if (!cinema.getFunciones().contains(funcion)) {
            throw new IllegalArgumentException("La función no está registrada en este cine");
        }
        if (funcion.getSala() == null || !cinema.getSalas().contains(funcion.getSala())) {
            throw new IllegalArgumentException("La sala de la función no está registrada en este cine");
        }
        if (funcion.getPelicula() == null || !cinema.getPeliculas().contains(funcion.getPelicula())) {
            throw new IllegalArgumentException("La película de la función no está registrada en este cine");
        }
        if (funcion.getEstado() != EstadoFuncion.PROGRAMADA) {
            throw new IllegalStateException("La función " + funcion.getCodigo() + " no está programada");
        }
        if (funcion.getSala().getEstado() != EstadoSala.DISPONIBLE) {
            throw new IllegalStateException("La sala " + funcion.getSala().getCodigo() + " no está disponible");
        }
        if (funcion.getPelicula().getEstado() != EstadoPelicula.DISPONIBLE) {
            throw new IllegalStateException("La película " + funcion.getPelicula().getCodigo() + " no está disponible");
        }
        if (cliente.getTipo() == null) {
            throw new IllegalArgumentException("El cliente debe tener un tipo asignado");
        }
        if (funcion.asientosDisponibles() == 0) {
            throw new SinAsientosDisponiblesException("No hay asientos disponibles en la función " + funcion.getCodigo());
        }

        Set<String> codigosUnicos = new HashSet<>();
        List<AsientoFuncion> asientosSeleccionados = new ArrayList<>();
        for (String codigoAsiento : codigosAsientos) {
            if (codigoAsiento == null || codigoAsiento.isBlank()) {
                throw new IllegalArgumentException("El código de asiento no puede estar vacío");
            }
            if (!codigosUnicos.add(codigoAsiento)) {
                throw new IllegalArgumentException("El asiento " + codigoAsiento + " fue seleccionado más de una vez");
            }
            AsientoFuncion asiento = funcion.buscarAsiento(codigoAsiento);
            if (asiento == null) {
                throw new IllegalArgumentException("El asiento " + codigoAsiento + " no existe en la función");
            }
            if (!asiento.estaDisponible()) {
                throw new AsientoOcupadoException("El asiento " + codigoAsiento + " ya está ocupado");
            }
            asientosSeleccionados.add(asiento);
        }

        double valorEntrada = cinema.buscarTarifa(cliente.getTipo()).getValor();
        double recargoSala = funcion.getSala().recargo();
        if (!Double.isFinite(valorEntrada) || valorEntrada < 0
                || !Double.isFinite(recargoSala) || recargoSala < 0) {
            throw new IllegalStateException("La tarifa o el recargo de la sala no tiene un valor válido");
        }
        int cantidadAsientos = asientosSeleccionados.size();
        Cobro cobro = new Cobro(valorEntrada * cantidadAsientos, recargoSala * cantidadAsientos, 0);
        Venta venta = new Venta(generarCodigo(), LocalDateTime.now(), cliente, funcion, cobro);
        venta.setAsientos(asientosSeleccionados);

        List<AsientoFuncion> asientosOcupados = new ArrayList<>();
        try {
            for (AsientoFuncion asiento : asientosSeleccionados) {
                asiento.ocupar();
                asientosOcupados.add(asiento);
            }
        } catch (AsientoOcupadoException e) {
            asientosOcupados.forEach(AsientoFuncion::liberar);
            throw e;
        }

        cinema.registrarVenta(venta);
        return venta;
    }

    /**
     * Cancela la venta indicada y libera sus asientos.
     *
     * @param venta venta que se va a cancelar
     */
    public void cancelarVenta(Venta venta) {
        venta.cancelar();
    }

    /**
     * Devuelve las funciones programadas para la fecha indicada.
     *
     * @param fecha fecha de consulta
     * @return funciones registradas para esa fecha
     */
    public List<Funcion> funcionesDia(LocalDate fecha) {
        return cinema.funcionesDia(fecha);
    }

    /**
     * Genera un código único para una nueva venta.
     */
    private String generarCodigo() {
        return "V" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public CinemaUQ getCinema() {
        return cinema;
    }

    public void setCinema(CinemaUQ cinema) {
        this.cinema = cinema;
    }

    @Override
    public String toString() {
        return "Operador{usuario='" + getUsuario() + "', nombre='" + getNombre() + "'}";
    }
}
