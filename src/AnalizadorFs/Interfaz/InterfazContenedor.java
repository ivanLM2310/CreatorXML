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
public class InterfazContenedor extends ComponenteGenerico {

    String color;
    ArrayList<ObjInterfaz> contenido;
    JPanel panelPrincipal;
    boolean borde;

    public InterfazContenedor(int ancho,int alto, String color, boolean borde, int x, int y) {
        contenido = new ArrayList();
        this.alto = alto;
        this.ancho = ancho;
        this.color = color;
        this.borde = borde;
        this.x = x;
        this.y = y;
        this.elemento = crearElemento();
    }
    private Component crearElemento(){
        JPanel p = new JPanel(null);
        panelPrincipal = p;
        p.setBackground(Color.decode(color));
        p.setBounds(0, 0, ancho, alto);
        p.setPreferredSize(new Dimension(10, 10));
        JScrollPane scrollP = new JScrollPane();
        scrollP.setBounds(x, y, ancho, alto);
        scrollP.add(p);
        scrollP.setViewportView(p);
        scrollP.setPreferredSize(new Dimension(1000, 1000));
        scrollP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); //SETTING SCHEME FOR HORIZONTAL BAR
        scrollP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        return scrollP;
    }
    
    public void add(ObjInterfaz item,Component comp){
        contenido.add(item);
        panelPrincipal.add(comp);
        //arreglar tamaños
        Component[] lista = panelPrincipal.getComponents();
        int xMax = 0;
        int yMax = 0;
        for(Component itemC: lista){
            int xtam = itemC.getX()+ itemC.getWidth()+10;
            int ytam = itemC.getY()+ itemC.getHeight()+10;
            xMax = (xtam > xMax)?xtam:xMax; 
            yMax = (ytam > yMax)?ytam:yMax; 
        }
        panelPrincipal.setPreferredSize(new Dimension(xMax, yMax));
        
    }
    
    

}
