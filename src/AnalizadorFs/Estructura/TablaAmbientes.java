/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorFs.Estructura;

import java.util.Stack;

/**
 *
 * @author ivanl
 */
public class TablaAmbientes {

    Stack<TablaEjecucion> ambientes;

    public TablaAmbientes(Stack<TablaEjecucion> ambientes) {
        this.ambientes = ambientes;
    }

    public TablaAmbientes() {
        ambientes = new Stack<>();
    }

    public TablaEjecucion getUltimo() {
        return ambientes.peek();
    }

    public void addAmbiente(TablaEjecucion tabla) {
        ambientes.push(tabla);
    }

    public TablaEjecucion removeUltimo() {
        int tam = ambientes.size();
        ambientes.set(tam-1, null);
        return ambientes.pop();
    }

    public TablaEjecucion getPrimero() {
        return ambientes.get(0);
    }
    
    public TablaEjecucion getElemento(int numero) {
        return ambientes.get(numero);
    }
    
    public void add_a_Ambiente(String llave, Valor val){
        this.ambientes.peek().variables.put(llave, val);
    }
    
    public void add_a_Ambiente(int numero, String llave, Valor val){
        this.ambientes.get(numero).variables.put(llave, val);
    }
    
    public int getNumeroAmbientes(){
        return ambientes.size();
    }
}
