/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorFs.Interfaz;

import AnalizadorFs.Estructura.NodoArbol;
import AnalizadorFs.Estructura.Valor;
import java.awt.Component;
import java.util.ArrayList;
import javax.swing.JComboBox;

/**
 *
 * @author ivanl
 */
public class CajaGenerico extends ComponenteGenerico {

    JComboBox elemento;

    public CajaGenerico(Valor vector) {
        elemento = new JComboBox();

        ArrayList<Valor> a = vector.getVector();
        for (Valor nodo : a) {
            String texto = nodo.getString();
            elemento.addItem(texto);
        }

    }

    @Override
    public Component get() {
        return elemento;
    }

    public JComboBox getElemento() {
        return elemento;
    }

    public void setElemento(JComboBox elemento) {
        this.elemento = elemento;
    }

}
