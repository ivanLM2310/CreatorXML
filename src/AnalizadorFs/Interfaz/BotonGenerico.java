/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorFs.Interfaz;

import java.awt.Component;
import javax.swing.JButton;

/**
 *
 * @author ivanl
 */
public class BotonGenerico  extends ComponenteGenerico{
    
    JButton elemento;
    
    
    public BotonGenerico(String texto){
        elemento = new JButton(texto);
        
    } 
   
    public void set(JButton elemento){
        this.elemento = elemento;
    }

    @Override
    public Component get() {
       return elemento;
    }

    public JButton getElemento() {
        return elemento;
    }

    public void setElemento(JButton elemento) {
        this.elemento = elemento;
    }
    
    
    
}
