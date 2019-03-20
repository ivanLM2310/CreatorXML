/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorFs.Estructura;

import AnalizadorGxml.Estructura.Documento;
import AnalizadorGxml.Estructura.Etiqueta;
import AnalizadorGxml.Estructura.*;
import java.util.ArrayList;

/**
 *
 * @author ivanl
 */
public class ObjetoGxml {

    Valor objAtributos;
    String strEtiqueta;
    int etiquetaGxml;
    ArrayList<ObjetoGxml> contenido;

    public ObjetoGxml() {
        contenido = new ArrayList();

    }

    public Valor getObjAtributos() {
        return objAtributos;
    }

    public void setObjAtributos(Valor objAtributos) {
        this.objAtributos = objAtributos;
    }

    public String getStrEtiqueta() {
        return strEtiqueta;
    }

    public void setStrEtiqueta(String strEtiqueta) {
        this.strEtiqueta = strEtiqueta;
    }

    public int getEtiquetaGxml() {
        return etiquetaGxml;
    }

    public void setEtiquetaGxml(int etiquetaGxml) {
        this.etiquetaGxml = etiquetaGxml;
    }

    public ArrayList<ObjetoGxml> getContenido() {
        return contenido;
    }

    public void setContenido(ArrayList<ObjetoGxml> contenido) {
        this.contenido = contenido;
    }

    public ArrayList<ObjetoGxml> getListaObjetos(Documento doc) {
        int tam = doc.getVentanas().size();
        ArrayList<ObjetoGxml> lista = new ArrayList();
        for (int i = 0; i < tam; i++) {
            ObjetoGxml nuevo = new ObjetoGxml();
            nuevo.setStrEtiqueta("ventana");
            nuevo.setObjAtributos(doc.getVentanas().get(i).generarObjeto());
            nuevo.setContenido(getListaObjetos(doc.getVentanas().get(i)));
            lista.add(nuevo);

        }
        return lista;

    }

    private ArrayList<ObjetoGxml> getListaObjetos(Etiqueta e) {
        int tam = e.getContenido().size();
        ArrayList<ObjetoGxml> lista = new ArrayList();
        for (int i = 0; i < tam; i++) {
            ObjetoGxml nuevo = new ObjetoGxml();
            String strE = getInstancia(e);
            nuevo.setStrEtiqueta(strE);
            nuevo.setObjAtributos(e.getContenido().get(i).generarObjeto());
            if (!(e instanceof EtiquetaDato) && !(e instanceof EtiquetaDefecto)) {
                nuevo.setContenido(getListaObjetos(e.getContenido().get(i)));
                lista.add(nuevo);
            }
        }
        return lista;
    }

    private void agregarValoresDefecto(Valor v, Etiqueta etiq) {
        if (etiq instanceof EtiquetaBoton) {
            //Objeto ob = v.
        } else if (etiq instanceof EtiquetaContenedor) {

        } else if (etiq instanceof EtiquetaControlador) {

        } else if (etiq instanceof EtiquetaDato) {

        } else if (etiq instanceof EtiquetaDefecto) {

        } else if (etiq instanceof EtiquetaEnviar) {

        } else if (etiq instanceof EtiquetaImportar) {

        } else if (etiq instanceof EtiquetaListaDatos) {

        } else if (etiq instanceof EtiquetaMultimedia) {

        } else if (etiq instanceof EtiquetaTexto) {

        } else if (etiq instanceof EtiquetaVentana) {

        }

    }

    private String getInstancia(Etiqueta etiq) {
        if (etiq instanceof EtiquetaBoton) {
            return "boton";
        } else if (etiq instanceof EtiquetaContenedor) {
            return "contenedor";
        } else if (etiq instanceof EtiquetaControlador) {
            return "controlador";
        } else if (etiq instanceof EtiquetaDato) {
            return "boton";
        } else if (etiq instanceof EtiquetaDefecto) {
            return "defecto";
        } else if (etiq instanceof EtiquetaEnviar) {
            return "enviar";
        } else if (etiq instanceof EtiquetaImportar) {
            return "importar";
        } else if (etiq instanceof EtiquetaListaDatos) {
            return "listadatos";
        } else if (etiq instanceof EtiquetaMultimedia) {
            return "multimedia";
        } else if (etiq instanceof EtiquetaTexto) {
            return "texto";
        } else if (etiq instanceof EtiquetaVentana) {
            return "ventana";
        }
        return "";
    }

    public void obtenerPorEtiqueta(ArrayList<Valor> lista, String etiqueta) {

        if (strEtiqueta.equals(etiqueta)) {
            lista.add(objAtributos);
        }
        int tamContenido = contenido.size();
        for (int i = 0; i < tamContenido; i++) {
            contenido.get(i).obtenerPorEtiqueta(lista, etiqueta);
        }
    }

    public Valor obtenerPorId(ArrayList<Valor> lista, String etiqueta) {
        if (objAtributos.isTipoIgual(ConstantesFs.TIPO_OBJETO)) {
            Objeto objGxml = (Objeto) objAtributos.valor;
            Valor val = objGxml.getValor("id");
            if (!val.isTipoIgual(ConstantesFs.TIPO_NULL)) {
                if(val.isTipoIgual(ConstantesFs.TIPO_CADENA)){
                    if(val.getString().equals(etiqueta)){
                        
                    }
                }
                
            }
            int tamContenido = contenido.size();
            for (int i = 0; i < tamContenido; i++) {
                contenido.get(i).obtenerPorEtiqueta(lista, etiqueta);
            }
        }

    }

    public void obtenerPorNombre(ArrayList<Valor> lista, String etiqueta) {

    }

}
