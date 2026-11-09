package uniquindio.edu.co.cineuq.model;

import uniquindio.edu.co.cineuq.model.enumeraciones.EstadoFuncion;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Funcion {
    private static final int ASIENTOS_POR_FILA = 10;

    //Atributos de la clase Funcion
    private String codigo;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private double valorBase;

    //Relaciones de la clase Funcion
    private EstadoFuncion estado;
    private List<AsientoFuncion> listFuncionAsientofun;
    private Sala sala;
    private Pelicula pelicula;
    private List<Venta> listFuncionVenta;

    /**
     * Constructor vacío de la clase Funcion.
     * Deja el estado en PROGRAMADA y la lista de asientos vacía; los demás atributos se asignan con los setters.
     */
    public Funcion() {
        this.estado = EstadoFuncion.PROGRAMADA;
        this.listFuncionAsientofun = new ArrayList<>();
        this.listFuncionVenta = new ArrayList<>();
    }

    /**
     * Constructor de la clase Funcion. Además genera un AsientoFuncion por cada asiento de la sala.
     *
     * @param codigo código de la función
     * @param pelicula película que se proyecta
     * @param sala sala en la que se realiza la función
     * @param fecha fecha en que se realiza la función
     * @param horaInicio hora a la que empieza la función
     * @param valorBase valor base de la función
     */
    public Funcion(String codigo, Pelicula pelicula, Sala sala, LocalDate fecha, LocalTime horaInicio, double valorBase) {
        this();
        this.codigo = codigo;
        this.pelicula = pelicula;
        this.sala = sala;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.valorBase = valorBase;
        for (int i = 0; i < sala.getCapacidad(); i++) {
            char fila = (char) ('A' + i / ASIENTOS_POR_FILA);
            int numero = i % ASIENTOS_POR_FILA + 1;
            listFuncionAsientofun.add(new AsientoFuncion(null, new Asiento("" + fila + numero, fila, numero)));
        }
    }

    /** Devuelve la cantidad de asientos disponibles para la venta. */
    public int asientosDisponibles() {
        return (int) listFuncionAsientofun.stream().filter(AsientoFuncion::estaDisponible).count();
    }

    /** Devuelve la cantidad de asientos ocupados. */
    public int asientosOcupados() {
        return listFuncionAsientofun.size() - asientosDisponibles();
    }

    /** Porcentaje de asientos ocupados (0 a 100). */
    public double porcentajeOcupacion() {
        if (listFuncionAsientofun.isEmpty()) return 0;
        return asientosOcupados() * 100.0 / listFuncionAsientofun.size();
    }

    /** Busca un asiento de la función por su código (por ejemplo "A5"). Devuelve null si no existe. */
    public AsientoFuncion buscarAsiento(String codigoAsiento) {
        for (AsientoFuncion af : listFuncionAsientofun) {
            if (af.getAsiento().getCodigo().equals(codigoAsiento)) return af;
        }
        return null;
    }

    public LocalTime getHoraFin() {
        return horaInicio.plusMinutes(pelicula.getDuracionMinutos());
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Pelicula getPelicula() {
        return pelicula;
    }

    public void setPelicula(Pelicula pelicula) {
        this.pelicula = pelicula;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public double getValorBase() {
        return valorBase;
    }

    public void setValorBase(double valorBase) {
        this.valorBase = valorBase;
    }

    public EstadoFuncion getEstado() {
        return estado;
    }

    public void setEstado(EstadoFuncion estado) {
        this.estado = estado;
    }

    public List<AsientoFuncion> getListFuncionAsientofun() {
        return listFuncionAsientofun;
    }

    public void setListFuncionAsientofun(List<AsientoFuncion> listFuncionAsientofun) {
        this.listFuncionAsientofun = listFuncionAsientofun;
    }

    public List<Venta> getListFuncionVenta() {
        return listFuncionVenta;
    }

    public void setListFuncionVenta(List<Venta> listFuncionVenta) {
        this.listFuncionVenta = listFuncionVenta;
    }

    @Override
    public String toString() {
        return "Funcion{codigo='" + codigo + "', pelicula=" + (pelicula == null ? null : pelicula.getCodigo())
                + ", sala=" + (sala == null ? null : sala.getCodigo()) + ", fecha=" + fecha
                + ", horaInicio=" + horaInicio + ", valorBase=" + valorBase + ", estado=" + estado
                + ", asientos=" + listFuncionAsientofun.size() + "}";
    }
}
