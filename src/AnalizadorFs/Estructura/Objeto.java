/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorFs.Estructura;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author ivanl
 */
public class Objeto {

    public Map<String, Valor> valores;

    public Objeto() {
        valores = new LinkedHashMap();
    }
    
    public Objeto(Map<String, Valor> val) {
        valores = val;
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
    
    public Valor getCopia(){
        Set<String> keys = valores.keySet();
        Map<String, Valor> valNuevo = new LinkedHashMap();
        for(String k:keys){
            valNuevo.put(k, valores.get(k).getCopia());
        }
        return new Valor(new Objeto(valNuevo),ConstantesFs.TIPO_OBJETO);
    }
}
