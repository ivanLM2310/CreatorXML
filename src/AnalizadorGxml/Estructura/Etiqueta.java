/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorGxml.Estructura;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

/**
 *
 * @author ivanl
 */
public abstract class Etiqueta {

    Boolean[][] atributosValidos;
    int numeroObligatorios;
    ArrayList<Atributo> atributos;
    ArrayList<Etiqueta> contenido;

    String cadenaObligatorios = "";
    String[] vectorTexto;
    Etiqueta padre;

    int linea = 0;
    int columna = 0;

    public Etiqueta() {
        atributosValidos = new Boolean[21][2];
        this.contenido = new ArrayList();
        this.atributos = new ArrayList();
        llenarVector();
    }

    public void setLineaColumna(int linea, int columna) {
        this.linea = linea;
        this.columna = columna;
    }

    private void llenarVector() {

        for (int i = 0; i < 21; i++) {
            atributosValidos[i][0] = false;
            atributosValidos[i][1] = false;
        }
        vectorTexto = new String[21];
        vectorTexto[0] = "atb_id";
        vectorTexto[1] = "atb_tipo";
        vectorTexto[2] = "atb_color";
        vectorTexto[3] = "atb_accionInicial";
        vectorTexto[4] = "atb_accionFinal";
        vectorTexto[5] = "atb_x";
        vectorTexto[6] = "atb_y";
        vectorTexto[7] = "atb_alto";
        vectorTexto[8] = "atb_ancho";
        vectorTexto[9] = "atb_borde";
        vectorTexto[10] = "atb_nombre";
        vectorTexto[11] = "atb_fuente";
        vectorTexto[12] = "atb_tam";
        vectorTexto[13] = "atb_negrita";
        vectorTexto[14] = "atb_cursiva";
        vectorTexto[15] = "atb_maximo";
        vectorTexto[16] = "atb_minimo";
        vectorTexto[17] = "atb_accion";
        vectorTexto[18] = "atb_referencia";
        vectorTexto[19] = "atb_path";
        vectorTexto[20] = "atb_autoRepro";
    }

    protected void setAtributosValidos(int... numeros) {
        for (int i = 0; i < numeros.length; i++) {
            atributosValidos[numeros[i] - 1][0] = true;
            cadenaObligatorios += vectorTexto[numeros[i] - 1] + ",";
        }
    }

    public void verificarError() {
        //reportar si no viene algun obligatori
        if (numeroObligatorios > 0) {

        }
    }

    public void addEtiquetasHijas(Etiqueta etq) {
        this.contenido.add(etq);
        etq.padre = this;
    }

    public ArrayList<Atributo> getAtributos() {
        return atributos;
    }

    public void setAtributos(ArrayList<Atributo> atributos) {

        for (Atributo atb : atributos) {
            addAtributo(atb);
        }
        verificarError();
    }

    public ArrayList<Etiqueta> getContenido() {
        return contenido;
    }

    public void setContenido(ArrayList<Etiqueta> contenido) {
        this.contenido = contenido;
        contenido.forEach((etq) -> {
            etq.padre = this;
        });
    }

    public String salidaConversion(int Constante, String defecto) {
        Atributo atb = getAtributoEsp(Constante);
        String salida = String.valueOf((atb != null) ? atb.valor : defecto);
        if (atb != null) {
            if (Constantes.tipo_cadena == atb.tipoValor) {
                salida = "\"" + salida + "\"";
            }
        }
        return salida;
    }

    public Atributo getAtributoEsp(int atributo) {
        int num = 0;
        while (num < this.atributos.size()) {
            if (this.atributos.get(num).tipoAtributo == atributo) {
                return this.atributos.get(num);
            }
            num += 1;
        }
        return null;
    }

    public String concatenar(String... parametros) {
        String salida = "";
        for (int i = 0; i < parametros.length; i++) {
            if (i > 0) {
                salida = salida.concat(" ");
            }
            salida = salida.concat(parametros[i]);
        }
        return salida;
    }

    public String concatenarComas(String... parametros) {
        String salida = "";
        for (int i = 0; i < parametros.length; i++) {
            if (i > 0) {
                salida = salida.concat(",");
            }
            salida = salida.concat(parametros[i]);
        }
        return salida;
    }

    public abstract void addAtributo(Atributo atb);

    public abstract String generarCodigo(String textoVentana);
}
