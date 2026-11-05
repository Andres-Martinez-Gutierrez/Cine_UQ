package uniquindio.edu.co.cineuq.model;

public class Cobro {
    private double valorEntrada;
    private double recargoSala;
    private double descuentoCliente;
    private double valorProductos;

    /**
     * Constructor vacío de la clase Cobro.
     * Los valores se asignan luego con los setters.
     */
    public Cobro() {
    }

    /**
     * Constructor de la clase Cobro.
     *
     * @param valorEntrada valor de la entrada según la tarifa del tipo de cliente
     * @param recargoSala recargo cobrado por la sala
     * @param descuentoCliente descuento aplicado al cliente sobre el total
     */
    public Cobro(double valorEntrada, double recargoSala, double descuentoCliente) {
        this.valorEntrada = valorEntrada;
        this.recargoSala = recargoSala;
        this.descuentoCliente = descuentoCliente;
    }

    public void agregarValorProductos(double valor) {
        this.valorProductos += valor;
    }

    /** Valor pagado = entrada + recargo de sala + productos - descuento del cliente. */
    public double calcularTotal() {
        return valorEntrada + recargoSala + valorProductos - descuentoCliente;
    }

    public double getValorEntrada() {
        return valorEntrada;
    }

    public void setValorEntrada(double valorEntrada) {
        this.valorEntrada = valorEntrada;
    }

    public double getRecargoSala() {
        return recargoSala;
    }

    public void setRecargoSala(double recargoSala) {
        this.recargoSala = recargoSala;
    }

    public double getDescuentoCliente() {
        return descuentoCliente;
    }

    public void setDescuentoCliente(double descuentoCliente) {
        this.descuentoCliente = descuentoCliente;
    }

    public double getValorProductos() {
        return valorProductos;
    }

    public void setValorProductos(double valorProductos) {
        this.valorProductos = valorProductos;
    }

    @Override
    public String toString() {
        return "Cobro{valorEntrada=" + valorEntrada + ", recargoSala=" + recargoSala
                + ", descuentoCliente=" + descuentoCliente + ", valorProductos=" + valorProductos
                + ", total=" + calcularTotal() + "}";
    }
}
