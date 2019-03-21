/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorGxml.Estructura;

import AnalizadorFs.Estructura.ConstantesFs;
import AnalizadorFs.Estructura.Valor;

/**
 *
 * @author ivanl
 */
public class Atributo {

    int tipoAtributo;
    int tipoValor;
    String valor;
    String textoAtributo;

    public Atributo(int tipoAtributo, int tipoValor) {
        this.tipoAtributo = tipoAtributo;
        this.tipoValor = tipoValor;
        getStrEtiqueta();
    }

    public Atributo(int tipoAtributo, int tipoValor, String valor) {
        this.tipoAtributo = tipoAtributo;
        this.tipoValor = tipoValor;
        this.valor = valor;
        getStrEtiqueta();
    }

    public int getTipoAtributo() {
        return tipoAtributo;
    }

    public void setTipoAtributo(int tipoAtributo) {
        this.tipoAtributo = tipoAtributo;
        getStrEtiqueta();
    }

    public int getTipoValor() {
        return tipoValor;
    }

    public void setTipoValor(int tipoValor) {
        this.tipoValor = tipoValor;
    }

    public Object getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    private void getStrEtiqueta() {
        switch (tipoAtributo) {
            case Constantes.atb_id:
                textoAtributo = "id";
                break;
            case Constantes.atb_tipo:
                textoAtributo = "tipo";
                break;
            case Constantes.atb_color:
                textoAtributo = "color";
                break;
            case Constantes.atb_accionInicial:
                textoAtributo = "accioninicial";
                break;
            case Constantes.atb_accionFinal:
                textoAtributo = "accionfinal";
                break;
            case Constantes.atb_x:
                textoAtributo = "x";
                break;
            case Constantes.atb_y:
                textoAtributo = "y";
                break;
            case Constantes.atb_alto:
                textoAtributo = "alto";
                break;
            case Constantes.atb_ancho:
                textoAtributo = "ancho";
                break;
            case Constantes.atb_borde:
                textoAtributo = "borde";
                break;
            case Constantes.atb_nombre:
                textoAtributo = "nombre";
                break;
            case Constantes.atb_fuente:
                textoAtributo = "fuente";
                break;
            case Constantes.atb_tam:
                textoAtributo = "tam";
                break;
            case Constantes.atb_negrita:
                textoAtributo = "negrita";
                break;
            case Constantes.atb_cursiva:
                textoAtributo = "cursiva";
                break;
            case Constantes.atb_maximo:
                textoAtributo = "maximo";
                break;
            case Constantes.atb_minimo:
                textoAtributo = "minimo";
                break;
            case Constantes.atb_accion:
                textoAtributo = "accion";
                break;
            case Constantes.atb_referencia:
                textoAtributo = "referencia";
                break;
            case Constantes.atb_path:
                textoAtributo = "path";
                break;
            case Constantes.atb_autoRepro:
                textoAtributo = "autorepro";
                break;
        }
    }

    public Valor getObjValor() {
        Valor v = null;
        switch (tipoValor) {
            case Constantes.tipo_cadena:
                return new Valor(valor,ConstantesFs.TIPO_CADENA);
            case Constantes.tipo_numero:
                return new Valor(valor,ConstantesFs.TIPO_NUMERO);
            case Constantes.tipo_booleano:
                if(valor.equals("verdadero")){
                     return new Valor("true",ConstantesFs.TIPO_BOOLEANO);
                }else if (valor.equals("falso")){
                     return new Valor("false",ConstantesFs.TIPO_BOOLEANO);
                }
            case Constantes.tipo_codigo:
                return new Valor(valor,ConstantesFs.TIPO_CADENA);
            case Constantes.tipo_objeto:
                return new Valor(valor,ConstantesFs.TIPO_CADENA);
            case Constantes.tipo_id:
                return new Valor(valor,ConstantesFs.TIPO_CADENA);
            default:
                return new Valor(valor,ConstantesFs.TIPO_CADENA);
        }
    }

}
