/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorGxml.Estructura;

import AnalizadorGxml.ErrorEjecucion;
import creatorxml.Main;


/**
 *
 * @author ivanl
 */
public class EtiquetaDefecto extends Etiqueta {

    String textoEtiqueta;

    public EtiquetaDefecto(String texto) {
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
        if (this.atributosValidos[atb.tipoAtributo - 201][0]
                && !this.atributosValidos[atb.tipoAtributo - 201][1]) {

            this.atributosValidos[atb.tipoAtributo - 201][1] = true;
            this.atributos.add(atb);
        } else {
            if (!this.atributosValidos[atb.tipoAtributo - 201][0]) {
                //reportar error
                //el atributo no deberia ser llamado por esta etiqueta
                String error = "el atributo de etiquetas \"" + vectorTexto[atb.tipoAtributo - 201] + "\" no es valida en la etiqueta DEFECTO";
                ErrorEjecucion err = new ErrorEjecucion("semantico Gxml", error, "semantico", "semantico", this.linea, this.columna);
                Main.errores.add(err);
            }
            if (this.atributosValidos[atb.tipoAtributo - 201][1]) {
                //reportar error
                //el atributo ya fue ingresado una vez
                String error = "el atributo de etiquetas \"" + vectorTexto[atb.tipoAtributo - 201] + "\" ya fue ingresado en etiqueta DEFECTO";
                ErrorEjecucion err = new ErrorEjecucion("semantico Gxml", error, "semantico", "semantico", this.linea, this.columna);
                Main.errores.add(err);
            }
        }
    }

    @Override
    public String generarCodigo(String textoVentana) {
        //fuente,tam , color, x, y , referencia
        return textoEtiqueta;
    }

}
