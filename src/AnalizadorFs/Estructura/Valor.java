/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorFs.Estructura;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @author ivanl
 */
public class Valor {

    Object valor;
    int etqTipo;

    int proveniente;

    public Valor(Object val, int etq) {
        this.valor = val;
        this.etqTipo = etq;
    }

    public Valor(String val, int etq) {
        switch (etq) {
            case ConstantesFs.TIPO_BOOLEANO:
                valor = Boolean.parseBoolean(val);
                break;
            case ConstantesFs.TIPO_CADENA:
                valor = val;
                break;
            case ConstantesFs.TIPO_NUMERO:

                valor = Double.parseDouble(val);
                break;

        }
        etqTipo = etq;
    }

    public Object getValor() {
        return valor;
    }

    public void setValor(Object valor) {
        this.valor = valor;
    }

    public int getEtqTipo() {
        return etqTipo;
    }

    public void setEtqTipo(int etqTipo) {
        this.etqTipo = etqTipo;
    }

    public double getNumber() {
        return Double.parseDouble(valor.toString());
    }

    public String getString() {
        if (etqTipo == ConstantesFs.TIPO_BOOLEANO) {
            if (getBoolean()) {
                return "verdadero";
            } else {
                return "falso";
            }
        } else if (etqTipo == ConstantesFs.TIPO_NUMERO) {
            double num = getNumber();
            if (num % 1 == 0 && num < 999999999) {
                int i = (int) num;
                return Integer.toString(i);
            }
        }

        return valor.toString();
    }

    public boolean getBoolean() {
        return Boolean.valueOf(valor.toString());
    }

    public ArrayList<Valor> getVector() {
        return (ArrayList<Valor>) valor;
    }

    public boolean isNull() {
        return isTipoIgual(ConstantesFs.TIPO_NULL);
    }

    public boolean isTipoIgual(int Etiqueta) {

        return this.etqTipo == Etiqueta;

    }

    public Valor getCopia() {
        Object vNuevo;
        switch (etqTipo) {
            case ConstantesFs.VECTOR_HETEROGENEO:
            case ConstantesFs.VECTOR_HOMOGENEO: {
                ArrayList<Valor> vector = getVector();
                ArrayList<Valor> vectorN = new ArrayList();
                int tam = vector.size();
                for (int i = 0; i < tam; i++) {
                    vectorN.add(vector.get(i).getCopia());
                }
                vNuevo = vectorN;
                break;
            }
            case ConstantesFs.TIPO_OBJETO: {
                vNuevo = ((Objeto) valor).getCopia();
                break;
            }
            default: {
                vNuevo = valor;
                break;
            }
        }
        return new Valor(vNuevo, etqTipo);
    }

    public void setValorTipo(Object objeto, int tipo) {
        this.etqTipo = tipo;
        valor = objeto;
    }

    public void copiarValor(Valor v) {
        this.etqTipo = v.etqTipo;
        this.valor = v.valor;
    }

    public int getProveniente() {
        return proveniente;
    }

    public void setProveniente(int proveniente) {
        this.proveniente = proveniente;
    }

    public boolean isProveniente(int proveniente) {
        return this.proveniente == proveniente;
    }

    public boolean isVectorHomogeneo(int tipo) {
        if (valor instanceof ArrayList) {
            ArrayList<Valor> temp = (ArrayList<Valor>) valor;
            for (Valor v : temp) {
                if (!v.isTipoIgual(tipo)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public boolean isVectorHomogeneo() {

        if (valor instanceof ArrayList) {
            ArrayList<Valor> temp = (ArrayList<Valor>) valor;
            int t = temp.size();
            if (t > 0) {
                int tipoI = temp.get(0).getEtqTipo();
                for (int i = 1; i < t; i++) {
                    if (!temp.get(i).isTipoIgual(tipoI)) {
                        return false;
                    }
                }
            }else{
                return false;
            }
            return true;
        }
        return false;
    }
}
