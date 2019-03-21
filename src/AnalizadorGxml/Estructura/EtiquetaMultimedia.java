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
public class EtiquetaMultimedia extends Etiqueta {

    public EtiquetaMultimedia() {
        super();
        super.setAtributosValidos(20, 2, 11, 6, 7, 8, 9, 21);
        this.numeroObligatorios = 5;
    }

    @Override
    public void addAtributo(Atributo atb) {
        if (this.atributosValidos[atb.tipoAtributo - 201][0]
                && !this.atributosValidos[atb.tipoAtributo - 201][1]) {
            switch (atb.getTipoAtributo()) {
                case Constantes.atb_path:
                case Constantes.atb_tipo:
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
                String error = "el atributo de etiquetas \"" + vectorTexto[atb.tipoAtributo - 201] + "\" no es valida en la etiqueta MULTIMEDIA";
                ErrorEjecucion err = new ErrorEjecucion("semantico Gxml", error, "semantico", "semantico", this.linea, this.columna);
                Main.errores.add(err);
            }
            if (this.atributosValidos[atb.tipoAtributo - 201][1]) {
                //reportar error
                //el atributo ya fue ingresado una vez
                String error = "el atributo de etiquetas \"" + vectorTexto[atb.tipoAtributo - 201] + "\" ya fue ingresado en etiqueta MULTIMEDIA";
                ErrorEjecucion err = new ErrorEjecucion("semantico Gxml", error, "semantico", "semantico", this.linea, this.columna);
                Main.errores.add(err);
            }
        }
    }

    @Override
    public String generarCodigo(String textoVentana) {

        String idPadre = padre.salidaConversion(Constantes.atb_id, "");

        String tipo = salidaConversion(Constantes.atb_tipo, "");

        String alto = salidaConversion(Constantes.atb_alto, "200");
        String ancho = salidaConversion(Constantes.atb_ancho, "200");
        String ruta = salidaConversion(Constantes.atb_path, "");
        String auto_r = salidaConversion(Constantes.atb_autoRepro, "verdadero");

        String x = salidaConversion(Constantes.atb_x, "0");
        String y = salidaConversion(Constantes.atb_y, "0");

        String parametros = concatenarComas(ruta, x, y, auto_r, alto, ancho);
        switch (tipo.toLowerCase()) {

            case "imagen":
                //Imagen
                //Contenedor.CrearImagen(Ruta, X, Y, Auto-reproductor, Alto, Ancho)
                return concatenar(idPadre + "_" + textoVentana + ".CrearImagen(" + parametros + ");\n");
            case "video":
                //video
                //Contenedor. CrearVideo (Ruta, X, Y, Auto-repr oductor, Alto, Ancho)
                return concatenar(idPadre + "_" + textoVentana + ".CrearVideo(" + parametros + ");\n");
            case "musica":
                //reproductor
                //Contenedor. CrearReproductor (Ruta, X, Y, Auto-reproductor, Alto, Ancho) 
                return concatenar(idPadre + "_" + textoVentana + ".CrearReproductor(" + parametros + ");\n");

            default:
                break;
        }
        return "";
    }


}
