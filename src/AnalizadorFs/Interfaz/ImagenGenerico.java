/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorFs.Interfaz;

import java.awt.Component;
import javax.swing.JLabel;

/**
 *
 * @author ivanl
 */
public class ImagenGenerico extends ComponenteGenerico {

    JLabel elemento;
    
    public ImagenGenerico (String texto){
        elemento = new JLabel();
        elemento.setText(texto);
    }
    
    @Override
    public Component get() {
        return elemento;
    }

    public JLabel getElemento() {
        return elemento;
    }

    public void setElemento(JLabel elemento) {
        this.elemento = elemento;
    }
    
    

   
}
