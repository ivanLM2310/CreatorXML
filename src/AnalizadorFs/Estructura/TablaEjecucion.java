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
public class TablaEjecucion {

    public Map<String, Valor> variables;
    String ambiente = "";

    public TablaEjecucion(Map<String, Valor> variables) {
        this.variables = variables;
    }

    public TablaEjecucion(String ambiente) {
        variables = new LinkedHashMap<>();
        this.ambiente = ambiente;
    }

    public TablaEjecucion() {
        variables = new LinkedHashMap<>();

    }

    public Map<String, Valor> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Valor> variables) {
        this.variables = variables;
    }

    public String getAmbiente() {
        return ambiente;
    }

    public void setAmbiente(String ambiente) {
        this.ambiente = ambiente;
    }

}
