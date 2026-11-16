package uniquindio.edu.co.cineuq.model;

import uniquindio.edu.co.cineuq.exception.FuncionNoEncontradaException;
import uniquindio.edu.co.cineuq.exception.PeliculaDuplicadaException;
import uniquindio.edu.co.cineuq.exception.SalaDuplicadaException;
import uniquindio.edu.co.cineuq.exception.SalaNoDisponibleException;
import uniquindio.edu.co.cineuq.exception.VentaNoEncontradaException;
import uniquindio.edu.co.cineuq.model.enumeraciones.EstadoFuncion;
import uniquindio.edu.co.cineuq.model.enumeraciones.EstadoSala;
import uniquindio.edu.co.cineuq.model.enumeraciones.EstadoVenta;
import uniquindio.edu.co.cineuq.model.enumeraciones.TipoCliente;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CinemaUQ {
    //Atributos de la clase CinemaUQ
    private String nombre;
    private String direccion;
    private String nit;

    //Relaciones de la clase CinemaUQ
    private List<Pelicula> listCinemaPeliculas;
    private List<Sala> listCinemaSalas;
    private List<Funcion> listCinemaFunciones;
    private List<Venta> listCinemaVentas;
    private List<Tarifa> listCinemaTarifas;
    private List<Usuario> listCinemaUsuarios;
    private List<Cliente> listCinemaClientes;

    /**
     * Constructor vacío de la clase CinemaUQ.
     * Inicializa las listas y crea una tarifa en cero por cada tipo de cliente.
     */
    public CinemaUQ() {
        this.listCinemaPeliculas = new ArrayList<>();
        this.listCinemaSalas = new ArrayList<>();
        this.listCinemaFunciones = new ArrayList<>();
        this.listCinemaVentas = new ArrayList<>();
        this.listCinemaTarifas = new ArrayList<>();
        this.listCinemaUsuarios = new ArrayList<>();
        this.listCinemaClientes = new ArrayList<>();
        for (TipoCliente tipo : TipoCliente.values()) {
            listCinemaTarifas.add(new Tarifa(tipo, 0));
        }
    }

    /**
     * Constructor de la clase CinemaUQ.
     *
     * @param nombre nombre comercial del cine
     * @param direccion dirección física del cine
     * @param nit número de identificación tributaria (NIT) del cine
     */
    public CinemaUQ(String nombre, String direccion, String nit) {
        this();
        this.nombre = nombre;
        this.direccion = direccion;
        this.nit = nit;
    }

    /** Registra una película en el cine. Lanza PeliculaDuplicadaException si ya existe una con el mismo código. */
    public void registrarPelicula(Pelicula pelicula) throws PeliculaDuplicadaException {
        for (Pelicula p : listCinemaPeliculas) {
            if (p.getCodigo().equals(pelicula.getCodigo())) {
                throw new PeliculaDuplicadaException("Ya existe la película " + pelicula.getCodigo());
            }
        }
        listCinemaPeliculas.add(pelicula);
    }

    /** Registra una sala en el cine. Lanza SalaDuplicadaException si ya existe una con el mismo código. */
    public void registrarSala(Sala sala) throws SalaDuplicadaException {
        for (Sala s : listCinemaSalas) {
            if (s.getCodigo().equals(sala.getCodigo())) {
                throw new SalaDuplicadaException("Ya existe la sala " + sala.getCodigo());
            }
        }
        listCinemaSalas.add(sala);
    }

    /** No se programa en una sala fuera de servicio ni se traslapa con otra función de la misma sala. */
    public void programarFuncion(Funcion funcion) throws SalaNoDisponibleException {
        Sala sala = funcion.getSala();
        if (sala.getEstado() == EstadoSala.FUERA_DE_SERVICIO) {
            throw new SalaNoDisponibleException("La sala " + sala.getCodigo() + " está fuera de servicio");
        }
        for (Funcion otra : listCinemaFunciones) {
            if (otra.getEstado() == EstadoFuncion.CANCELADA) continue;
            if (!otra.getSala().getCodigo().equals(sala.getCodigo())) continue;
            if (!otra.getFecha().equals(funcion.getFecha())) continue;
            boolean seTraslapan = funcion.getHoraInicio().isBefore(otra.getHoraFin())
                    && otra.getHoraInicio().isBefore(funcion.getHoraFin());
            if (seTraslapan) {
                throw new SalaNoDisponibleException("La sala " + sala.getCodigo()
                        + " ya tiene la función " + otra.getCodigo() + " en ese horario");
            }
        }
        listCinemaFunciones.add(funcion);
    }

    /** Agrega una venta al historial del cine. */
    public void registrarVenta(Venta venta) {
        listCinemaVentas.add(venta);
    }

    /** Busca una venta por su código. Lanza VentaNoEncontradaException si no existe. */
    public Venta buscarVenta(String codigo) throws VentaNoEncontradaException {
        for (Venta v : listCinemaVentas) {
            if (v.getCodigo().equals(codigo)) return v;
        }
        throw new VentaNoEncontradaException("No existe la venta " + codigo);
    }

    /** Devuelve el número total de asientos vendidos en ventas activas. */
    public int totalEntradasVendidas() {
        return listCinemaVentas.stream()
                .filter(v -> v.getEstado() == EstadoVenta.ACTIVA)
                .mapToInt(v -> v.getListVentaAsientofun().size())
                .sum();
    }

    /** Devuelve el total recaudado por las ventas activas. */
    public double totalIngresos() {
        return listCinemaVentas.stream()
                .filter(v -> v.getEstado() == EstadoVenta.ACTIVA)
                .mapToDouble(Venta::getTotal)
                .sum();
    }

    /** Película con más entradas vendidas en ventas activas; null si no hay ventas de entradas. */
    public Pelicula peliculaMasVendida() {
        Map<Pelicula, Integer> entradas = new HashMap<>();
        for (Venta v : listCinemaVentas) {
            if (v.getEstado() != EstadoVenta.ACTIVA || v.getFuncion() == null) continue;
            entradas.merge(v.getFuncion().getPelicula(), v.getListVentaAsientofun().size(), Integer::sum);
        }
        return entradas.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    /** Devuelve el porcentaje de ocupación de la función indicada (0 a 100). */
    public double porcentajeOcupacion(Funcion funcion) {
        return funcion.porcentajeOcupacion();
    }

    /** Devuelve las funciones programadas para la fecha indicada. */
    public List<Funcion> funcionesDia(LocalDate fecha) {
        return listCinemaFunciones.stream().filter(f -> f.getFecha().equals(fecha)).toList();
    }

    /** Busca una función por su código. Lanza FuncionNoEncontradaException si no existe. */
    public Funcion buscarFuncion(String codigo) throws FuncionNoEncontradaException {
        for (Funcion f : listCinemaFunciones) {
            if (f.getCodigo().equals(codigo)) return f;
        }
        throw new FuncionNoEncontradaException("No existe la función " + codigo);
    }

    /** Devuelve la tarifa del tipo de cliente indicado. Lanza IllegalStateException si no existe. */
    public Tarifa buscarTarifa(TipoCliente tipo) {
        for (Tarifa t : listCinemaTarifas) {
            if (t.getTipoCliente() == tipo) return t;
        }
        throw new IllegalStateException("No hay tarifa para " + tipo);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public List<Funcion> getListCinemaFunciones() {
        return List.copyOf(listCinemaFunciones);
    }

    public void setListCinemaFunciones(List<Funcion> listCinemaFunciones) {
        this.listCinemaFunciones = new ArrayList<>(listCinemaFunciones);
    }


    public List<Usuario> getListCinemaUsuarios() {
        return listCinemaUsuarios;
    }

    public void setListCinemaUsuarios(List<Usuario> listCinemaUsuarios) {
        this.listCinemaUsuarios = listCinemaUsuarios;
    }

    public List<Cliente> getListCinemaClientes() {
        return listCinemaClientes;
    }

    public void setListCinemaClientes(List<Cliente> listCinemaClientes) {
        this.listCinemaClientes = listCinemaClientes;
    }

    public List<Venta> getListCinemaVentas() {
        return listCinemaVentas;
    }

    public void setListCinemaVentas(List<Venta> listCinemaVentas) {
        this.listCinemaVentas = listCinemaVentas;
    }

    public List<Tarifa> getListCinemaTarifas() {
        return listCinemaTarifas;
    }

    public void setListCinemaTarifas(List<Tarifa> listCinemaTarifas) {
        this.listCinemaTarifas = listCinemaTarifas;
    }

    public List<Sala> getListCinemaSalas() {
        return listCinemaSalas;
    }

    public void setListCinemaSalas(List<Sala> listCinemaSalas) {
        this.listCinemaSalas = listCinemaSalas;
    }

    public List<Pelicula> getListCinemaPeliculas() {
        return listCinemaPeliculas;
    }

    public void setListCinemaPeliculas(List<Pelicula> listCinemaPeliculas) {
        this.listCinemaPeliculas = listCinemaPeliculas;
    }

    @Override
    public String toString() {
        return "CinemaUQ{" +
                "nombre='" + nombre + '\'' +
                ", direccion='" + direccion + '\'' +
                ", nit='" + nit + '\'' +
                '}';
    }
}
