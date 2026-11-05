package uniquindio.edu.co.cineuq.model;

import uniquindio.edu.co.cineuq.exception.FuncionNoEncontradaException;
import uniquindio.edu.co.cineuq.exception.PeliculaDuplicadaException;
import uniquindio.edu.co.cineuq.exception.SalaDuplicadaException;
import uniquindio.edu.co.cineuq.exception.SalaNoDisponibleException;
import uniquindio.edu.co.cineuq.exception.VentaNoEncontradaException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CinemaUQ {
    private String nombre;
    private String direccion;
    private String nit;
    private List<Pelicula> peliculas;
    private List<Sala> salas;
    private List<Funcion> funciones;
    private List<Venta> ventas;
    private List<Tarifa> tarifas;

    /**
     * Constructor vacío de la clase CinemaUQ.
     * Inicializa las listas y crea una tarifa en cero por cada tipo de cliente.
     */
    public CinemaUQ() {
        this.peliculas = new ArrayList<>();
        this.salas = new ArrayList<>();
        this.funciones = new ArrayList<>();
        this.ventas = new ArrayList<>();
        this.tarifas = new ArrayList<>();
        for (TipoCliente tipo : TipoCliente.values()) {
            tarifas.add(new Tarifa(tipo, 0));
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

    public void registrarPelicula(Pelicula pelicula) throws PeliculaDuplicadaException {
        for (Pelicula p : peliculas) {
            if (p.getCodigo().equals(pelicula.getCodigo())) {
                throw new PeliculaDuplicadaException("Ya existe la película " + pelicula.getCodigo());
            }
        }
        peliculas.add(pelicula);
    }

    public void registrarSala(Sala sala) throws SalaDuplicadaException {
        for (Sala s : salas) {
            if (s.getCodigo().equals(sala.getCodigo())) {
                throw new SalaDuplicadaException("Ya existe la sala " + sala.getCodigo());
            }
        }
        salas.add(sala);
    }

    /** No se programa en una sala fuera de servicio ni se traslapa con otra función de la misma sala. */
    public void programarFuncion(Funcion funcion) throws SalaNoDisponibleException {
        Sala sala = funcion.getSala();
        if (sala.getEstado() == EstadoSala.FUERA_DE_SERVICIO) {
            throw new SalaNoDisponibleException("La sala " + sala.getCodigo() + " está fuera de servicio");
        }
        for (Funcion otra : funciones) {
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
        funciones.add(funcion);
    }

    public void registrarVenta(Venta venta) {
        ventas.add(venta);
    }

    public Venta buscarVenta(String codigo) throws VentaNoEncontradaException {
        for (Venta v : ventas) {
            if (v.getCodigo().equals(codigo)) return v;
        }
        throw new VentaNoEncontradaException("No existe la venta " + codigo);
    }

    public int totalEntradasVendidas() {
        return ventas.stream()
                .filter(v -> v.getEstado() == EstadoVenta.ACTIVA)
                .mapToInt(v -> v.getAsientos().size())
                .sum();
    }

    public double totalIngresos() {
        return ventas.stream()
                .filter(v -> v.getEstado() == EstadoVenta.ACTIVA)
                .mapToDouble(Venta::getTotal)
                .sum();
    }

    /** Película con más entradas vendidas en ventas activas; null si no hay ventas. */
    public Pelicula peliculaMasVendida() {
        Map<Pelicula, Integer> entradas = new HashMap<>();
        for (Venta v : ventas) {
            if (v.getEstado() != EstadoVenta.ACTIVA) continue;
            entradas.merge(v.getFuncion().getPelicula(), v.getAsientos().size(), Integer::sum);
        }
        return entradas.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public double porcentajeOcupacion(Funcion funcion) {
        return funcion.porcentajeOcupacion();
    }

    public List<Funcion> funcionesDia(LocalDate fecha) {
        return funciones.stream().filter(f -> f.getFecha().equals(fecha)).toList();
    }

    public Funcion buscarFuncion(String codigo) throws FuncionNoEncontradaException {
        for (Funcion f : funciones) {
            if (f.getCodigo().equals(codigo)) return f;
        }
        throw new FuncionNoEncontradaException("No existe la función " + codigo);
    }

    public Tarifa buscarTarifa(TipoCliente tipo) {
        for (Tarifa t : tarifas) {
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

    public List<Pelicula> getPeliculas() {
        return List.copyOf(peliculas);
    }

    public void setPeliculas(List<Pelicula> peliculas) {
        this.peliculas = new ArrayList<>(peliculas);
    }

    public List<Sala> getSalas() {
        return List.copyOf(salas);
    }

    public void setSalas(List<Sala> salas) {
        this.salas = new ArrayList<>(salas);
    }

    public List<Funcion> getFunciones() {
        return List.copyOf(funciones);
    }

    public void setFunciones(List<Funcion> funciones) {
        this.funciones = new ArrayList<>(funciones);
    }

    public List<Venta> getVentas() {
        return List.copyOf(ventas);
    }

    public void setVentas(List<Venta> ventas) {
        this.ventas = new ArrayList<>(ventas);
    }

    public List<Tarifa> getTarifas() {
        return List.copyOf(tarifas);
    }

    public void setTarifas(List<Tarifa> tarifas) {
        this.tarifas = new ArrayList<>(tarifas);
    }

    @Override
    public String toString() {
        return "CinemaUQ{nombre='" + nombre + "', direccion='" + direccion + "', nit='" + nit
                + "', peliculas=" + peliculas.size() + ", salas=" + salas.size()
                + ", funciones=" + funciones.size() + ", ventas=" + ventas.size() + "}";
    }
}
