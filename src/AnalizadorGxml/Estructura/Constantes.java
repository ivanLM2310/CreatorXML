/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorGxml.Estructura;

/**
 *
 * @author ivanl
 */
public class Constantes {

    //constantes tipo 1-10
    public static final int tipo_cadena = 1
            ,tipo_numero = 2
            ,tipo_booleano = 3
            ,tipo_caracter = 4
            ,tipo_codigo = 6
            ,tipo_objeto = 7
            ,tipo_id = 8;
            
    //constantes etiquetas 100-200
    public static final int etq_importar = 100
            ,etq_ventana = 101
            ,etq_contenedor=102
            ,etq_texto = 103
            ,etq_dato =104
            ,etq_controladores =105
            ,etq_listaDatos = 106
            ,etq_defecto = 107
            ,etq_multimedia = 108
            ,etq_boton = 109
            ,etq_enviar =110;
    //constantes atributos 201-300
    
     public static final int atb_id = 201
             ,atb_tipo = 202
             ,atb_color = 203
             ,atb_accionInicial =204
             ,atb_accionFinal = 205
             ,atb_x =206
             ,atb_y =207
             ,atb_alto =208
             ,atb_ancho =209
             ,atb_borde =210
             ,atb_nombre =211
             ,atb_fuente =212
             ,atb_tam = 213
             ,atb_negrita = 214
             ,atb_cursiva = 215
             ,atb_maximo = 216
             ,atb_minimo = 217
             ,atb_accion = 218
             ,atb_referencia =219
             ,atb_path =220
             ,atb_autoRepro=221;
        //tipos de controladores
    public static final int tcont_texto = 222
            ,tcont_numerico = 223
            ,tcont_textoArea =224
            ,tcont_desplegable =225;
    
}
