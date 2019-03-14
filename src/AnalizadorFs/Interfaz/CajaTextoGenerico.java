/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorFs.Interfaz;

import java.awt.Component;
import javax.swing.JTextField;

/**
 *
 * @author ivanl
 */
public class CajaTextoGenerico extends ComponenteGenerico {

    JTextField elemento;

    public CajaTextoGenerico(String texto) {
        elemento = new JTextField();
        if (!texto.equals("")) {
            elemento.setText(texto );
        }
    }

    @Override
    public Component get() {
        return elemento;
    }

    public JTextField getElemento() {
        return elemento;
    }

    public void setElemento(JTextField elemento) {
        this.elemento = elemento;
    }

}
