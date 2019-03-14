/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorFs.Interfaz;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JTextPane;

/**
 *
 * @author ivanl
 */
public class CajaAreaTexto extends ComponenteGenerico {

    JTextPane elemento;

    public CajaAreaTexto(String texto) {
        elemento = new JTextPane();

        //if (!texto.equals("")) {
        elemento.setText(texto);

    }

    @Override
    public Component get() {
        return elemento;
    }

    public JTextPane getElemento() {
        return elemento;
    }

    public void setElemento(JTextPane elemento) {
        this.elemento = elemento;
    }

}
