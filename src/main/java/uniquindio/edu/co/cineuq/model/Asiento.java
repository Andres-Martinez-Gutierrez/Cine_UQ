package uniquindio.edu.co.cineuq.model;

import java.util.ArrayList;
import java.util.List;

public class Asiento {
    //Atributos de la clase Asiento
    private String codigo;
    private char fila;
    private int numero;
    private Boleta theAsientoBoleta;

    //Relaciones de la clase Asiento
    private List<Asiento> listAsientofunAsiento;

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
     * @param theAsientoBoleta comprobante de tu asiento
     */
    public Asiento(String codigo, char fila, int numero, Boleta theAsientoBoleta) {
        this.codigo = codigo;
        this.fila = fila;
        this.numero = numero;
        this.theAsientoBoleta = theAsientoBoleta;
        this.listAsientofunAsiento = new ArrayList<>();
    }

    public Boleta getTheAsientoBoleta() {
        return theAsientoBoleta;
    }

    public void setTheAsientoBoleta(Boleta theAsientoBoleta) {
        this.theAsientoBoleta = theAsientoBoleta;
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

    public List<Asiento> getListAsientofunAsiento() {
        return listAsientofunAsiento;
    }

    public void setListAsientofunAsiento(List<Asiento> listAsientofunAsiento) {
        this.listAsientofunAsiento = listAsientofunAsiento;
    }

    @Override
    public String toString() {
        return "Asiento{" +
                "codigo='" + codigo + '\'' +
                ", fila=" + fila +
                ", numero=" + numero +
                ", theAsientoBoleta=" + theAsientoBoleta +
                ", listAsientofunAsiento=" + listAsientofunAsiento +
                '}';
    }
}
