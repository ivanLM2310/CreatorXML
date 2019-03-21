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
import java.util.ArrayList;

/**
 *
 * @author ivanl
 */
public class EtiquetaListaDatos extends Etiqueta {

    public EtiquetaListaDatos() {
        super();
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
                String error = "el atributo de etiquetas \"" + vectorTexto[atb.tipoAtributo - 201] + "\" no es valida en la etiqueta LISTA_DATOS";
                ErrorEjecucion err = new ErrorEjecucion("semantico Gxml", error, "semantico", "semantico", this.linea, this.columna);
                Main.errores.add(err);
            }
            if (this.atributosValidos[atb.tipoAtributo - 201][1]) {
                //reportar error
                //el atributo ya fue ingresado una vez
                String error = "el atributo de etiquetas \"" + vectorTexto[atb.tipoAtributo - 201] + "\" ya fue ingresado en etiqueta LISTA_DATOS";
                ErrorEjecucion err = new ErrorEjecucion("semantico Gxml", error, "semantico", "semantico", this.linea, this.columna);
                Main.errores.add(err);
            }
        }
    }

    @Override
    public String generarCodigo(String textoVentana) {
        String elementosV = "";
        int i = 0;
        for (Etiqueta etq : this.contenido) {
            EtiquetaDato dato = (EtiquetaDato) etq;

            if (i == 0) {
                elementosV = "\""+dato.textoEtiqueta + "\"";
            } else {
                elementosV = elementosV.concat("," + "\"" + dato.textoEtiqueta + "\"");
            }
            i += 1;
        }
        String idPadre = padre.salidaConversion(Constantes.atb_nombre, "");
        return concatenar("Var", idPadre + "_listaDatos", "=", "[" + elementosV + " ];");
    }

    @Override
    public Valor generarObjeto() {
        int tamDatos = this.contenido.size();
        ArrayList<Valor> lista = new ArrayList();
        for (int i = 0; i < tamDatos; i++) {
            EtiquetaDato dato = (EtiquetaDato) contenido.get(0);
            lista.add(new Valor(dato.textoEtiqueta, ConstantesFs.TIPO_CADENA));
        }
        return new Valor(lista, ConstantesFs.VECTOR_HETEROGENEO);
    }

}
