/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorGxml;

import AnalizadorGxml.Estructura.Etiqueta;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import creatorxml.Main;

/**
 *
 * @author IvanAlfonso
 */
public class ErrorEjecucion {

    String ClaseEjecutado;
    String Error;
    String ambiente;
    String tipo;
    int linea;
    int columna;

    public ErrorEjecucion(String ClaseEjecutado, String Error, String ambiente,int linea, int columna) {
        this.ClaseEjecutado = ClaseEjecutado;
        this.Error = Error;
        this.ambiente = ambiente;
        this.tipo = "semantico";
        this.linea = linea;
        this.columna = columna;
    }

    public ErrorEjecucion(String ClaseEjecutado, String Error, String ambiente, String tipo,int linea, int columna) {
        this.ClaseEjecutado = ClaseEjecutado;
        this.Error = Error;
        this.ambiente = ambiente;
        this.tipo = tipo;
        this.linea = linea;
        this.columna = columna;
    }

    public ErrorEjecucion() {

    }

    public String GraficarTabla(ArrayList<ErrorEjecucion> listaErrores) {
        String tablasimboloshtml = "";

        Calendar fecha = Calendar.getInstance();
        int anio = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH) + 1;
        int dia = fecha.get(Calendar.DAY_OF_MONTH);
        int hora = fecha.get(Calendar.HOUR_OF_DAY);
        int minuto = fecha.get(Calendar.MINUTE);
        int segundo = fecha.get(Calendar.SECOND);

        tablasimboloshtml += "<html>\n\t<head>\n\t\t<title>Tabla de Simbolos</title>" + "<meta charset=" + "\"" + "utf-8" + "\"" + ">"
                + "\n\t\t<link rel=" + "\"" + "stylesheet" + "\"" + "type=" + "\"" + "text/css" + "\"" + " href=" + "\"" + "Estilo.css"
                + "\"" + ">\n\t</head>\n\t<body>"
                + "\n\t\t<div style=" + "\"" + "text-align:left;" + "\"" + ">"
                + "\n\t\t\t<h1>TABLA DE SIMBOLOS</h1>"
                + "\n\t\t\t<h2>Dia de ejecución:" + dia + " de " + getMes(mes) + " de " + anio + "</h2>"
                + "\n\t\t\t<h2>Hora de ejecución:" + hora + ":" + minuto + ":" + segundo + " " + getHora(hora) + "</h2>"
                + "\n\t\t\t<table style=\"margin: margin: 5 auto;\" border=\"2\">\n";
        tablasimboloshtml += "\t\t\t\t<TR>\n\t\t\t\t\t<TH>Clase Ejecucion</TH> <TH>Tipo</TH> <TH>ambito</TH> <TH>error</TH> <TH>linea</TH><TH>columna</TH>\n\t\t\t\t</TR>";

        for (ErrorEjecucion nodo : listaErrores) {
            tablasimboloshtml += "\n\t\t\t\t<TR>";
            tablasimboloshtml += "\n\t\t\t\t\t<TD>" + nodo.ClaseEjecutado + "</TD>" + "<TD>" + nodo.tipo + "</TD>" + "<TD>" + nodo.ambiente + "</TD>" + "<TD>" + nodo.Error + "</TD>"+ "<TD>" + nodo.linea + "</TD>"+ "<TD>" + nodo.columna + "</TD>";
            tablasimboloshtml += "\n\t\t\t\t</TR>";

        }

        tablasimboloshtml += "\n\t\t\t</table>\n\t\t</div>\n\t</body>\n</html>";
        return tablasimboloshtml;
    }

    public String getMes(int mes) {
        String m = "";
        switch (mes) {
            case 1:
                m = "Enero";
                break;
            case 2:
                m = "Febrero";
                break;
            case 3:
                m = "Marzo";
                break;
            case 4:
                m = "Abril";
                break;
            case 5:
                m = "Mayo";
                break;
            case 6:
                m = "Junio";
                break;
            case 7:
                m = "Julio";
                break;
            case 8:
                m = "Agosto";
                break;
            case 9:
                m = "Septiembre";
                break;
            case 10:
                m = "Octubre";
                break;
            case 11:
                m = "Noviembre";
                break;
            default:
                m = "Diciembre";
        }
        return m;

    }

    public String getHora(int hora) {
        String h = "p.m.";
        if (hora < 12) {
            h = "a.m.";
        }
        return h;
    }

    public void printTablaSimbolos(ArrayList<ErrorEjecucion> listaErrores) {
        FileWriter writer = null;
        PrintWriter pw = null;

        try {
            writer = new FileWriter("errores.html");
            pw = new PrintWriter(writer);

            String a = GraficarTabla(listaErrores);
            pw.println(a);
            pw.println("\n");
            pw.close();

        } catch (Exception ex) {

        } finally {
            if (writer != null) {
                try {
                    writer.close();

                } catch (IOException ex) {
                    Logger.getLogger(Etiqueta.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
     public void envio() {
       
        Main.errores .add(this);
    }

}
