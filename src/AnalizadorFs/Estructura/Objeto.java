/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorFs.Estructura;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author ivanl
 */
public class Objeto {

    public Map<String, Valor> valores;

    public Objeto() {
        valores = new LinkedHashMap();
    }
    
    public void addAtributoValor(String clave, Valor val){
        if(!valores.containsKey(clave)){
            valores.put(clave, val);
        }
    }
    
    public Valor getValor(String clave){
        
        if(valores.containsKey(clave)){
            return valores.get(clave);
        }
        return new Valor("", ConstantesFs.TIPO_NULL);
    }
}
