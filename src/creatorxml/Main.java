package creatorxml;

import AnalizadorFs.Estructura.DocumentoFs;
import AnalizadorFs.Estructura.NodoArbol;
import AnalizadorGxml.ErrorEjecucion;
import AnalizadorGxml.Estructura.Documento;
import AnalizadorGxml.scannerGxml;
import AnalizadorGxml.sintacticoGxml;

import AnalizadorFs.scannerFs;
import AnalizadorFs.sintacticoFs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import jsyntaxpane.DefaultSyntaxKit;
import jsyntaxpane.actions.CaretMonitor;

/**
 *
 * @author IvanAlfonso
 */
public class Main extends javax.swing.JFrame {

    /**
     * Creates new form intefaz
     */
    public static ArrayList<ErrorEjecucion> errores = new ArrayList();

    /* representa un documento abierto */
    pestania pestania = new pestania();
    /* Metodo para abrir archivos */
    ArrayList<pestania> listaPestanias = new ArrayList();
    //public String dirProyecto = "";
    Thread HiloEjecucion;

    public Main() {
        /* inicializamos los componentes */
        initComponents();
        /* cargamos el editor */
        cargar_editor();
        listaPestanias.add(pestania);
    }

    /* crea y muestra los componentes del editor  */
    private void cargar_editor() {
        /* llamada a la libreria que da estilo a los archivos */
        DefaultSyntaxKit.initKit();
        /* creamos un archivo por default */
        pestania.textPane = new JEditorPane();
        /* Mostramos el primer elemento de la lista */
        JScrollPane scrollPane = new JScrollPane(pestania.textPane);
        /* Agregamos el panel al contenedor */
        contentPane.add(scrollPane, "Archvio Gxml");
        /* asignamos estilo a nuestro aarchvo abierto */
        pestania.textPane.setContentType("text/xml");
        pestania.dirProyecto = new File("").getAbsolutePath();
        pestania.tipoArchivo = "Gxml";

        /* creacion de todos los efectos del editor */
        CaretMonitor caretMonitor
                = new jsyntaxpane.actions.CaretMonitor(pestania.textPane, lblCaretPos);

    }

