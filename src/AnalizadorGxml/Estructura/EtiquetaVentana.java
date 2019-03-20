/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorGxml.Estructura;

import AnalizadorGxml.ErrorEjecucion;
import java.util.ArrayList;
import creatorxml.Main;


/**
 *
 * @author ivanl
 */
public class EtiquetaVentana extends Etiqueta {

    public EtiquetaVentana() {
        super();
        super.setAtributosValidos(1, 2, 3, 4, 5);
        this.numeroObligatorios = 2;
    }

    @Override
    public void addAtributo(Atributo atb) {
        if (this.atributosValidos[atb.tipoAtributo - 201][0]
                && !this.atributosValidos[atb.tipoAtributo - 201][1]) {
            switch (atb.getTipoAtributo()) {
                case Constantes.atb_id:
                case Constantes.atb_tipo:
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
                String error = "el atributo de etiquetas \"" + vectorTexto[atb.tipoAtributo - 201] + "\" no es valida en la etiqueta VENTANA";
                ErrorEjecucion err = new ErrorEjecucion("semantico Gxml", error, "semantico", "semantico", this.linea, this.columna);
                Main.errores.add(err);
            }
            if (this.atributosValidos[atb.tipoAtributo - 201][1]) {
                //reportar error
                //el atributo ya fue ingresado una vez
                String error = "el atributo de etiquetas \"" + vectorTexto[atb.tipoAtributo - 201] + "\" ya fue ingresado en etiqueta VENTANA";
                ErrorEjecucion err = new ErrorEjecucion("semantico Gxml", error, "semantico", "semantico", this.linea, this.columna);
                Main.errores.add(err);
            }
        }

    }

    @Override
    public String generarCodigo(String textoVentana) {

        //String id = String.valueOf((getAtributoEsp(Constantes.atb_id) != null) ? getAtributoEsp(Constantes.atb_id).valor : "");
        String id = salidaConversion(Constantes.atb_id, "");
        String color = salidaConversion(Constantes.atb_color, "#F3EEED");
        //= String.valueOf((getAtributoEsp(Constantes.atb_color) != null) ? getAtributoEsp(Constantes.atb_color).valor : "");
        return concatenar("Var", "Ven_" + id, "=", "CrearVentana(", color, ",", "100", ",", "100", ");\n");
    }

}
