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
public class EtiquetaContenedor extends Etiqueta {

    public EtiquetaContenedor() {
        super();
        super.setAtributosValidos(1, 6, 7, 8, 9, 3, 10);
        this.numeroObligatorios = 3;

    }

    @Override
    public void addAtributo(Atributo atb) {

        if (this.atributosValidos[atb.tipoAtributo - 201][0]
                && !this.atributosValidos[atb.tipoAtributo - 201][1]) {
            switch (atb.getTipoAtributo()) {
                case Constantes.atb_id:
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
                String error = "el atributo de etiquetas \"" + vectorTexto[atb.tipoAtributo - 201] + "\" no es valida en la etiqueta CONTENEDOR";
                ErrorEjecucion err = new ErrorEjecucion("semantico Gxml", error, "semantico", "semantico", this.linea, this.columna);
                Main.errores.add(err);
            }
            if (this.atributosValidos[atb.tipoAtributo - 201][1]) {
                //reportar error
                //el atributo ya fue ingresado una vez
                String error = "el atributo de etiquetas \"" + vectorTexto[atb.tipoAtributo - 201] + "\" ya fue ingresado en etiqueta CONTENEDOR";
                ErrorEjecucion err = new ErrorEjecucion("semantico Gxml", error, "semantico", "semantico", this.linea, this.columna);
                Main.errores.add(err);
            }
        }

    }

    @Override
    public String generarCodigo(String textoVentana) {
        //alto, ancho, color, borde, iniciox,inicioy
        String id = salidaConversion(Constantes.atb_id, "");
        String alto = salidaConversion(Constantes.atb_alto, "500");
        String ancho = salidaConversion(Constantes.atb_ancho, "500");
        String color = salidaConversion(Constantes.atb_color, padre.salidaConversion(Constantes.atb_color, "\"ffffff\""));

        String borde = salidaConversion(Constantes.atb_borde, "falso");
        String x = salidaConversion(Constantes.atb_x, "0");
        String y = salidaConversion(Constantes.atb_y, "0");

        String idPadre = padre.salidaConversion(Constantes.atb_id, "");
        return concatenar("Var", id + "_" + textoVentana, "=", "Ven_" + idPadre + ".CrearContenedor(" + alto + "," + ancho + "," + color + "," + borde + "," + x + "," + y, ");\n");

    }

   

}
