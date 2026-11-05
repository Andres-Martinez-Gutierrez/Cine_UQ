package uniquindio.edu.co.cineuq.model;

import uniquindio.edu.co.cineuq.exception.AsientoOcupadoException;

public class AsientoFuncion {
    private Asiento asiento;
    private EstadoAsiento estado;

    /**
     * Constructor vacío de la clase AsientoFuncion.
     * Deja el estado del asiento en DISPONIBLE; el asiento se asigna luego con el setter.
     */
    public AsientoFuncion() {
        this.estado = EstadoAsiento.DISPONIBLE;
    }

    /**
     * Constructor de la clase AsientoFuncion.
     *
     * @param asiento asiento físico de la sala al que corresponde este registro
     */
    public AsientoFuncion(Asiento asiento) {
        this();
        this.asiento = asiento;
    }

    /** Marca el asiento como ocupado. Lanza AsientoOcupadoException si ya estaba ocupado. */
    public void ocupar() throws AsientoOcupadoException {
        if (estado == EstadoAsiento.OCUPADO) {
            throw new AsientoOcupadoException("El asiento " + asiento.getCodigo() + " ya está ocupado");
        }
        estado = EstadoAsiento.OCUPADO;
    }

    /** Marca el asiento como disponible nuevamente. */
    public void liberar() {
        estado = EstadoAsiento.DISPONIBLE;
    }

    /** Indica si el asiento está disponible para la venta. */
    public boolean estaDisponible() {
        return estado == EstadoAsiento.DISPONIBLE;
    }

    public Asiento getAsiento() {
        return asiento;
    }

    public void setAsiento(Asiento asiento) {
        this.asiento = asiento;
    }

    public EstadoAsiento getEstado() {
        return estado;
    }

    public void setEstado(EstadoAsiento estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "AsientoFuncion{asiento=" + (asiento == null ? null : asiento.getCodigo())
                + ", estado=" + estado + "}";
    }
}
