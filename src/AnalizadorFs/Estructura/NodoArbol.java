/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorFs.Estructura;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ivanl
 */
public class NodoArbol {

    int constEtiqueta = 0;
    String valor = "";
    int linea;
    int columna;
    int constTipo = 0;
    String strEtiqueta;
    String strTipo;
    ArrayList<NodoArbol> hijosNodo = new ArrayList();

    public NodoArbol(int etiqueta, String valor, int tipo) {
        this.constEtiqueta = etiqueta;
        this.valor = valor;
        this.constTipo = tipo;
        generarStrConst();

    }

    public NodoArbol(int etiqueta, int tipo) {
        this.constEtiqueta = etiqueta;
        this.constTipo = tipo;
        generarStrConst();
    }

    public NodoArbol(int etiqueta, String valor) {
        this.constEtiqueta = etiqueta;
        this.valor = valor;
        generarStrConst();
    }

    public NodoArbol(int etiqueta) {
        this.constEtiqueta = etiqueta;
        generarStrConst();
    }

    public void addHijos(NodoArbol hijo1) {
        this.hijosNodo.add(hijo1);
    }

    public void addHijos(NodoArbol hijo1, NodoArbol hijo2) {
        this.hijosNodo.add(hijo1);
        this.hijosNodo.add(hijo2);
    }

    public void addHijos(NodoArbol hijo1, NodoArbol hijo2, NodoArbol hijo3) {
        this.hijosNodo.add(hijo1);
        this.hijosNodo.add(hijo2);
        this.hijosNodo.add(hijo3);
    }

    public void addHijos(NodoArbol hijo1, NodoArbol hijo2, NodoArbol hijo3, NodoArbol hijo4) {
        this.hijosNodo.add(hijo1);
        this.hijosNodo.add(hijo2);
        this.hijosNodo.add(hijo3);
        this.hijosNodo.add(hijo4);
    }

    public void addHijos(NodoArbol hijo1, NodoArbol hijo2, NodoArbol hijo3, NodoArbol hijo4, NodoArbol hijo5) {
        this.hijosNodo.add(hijo1);
        this.hijosNodo.add(hijo2);
        this.hijosNodo.add(hijo3);
        this.hijosNodo.add(hijo4);
        this.hijosNodo.add(hijo5);
    }

    private void generarStrConst() {
        this.strEtiqueta = this.getTextoConst(this.constEtiqueta);
        this.strTipo = this.getTextoConst(this.constTipo);

    }

    public ArrayList<NodoArbol> getHijosNodo() {
        return hijosNodo;
    }

    public void setHijosNodo(ArrayList<NodoArbol> hijosNodo) {
        this.hijosNodo = hijosNodo;
    }

    public void addTodosHijos(ArrayList<NodoArbol> hijosNodo) {
        for (NodoArbol n : hijosNodo) {
            this.addHijos(n);
        }
    }

    public int getConstEtiqueta() {
        return constEtiqueta;
    }

