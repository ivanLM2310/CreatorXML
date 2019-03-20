/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorGxml.Estructura;

import AnalizadorFs.Estructura.ConstantesFs;
import AnalizadorFs.Estructura.Objeto;
import AnalizadorFs.Estructura.Valor;
import AnalizadorGxml.ErrorEjecucion;
import creatorxml.Main;


/**
 *
 * @author ivanl
 */
public class EtiquetaControlador extends Etiqueta {

    public EtiquetaControlador() {
        super();
        super.setAtributosValidos(2, 11, 6, 7, 8, 9, 12, 13, 3, 14, 15, 16, 17, 18);
        this.numeroObligatorios = 4;

    }

    @Override
    public void addAtributo(Atributo atb) {
        if (this.atributosValidos[atb.tipoAtributo - 201][0]
                && !this.atributosValidos[atb.tipoAtributo - 201][1]) {
            switch (atb.getTipoAtributo()) {
                case Constantes.atb_tipo:
                case Constantes.atb_nombre:
                case Constantes.atb_x:
                case Constantes.atb_y:
                    this.numeroObligatorios -= 1;
                       this.cadenaObligatorios= this.cadenaObligatorios.replaceFirst(vectorTexto[atb.tipoAtributo - 201]+",", "");
                    break;
            }
            this.atributosValidos[atb.tipoAtributo - 201][1] = true;
            this.atributos.add(atb);
        } else {
            if (!this.atributosValidos[atb.tipoAtributo - 201][0]) {
                //reportar error
                //el atributo no deberia ser llamado por esta etiqueta
                    String error = "el atributo de etiquetas \"" + vectorTexto[atb.tipoAtributo - 201] + "\" no es valida en la etiqueta CONTROLADOR";
                    ErrorEjecucion err = new ErrorEjecucion("semantico Gxml",error,"semantico","semantico",this.linea,this.columna);
                    Main.errores.add(err);
            }
            if (this.atributosValidos[atb.tipoAtributo - 201][1]) {
                //reportar error
                //el atributo ya fue ingresado una vez
                     String error = "el atributo de etiquetas \"" + vectorTexto[atb.tipoAtributo - 201] + "\" ya fue ingresado en etiqueta CONTROLADOR";
                    ErrorEjecucion err = new ErrorEjecucion("semantico Gxml",error,"semantico","semantico",this.linea,this.columna);
                    Main.errores.add(err);
            }
        }

    }

    @Override
    public String generarCodigo(String textoVentana) {

        String id = salidaConversion(Constantes.atb_nombre, "");
        String idPadre = padre.salidaConversion(Constantes.atb_id, "");

        String tipo = salidaConversion(Constantes.atb_tipo, "");

        String alto = salidaConversion(Constantes.atb_alto, "50");
        String ancho = salidaConversion(Constantes.atb_ancho, "100");
        String fuente = salidaConversion(Constantes.atb_fuente, "");
        String tamaño = salidaConversion(Constantes.atb_tam, "500");
        String color = salidaConversion(Constantes.atb_color, "F3EEED");
        String x = salidaConversion(Constantes.atb_x, "0");
        String y = salidaConversion(Constantes.atb_y, "0");
        String negrita = salidaConversion(Constantes.atb_negrita, "falso");
        String cursiva = salidaConversion(Constantes.atb_cursiva, "falso");

        String maximo = salidaConversion(Constantes.atb_maximo, "falso");
        String minimo = salidaConversion(Constantes.atb_minimo, "falso");

        String parametros;

        String defecto = "nulo";
        String listaDatos = "nulo";
        switch (this.contenido.size()) {
            case 0:

                break;
            case 1:
                if (this.contenido.get(0) instanceof EtiquetaDefecto) {
                    defecto = ((EtiquetaDefecto) this.contenido.get(0)).textoEtiqueta;
                } else if (this.contenido.get(0) instanceof EtiquetaListaDatos) {
                    listaDatos = ((EtiquetaListaDatos) this.contenido.get(0)).generarCodigo(null);
                }
                break;
            case 2:
                for (Etiqueta etq : this.contenido) {
                    if (etq instanceof EtiquetaDefecto) {
                        defecto = ((EtiquetaDefecto) this.contenido.get(0)).generarCodigo(null);
                    } else if (etq instanceof EtiquetaListaDatos) {
                        listaDatos = ((EtiquetaListaDatos) this.contenido.get(0)).generarCodigo(null);
                    }
                }
                break;
            default:

                break;
        }

        switch (tipo.toLowerCase()) {

            case "texto":
                //cajaTexto
                //alto,ancho,fuente,tamaño,color,x,y,negrita,cursiva,defecto,nombre
                parametros = concatenarComas(alto, ancho, fuente, tamaño, color, x, y, negrita, cursiva, "\"" + defecto + "\"", "\"" + id + "\"");
                return concatenar( idPadre+"_"+textoVentana + ".CrearCajaTexto(" + parametros + ");\n");

            case "textoarea":

                //areaTexto
                //alto,ancho,fuente,tamaño,color,x,y,negrita,cursiva
                parametros = concatenarComas(alto, ancho, fuente, tamaño, color, x, y, negrita, cursiva, "\"" + defecto + "\"", "\"" + id + "\"");
                return concatenar(idPadre+"_"+textoVentana+ ".CrearAreaTexto(" + parametros + ");\n");
            case "numerico":
                //contro numerico
                //alto, ancho, maximo,minimo,x,y
                parametros = concatenarComas(alto, ancho, maximo, minimo, x, y, "\"" + defecto + "\"", "\"" + id + "\"");
                return concatenar(idPadre+"_"+textoVentana+ ".CrearControlNumerico(" + parametros + ");\n");

            case "desplegable":
                //control desplegable
                //alto,ancho,listaDatos,X,y,defecto

                parametros = concatenarComas(alto, ancho, listaDatos, fuente, x, y, "\"" + defecto + "\"", "\"" + id + "\"");
                return concatenar(idPadre+"_"+textoVentana+ ".CrearDesplegable(" + parametros + ")");
            default:
                break;
        }
        return "";
    }
    
     @Override
    public Valor generarObjeto() {
        Objeto objNuevo = new Objeto();
        int tam = atributos.size();
        for (int i = 0; i < tam; i++) {
            objNuevo.addAtributoValor(atributos.get(i).textoAtributo, atributos.get(i).getObjValor());
        }
        int tamEtiq = this.contenido.size();
        for(int i = 0; i < tamEtiq; i++){
            if(contenido.get(i) instanceof EtiquetaListaDatos){
                 objNuevo.addAtributoValor("lista", ((EtiquetaListaDatos) contenido.get(i)).generarObjeto());
            }else if(contenido.get(i) instanceof EtiquetaDefecto){
                 objNuevo.addAtributoValor("defecto", ((EtiquetaDefecto) contenido.get(i)).generarObjeto());
            }
        }
        
        return new Valor(objNuevo, ConstantesFs.TIPO_OBJETO);
    }

}
