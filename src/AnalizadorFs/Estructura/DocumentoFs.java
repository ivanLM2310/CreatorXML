/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorFs.Estructura;

import AnalizadorFs.scannerFs;
import AnalizadorFs.sintacticoFs;
import AnalizadorGxml.ErrorEjecucion;
import creatorxml.Main;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ivanl
 */
public class DocumentoFs {

    ArrayList<NodoArbol> documentos;
    String direccion = "";

    public DocumentoFs(NodoArbol raiz, String direccion) {
        this.documentos = new ArrayList();
        this.direccion = direccion;
        addDocumento(raiz);
    }

    public void ejecutarDocumento() {
        EjecutarFs e = new EjecutarFs(this);
        e.iniciarEjecucion();
    }

    private void addDocumento(NodoArbol nodo) {

        documentos.add(nodo);

        NodoArbol nodoImportar = nodo.BuscarNodo(ConstantesFs.LISTA_IMPORTAR);

        if (nodoImportar == null) {
            return;
        }

        for (NodoArbol nImport : nodoImportar.getHijosNodo()) {

            String ruta = nImport.valor.trim();
            if (ruta.length() > 0) {
                ruta = (ruta.charAt(0) != '\'' && ruta.charAt(0) != '/') ? "\\" + ruta : ruta;
            }
            try {
                System.err.println("*********************************************************"+direccion + ruta);
                scannerFs lexicoG = new scannerFs(new BufferedReader(new FileReader(new File(direccion + ruta))));
                sintacticoFs sintactico = new sintacticoFs(lexicoG);
                sintactico.setDireccion(direccion + ruta);
                ErrorEjecucion err = new ErrorEjecucion();
                sintactico.parse();
                NodoArbol doc = sintactico.getNodoRaiz();
                if (doc != null) {

                    addDocumento(doc);
                    err.printTablaSimbolos(Main.errores);
                } else {
                    err.printTablaSimbolos(Main.errores);
                }

            } catch (Exception ex) {
                //error
                ErrorEjecucion error1 = new ErrorEjecucion("fs", "excepcion al leer fs:" + ex.getMessage(), "", nodo.getLinea(), nodo.getColumna());
                error1.envio();
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void setDireccionDocumento(String dir) {
        this.direccion = dir;
    }

}
