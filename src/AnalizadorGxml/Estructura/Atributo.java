/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorGxml.Estructura;

/**
 *
 * @author ivanl
 */
public class Atributo {

    int tipoAtributo;
    int tipoValor;
    String valor;

    public Atributo(int tipoAtributo, int tipoValor) {
        this.tipoAtributo = tipoAtributo;
        this.tipoValor = tipoValor;
    }

    public Atributo(int tipoAtributo, int tipoValor, String valor) {
        this.tipoAtributo = tipoAtributo;
        this.tipoValor = tipoValor;
        this.valor = valor;
    }

    public int getTipoAtributo() {
        return tipoAtributo;
    }

    public void setTipoAtributo(int tipoAtributo) {
        this.tipoAtributo = tipoAtributo;
    }

    public int getTipoValor() {
        return tipoValor;
    }

    public void setTipoValor(int tipoValor) {
        this.tipoValor = tipoValor;
    }

    public Object getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

}
