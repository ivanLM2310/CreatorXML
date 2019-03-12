/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorFs.Estructura;

import java.awt.Component;

/**
 *
 * @author ivanl
 */
public class ObjInterfaz {

    Component elemento;
    int x;
    int y;
    int alto;
    int ancho;

    public Component get() {
        return elemento;
    }

    public void set(Component elemento) {
        this.elemento = elemento;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getAlto() {
        return alto;
    }

    public void setAlto(int alto) {
        this.alto = alto;
    }

    public int getAncho() {
        return ancho;
    }

    public void setAncho(int ancho) {
        this.ancho = ancho;
    }
    
    public void crearContenedor(){
    
    }
    

}
