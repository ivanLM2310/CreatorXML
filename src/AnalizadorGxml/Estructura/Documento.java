/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorGxml.Estructura;

import AnalizadorFs.Estructura.ObjetoGxml;
import AnalizadorGxml.ErrorEjecucion;
import AnalizadorGxml.scannerGxml;
import AnalizadorGxml.sintacticoGxml;
import creatorxml.Main;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ivanl
 */
public class Documento {

    public ArrayList<EtiquetaImportar> archivosImportados;
    public ArrayList<EtiquetaVentana> ventanas;

    public ArrayList<Documento> documentosImportados;
    String direccionDocumento = "";

    public Documento() {
        archivosImportados = new ArrayList();
        ventanas = new ArrayList();
    }

    public Documento(File archivo, String dirProyecto) {
        archivosImportados = new ArrayList();
        ventanas = new ArrayList();
        leerGxml(archivo, dirProyecto);
    }

    private void leerGxml(File archivo, String dirProyecto) {
        try {

            scannerGxml lexicoG = new scannerGxml(new BufferedReader(new FileReader(archivo)));
            sintacticoGxml sintactico = new sintacticoGxml(lexicoG);
            ErrorEjecucion err = new ErrorEjecucion();
            sintactico.parse();
            Documento doc = sintactico.getDocumento();
            doc.setDireccionDocumento(dirProyecto);
            doc.compilar();

            if (!Main.errores.isEmpty()) {
                //doc.generarFS();
                err.printTablaSimbolos(Main.errores);
                Main.errores = new ArrayList<>();
            }

        } catch (FileNotFoundException ex) {
            //cuando no existe el archivo
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            //error del analizador
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getDireccionDocumento() {
        return direccionDocumento;
    }

    public void setDireccionDocumento(String direccionDocumento) {
        this.direccionDocumento = direccionDocumento;
    }

    public ArrayList<EtiquetaImportar> getArchivosImportados() {
        return archivosImportados;
    }

    public void setArchivosImportados(ArrayList<EtiquetaImportar> archivosImportados) {
        this.archivosImportados = archivosImportados;
    }

    public ArrayList<EtiquetaVentana> getVentanas() {
        return ventanas;
    }

    public void setVentanas(ArrayList<EtiquetaVentana> ventanas) {
        this.ventanas = ventanas;
    }

    public void addListaVentana(ArrayList<EtiquetaVentana> ventanas) {
        ventanas.forEach((v) -> {
            this.addVentana(v);
        });
    }

    public void addImportados(Etiqueta archivo) {
        archivosImportados.add((EtiquetaImportar) archivo);
    }

    public void addVentana(Etiqueta ventana) {
        ventanas.add((EtiquetaVentana) ventana);
    }

    public void compilar() {
        for (EtiquetaImportar e : archivosImportados) {
            if (e.generarCodigo(direccionDocumento).equals("gxml")) {
                Documento docResultado = compilarTodo(direccionDocumento + e.getTexto());
                if (docResultado != null) {
                    documentosImportados.add(docResultado);
                }
            }
        }
    }

    private Documento compilarTodo(String dir) {

        String texto = "";
        boolean estado = true;
        try {
            FileReader lector = new FileReader(dir);
            BufferedReader contenido = new BufferedReader(lector);
            String linea = "";
            while ((linea = contenido.readLine()) != null) {
                texto += (!texto.isEmpty()) ? "\n" + linea : linea;
            }
        } catch (IOException e) {
            estado = false;
            System.out.println("Error al leer");
        }
        Documento doc = null;
        if (estado) {
            try {
                scannerGxml lexicoG = new scannerGxml(new BufferedReader(new StringReader(texto)));
                sintacticoGxml sintactico = new sintacticoGxml(lexicoG);
                ErrorEjecucion err = new ErrorEjecucion();
                sintactico.parse();
                doc = sintactico.getDocumento();
                doc.setDireccionDocumento(direccionDocumento);
                doc.compilar();
            } catch (Exception ex) {
                Logger.getLogger(Documento.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return doc;

    }

    public void generarFS() {

        String s1 = generarEncabezadoFs();
        s1 += generarCuerpoFs();
        try {
            File archivo = new File(direccionDocumento+"\\ResultadoTraduccion.fs");
            try (FileWriter escribir = new FileWriter(archivo, false)) {
                escribir.write(s1);
            }
        } catch (IOException e) {
            System.out.println("Error al escribir");
        }
    }

    private String generarCuerpoFs() {
        String s1 = "";
        for (EtiquetaVentana ventana : ventanas) {
            String linea = ventana.generarCodigo("");
            String id = ventana.salidaConversion(Constantes.atb_id, "");
            String contenidoV = gen(id, ventana);
            s1 += linea + contenidoV;
        }

        for (Documento doc : documentosImportados) {

            s1 += doc.generarCuerpoFs();
        }
        return s1;
    }

    private String generarEncabezadoFs() {
        String s1 = "";
        for (EtiquetaImportar imp : archivosImportados) {
            String linea = imp.generarCodigo(direccionDocumento);
            s1 += (!linea.equals("gxml") && !linea.equals("i")) ? linea + "\n" : "";
        }

        for (Documento doc : documentosImportados) {

            s1 += doc.generarEncabezadoFs();
        }
        return s1;
    }

    public void unirDocumentos(Documento doc) {
        for (EtiquetaVentana v : doc.ventanas) {

        }
    }

    public String gen(String nombreV, Etiqueta etq) {
        String salida = "";

        for (Etiqueta ventana : etq.getContenido()) {
            if (ventana instanceof EtiquetaDato) {
            } else if (ventana instanceof EtiquetaListaDatos) {
            } else if (ventana instanceof EtiquetaDefecto) {
            } else {
                String linea = ventana.generarCodigo(nombreV);
                salida += linea;
                String resultado = gen(nombreV, ventana);
                if (!resultado.equals("")) {
                    salida += resultado;
                }
            }
        }

        return salida;
    }
}
