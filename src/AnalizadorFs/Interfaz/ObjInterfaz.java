/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorFs.Interfaz;

import AnalizadorFs.Estructura.ConstantesFs;
import AnalizadorFs.Estructura.EjecutarFs;
import AnalizadorFs.Estructura.NodoArbol;
import AnalizadorFs.Estructura.TablaAmbientes;
import AnalizadorFs.Estructura.Valor;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author ivanl
 */
public class ObjInterfaz extends ComponenteGenerico {

    public ObjInterfaz() {

    }

    public Component crearTexto(String strFuente, int tamaño, String color, int x, int y, boolean negrilla, boolean cursiva, String Valor) {
        this.x = x;
        this.y = y;
        this.etiquetaTipo = ConstantesFs.INTERFAZ_TEXTO;

        JLabel l = new JLabel();
        l.setText(Valor);
        int valTip = 0;
        if (negrilla && cursiva) {
            valTip = 3;
        } else if (negrilla) {
            valTip = 1;
        } else if (cursiva) {
            valTip = 2;
        }

        Font fuente = new Font(strFuente, valTip, tamaño);
        l.setFont(fuente);
        l.setForeground(Color.decode(color));
        Dimension dimL = l.getPreferredSize();
        this.alto = dimL.height;
        this.ancho = dimL.width;
        l.setBounds(x, y, dimL.width, dimL.height);
        elemento = l;
        return l;
    }

    public Component crearCajaTexto(int alto, int ancho, String strFuente, int tamaño, String color, int x, int y, boolean negrilla, boolean cursiva, String defecto, String nombre) {
        this.x = x;
        this.y = y;
        this.id = nombre;
        this.etiquetaTipo = ConstantesFs.INTERFAZ_CAJATEXTO;

        JTextField l = new JTextField();
        l.setText(defecto);
        int valTip = 0;
        if (negrilla && cursiva) {
            valTip = 3;
        } else if (negrilla) {
            valTip = 1;
        } else if (cursiva) {
            valTip = 2;
        }

        Font fuente = new Font(strFuente, valTip, tamaño);
        l.setFont(fuente);
        l.setBackground(Color.decode(color));
        Dimension dimL = l.getPreferredSize();
        this.alto = alto;
        this.ancho = ancho;
        l.setBounds(x, y, ancho, alto);
        elemento = l;
        return l;
    }

    public Component crearAreaTexto(int alto, int ancho, String strFuente, int tamaño, String color, int x, int y, boolean negrilla, boolean cursiva, String defecto, String nombre) {

        this.x = x;
        this.y = y;
        this.id = nombre;
        this.etiquetaTipo = ConstantesFs.INTERFAZ_AREATEXTO;

        JTextArea l = new JTextArea();

        JScrollPane scroll1 = new JScrollPane(l);
        l.setBounds(x, y, ancho, alto);
        scroll1.setBounds(x, y, ancho, alto);
        l.setText(defecto);
        l.setBackground(Color.decode(color));
        //l.setColumns(20);
        //l.setRows(5);
        int valTip = 0;
        if (negrilla && cursiva) {
            valTip = 3;
        } else if (negrilla) {
            valTip = 1;
        } else if (cursiva) {
            valTip = 2;
        }

        Font fuente = new Font(strFuente, valTip, tamaño);
        l.setFont(fuente);
        this.alto = alto;
        this.ancho = ancho;
        elemento = l;
        return scroll1;
    }

    public Component crearControNumerico(int alto, int ancho, int maximo, int minimo, int x, int y, int defecto, String nombre) {

        this.x = x;
        this.y = y;
        this.id = nombre;
        this.etiquetaTipo = ConstantesFs.INTERFAZ_CONTROLNUM;
        JSpinner l = new JSpinner();
        l.setModel(new javax.swing.SpinnerNumberModel(defecto, minimo, maximo, 1));
        this.alto = alto;
        this.ancho = ancho;
        l.setBounds(x, y, ancho, alto);
        elemento = l;
        return l;
    }

    public Component crearDesplegable(int alto, int ancho, ArrayList<Valor> lista, int x, int y, String defecto, String nombre) {
        this.x = x;
        this.y = y;
        this.id = nombre;
        this.etiquetaTipo = ConstantesFs.INTERFAZ_DESPLEGABLE;
        JComboBox l = new JComboBox();
        int numeroDefecto = 0;

        for (int i = 0; i < lista.size(); i++) {
            String val = lista.get(i).getString();
            if (val.equals(defecto)) {
                numeroDefecto = i;
            }
            l.addItem(lista.get(i).getString());
        }
        l.setSelectedIndex(numeroDefecto);
        this.alto = alto;
        this.ancho = ancho;
        l.setBounds(x, y, ancho, alto);
        elemento = l;
        return l;
    }

