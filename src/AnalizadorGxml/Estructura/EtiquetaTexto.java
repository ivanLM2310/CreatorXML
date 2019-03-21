/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorGxml.Estructura;

import AnalizadorFs.Estructura.Valor;
import AnalizadorGxml.ErrorEjecucion;
import creatorxml.Main;


/**
 *
 * @author ivanl
 */
public class EtiquetaTexto extends Etiqueta {

    boolean tipoTextoInterno = false;
    String textoEtiqueta;

    public EtiquetaTexto(String texto) {
        super();
        this.textoEtiqueta = texto;
        super.setAtributosValidos(11, 6, 7, 12, 13, 3, 14, 15);

        this.numeroObligatorios = 3;
    }

    public boolean isTipoTextoInterno() {
        return tipoTextoInterno;
    }

    public void setTipoTextoInterno(boolean tipoTextoInterno) {
        this.tipoTextoInterno = tipoTextoInterno;
        this.numeroObligatorios = (tipoTextoInterno) ? 1 : 3;
    }

    public String getTexto() {
        return textoEtiqueta;
    }

    public void setTexto(String textoEtiqueta) {
        this.textoEtiqueta = textoEtiqueta;
    }

    @Override
    public void addAtributo(Atributo atb) {
        if (tipoTextoInterno) {
            if (this.atributosValidos[atb.tipoAtributo - 201][0]
                    && !this.atributosValidos[atb.tipoAtributo - 201][1]) {
                switch (atb.getTipoAtributo()) {
                    case Constantes.atb_nombre:
                        this.numeroObligatorios -= 1;
                        this.cadenaObligatorios = this.cadenaObligatorios.replaceFirst(vectorTexto[atb.tipoAtributo - 201] + ",", "");

                        break;
                }
                this.atributosValidos[atb.tipoAtributo - 201][1] = true;
                this.atributos.add(atb);
            } else {
                if (!this.atributosValidos[atb.tipoAtributo - 201][0]) {
                    //reportar error
                    //el atributo no deberia ser llamado por esta etiqueta
                    String error = "el atributo de etiquetas \"" + vectorTexto[atb.tipoAtributo - 201] + "\" no es valida en la etiqueta ";
                    ErrorEjecucion err = new ErrorEjecucion("semantico Gxml", error, "semantico", "semantico", this.linea, this.columna);
                    Main.errores.add(err);

                }
                if (this.atributosValidos[atb.tipoAtributo - 201][1]) {
                    //reportar error
                    //el atributo ya fue ingresado una vez
                    String error = "el atributo de etiquetas \"" + vectorTexto[atb.tipoAtributo - 201] + "\" ya fue ingresado en etiqueta ";
                    ErrorEjecucion err = new ErrorEjecucion("semantico Gxml", error, "semantico", "semantico", this.linea, this.columna);
                    Main.errores.add(err);
                }
            }
        } else {
            if (this.atributosValidos[atb.tipoAtributo - 201][0]
                    && !this.atributosValidos[atb.tipoAtributo - 201][1]) {
                switch (atb.getTipoAtributo()) {
                    case Constantes.atb_nombre:
                    case Constantes.atb_x:
                    case Constantes.atb_y:
                        this.numeroObligatorios -= 1;
                        this.cadenaObligatorios = this.cadenaObligatorios.replaceFirst(vectorTexto[atb.tipoAtributo - 201] + ",", "");
                        break;
                }
                this.atributosValidos[atb.tipoAtributo - 201][1] = true;
                this.atributos.add(atb);
            } else {
                if (!this.atributosValidos[atb.tipoAtributo - 201][0]) {
                    //reportar error
                    //el atributo no deberia ser llamado por esta etiqueta
                    String error = "el atributo de etiquetas \"" + vectorTexto[atb.tipoAtributo - 201] + "\" no es valida en la etiqueta TEXTO";
                    ErrorEjecucion err = new ErrorEjecucion("semantico Gxml", error, "semantico", "semantico", this.linea, this.columna);
                    Main.errores.add(err);
                }
                if (this.atributosValidos[atb.tipoAtributo - 201][1]) {
                    //reportar error
                    //el atributo ya fue ingresado una vez
                    String error = "el atributo de etiquetas \"" + vectorTexto[atb.tipoAtributo - 201] + "\" ya fue ingresado en etiqueta TEXTO";
                    ErrorEjecucion err = new ErrorEjecucion("semantico Gxml", error, "semantico", "semantico", this.linea, this.columna);
                    Main.errores.add(err);
                }
            }
        }
    }

    @Override
    public String generarCodigo(String textoVentana) {
        //fuente, tamanio, color, x, y , negrita, cursiva,texto
        String id = salidaConversion(Constantes.atb_nombre, "");
        String idPadre = padre.salidaConversion(Constantes.atb_id, "");

        String fuente = salidaConversion(Constantes.atb_fuente, "\"Arial\"");
        String tamanio = salidaConversion(Constantes.atb_tam, "14");
        String color = salidaConversion(Constantes.atb_color, "\"#000000\"");
        String x = salidaConversion(Constantes.atb_x, "0");
        String y = salidaConversion(Constantes.atb_y, "0");
        String cursiva = salidaConversion(Constantes.atb_cursiva, "falso");
        String negrita = salidaConversion(Constantes.atb_negrita, "falso");

        String parametros = concatenarComas(fuente, tamanio, color, x, y, negrita, cursiva, "\"" + textoEtiqueta + "\"");
        return concatenar(idPadre + "_" + textoVentana + ".CrearTexto(" + parametros + ");\n");
    }



}
