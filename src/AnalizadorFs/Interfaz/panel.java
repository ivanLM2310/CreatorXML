/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorFs.Interfaz;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.LinkedList;
import javax.swing.JPanel;

/**
 *
 * @author ivanl
 */
public class panel extends ComponenteGenerico {

    public int actualX, actualY = 0;

    int posActX, PosActY, tamañoYMax, tamañoXMax = 0;
    JPanel elemento;

    public LinkedList<ComponenteGenerico> componentes = new LinkedList<>();

    LinkedList<JPanel> filas;
    JPanel filaActual;

    public panel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        //panel.setBackground(Color.green);
        elemento = panel;

        filas = new LinkedList<>();

        addFila();

        generarPanel(0, 30);
    }

    public panel(int val) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        //panel.setBackground(Color.green);
        elemento = panel;

        filas = new LinkedList<>();

        addFila();

        //generarPanel(0, 10);
    }
    
    public void initLimpiado(){
        addFila();
        generarPanel(0, 30);
    }

    private void addFila() {
        filaActual = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        filaActual.setBackground(this.elemento.getBackground());
        //filaActual.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        filaActual.setVisible(true);

        filas.add(filaActual);
        addPanelFila(filaActual);
    }

    private void addPanelFila(Component panelFila) {

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;
        constraints.weightx = 0.5;
        constraints.weighty = 0.5;

        constraints.gridx = actualX;
        constraints.gridy = actualY;

        constraints.fill = GridBagConstraints.BOTH;

        //hacer mas grande el panel
        elemento.add(panelFila, constraints);
        System.out.println("fue ingresado fila ........................");

    }

    public void addEnter() {
        actualX = 0;
        actualY += 1;
        System.out.println(actualY);
        addFila();
        //hacer mas grande el panel
    }

    public Component add(ComponenteGenerico comp) {

        filaActual.add(comp.get());
        return comp.get();
    }

    public LinkedList<ComponenteGenerico> getComponentes() {
        return componentes;
    }

    @Override
    public Component get() {
        return elemento;
    }

    public JPanel getElemento() {
        return elemento;
    }

    private void generarPanel(int ancho, int alto) {

        for (int i = 0; i < alto + ancho; i++) {
            if (i < ancho) {
                AreaTextoGenerico label = new AreaTextoGenerico("");
                addPanelFila(label.get());
                continue;
            }
            actualX = 0;
            actualY += 1;
            AreaTextoGenerico label = new AreaTextoGenerico("");
            addPanelFila(label.get());
        }
        actualX = 0;
        actualY = 0;
    }

    public void cambiarColor(Color color) {
        elemento.setBackground(color);
        filas.forEach((fila) -> {
            fila.setBackground(color);
        });
    }

}
