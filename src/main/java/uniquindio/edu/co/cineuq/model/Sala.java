package uniquindio.edu.co.cineuq.model;

import uniquindio.edu.co.cineuq.model.enumeraciones.EstadoSala;
import uniquindio.edu.co.cineuq.model.enumeraciones.TipoSala;

import java.util.ArrayList;
import java.util.List;

public class Sala {
    //Atributos de la clase Sala
    private String codigo;
    private String nombre;
    private int capacidad;

    //Relaciones de la clase Sala
    private TipoSala tipo;
    private EstadoSala estado;
    private List<Asiento> listSalaAientos;
    private List<Funcion> listSalaFunciones;

    /**
     * Constructor vacío de la clase Sala.
     * Deja el estado de la sala en DISPONIBLE; los demás atributos se asignan con los setters.
     */
    public Sala() {
        this.estado = EstadoSala.DISPONIBLE;
    }

    /**
     * Constructor de la clase Sala.
     *
     * @param codigo código de la sala
     * @param nombre nombre descriptivo de la sala
     * @param capacidad número máximo de asientos de la sala
     * @param tipo tipo de sala (tradicional, VIP, 3D o IMAX)
     */
    public Sala(String codigo, String nombre, int capacidad, TipoSala tipo) {
        this();
        this.codigo = codigo;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.tipo = tipo;
        this.listSalaAientos = new ArrayList<>();
        this.listSalaFunciones = new ArrayList<>();
    }

    /** Marca la sala como fuera de servicio. */
    public void deshabilitar() {
        estado = EstadoSala.FUERA_DE_SERVICIO;
    }

    /** Recargo según el tipo de sala. Valores de ejemplo: ajustar a la tarifa real del cine. */
    public double recargo() {
        return switch (tipo) {
            case TRADICIONAL -> 0;
            case VIP -> 5000;
            case SALA_3D -> 3000;
            case IMAX -> 8000;
        };
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public TipoSala getTipo() {
        return tipo;
    }

    public void setTipo(TipoSala tipo) {
        this.tipo = tipo;
    }

    public EstadoSala getEstado() {
        return estado;
    }

    public void setEstado(EstadoSala estado) {
        this.estado = estado;
    }

    public List<Asiento> getListSalaAientos() {
        return listSalaAientos;
    }

    public void setListSalaAientos(List<Asiento> listSalaAientos) {
        this.listSalaAientos = listSalaAientos;
    }

    public List<Funcion> getListSalaFunciones() {
        return listSalaFunciones;
    }

    public void setListSalaFunciones(List<Funcion> listSalaFunciones) {
        this.listSalaFunciones = listSalaFunciones;
    }

    @Override
    public String toString() {
        return "Sala{codigo='" + codigo + "', nombre='" + nombre + "', capacidad=" + capacidad
                + ", tipo=" + tipo + ", estado=" + estado + "}";
    }
}
