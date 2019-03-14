/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorFs.Interfaz;


import java.awt.Component;
import java.util.LinkedList;
import javax.swing.JPanel;
import javax.swing.JSpinner;

/**
 *
 * @author ivanl
 */
public class PanelGenerico extends  ComponenteGenerico {

    int posActX, PosActY, tamañoYMax, tamañoXMax = 0;


    public LinkedList<ComponenteGenerico> componentes = new LinkedList<>();

    
    public PanelGenerico() {
        JPanel p = (JPanel) get(); 
        p.setLayout(null);
    }

    public void setPosActX(int posActX) {
        this.posActX = posActX;
    }

    public int getPosActY() {
        return PosActY;
    }

    public void setPosActY(int PosActY) {
        this.PosActY = PosActY;
    }

    public int getTamañoYMax() {
        return tamañoYMax;
    }

    public void setTamañoYMax(int tamañoYMax) {
        this.tamañoYMax = tamañoYMax;
    }

    public LinkedList<ComponenteGenerico> getComponentes() {
        return componentes;
    }

    public void setComponentes(LinkedList<ComponenteGenerico> componentes) {
        this.componentes = componentes;
    }

    @Override
    public Component get() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public JSpinner getElemento() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


}
