package uniquindio.edu.co.cineuq.model;

public class Asiento {
    private String codigo;
    private char fila;
    private int numero;

    /**
     * Constructor vacío de la clase Asiento.
     * Los atributos se asignan luego con los setters.
     */
    public Asiento() {
    }

    /**
     * Constructor de la clase Asiento.
     *
     * @param codigo código del asiento (fila y número, por ejemplo "A5")
     * @param fila letra de la fila del asiento
     * @param numero número del asiento dentro de su fila
     */
    public Asiento(String codigo, char fila, int numero) {
        this.codigo = codigo;
        this.fila = fila;
        this.numero = numero;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public char getFila() {
        return fila;
    }

    public void setFila(char fila) {
        this.fila = fila;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    @Override
    public String toString() {
        return "Asiento{codigo='" + codigo + "', fila=" + fila + ", numero=" + numero + "}";
    }
}
