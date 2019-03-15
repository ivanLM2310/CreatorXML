/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorFs.Interfaz;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author ivanl
 */
public class InterfazVentana extends ComponenteGenerico  {

    String color;
    ArrayList<InterfazContenedor> contenido;
    JPanel panelPrincipal ;
    
    public InterfazVentana(String color, int alto, int ancho, String id) {
        contenido = new ArrayList();
        this.alto = alto;
        this.ancho = ancho;
        this.id = id;
        this.color = color;
        this.elemento = crearElemento();
    }
 
    private Component crearElemento(){
        JFrame formulario = new JFrame();
        Dimension a = new Dimension( ancho,alto);
        formulario.setPreferredSize(a);
        formulario.setSize(a);
        formulario.setVisible(true);
        
        JPanel panelP = new JPanel(null);
        panelPrincipal = panelP;
        panelP.setBackground(Color.decode(color));
        panelP.setPreferredSize(new Dimension(100, 100));
        JScrollPane scroll = new JScrollPane(); 
        scroll.setViewportView(panelP);
        formulario.add(scroll);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); //SETTING SCHEME FOR HORIZONTAL BAR
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        return formulario;
    }
    
    public void add(InterfazContenedor item){
        contenido.add(item);
        panelPrincipal.add(item.get());
        
    }
    
    
    public void redimencionar(){
        int xMax = 0;
        int yMax = 0;
        for(InterfazContenedor item: contenido){
            int xtam = item.elemento.getX()+ item.elemento.getWidth()+10;
            int ytam = item.elemento.getY()+ item.elemento.getHeight()+10;
            xMax = (xtam > xMax)?xtam:xMax; 
            yMax = (ytam > yMax)?ytam:yMax; 
        }
        panelPrincipal.setPreferredSize(new Dimension(xMax, yMax));
    }
}