    public void compilarFs() {
        System.out.println("tam:" + contentPane.getSelectedIndex());
        if (!"".equals(listaPestanias.get(contentPane.getSelectedIndex()).textPane.getText())) {
            try {
                scannerFs lexicoG = new scannerFs(new BufferedReader(new StringReader(listaPestanias.get(contentPane.getSelectedIndex()).textPane.getText())));
                sintacticoFs sintactico = new sintacticoFs(lexicoG);
                ErrorEjecucion err = new ErrorEjecucion();
                sintactico.parse();
                NodoArbol doc = sintactico.getNodoRaiz();
                if (doc != null) {
                    DocumentoFs fs = new DocumentoFs(doc);
                    fs.setDireccionDocumento(listaPestanias.get(contentPane.getSelectedIndex()).dirProyecto);
                    //doc.setDireccionDocumento(listaPestañas.get(contentPane.getSelectedIndex()).dirProyecto);
                    //doc.compilar();
                    fs.ejecutarDocumento();
                } else {
                    err.printTablaSimbolos(Main.errores);
                    Main.errores = new ArrayList<>();
                }

            } catch (Exception ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            /* indicamos que no hay contenido a evaluar */
            JOptionPane.showMessageDialog(null,
                    "No hay caden apara analizar !!",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    public void compilar() {
        /* inicio del analisis */
        System.out.println("tam:" + contentPane.getSelectedIndex());
        System.out.println(listaPestanias.get(contentPane.getSelectedIndex()).dirProyecto);

        if (!"".equals(listaPestanias.get(contentPane.getSelectedIndex()).textPane.getText())) {
            try {

                scannerGxml lexicoG = new scannerGxml(new BufferedReader(new StringReader(listaPestanias.get(contentPane.getSelectedIndex()).textPane.getText())));
                sintacticoGxml sintactico = new sintacticoGxml(lexicoG);
                ErrorEjecucion err = new ErrorEjecucion();
                sintactico.parse();
                Documento doc = sintactico.getDocumento();
                doc.setDireccionDocumento(listaPestanias.get(contentPane.getSelectedIndex()).dirProyecto);
                doc.compilar();

                if (Main.errores.isEmpty()) {
                    doc.generarFS();
                }
                err.printTablaSimbolos(Main.errores);
                Main.errores = new ArrayList<>();
            } catch (FileNotFoundException ex) {
                //cuando no existe el archivo
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                //error del analizador
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            /* indicamos que no hay contenido a evaluar */
            JOptionPane.showMessageDialog(null,
                    "No hay cadena para analizar!!",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
        }

    }

    private void abrirArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        }
        File name = fileChooser.getSelectedFile();

        listaPestanias.get(contentPane.getSelectedIndex()).archivo = name;
        contentPane.setSelectedIndex(listaPestanias.size() - 1);
        listaPestanias.get(contentPane.getSelectedIndex()).dirProyecto = fileChooser.getCurrentDirectory().getAbsolutePath();
        if (name.exists()) {
            if (name.isFile()) {
                try {
                    BufferedReader input;
                    input = new BufferedReader(new FileReader(name));
                    StringBuilder buffer = new StringBuilder();
                    String text;
                    listaPestanias.get(contentPane.getSelectedIndex()).textPane.setText("");
                    while ((text = input.readLine()) != null) {
                        buffer.append(text).append("\n");
                    }
                    listaPestanias.get(contentPane.getSelectedIndex()).textPane.setText(buffer.toString());

                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null,
                            "Error en el archivo",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else if (name.isDirectory()) {
                String directory[] = name.list();
                listaPestanias.get(contentPane.getSelectedIndex()).textPane.setText("\n\nContenido del directorio:\n");
                for (String directory1 : directory) {
                    listaPestanias.get(contentPane.getSelectedIndex()).textPane.setText(directory1 + "\n");
                }
            } else {
                JOptionPane.showMessageDialog(null,
                        " No existe ", " Error ",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /* Metodo para crear un archivo */
    private void nuevoArchivo() {
        String[] values = {"Gxml", "FucionScript"};
        String t = "";

        Object selected = JOptionPane.showInputDialog(null, "¿Tipo de Archivo?", "Seleccione ...", JOptionPane.DEFAULT_OPTION, null, values, "0");
        if (selected != null) {//null if the user cancels. 
            String selectedString = selected.toString();
            if (selected.toString().equals("Gxml")) {
                t = "xml";
            } else {
                t = "javascript";
            }
            System.out.println(selectedString);

            pestania p = new pestania();
            p.dirProyecto = new File("").getAbsolutePath();
            p.textPane = new JEditorPane();
            JScrollPane scrollPane = new JScrollPane(p.textPane);
            contentPane.add(scrollPane, "Archvio " + selectedString);
            p.textPane.setContentType("text/" + t);
            CaretMonitor caretMonitor
                    = new jsyntaxpane.actions.CaretMonitor(p.textPane, lblCaretPos);
            p.tipoArchivo = selectedString;
            listaPestanias.add(p);

            contentPane.setSelectedIndex(listaPestanias.size() - 1);
        } else {
            System.out.println("User cancelled");
        }
    }

    /* Metodo que guardarComo un archivo por primera vez */
    private void guardarComo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        }
        File name = fileChooser.getSelectedFile();
        listaPestanias.get(contentPane.getSelectedIndex()).archivo = name;
        try {
            try (PrintWriter output = new PrintWriter(new FileWriter(name))) {
                output.write(listaPestanias.get(contentPane.getSelectedIndex()).textPane.getText());
            }
        } catch (IOException ioException) {
            JOptionPane.showMessageDialog(null,
                    "Error en el archivo", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /* Metodo que gurda los cambios de un archvio */
    private void guardar() {
        if (listaPestanias.get(contentPane.getSelectedIndex()).archivo == null) {
            guardarComo();
        } else {
            try {
                try (PrintWriter output = new PrintWriter(
                        new FileWriter(listaPestanias.get(contentPane.getSelectedIndex()).archivo.getName()))) {
                    output.write(listaPestanias.get(contentPane.getSelectedIndex()).textPane.getText());
                }
            } catch (IOException ioException) {
                JOptionPane.showMessageDialog(null,
                        "Error en el archivo",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /*........................................................................*/
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        toolsBar1 = new javax.swing.JToolBar();
        lblCaretPos = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        toolsBar = new javax.swing.JToolBar();
        btnNuevo = new javax.swing.JButton();
        btnAbrir = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jLabel1 = new javax.swing.JLabel();
        btnRunWithTable = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        jSplitPane1 = new javax.swing.JSplitPane();
        contentPane = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        consola = new javax.swing.JTextArea();
        menuBar = new javax.swing.JMenuBar();
        menuArchivo = new javax.swing.JMenu();
        itemAbrir = new javax.swing.JMenuItem();
        itemNuevo = new javax.swing.JMenuItem();
        itemGuardar = new javax.swing.JMenuItem();
        itemGuardarComo = new javax.swing.JMenuItem();
        separador = new javax.swing.JPopupMenu.Separator();
        itemSalir = new javax.swing.JMenuItem();
        menuEdicion = new javax.swing.JMenu();
        itemCopiar = new javax.swing.JMenuItem();
        itemCortar = new javax.swing.JMenuItem();
        itemPegar = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("CreatorXML");

        toolsBar1.setRollover(true);

        lblCaretPos.setText("PosCursor");
        toolsBar1.add(lblCaretPos);

        jLabel5.setText("               ");
        toolsBar1.add(jLabel5);

        toolsBar.setFloatable(false);

        btnNuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imgs/tools/nuevo.png"))); // NOI18N
        btnNuevo.setToolTipText("Nuevo archivo");
        btnNuevo.setFocusable(false);
        btnNuevo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNuevo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoActionPerformed(evt);
            }
        });
        toolsBar.add(btnNuevo);

        btnAbrir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imgs/tools/abrir.png"))); // NOI18N
        btnAbrir.setToolTipText("Abrir archivo");
        btnAbrir.setFocusable(false);
        btnAbrir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAbrir.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbrirActionPerformed(evt);
            }
        });
        toolsBar.add(btnAbrir);

        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imgs/tools/guardar.png"))); // NOI18N
        btnGuardar.setToolTipText("Guardar cambios");
        btnGuardar.setFocusable(false);
        btnGuardar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGuardar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });
        toolsBar.add(btnGuardar);
        toolsBar.add(jSeparator2);

        jLabel1.setText("Compilar");
        toolsBar.add(jLabel1);

        btnRunWithTable.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imgs/tools/ejecutar.png"))); // NOI18N
        btnRunWithTable.setToolTipText("Compilar\n");
        btnRunWithTable.setFocusable(false);
        btnRunWithTable.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRunWithTable.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRunWithTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRunWithTableActionPerformed(evt);
            }
        });
        toolsBar.add(btnRunWithTable);
        toolsBar.add(jSeparator4);

        jSplitPane1.setDividerLocation(450);
        jSplitPane1.setDividerSize(10);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setOneTouchExpandable(true);
        jSplitPane1.setTopComponent(contentPane);

        consola.setColumns(20);
        consola.setRows(5);
        jScrollPane1.setViewportView(consola);

        jSplitPane1.setRightComponent(jScrollPane1);

        menuArchivo.setText("Archivo");

        itemAbrir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        itemAbrir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imgs/menu/abrir.png"))); // NOI18N
        itemAbrir.setText("Abrir Archivo");
        itemAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemAbrirActionPerformed(evt);
            }
        });
        menuArchivo.add(itemAbrir);

        itemNuevo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        itemNuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imgs/menu/nuevo.png"))); // NOI18N
        itemNuevo.setText("Nuevo Archivo");
        itemNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemNuevoActionPerformed(evt);
            }
        });
        menuArchivo.add(itemNuevo);

        itemGuardar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_MASK));
        itemGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imgs/menu/guardar.png"))); // NOI18N
        itemGuardar.setText("Guardar Archivo");
        itemGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemGuardarActionPerformed(evt);
            }
        });
        menuArchivo.add(itemGuardar);

        itemGuardarComo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imgs/menu/guardar_como.png"))); // NOI18N
        itemGuardarComo.setText("Guardar como");
        itemGuardarComo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemGuardarComoActionPerformed(evt);
            }
        });
        menuArchivo.add(itemGuardarComo);
        menuArchivo.add(separador);

        itemSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imgs/menu/salir.png"))); // NOI18N
        itemSalir.setText("Salir");
        itemSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemSalirActionPerformed(evt);
            }
        });
        menuArchivo.add(itemSalir);

        menuBar.add(menuArchivo);

        menuEdicion.setText("Edición");

        itemCopiar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imgs/menu/copiar.png"))); // NOI18N
        itemCopiar.setText("Copiar");
        itemCopiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemCopiarActionPerformed(evt);
            }
        });
        menuEdicion.add(itemCopiar);

        itemCortar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imgs/menu/cortar.png"))); // NOI18N
        itemCortar.setText("Cortar");
        itemCortar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemCortarActionPerformed(evt);
            }
        });
        menuEdicion.add(itemCortar);

        itemPegar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imgs/menu/pegar.png"))); // NOI18N
        itemPegar.setText("Pegar");
        itemPegar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemPegarActionPerformed(evt);
            }
        });
        menuEdicion.add(itemPegar);

        menuBar.add(menuEdicion);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolsBar, javax.swing.GroupLayout.DEFAULT_SIZE, 791, Short.MAX_VALUE)
            .addComponent(toolsBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(toolsBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 608, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(toolsBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        setSize(new java.awt.Dimension(809, 746));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void itemAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemAbrirActionPerformed
        abrirArchivo();

    }//GEN-LAST:event_itemAbrirActionPerformed

    private void itemNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemNuevoActionPerformed
        nuevoArchivo();
    }//GEN-LAST:event_itemNuevoActionPerformed

    private void itemGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemGuardarActionPerformed
        guardar();
    }//GEN-LAST:event_itemGuardarActionPerformed

    private void itemGuardarComoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemGuardarComoActionPerformed

        guardarComo();

    }//GEN-LAST:event_itemGuardarComoActionPerformed

    private void itemSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemSalirActionPerformed
        System.exit(0);
    }//GEN-LAST:event_itemSalirActionPerformed

    private void itemCopiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemCopiarActionPerformed
        listaPestanias.get(contentPane.getSelectedIndex()).textPane.copy();
    }//GEN-LAST:event_itemCopiarActionPerformed

    private void itemCortarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemCortarActionPerformed
        listaPestanias.get(contentPane.getSelectedIndex()).textPane.cut();
    }//GEN-LAST:event_itemCortarActionPerformed

    private void itemPegarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemPegarActionPerformed
        listaPestanias.get(contentPane.getSelectedIndex()).textPane.paste();
    }//GEN-LAST:event_itemPegarActionPerformed

    private void btnRunWithTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRunWithTableActionPerformed
        if (listaPestanias.get(contentPane.getSelectedIndex()).tipoArchivo.equals("Gxml")) {
            compilar();
        } else if (listaPestanias.get(contentPane.getSelectedIndex()).tipoArchivo.equals("FucionScript")) {
            compilarFs();
        }
    }//GEN-LAST:event_btnRunWithTableActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        guardar();
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbrirActionPerformed
        abrirArchivo();
    }//GEN-LAST:event_btnAbrirActionPerformed

    private void btnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoActionPerformed
        nuevoArchivo();
    }//GEN-LAST:event_btnNuevoActionPerformed

    boolean banderaActivado = false;

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new Main().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAbrir;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JButton btnRunWithTable;
    public static javax.swing.JTextArea consola;
    private javax.swing.JTabbedPane contentPane;
    private javax.swing.JMenuItem itemAbrir;
    private javax.swing.JMenuItem itemCopiar;
    private javax.swing.JMenuItem itemCortar;
    private javax.swing.JMenuItem itemGuardar;
    private javax.swing.JMenuItem itemGuardarComo;
    private javax.swing.JMenuItem itemNuevo;
    private javax.swing.JMenuItem itemPegar;
    private javax.swing.JMenuItem itemSalir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JLabel lblCaretPos;
    private javax.swing.JMenu menuArchivo;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu menuEdicion;
    private javax.swing.JPopupMenu.Separator separador;
    private javax.swing.JToolBar toolsBar;
    private javax.swing.JToolBar toolsBar1;
    // End of variables declaration//GEN-END:variables

}
