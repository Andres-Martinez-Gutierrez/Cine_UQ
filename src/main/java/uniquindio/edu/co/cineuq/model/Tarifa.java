package uniquindio.edu.co.cineuq.model;

public class Tarifa {
    private TipoCliente tipoCliente;
    private double valor;

    /**
     * Constructor vacío de la clase Tarifa.
     * Los atributos se asignan luego con los setters.
     */
    public Tarifa() {
    }

    /**
     * Constructor de la clase Tarifa.
     *
     * @param tipoCliente tipo de cliente al que aplica la tarifa
     * @param valor valor cobrado por entrada para ese tipo de cliente
     */
    public Tarifa(TipoCliente tipoCliente, double valor) {
        this.tipoCliente = tipoCliente;
        this.valor = valor;
    }

    /** Actualiza el valor de la entrada para el tipo de cliente de esta tarifa. */
    public void actualizarValor(double nuevoValor) {
        this.valor = nuevoValor;
    }

    public TipoCliente getTipoCliente() {
        return tipoCliente;
    }

    public void setTipoCliente(TipoCliente tipoCliente) {
        this.tipoCliente = tipoCliente;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "Tarifa{tipoCliente=" + tipoCliente + ", valor=" + valor + "}";
    }
}
