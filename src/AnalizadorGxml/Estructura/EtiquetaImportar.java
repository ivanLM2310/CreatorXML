/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorGxml.Estructura;

import AnalizadorFs.Estructura.Valor;
import java.io.File;

/**
 *
 * @author ivanl
 */
public class EtiquetaImportar extends Etiqueta {

    String textoEtiqueta;

    public EtiquetaImportar(String texto) {
        super();
        this.textoEtiqueta = texto;
    }

    public String getTexto() {
        return textoEtiqueta;
    }

    public void setTexto(String textoEtiqueta) {
        this.textoEtiqueta = textoEtiqueta;
    }

    @Override
    public void addAtributo(Atributo atb) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String generarCodigo(String textoVentana) {

        if (textoEtiqueta.length() > 0) {
            textoEtiqueta = (textoEtiqueta.charAt(0) != '\'' && textoEtiqueta.charAt(0) != '/') ? "\\" + textoEtiqueta : textoEtiqueta;
        }
        File a = new File(textoVentana + textoEtiqueta.trim());
        if (a.isFile()) {
            String ext = "";
            String name = a.getName();
            int lastIndexOf = name.lastIndexOf(".");
            if (lastIndexOf == -1) {
                return "i"; // empty extension
            }
            ext = name.substring(lastIndexOf);
            if (ext.toLowerCase().equals(".gxml")) {
                return "gxml";
            }
            return "Importar(\"" + textoEtiqueta + "\");";
        } else if (a.exists()) {
            System.out.println("verdadero");
        } else {
            System.out.println("falso");
        }
        return "i";
    }


}
