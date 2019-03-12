/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorFs.Estructura;

import java.util.ArrayList;

/**
 *
 * @author ivanl
 */
public class DocumentoFs {
    
    ArrayList<NodoArbol> documentos = new ArrayList();
    
    public DocumentoFs(NodoArbol raiz){
        this.documentos.add(raiz);
        NodoArbol nodoImportar = raiz.BuscarNodo(ConstantesFs.LISTA_IMPORTAR);
        
        if(nodoImportar != null){
            for(NodoArbol nImport : nodoImportar.getHijosNodo()){
                
            }
        }
    }
    
    public void ejecutarDocumento(){
        EjecutarFs e = new EjecutarFs(this);
        e.iniciarEjecucion();
    }
    
    public void addDocumento(NodoArbol nodo){
        
    }
    
    
    
}
