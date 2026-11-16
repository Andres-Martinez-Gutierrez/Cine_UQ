package uniquindio.edu.co.cineuq.model;

import java.util.ArrayList;
import java.util.List;

public class Asiento {
    //Atributos de la clase Asiento
    private String codigo;
    private char fila;
    private int numero;
    private Boleta boleta;

    //Relaciones de la clase Asiento
    private List<AsientoFuncion> listAsientoAsientofun;

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
     * @param boleta boleta asociada al asiento
     */
    public Asiento(String codigo, char fila, int numero, Boleta boleta) {
        this.codigo = codigo;
        this.fila = fila;
        this.numero = numero;
        this.boleta = boleta;
        this.listAsientoAsientofun = new ArrayList<>();
    }

    public Boleta getBoleta() {
        return boleta;
    }

    public void setBoleta(Boleta boleta) {
        this.boleta = boleta;
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

    public List<AsientoFuncion> getListAsientoAsientofun() {
        return listAsientoAsientofun;
    }

    public void setListAsientoAsientofun(List<AsientoFuncion> listAsientoAsientofun) {
        this.listAsientoAsientofun = listAsientoAsientofun;
    }

    @Override
    public String toString() {
        return "Asiento{" +
                "codigo='" + codigo + '\'' +
                ", fila=" + fila +
                ", numero=" + numero +
                ", boleta=" + boleta +
                ", asientosEnFunciones=" + (listAsientoAsientofun == null ? 0 : listAsientoAsientofun.size()) +
                '}';
    }
}
