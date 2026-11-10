package uniquindio.edu.co.cineuq.model;

public class Boleta {
    private String codigo;
    private double valorPagado;
    private Asiento theBoletaAsiento;

    /**
     * Constructor de la clase Boleta.
     *
     * @param codigo que tiene la boleta
     * @param valorPagado de la boleta
     * @param theBoletaAsiento el asiento que hace referenia en la boleta
     */
    public Boleta(double valorPagado, String codigo, Asiento theBoletaAsiento) {
        this.valorPagado = valorPagado;
        this.codigo = codigo;
        this.theBoletaAsiento = theBoletaAsiento;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public double getValorPagado() {
        return valorPagado;
    }

    public void setValorPagado(double valorPagado) {
        this.valorPagado = valorPagado;
    }

    public Asiento getTheBoletaAsiento() {
        return theBoletaAsiento;
    }

    public void setTheBoletaAsiento(Asiento theBoletaAsiento) {
        this.theBoletaAsiento = theBoletaAsiento;
    }

    @Override
    public String toString() {
        return "Boleta{" +
                "codigo='" + codigo + '\'' +
                ", valorPagado=" + valorPagado +
                ", theBoletaAsiento=" + theBoletaAsiento +
                '}';
    }
}