    public void setConstEtiqueta(int constEtiqueta) {
        this.constEtiqueta = constEtiqueta;
        generarStrConst();
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public int getConstTipo() {
        return constTipo;
    }

    public void setConstTipo(int constTipo) {
        this.constTipo = constTipo;
        generarStrConst();
    }

    public void graficarArbol(String nombreSalidaArchivo, int num) {
        try {
            BufferedWriter bw;
            bw = new BufferedWriter(new FileWriter(new File(nombreSalidaArchivo + ".dot")));
            bw.write("digraph g {");
            String cadena = getDot(num);
            bw.write(cadena);
            bw.write("\n}");
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(NodoArbol.class.getName()).log(Level.SEVERE, null, ex);
        }
        String dotPath = "C:\\Program Files (x86)\\Graphviz2.38\\bin\\dot.exe";
        String tParam = dotPath + " -Tjpg " + nombreSalidaArchivo + ".dot -o " + nombreSalidaArchivo + ".jpg";
        try {
            Runtime.getRuntime().exec(tParam);
        } catch (IOException ex) {
            Logger.getLogger(NodoArbol.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String getDot(int num) {
        String cadenaSalida = "0";
        if (this != null) {
            cadenaSalida = getDot(this);
        }
        return cadenaSalida;
    }

    private String getDot(NodoArbol nodo) {
        String salida = "";
        if (nodo != null) {
            salida = "N" + nodo.hashCode() + "[label=\"etiqueta:" + nodo.getTextoConst(nodo.constEtiqueta) + " \nTipo:"
                    + nodo.getTextoConst(constTipo) + "\"];";

            for (int i = 0; i < nodo.getHijosNodo().size(); i++) {

                salida += "\n N" + nodo.hashCode() + "->N" + nodo.getHijosNodo().get(i).hashCode() + ";";
                salida += getDot(nodo.getHijosNodo().get(i));
            }
        }
        return salida;
    }

    public int getTamañoH() {
        return this.hijosNodo.size();
    }

    public boolean isVaciaH() {
        return this.hijosNodo.isEmpty();
    }

    public NodoArbol getElemento(int num) {
        return this.hijosNodo.get(num);
    }

    public NodoArbol BuscarNodo(int constante) {

        for (NodoArbol hijo : this.hijosNodo) {
            if (hijo.getConstEtiqueta() == constante) {
                return hijo;
            }
        }
        return null;
    }

    public boolean isNull() {
        if (this == null) {
            return true;
        }
        return false;
    }

    public boolean isEtiquetaIgual(int val) {
        return this.constEtiqueta == val;
    }

    public boolean isTipoIgual(int val) {
        return this.constTipo == val;
    }

    public boolean isValorIgual(String val) {
        return this.valor.equals(val);
    }

    public void setPosicion(int linea, int columna) {
        this.linea = linea;
        this.columna = columna;
    }

    public int getLinea() {
        return linea;
    }

    public int getColumna() {
        return columna;
    }

    public String getTextoConst(int constante) {
        switch (constante) {
            case ConstantesFs.CADENA:
                return "CADENA";
            case ConstantesFs.POTENCIA:
                return "POTENCIA";
            case ConstantesFs.DIVISION:
                return "DIVISION";
            case ConstantesFs.MULTIPLICACION:
                return "MULTIPLICACION";
            case ConstantesFs.RESTA:
                return "RESTA";
            case ConstantesFs.SUMA:
                return "SUMA";
            case ConstantesFs.IGUALIGUAL:
                return "IGUALIGUAL";
            case ConstantesFs.DIFERENTE:
                return "DIFERENTE";
            case ConstantesFs.MENORQ:
                return "MENORQ";
            case ConstantesFs.MAYORQ:
                return "MAYORQ";
            case ConstantesFs.MAYORIGUALQ:
                return "MAYORIGUALQ";
            case ConstantesFs.MENORIGUALQ:
                return "MENORIGUALQ";
            case ConstantesFs.AND:
                return "AND";
            case ConstantesFs.OR:
                return "OR";
            case ConstantesFs.NOT:
                return "NOT";
            case ConstantesFs.VARIABLE:
                return "VARIABLE";
            case ConstantesFs.VECTOR:
                return "VECTOR";
            case ConstantesFs.SI:
                return "SI";
            case ConstantesFs.SELECCIONA:
                return "SELECCIONA";
            case ConstantesFs.CASO:
                return "CASO";
            case ConstantesFs.DETENER:
                return "DETENER";
            case ConstantesFs.IMPRIMIR:
                return "IMPRIMIR";
            case ConstantesFs.FUNCIONES:
                return "FUNCIONES";
            case ConstantesFs.RETORNAR:
                return "RETORNAR";
            case ConstantesFs.VECTOR_DECLARAR:
                return "VECTOR_DECLARAR";
            case ConstantesFs.ID:
                return "ID";
            case ConstantesFs.DECLARAR:
                return "DECLARAR";
            case ConstantesFs.LLAMADAS_METODOS_NATIVOS:
                return "LLAMADAS_METODOS_NATIVOS";
            case ConstantesFs.ASIGNACION:
                return "ASIGNACION";
            case ConstantesFs.CASOS:
                return "CASOS";
            case ConstantesFs.DEFECTO:
                return "DEFECTO";
            case ConstantesFs.MASMAS:
                return "MASMAS";
            case ConstantesFs.MENOSMENOS:
                return "MENOSMENOS";
            case ConstantesFs.LISTA_PARAMETROS:
                return "LISTA_PARAMETROS";
            case ConstantesFs.LISTA_VALORES:
                return "LISTA_VALORES";
            case ConstantesFs.LLAMADA_METODO:
                return "LLAMADA_METODO";
            case ConstantesFs.FUNCION:
                return "FUNCION";
            case ConstantesFs.LISTA_SENTENCIAS:
                return "LISTA_SENTENCIAS";
            case ConstantesFs.DOCUMENTO:
                return "DOCUMENTO";
            case ConstantesFs.MODULAR:
                return "MODULAR";
            case ConstantesFs.AUMENTO:
                return "AUMENTO";
            case ConstantesFs.DISMINUCION:
                return "DISMINUCION";
            case ConstantesFs.ID_VECTOR:
                return "ID_VECTOR";
            case ConstantesFs.DESCENDENTE:
                return "DESCENDENTE";
            case ConstantesFs.ASCENDENTE:
                return "ASCENDENTE";
            case ConstantesFs.INVERTIR:
                return "INVERTIR";
            case ConstantesFs.MAXIMO:
                return "MAXIMO";
            case ConstantesFs.FILTER:
                return "FILTER";
            case ConstantesFs.BUSCAR:
                return "BUSCAR";
            case ConstantesFs.MAP:
                return "MAP";
            case ConstantesFs.REDUCE:
                return "REDUCE";
            case ConstantesFs.TODOS:
                return "TODOS";
            case ConstantesFs.ALGUNO:
                return "ALGUNO";
            case ConstantesFs.LISTA_PUNTO:
                return "LISTA_PUNTO";
            case ConstantesFs.LISTA_IDS:
                return "LISTA_IDS";
            case ConstantesFs.OBJETO_DECLARAR:
                return "OBJETO_DECLARAR";
            case ConstantesFs.NUMERO:
                return "NUMERO";
            case ConstantesFs.TERNARIO:
                return "TERNARIO";
            case ConstantesFs.SI_PADRE:
                return "SI_PADRE";
            case ConstantesFs.LISTA_IMPORTAR:
                return "LISTA_IMPORTAR";
            case ConstantesFs.IMPORTAR:
                return "IMPORTAR";
            case ConstantesFs.TIPO_CADENA:
                return "TIPO_CADENA";
            case ConstantesFs.TIPO_BOOLEANO:
                return "TIPO_BOOLEANO";
            case ConstantesFs.TIPO_NUMERO:
                return "TIPO_NUMERO";
            case ConstantesFs.TIPO_NULL:
                return "TIPO_NULL";
            case ConstantesFs.TIPO_VECTOR:
                return "TIPO_VECTOR";
            case ConstantesFs.TIPO_OBJETO:
                return "TIPO_OBJETO";
            case ConstantesFs.RETORNO_VALOR:
                return "RETORNO_VALOR";
            case ConstantesFs.RETORNO_BREAK:
                return "RETORNO_BREAK";
            case ConstantesFs.RETORNO_METODO:
                return "RETORNO_METODO";
            case ConstantesFs.RETORNO_SI:
                return "RETORNO_SI";
            case ConstantesFs.BOOLEANO_TRUE:
                return "BOOLEANO_TRUE";
            case ConstantesFs.BOOLEANO_FALSE:
                return "BOOLEANO_FALSE";
            case ConstantesFs.MAS_IGUAL:
                return "MAS_IGUAL";
            case ConstantesFs.MENOS_IGUAL:
                return "MENOS_IGUAL";
            case ConstantesFs.POR_IGUAL:
                return "POR_IGUAL";
            case ConstantesFs.DIV_IGUAL:
                return "DIV_IGUAL";
            case ConstantesFs.IGUAL:
                return "IGUAL";
            case ConstantesFs.VECTOR_HETEROGENEO:
                return "VECTOR_HETEROGENEO";
            case ConstantesFs.VECTOR_HOMOGENEO:
                return "VECTOR_HOMOGENEO";
            case ConstantesFs.MINIMO:
                return "MINIMO";
        }
        return "";

    }

}
