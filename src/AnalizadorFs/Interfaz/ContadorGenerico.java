/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorFs.Interfaz;

import java.awt.Component;
import javax.swing.JSpinner;

/**
 *
 * @author ivanl
 */
public class ContadorGenerico extends ComponenteGenerico{
  
    
    JSpinner elemento;

    
    public ContadorGenerico(int numero){
        elemento = new JSpinner();
        elemento.setValue(numero);
    }
    @Override
    public Component get() {
        return elemento;
    }

    public JSpinner getElemento() {
        return elemento;
    }

    public void setElemento(JSpinner elemento) {
        this.elemento = elemento;
    }
    
    
    
}
