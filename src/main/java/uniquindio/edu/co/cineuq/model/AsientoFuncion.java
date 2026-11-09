package uniquindio.edu.co.cineuq.model;

import uniquindio.edu.co.cineuq.exception.AsientoOcupadoException;

public class AsientoFuncion {

    //Relaciones de la clase AsientoFuncion
    private Asiento asiento;
    private EstadoAsiento estado;
    private Venta venta;

    /**
     * Constructor vacío de la clase AsientoFuncion.
     * Deja el estado del asiento en DISPONIBLE; el asiento se asigna luego con el setter.
     */
    public AsientoFuncion() {
        this.estado = EstadoAsiento.DISPONIBLE;
    }

    /**
     * Constructor de la clase AsientoFuncion.
     */
    public AsientoFuncion(Venta venta, Asiento asiento) {
        this.venta = venta;
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

    public EstadoAsiento getEstado() {
        return estado;
    }

    public void setEstado(EstadoAsiento estado) {
        this.estado = estado;
    }

    public Asiento getAsiento() {
        return asiento;
    }

    public void setAsiento(Asiento asiento) {
        this.asiento = asiento;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    @Override
    public String toString() {
        return "AsientoFuncion{" +
                "asiento=" + asiento +
                ", estado=" + estado +
                ", venta=" + venta +
                '}';
    }
}
