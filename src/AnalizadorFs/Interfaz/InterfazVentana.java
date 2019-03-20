/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorFs.Interfaz;

import AnalizadorFs.Estructura.EjecutarFs;
import AnalizadorFs.Estructura.NodoArbol;
import AnalizadorFs.Estructura.TablaAmbientes;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author ivanl
 */
public class InterfazVentana extends ComponenteGenerico {

    String color;
    ArrayList<InterfazContenedor> contenido;
    JPanel panelPrincipal;

    EjecutarFs instanciaFs = null;
    TablaAmbientes ambientes = null;
    NodoArbol raiz = null;

    public InterfazVentana(String color, int alto, int ancho, String id) {
        contenido = new ArrayList();
        this.alto = alto;
        this.ancho = ancho;
        this.id = id;
        this.color = color;
        this.elemento = crearElemento();
        addEventosventana();

    }

    private Component crearElemento() {
        JFrame formulario = new JFrame();
        Dimension a = new Dimension(ancho, alto);
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

    public void add(InterfazContenedor item) {
        contenido.add(item);
        panelPrincipal.add(item.get());
        redimencionar();
    }

    public void show() {
        elemento.setVisible(true);
    }

    public void redimencionar() {
        int xMax = 0;
        int yMax = 0;
        Component[] lista = panelPrincipal.getComponents();
        for (Component item : lista) {
            int xtam = item.getX() + item.getWidth() + 10;
            int ytam = item.getY() + item.getHeight() + 10;
            xMax = (xtam > xMax) ? xtam : xMax;
            yMax = (ytam > yMax) ? ytam : yMax;
        }
        panelPrincipal.setPreferredSize(new Dimension(xMax, yMax));
    }

    private String getGdato() {
        String cad = "";
        int tam = contenido.size();
        for (int i = 0; i < tam; i++) {
            cad += (i > 0) ? "\n" + contenido.get(i).getGdato() : contenido.get(i).getGdato();
        }
        return cad;
    }

    public void escribirGdato() {
        String texto = "";
        if (new File(id + ".gdato").exists()) {

            try {
                FileReader lector = new FileReader(id + ".gdato");
                BufferedReader contenidoA = new BufferedReader(lector);
                String temp = "";
                while ((temp = contenidoA.readLine()) != null) {
                    texto = (!texto.isEmpty()) ? "\n" + temp : temp;
                }
            } catch (IOException e) {
                System.out.println("Error al leer");
            }
        }
        String gdato = getGdato();
        if (texto.isEmpty()) {
            gdato = "<lista><principal>" + gdato + "</principal></lista>";
        } else {
            gdato = texto.replaceAll("</lista>", "<principal>" + gdato + "</principal></lista>");
        }

        try {
            File archivo = new File(id + ".gdato");
            FileWriter escribir = new FileWriter(archivo, false);
            escribir.write(gdato);
            escribir.close();
        } catch (IOException e) {
            System.out.println("Error al escribir");
        }
    }

    public void alCargar(EjecutarFs instanciaFs, TablaAmbientes ambientes, NodoArbol raiz) {
        this.instanciaFs = instanciaFs;
        this.ambientes = ambientes;
        this.raiz = raiz;
    }

    public void alCerrar(EjecutarFs instanciaFs, TablaAmbientes ambientes, NodoArbol raiz) {
        this.instanciaFs = instanciaFs;
        this.ambientes = ambientes;
        this.raiz = raiz;
    }
    
    public void alCargar() {
        ((JFrame) elemento).setVisible(true);
    }

    public void alCerrar() {
        ((JFrame) elemento).dispose();
    }

    private void addEventosventana() {

        ((JFrame) elemento).addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent we) {
                if (instanciaFs != null && raiz != null && ambientes != null) {
                    instanciaFs.evaluarExp(raiz, ambientes);
                }
            }

            @Override
            public void windowActivated(WindowEvent we) {
                if (instanciaFs != null && raiz != null && ambientes != null) {
                    instanciaFs.evaluarExp(raiz, ambientes);
                }
            }
        });

    }
}
