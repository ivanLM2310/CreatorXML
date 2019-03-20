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
public class EtiquetaBoton extends Etiqueta {

    String textoEtiqueta;

    public EtiquetaBoton(String texto) {
        super();
        this.textoEtiqueta = texto;
        super.setAtributosValidos(11, 6, 7, 8, 9, 19, 18);
        this.numeroObligatorios = 3;
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
                String error = "el atributo de etiquetas \"" + vectorTexto[atb.tipoAtributo - 201] + "\" no es valida en la etiqueta BOTON";
                ErrorEjecucion err = new ErrorEjecucion("semantico Gxml", error, "semantico", "semantico", this.linea, this.columna);
                Main.errores.add(err);
            }
            if (this.atributosValidos[atb.tipoAtributo - 201][1]) {
                //reportar error
                //el atributo ya fue ingresado una vez
                String error = "el atributo de etiquetas \"" + vectorTexto[atb.tipoAtributo - 201] + "\" ya fue ingresado en etiqueta BOTON";
                ErrorEjecucion err = new ErrorEjecucion("semantico Gxml", error, "semantico", "semantico", this.linea, this.columna);
                Main.errores.add(err);
            }
        }
    }

    @Override
    public String generarCodigo(String textoVentana) {
        /*
        Var bot = Contenedor.CrearBoton(Fuente, Tamaño, Color, X, Y,Referencia, valor, Alto, Ancho) 4
        Bot.AlClic(Metodo(12));
         */

        String id = salidaConversion(Constantes.atb_nombre, "");
        String idPadre = padre.salidaConversion(Constantes.atb_id, "");

        String fuente = salidaConversion(Constantes.atb_fuente, "500");
        String tamaño = salidaConversion(Constantes.atb_tam, "500");
        String color = salidaConversion(Constantes.atb_color, "#F3EEED");
        String x = salidaConversion(Constantes.atb_x, "0");
        String y = salidaConversion(Constantes.atb_y, "0");
        String referencia = salidaConversion(Constantes.atb_referencia, "nulo");
        referencia = "CargarVentana_" + referencia + "()";
        String alto = salidaConversion(Constantes.atb_alto, "500");
        String ancho = salidaConversion(Constantes.atb_ancho, "500");

        String parametros = concatenarComas(fuente, tamaño, color, x, y, referencia, "\"" + textoEtiqueta + "\"", alto, ancho);
        return concatenar("Var", id + "_" + textoVentana, "=", idPadre + "_" + textoVentana + ".CrearBoton(" + parametros + ");\n");
    }

}
