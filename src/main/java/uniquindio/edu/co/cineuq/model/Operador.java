package uniquindio.edu.co.cineuq.model;

import uniquindio.edu.co.cineuq.exception.CinemaUQException;
import uniquindio.edu.co.cineuq.exception.SinAsientosDisponiblesException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
     * @param usuario nombre de usuario con el que inicia sesión
     * @param contrasena contraseña del operador
     * @param nombre nombre completo del operador
     * @param cinema cine en el que el operador realiza las ventas
     */
    public Operador(String usuario, String contrasena, String nombre, CinemaUQ cinema) {
        super(usuario, contrasena, nombre);
        this.cinema = cinema;
    }

    /**
     * Vende un asiento de la función al cliente. El valor de entrada es la tarifa del tipo de cliente
     * y se suma el recargo de la sala; los productos se agregan luego con Venta.agregarProducto.
     */
    public Venta vender(Funcion funcion, Cliente cliente, String codigoAsiento) throws CinemaUQException {
        if (funcion.getEstado() != EstadoFuncion.PROGRAMADA) {
            throw new IllegalStateException("La función " + funcion.getCodigo() + " no está programada");
        }
        if (funcion.asientosDisponibles() == 0) {
            throw new SinAsientosDisponiblesException("No hay asientos disponibles en la función " + funcion.getCodigo());
        }
        AsientoFuncion asiento = funcion.buscarAsiento(codigoAsiento);
        if (asiento == null) {
            throw new IllegalArgumentException("El asiento " + codigoAsiento + " no existe en la función");
        }
        asiento.ocupar();

        double valorEntrada = cinema.buscarTarifa(cliente.getTipo()).getValor();
        Cobro cobro = new Cobro(valorEntrada, funcion.getSala().recargo(), 0);
        Venta venta = new Venta(generarCodigo(), LocalDateTime.now(), cliente, funcion, cobro);
        venta.agregarAsiento(asiento);
        cinema.registrarVenta(venta);
        return venta;
    }

    /** Cancela la venta indicada y libera sus asientos. */
    public void cancelarVenta(Venta venta) {
        venta.cancelar();
    }

    /** Devuelve las funciones programadas para la fecha indicada. */
    public List<Funcion> funcionesDia(LocalDate fecha) {
        return cinema.funcionesDia(fecha);
    }

    /** Genera un código único para una nueva venta. */
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