    NodoArbol alClic = null;
    NodoArbol alReferencia = null;
    EjecutarFs instanciaFs = null;
    TablaAmbientes ambientes = null;

    public void eventoClic(EjecutarFs instanciaFs, TablaAmbientes ambientes, NodoArbol raiz) {
        this.alClic = raiz;
        this.instanciaFs = instanciaFs;
        this.ambientes = ambientes;
    }
    
     public void eventoReferencia(EjecutarFs instanciaFs, TablaAmbientes ambientes, NodoArbol raiz) {
        this.alReferencia = raiz;
        this.instanciaFs = instanciaFs;
        this.ambientes = ambientes;
    }

    public Component crearBoton(String strFuente, int tamaño, String color, int x, int y, String valor, int alto, int ancho) {

        this.x = x;
        this.y = y;
        this.etiquetaTipo = ConstantesFs.INTERFAZ_BOTON;
        JButton l = new JButton();
        Font fuente = new Font(strFuente, 0, tamaño);
        l.setFont(fuente);
        l.setText(valor);
        l.setBackground(Color.decode(color));
        l.setBounds(x, y, ancho, alto);
        this.alto = alto;
        this.ancho = ancho;
        l.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (instanciaFs != null && ambientes != null) {
                    if (alClic != null) {
                        instanciaFs.evaluarExp(alClic, ambientes);
                    }
                    if (alReferencia != null) {
                        instanciaFs.evaluarExp(alReferencia, ambientes);
                    }
                }
            }
        });
        elemento = l;
        return l;
    }

    public PantallaVideo crearImagen(String ruta, int x, int y, int ancho, int alto) {
        this.x = x;
        this.y = y;
        this.alto = alto;
        this.ancho = ancho;
        PantallaVideo panelVideo = new PantallaVideo();
        //Dimension b = new Dimension(250, 250);
        panelVideo.setBounds(new java.awt.Rectangle(x, y, ancho, alto));
        panelVideo.setDireccion(ruta);
        elemento = panelVideo;
        return panelVideo;
    }

    public PantallaVideo crearReproductor(String ruta, int x, int y, boolean auto, int ancho, int alto) {
        this.x = x;
        this.y = y;
        this.alto = alto;
        this.ancho = ancho;

        PantallaVideo panelVideo = new PantallaVideo();
        //Dimension b = new Dimension(250, 250);
        panelVideo.setBounds(new java.awt.Rectangle(x, y, ancho, alto));
        panelVideo.setDireccion(ruta);
        elemento = panelVideo;
        return panelVideo;
    }

    public PantallaVideo crearVideo(String ruta, int x, int y, boolean auto, int ancho, int alto) {
        this.x = x;
        this.y = y;
        this.alto = alto;
        this.ancho = ancho;
        PantallaVideo panelVideo = new PantallaVideo();
        //Dimension b = new Dimension(250, 250);
        panelVideo.setBounds(new java.awt.Rectangle(x, y, ancho, alto));
        panelVideo.setDireccion(ruta);
        elemento = panelVideo;
        return panelVideo;
    }

    public void set(Component elemento) {
        this.elemento = elemento;
    }

    public String getInfoDato() {
        String textoCont = "";
        switch (etiquetaTipo) {
            case ConstantesFs.INTERFAZ_CAJATEXTO:
                textoCont = "\"" + ((JTextField) elemento).getText() + "\"";
                break;
            case ConstantesFs.INTERFAZ_AREATEXTO:
                textoCont = "\"" + ((JTextArea) elemento).getText() + "\"";
                break;
            case ConstantesFs.INTERFAZ_CONTROLNUM:
                textoCont = String.valueOf((Integer) ((JSpinner) elemento).getValue());
                break;
            case ConstantesFs.INTERFAZ_DESPLEGABLE:
                textoCont = "\"" + (String) ((JComboBox) elemento).getSelectedItem() + "\"";
                break;

        }
        if (!textoCont.isEmpty()) {
            return "<" + id + ">" + textoCont + "</" + id + ">";
        }
        return "";
    }
}
