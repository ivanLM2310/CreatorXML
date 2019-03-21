/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorFs.Estructura;

import AnalizadorFs.gdato.scannerGdato;
import AnalizadorFs.gdato.sintacticoGdato;
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
public class Gdato {

    
    String dirI;
    public Gdato() {
        
    }

    public String getDirI() {
        return dirI;
    }

    public void setDirI(String dirI) {
        this.dirI = dirI;
    }
    
    

    public ArrayList<Valor> getVectorGdato(String ruta) {
        ruta = ruta.trim();
        if (ruta.length() > 0) {
            ruta = (ruta.charAt(0) != '\''&& ruta.charAt(0) != '/') ? "\\" + ruta : ruta;
        }
        
        try {
            scannerGdato lexicoG = new scannerGdato(new BufferedReader(new FileReader(new File(dirI+ruta))));
            sintacticoGdato sintactico = new sintacticoGdato(lexicoG);
            sintactico.parse();
            ArrayList<Valor> doc = sintactico.getVector();

            return doc;
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
