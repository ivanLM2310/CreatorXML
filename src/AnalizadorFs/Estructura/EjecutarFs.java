/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorFs.Estructura;

import AnalizadorGxml.ErrorEjecucion;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import creatorxml.Main;

/**
 *
 * @author ivanl
 */
public class EjecutarFs {

    public String cadenaSalida = "";
    public TablaAmbientes ambienteGlobal;
    public String nombreArchivo;
    public ArrayList<ErrorEjecucion> listaErrores = new ArrayList();
    String strAmbito;
    public Map<String, NodoArbol> metodos;
    public DocumentoFs documentoFinal;

    public EjecutarFs(DocumentoFs doc) {

        ambienteGlobal = new TablaAmbientes();
        ambienteGlobal.addAmbiente(new TablaEjecucion("global"));
        strAmbito = "main";
        metodos = new LinkedHashMap();
        this.documentoFinal = doc;
    }

    public void iniciarEjecucion() {
        ArrayList<NodoArbol> sentenciasIniciales = llenarTablaMetodos(documentoFinal);
        NodoArbol raiz = new NodoArbol(ConstantesFs.LISTA_SENTENCIAS);
        raiz.setHijosNodo(sentenciasIniciales);
        ejecutarSentencias(raiz, ambienteGlobal);

    }

    private ArrayList<NodoArbol> llenarTablaMetodos(DocumentoFs doc) {

        ArrayList<NodoArbol> sentencias = new ArrayList<>();
        doc.documentos.forEach((raiz) -> {
            if (raiz.isEtiquetaIgual(ConstantesFs.DOCUMENTO)) {

                NodoArbol listaSents = raiz.BuscarNodo(ConstantesFs.LISTA_SENTENCIAS);
                if (listaSents != null) {
                    listaSents.getHijosNodo().forEach((docInicio) -> {
                        if (docInicio.isEtiquetaIgual(ConstantesFs.FUNCION)) {
                            if (!metodos.containsKey("fun_" + docInicio.getValor())) {
                                metodos.put("fun_" + docInicio.getValor(), docInicio);
                            }
                        } else {
                            sentencias.add(docInicio);
                        }
                    });

                }
            }
        });
        return sentencias;
    }

    public Valor ejecutarMetodo(NodoArbol raizMetodo, TablaAmbientes ambientes) {
        //ver que tipo de parametros son los que se van a valuar
        a +=1;
        if(a % 100 == 0){
            System.err.println("salida:"+a);
        }
        String cadenaError = "";
        ArrayList<Valor> parametros = new ArrayList();

        int p = 0;
        for (NodoArbol parametro : raizMetodo.getElemento(0).getHijosNodo()) {
            //valores que tiene  cada parametro
            parametros.add(evaluarExp(parametro, ambientes));
            p += 1;
        }

        //encontrar metodo en la lista
        NodoArbol met = null;
        // buscar el metodo
        if (metodos.containsKey("fun_" + raizMetodo.getValor())) {

            String tempAmbiente = strAmbito;
            strAmbito += "-" + "fun_" + raizMetodo.getValor();
            met = metodos.get("fun_" + raizMetodo.getValor());
            TablaEjecucion tablaMetodo = new TablaEjecucion();

            for (int i = 0; i < met.getElemento(0).getTamañoH(); i++) {
                //aqui creo una nueva tabla Ejecucion solo del metodo para apilarla
                tablaMetodo.variables.put(met.getElemento(0).getElemento(i).getValor(), parametros.get(i));
            }
            //agregando a la pila la nueva tabla 
            TablaAmbientes ambienteMetodo = new TablaAmbientes();
            ambienteMetodo.addAmbiente(ambientes.getPrimero());
            ambienteMetodo.addAmbiente(tablaMetodo);

            Valor valorR = ejecutarSentencias(met.getElemento(1), ambienteMetodo);
            ambienteMetodo = null;
            strAmbito = tempAmbiente;
            return valorR;

        }
        return detectarError("no existe el Metodo :" + raizMetodo.getValor(), ambientes, raizMetodo);

    }

    private Valor ejecutarSentencias(NodoArbol raiz, TablaAmbientes ambientes) {
        for (NodoArbol nSentencia : raiz.getHijosNodo()) {
            Valor v = new Valor("", ConstantesFs.TIPO_NULL);
            if (nSentencia.isEtiquetaIgual(ConstantesFs.DECLARAR)) {
                ejecutarDeclaracion(nSentencia, ambientes);
            } else if (nSentencia.isEtiquetaIgual(ConstantesFs.ASIGNACION)) {
                ejecutarAsignacion(nSentencia, ambientes);
            } else if (nSentencia.isEtiquetaIgual(ConstantesFs.SI_PADRE)) {
                TablaEjecucion tab = new TablaEjecucion("si");
                ambientes.addAmbiente(tab);
                v = ejecutarSi(nSentencia, ambientes);
                ambientes.removeUltimo();

            } else if (nSentencia.isEtiquetaIgual(ConstantesFs.RETORNAR)) {

                return ejecutarRetornar(nSentencia, ambientes);
            } else if (nSentencia.isEtiquetaIgual(ConstantesFs.DETENER)) {

                v = ejecutarDetener(nSentencia, ambientes);
                if (v.isTipoIgual(ConstantesFs.RETORNO_BREAK)) {
                    return v;
                }

            } else if (nSentencia.isEtiquetaIgual(ConstantesFs.SELECCIONA)) {

                TablaEjecucion tab = new TablaEjecucion("ciclo");
                ambientes.addAmbiente(tab);
                v = ejecutarSelecciona(nSentencia, ambientes);
                ambientes.removeUltimo();

            } else if (nSentencia.isEtiquetaIgual(ConstantesFs.LLAMADA_METODO)) {
                //metodos que no retornan nada
                ejecutarMetodo(nSentencia, ambientes);
                //evaluarMetodo(ClaseActual, (NodoEjecutarMetodo) nSentencia, variablesLocales);
            } else if (nSentencia.isEtiquetaIgual(ConstantesFs.IMPRIMIR)) {
                ejecutarImprimir(nSentencia, ambientes);
            }

            if (v.isTipoIgual(ConstantesFs.RETORNO_BREAK)) {
                return v;
            }

            if (v.getProveniente() == ConstantesFs.RETORNO_VALOR) {
                return v;
            }
        }
        return new Valor("metodo", ConstantesFs.RETORNO_METODO);
    }

    public void ejecutarImprimir(NodoArbol raiz, TablaAmbientes ambientes) {
        Valor salida = evaluarExp(raiz.getElemento(0), ambientes);
        if (!salida.isTipoIgual(ConstantesFs.TIPO_NULL)) {
            String consola = salida.getString();
            Main.consola.append(consola + "\n");
            //cadenaSalida += salida.valor.toString() + "\n";
        } else {
            String cadenaError = "la expresion del Imprimir, no es valida";
            detectarError(cadenaError, ambientes, raiz);
        }
    }

    public void ejecutarDeclaracion(NodoArbol raiz, TablaAmbientes ambientes) {

        if (raiz.getTamañoH() == 1) {
            for (NodoArbol v : raiz.getElemento(0).getHijosNodo()) {
                if (!ambientes.getUltimo().variables.containsKey(v.getValor())) {
                    ambientes.add_a_Ambiente(v.getValor(), new Valor("", ConstantesFs.TIPO_NULL));
                } else {
                    detectarError("ya existe una variable con el nombre:" + v.getValor(), ambientes, v);
                }
            }

        } else if (raiz.getTamañoH() == 2) {
            NodoArbol ultimo = null;
            int i = 0;
            for (i = 0; i < raiz.getElemento(0).getTamañoH() - 1; i++) {
                NodoArbol v = raiz.getElemento(0).getElemento(i);
                if (!ambientes.getUltimo().variables.containsKey(v.getValor())) {
                    ambientes.add_a_Ambiente(v.getValor(), new Valor("", ConstantesFs.TIPO_NULL));
                } else {
                    detectarError("ya existe una variable con el nombre:" + v.getValor(), ambientes, v);
                }

            }
            ultimo = raiz.getElemento(0).getElemento(i);
            if (!ambientes.getUltimo().variables.containsKey(ultimo.getValor())) {
                Valor v = evaluarExp(raiz.getElemento(1), ambientes);
                ambientes.add_a_Ambiente(ultimo.getValor(), v);
            } else {
                detectarError("ya existe una variable con el nombre:" + ultimo.getValor(), ambientes, ultimo);
            }

        }
    }

    public Valor ejecutarAsignacion(NodoArbol raiz, TablaAmbientes ambientes) {

        NodoArbol ladoAsig = raiz.getElemento(0);
        Valor variable = evaluarElemento(ladoAsig, ambientes);
        if (!variable.isNull()) {
            Valor result = evaluarExp(raiz.getElemento(0), ambientes);
            if (!result.isNull()) {

                variable.copiarValor(result);
                return result;
            }
            return detectarError("Error en la expresion para asignar a:" + raiz.getValor(), ambientes, raiz);
        }
        return detectarError("la variable a la que le intenta asignar no existe:" + raiz.getValor(), ambientes, raiz);
    }

    int a = 0;
    public Valor ejecutarRetornar(NodoArbol raiz, TablaAmbientes ambientes) {
        
        if (!raiz.isVaciaH()) {
            Valor v = evaluarExp(raiz.getElemento(0), ambientes);
            v.setProveniente(ConstantesFs.RETORNO_VALOR);
            return v;
        } else {
            Valor v = new Valor("metodo", ConstantesFs.RETORNO_VALOR);;
            v.setProveniente(ConstantesFs.RETORNO_VALOR);
            return v;
        }

    }

    public Valor ejecutarSi(NodoArbol raiz, TablaAmbientes ambientes) {
        for (NodoArbol nSi : raiz.hijosNodo) {
            Valor valorCondicion = evaluarExp(nSi.getElemento(0), ambientes);
            if (nSi.getHijosNodo().size() == 2) {
                if (valorCondicion.isTipoIgual(ConstantesFs.TIPO_BOOLEANO)) {
                    if (valorCondicion.getBoolean()) {
                        Valor retorno = ejecutarSentencias(nSi.getElemento(1), ambientes);
                        if (retorno.isTipoIgual(ConstantesFs.RETORNO_BREAK)) {
                            return retorno;
                        }
                        if (retorno.isProveniente(ConstantesFs.RETORNO_VALOR)) {
                            return retorno;
                        }
                        return new Valor("", ConstantesFs.RETORNO_SI);
                    }
                } else {
                    return detectarError("la expresion del SI no es valida", ambientes, raiz);
                }

            }
        }
        return new Valor("", ConstantesFs.RETORNO_SI);
    }

    public Valor ejecutarDetener(NodoArbol raiz, TablaAmbientes ambientes) {
        for (int i = ambientes.ambientes.size() - 1; i >= 0; i--) {
            if (ambientes.getElemento(i).getAmbiente().equals("ciclo")) {
                return new Valor("", ConstantesFs.RETORNO_BREAK);
            }
        }
        return new Valor("", ConstantesFs.TIPO_NULL);
    }

    public Valor ejecutarSelecciona(NodoArbol raiz, TablaAmbientes ambientes) {
        NodoArbol padreCasos = raiz.getElemento(1);
        NodoArbol exp = raiz.getElemento(0);

        Valor valExp = evaluarExp(exp, ambientes);
        boolean encontrado = false;
        if (valExp.isTipoIgual(ConstantesFs.TIPO_NULL)) {
            detectarError("la expresion del selecciona no es valido", ambientes, raiz);
            return new Valor("", ConstantesFs.RETORNO_CICLO);
        }
        for (NodoArbol caso : padreCasos.getHijosNodo()) {
            if (caso.isEtiquetaIgual(ConstantesFs.CASO)) {
                if (!encontrado) {
                    Valor valExpCaso = evaluarExp(caso.getElemento(0), ambientes);
                    if (valExp.valor.equals(valExpCaso.valor)) {
                        encontrado = true;
                    } else {
                        continue;
                    }
                }
                if (caso.getHijosNodo().size() == 2) {
                    Valor retorno = ejecutarSentencias(caso.getElemento(1), ambientes);
                    if (retorno.isTipoIgual(ConstantesFs.RETORNO_BREAK)) {
                        break;
                    }
                    if (retorno.isProveniente(ConstantesFs.RETORNO_VALOR)) {
                        return retorno;
                    }
                }
            }
        }
        if (!encontrado) {
            NodoArbol casoDefecto = padreCasos.BuscarNodo(ConstantesFs.DEFECTO);
            if (casoDefecto != null) {
                if (casoDefecto.getHijosNodo().size() == 2) {
                    Valor retorno = ejecutarSentencias(casoDefecto.getElemento(1), ambientes);
                    if (retorno.isTipoIgual(ConstantesFs.RETORNO_BREAK)) {

                    }
                    if (retorno.isProveniente(ConstantesFs.RETORNO_VALOR)) {
                        return retorno;
                    }
                }
            }
        }
        return new Valor("", ConstantesFs.RETORNO_CICLO);
    }

    // <editor-fold defaultstate="collapsed" desc="ejecutar expresion">
    public Valor evaluarExp(NodoArbol raizOperacion, TablaAmbientes ambientes) {
        if (raizOperacion.getHijosNodo().isEmpty()
                || raizOperacion.isTipoIgual(ConstantesFs.ID)
                || raizOperacion.isTipoIgual(ConstantesFs.ID_VECTOR)
                || raizOperacion.isEtiquetaIgual(ConstantesFs.VECTOR_DECLARAR)
                || raizOperacion.isEtiquetaIgual(ConstantesFs.OBJETO_DECLARAR)
                || raizOperacion.isEtiquetaIgual(ConstantesFs.LLAMADA_METODO)) {
            return evaluarElemento(raizOperacion, ambientes);
        } else if (raizOperacion.getHijosNodo().size() == 1) {
            Valor vI = evaluarExp(raizOperacion.getHijosNodo().get(0), ambientes);
            switch (raizOperacion.getConstEtiqueta()) {
                case ConstantesFs.NOT:
                    return evaluarNot(vI, ambientes);
            }
        } else if (raizOperacion.getHijosNodo().size() == 2) {
            Valor vI = evaluarExp(raizOperacion.getHijosNodo().get(0), ambientes);
            Valor vD = evaluarExp(raizOperacion.getHijosNodo().get(1), ambientes);
            switch (raizOperacion.getConstEtiqueta()) {
                case ConstantesFs.SUMA:
                    return evaluarSuma(vI, vD, ambientes, raizOperacion);
                case ConstantesFs.RESTA:
                    return evaluarResta(vI, vD, ambientes, raizOperacion);
                case ConstantesFs.MULTIPLICACION:
                    return evaluarMultiplicacion(vI, vD, ambientes, raizOperacion);
                case ConstantesFs.DIVISION:
                    return evaluarDividir(vI, vD, ambientes, raizOperacion);
                case ConstantesFs.POTENCIA:
                    return evaluarPotencia(vI, vD, ambientes, raizOperacion);
                case ConstantesFs.MAYORQ:
                    return evaluarMayorQue(vI, vD, ambientes, raizOperacion);
                case ConstantesFs.MENORQ:
                    return evaluarMenorQue(vI, vD, ambientes, raizOperacion);
                case ConstantesFs.MAYORIGUALQ:
                    return evaluarMayorIgualQue(vI, vD, ambientes, raizOperacion);
                case ConstantesFs.MENORIGUALQ:
                    return evaluarMenorIgualQue(vI, vD, ambientes, raizOperacion);
                case ConstantesFs.IGUALIGUAL:
                    return evaluarIgualIgualQue(vI, vD, ambientes, raizOperacion);
                case ConstantesFs.DIFERENTE:
                    return evaluarDiferenteQue(vI, vD, ambientes, raizOperacion);
                case ConstantesFs.OR:
                    return evaluarOr(vI, vD, ambientes, raizOperacion);
                case ConstantesFs.AND:
                    return evaluarAnd(vI, vD, ambientes, raizOperacion);
                case ConstantesFs.AUMENTO:
                    return evaluarAumento(vI, vD, ambientes, raizOperacion);
                case ConstantesFs.DISMINUCION:
                    return evaluarDisminucion(vI, vD, ambientes, raizOperacion);

            }
        }
        return new Valor("", ConstantesFs.TIPO_NULL);
    }

    public Valor evaluarSuma(Valor vI, Valor vD, TablaAmbientes ambientes, NodoArbol raiz) {

        try {
            switch (vI.etqTipo) {
                case ConstantesFs.TIPO_CADENA:
                    switch (vD.etqTipo) {
                        case ConstantesFs.TIPO_BOOLEANO:
                            return new Valor(vI.getString() + boolToString(vD), ConstantesFs.TIPO_CADENA);
                        case ConstantesFs.TIPO_CADENA:
                            return new Valor(vI.getString() + vD.getString(), ConstantesFs.TIPO_CADENA);
                        case ConstantesFs.TIPO_NUMERO:
                            return new Valor(vI.getString() + vD.getString(), ConstantesFs.TIPO_CADENA);
                    }
                    break;
                case ConstantesFs.TIPO_NUMERO:
                    switch (vD.etqTipo) {
                        case ConstantesFs.TIPO_CADENA:
                            return new Valor(vI.getString() + vD.getString(), ConstantesFs.TIPO_CADENA);
                        case ConstantesFs.TIPO_NUMERO:
                            return new Valor(vI.getNumber() + vD.getNumber(), ConstantesFs.TIPO_NUMERO);
                    }
                    break;
            }
        } catch (Exception e) {

        }
        return detectarError(vI, vD, ambientes, raiz);
    }

    public Valor evaluarResta(Valor vI, Valor vD, TablaAmbientes ambientes, NodoArbol raiz) {

        try {
            switch (vI.etqTipo) {
                case ConstantesFs.TIPO_NUMERO:
                    switch (vD.etqTipo) {
                        case ConstantesFs.TIPO_BOOLEANO:
                            return new Valor(vI.getNumber() - boolToInt(vD.valor), ConstantesFs.TIPO_NUMERO);
                        case ConstantesFs.TIPO_NUMERO:
                            return new Valor(vI.getNumber() - vD.getNumber(), ConstantesFs.TIPO_NUMERO);
                    }
                    break;
            }
        } catch (Exception e) {

        }
        return detectarError(vI, vD, ambientes, raiz);

    }

    public Valor evaluarMultiplicacion(Valor vI, Valor vD, TablaAmbientes ambientes, NodoArbol raiz) {
        try {
            switch (vI.etqTipo) {
                case ConstantesFs.TIPO_NUMERO:
                    switch (vD.etqTipo) {
                        case ConstantesFs.TIPO_NUMERO:
                            return new Valor(vI.getNumber() * vD.getNumber(), ConstantesFs.TIPO_NUMERO);
                    }
                    break;
            }
        } catch (Exception e) {

        }
        return detectarError(vI, vD, ambientes, raiz);
    }

    public Valor evaluarDividir(Valor vI, Valor vD, TablaAmbientes ambientes, NodoArbol raiz) {
        try {
            switch (vI.etqTipo) {
                case ConstantesFs.TIPO_NUMERO:
                    switch (vD.etqTipo) {
                        case ConstantesFs.TIPO_NUMERO:
                            return new Valor(vI.getNumber() / vD.getNumber(), ConstantesFs.TIPO_NUMERO);
                    }
                    break;
            }
        } catch (Exception e) {

        }
        return detectarError(vI, vD, ambientes, raiz);
    }

    public Valor evaluarPotencia(Valor vI, Valor vD, TablaAmbientes ambientes, NodoArbol raiz) {
        try {
            switch (vI.etqTipo) {

                case ConstantesFs.TIPO_NUMERO:
                    switch (vD.etqTipo) {
                        case ConstantesFs.TIPO_NUMERO:
                            return new Valor(Math.pow(vI.getNumber(), vD.getNumber()), ConstantesFs.TIPO_NUMERO);
                    }
                    break;
            }
        } catch (Exception e) {

        }
        return detectarError(vI, vD, ambientes, raiz);
    }

    public Valor evaluarAumento(Valor vI, Valor vD, TablaAmbientes ambientes, NodoArbol raiz) {
        try {
            switch (vI.etqTipo) {
                case ConstantesFs.TIPO_NUMERO:
                    switch (vD.etqTipo) {
                        case ConstantesFs.TIPO_NUMERO:
                            //agregar codigo de asignacion ;
                            return new Valor(vI.getNumber() + vD.getNumber(), ConstantesFs.TIPO_NUMERO);
                    }
                    break;
            }
        } catch (Exception e) {

        }
        return detectarError(vI, vD, ambientes, raiz);
    }

    public Valor evaluarDisminucion(Valor vI, Valor vD, TablaAmbientes ambientes, NodoArbol raiz) {
        try {
            switch (vI.etqTipo) {
                case ConstantesFs.TIPO_NUMERO:
                    switch (vD.etqTipo) {
                        //agregar codigo de asignacion ;
                        case ConstantesFs.TIPO_NUMERO:
                            return new Valor(vI.getNumber() - vD.getNumber(), ConstantesFs.TIPO_NUMERO);
                    }
                    break;
            }
        } catch (Exception e) {

        }
        return detectarError(vI, vD, ambientes, raiz);
    }

    public Valor evaluarMayorQue(Valor vI, Valor vD, TablaAmbientes ambientes, NodoArbol raiz) {
        try {
            switch (vI.etqTipo) {
                case ConstantesFs.TIPO_BOOLEANO:
                    switch (vD.etqTipo) {
                        case ConstantesFs.TIPO_BOOLEANO:
                            return new Valor(boolToInt(vI.valor) > boolToInt(vD.valor), ConstantesFs.TIPO_BOOLEANO);
                    }
                    break;
                case ConstantesFs.TIPO_CADENA:
                    switch (vD.etqTipo) {
                        case ConstantesFs.TIPO_CADENA:
                            return new Valor(vI.getString().compareTo(vD.getString()) > 0, ConstantesFs.TIPO_BOOLEANO);
                    }
                    break;

                case ConstantesFs.TIPO_NUMERO:
                    switch (vD.etqTipo) {
                        case ConstantesFs.TIPO_NUMERO:
                            return new Valor(vI.getNumber() > vD.getNumber(), ConstantesFs.TIPO_BOOLEANO);
                    }
                    break;
            }
        } catch (Exception e) {

        }
        return detectarError(vI, vD, ambientes, raiz);
    }

    public Valor evaluarMenorQue(Valor vI, Valor vD, TablaAmbientes ambientes, NodoArbol raiz) {
        try {
            switch (vI.etqTipo) {
                case ConstantesFs.TIPO_BOOLEANO:
                    switch (vD.etqTipo) {
                        case ConstantesFs.TIPO_BOOLEANO:
                            return new Valor(boolToInt(vI.valor) < boolToInt(vD.valor), ConstantesFs.TIPO_BOOLEANO);
                    }
                    break;
                case ConstantesFs.TIPO_CADENA:
                    switch (vD.etqTipo) {
                        case ConstantesFs.TIPO_CADENA:
                            return new Valor(vI.getString().compareTo(vD.getString()) < 0, ConstantesFs.TIPO_BOOLEANO);
                    }
                    break;
                case ConstantesFs.TIPO_NUMERO:
                    switch (vD.etqTipo) {
                        case ConstantesFs.TIPO_NUMERO:
                            return new Valor(vI.getNumber() < vD.getNumber(), ConstantesFs.TIPO_BOOLEANO);
                    }
                    break;
            }
        } catch (Exception e) {

        }
        return detectarError(vI, vD, ambientes, raiz);
    }

    public Valor evaluarMayorIgualQue(Valor vI, Valor vD, TablaAmbientes ambientes, NodoArbol raiz) {
        try {
            switch (vI.etqTipo) {
                case ConstantesFs.TIPO_BOOLEANO:
                    switch (vD.etqTipo) {
                        case ConstantesFs.TIPO_BOOLEANO:
                            return new Valor(boolToInt(vI.valor) >= boolToInt(vD.valor), ConstantesFs.TIPO_BOOLEANO);
                    }
                    break;
                case ConstantesFs.TIPO_CADENA:
                    switch (vD.etqTipo) {
                        case ConstantesFs.TIPO_CADENA:
                            return new Valor(vI.getString().compareTo(vD.getString()) >= 0, ConstantesFs.TIPO_BOOLEANO);
                    }
                    break;

                case ConstantesFs.TIPO_NUMERO:
                    switch (vD.etqTipo) {
                        case ConstantesFs.TIPO_NUMERO:
                            return new Valor(vI.getNumber() >= vD.getNumber(), ConstantesFs.TIPO_BOOLEANO);
                    }
                    break;
            }
        } catch (Exception e) {

        }
        return detectarError(vI, vD, ambientes, raiz);
    }

    public Valor evaluarMenorIgualQue(Valor vI, Valor vD, TablaAmbientes ambientes, NodoArbol raiz) {
        try {
            switch (vI.etqTipo) {
                case ConstantesFs.TIPO_BOOLEANO:
                    switch (vD.etqTipo) {
                        case ConstantesFs.TIPO_BOOLEANO:
                            return new Valor(boolToInt(vI.valor) <= boolToInt(vD.valor), ConstantesFs.TIPO_BOOLEANO);
                    }
                    break;
                case ConstantesFs.TIPO_CADENA:
                    switch (vD.etqTipo) {
                        case ConstantesFs.TIPO_CADENA:
                            return new Valor(vI.getString().compareTo(vD.getString()) <= 0, ConstantesFs.TIPO_BOOLEANO);
                    }
                    break;
                case ConstantesFs.TIPO_NUMERO:
                    switch (vD.etqTipo) {
                        case ConstantesFs.TIPO_NUMERO:
                            return new Valor(vI.getNumber() <= vD.getNumber(), ConstantesFs.TIPO_BOOLEANO);
                    }
                    break;
            }
        } catch (Exception e) {

        }
        return detectarError(vI, vD, ambientes, raiz);
    }

    public Valor evaluarIgualIgualQue(Valor vI, Valor vD, TablaAmbientes ambientes, NodoArbol raiz) {
        try {
            switch (vI.etqTipo) {
                case ConstantesFs.TIPO_BOOLEANO:
                    switch (vD.etqTipo) {
                        case ConstantesFs.TIPO_BOOLEANO:
                            return new Valor(boolToInt(vI.valor) == boolToInt(vD.valor), ConstantesFs.TIPO_BOOLEANO);
                    }
                    break;
                case ConstantesFs.TIPO_CADENA:
                    switch (vD.etqTipo) {
                        case ConstantesFs.TIPO_CADENA:
                            return new Valor(vI.getString().compareTo(vD.getString()) == 0, ConstantesFs.TIPO_BOOLEANO);
                    }
                    break;
                case ConstantesFs.TIPO_NUMERO:
                    switch (vD.etqTipo) {
                        case ConstantesFs.TIPO_NUMERO:
                            return new Valor(vI.getNumber() == vD.getNumber(), ConstantesFs.TIPO_BOOLEANO);
                    }
                    break;
            }
        } catch (Exception e) {

        }
        return detectarError(vI, vD, ambientes, raiz);
    }

    public Valor evaluarDiferenteQue(Valor vI, Valor vD, TablaAmbientes ambientes, NodoArbol raiz) {
        try {
            switch (vI.etqTipo) {
                case ConstantesFs.TIPO_BOOLEANO:
                    switch (vD.etqTipo) {
                        case ConstantesFs.TIPO_BOOLEANO:
                            return new Valor(boolToInt(vI.valor) == boolToInt(vD.valor), ConstantesFs.TIPO_BOOLEANO);
                    }
                    break;
                case ConstantesFs.TIPO_CADENA:
                    switch (vD.etqTipo) {
                        case ConstantesFs.TIPO_CADENA:
                            return new Valor(vI.getString().compareTo(vD.getString()) == 0, ConstantesFs.TIPO_BOOLEANO);
                    }
                    break;
                case ConstantesFs.TIPO_NUMERO:
                    switch (vD.etqTipo) {
                        case ConstantesFs.TIPO_NUMERO:
                            return new Valor(vI.getNumber() == vD.getNumber(), ConstantesFs.TIPO_BOOLEANO);
                    }
                    break;
            }
        } catch (Exception e) {

        }
        return detectarError(vI, vD, ambientes, raiz);
    }

    public Valor evaluarOr(Valor vI, Valor vD, TablaAmbientes ambientes, NodoArbol raiz) {
        try {
            if (vI.isTipoIgual(ConstantesFs.TIPO_BOOLEANO) && vD.isTipoIgual(ConstantesFs.TIPO_BOOLEANO)) {
                if (vI.getBoolean() || vD.getBoolean()) {
                    return new Valor(true, ConstantesFs.TIPO_BOOLEANO);
                } else {
                    return new Valor(false, ConstantesFs.TIPO_BOOLEANO);
                }
            }

        } catch (Exception e) {

        }
        return detectarError(vI, vD, ambientes, raiz);

    }

    public Valor evaluarAnd(Valor vI, Valor vD, TablaAmbientes ambientes, NodoArbol raiz) {
        try {
            if (vI.isTipoIgual(ConstantesFs.TIPO_BOOLEANO) && vD.isTipoIgual(ConstantesFs.TIPO_BOOLEANO)) {
                if (vI.getBoolean() && vD.getBoolean()) {
                    return new Valor(true, ConstantesFs.TIPO_BOOLEANO);
                } else {
                    return new Valor(false, ConstantesFs.TIPO_BOOLEANO);
                }
            }

        } catch (Exception e) {

        }
        return detectarError(vI, vD, ambientes, raiz);

    }

    public Valor evaluarNot(Valor vI, TablaAmbientes ambientes) {
        try {
            if (vI.isTipoIgual(ConstantesFs.TIPO_BOOLEANO)) {
                if (!vI.getBoolean()) {
                    return new Valor(true, ConstantesFs.TIPO_BOOLEANO);
                } else {
                    return new Valor(false, ConstantesFs.TIPO_BOOLEANO);
                }
            }

        } catch (Exception e) {

        }
        return detectarError(vI, ambientes);

    }
    // </editor-fold>

    public Valor evaluarElemento(NodoArbol raizOperacion, TablaAmbientes ambientes) {

        switch (raizOperacion.getConstTipo()) {
            case ConstantesFs.TIPO_BOOLEANO:
            case ConstantesFs.TIPO_CADENA:
            case ConstantesFs.TIPO_NUMERO:
                return new Valor(raizOperacion.getValor(), raizOperacion.getConstTipo());
            case ConstantesFs.ID: {
                for (int i = ambientes.getNumeroAmbientes() - 1; i >= 0; i--) {
                    if (ambientes.getElemento(i).variables.containsKey(raizOperacion.getValor())) {
                        if (raizOperacion.getTamañoH() == 0) {

                        } else if (raizOperacion.getTamañoH() == 1) {

                        }
                        return ambientes.getElemento(i).variables.get(raizOperacion.getValor());
                    }
                }
                return detectarError("la variable \"" + raizOperacion.getValor() + "\" no ha sido declarada", ambientes, raizOperacion);
            }
            case ConstantesFs.ID_VECTOR: {
                for (int i = ambientes.getNumeroAmbientes() - 1; i >= 0; i--) {
                    if (ambientes.getElemento(i).variables.containsKey(raizOperacion.getValor())) {
                        Valor v = ambientes.getElemento(i).variables.get(raizOperacion.getValor());
                        if (v.isTipoIgual(ConstantesFs.VECTOR_HETEROGENEO)
                                || v.isTipoIgual(ConstantesFs.VECTOR_HOMOGENEO)) {
                            Valor indice = evaluarExp(raizOperacion.getElemento(0), ambientes);
                            if (indice.isTipoIgual(ConstantesFs.TIPO_NUMERO)) {
                                try {
                                    ArrayList vectorCons = v.getVector();
                                    int numIndice = (int) indice.getNumber();
                                    Valor indiceVector = (Valor) vectorCons.get(numIndice);

                                    if (raizOperacion.getTamañoH() == 2) {

                                    }
                                    return indiceVector;
                                } catch (Exception e) {
                                    return detectarError("se tubo un problema con el indice \"" + raizOperacion.getValor() + "\" no es un numero", ambientes, raizOperacion);
                                }
                            } else {
                                return detectarError("el indice de la variable \"" + raizOperacion.getValor() + "\" no es un numero", ambientes, raizOperacion);
                            }
                        } else {
                            return detectarError("la variable \"" + raizOperacion.getValor() + "\" no es un vector", ambientes, raizOperacion);
                        }
                    }
                }
                return detectarError("la variable \"" + raizOperacion.getValor() + "\" no ha sido declarada", ambientes, raizOperacion);
            }
        }
        switch (raizOperacion.getConstEtiqueta()) {

            case ConstantesFs.LLAMADAS_METODOS_NATIVOS: {

                break;
            }
            case ConstantesFs.LLAMADA_METODO: {
                Valor retornoMetodo = ejecutarMetodo(raizOperacion, ambientes);
                if (!retornoMetodo.isTipoIgual(ConstantesFs.RETORNO_METODO)) {
                    return retornoMetodo;
                } else {
                    return detectarError("el metodo \"" + raizOperacion.getValor() + "\" no retorna ningun valor", ambientes, raizOperacion);
                }

            }
        }
        return new Valor("", ConstantesFs.TIPO_NULL);

    }

    public Valor evaluarPunto(NodoArbol raizOperacion, TablaAmbientes ambientes, Valor vAnterior) {
        int tamTotal = raizOperacion.getTamañoH();
        NodoArbol actual;
        for (int i = 0; i < tamTotal; i++) {
            actual = raizOperacion.getElemento(i);
            if (vAnterior.isTipoIgual(ConstantesFs.TIPO_OBJETO)) {
                if (actual.isEtiquetaIgual(ConstantesFs.ID)) {
                    Objeto ob = (Objeto) vAnterior.valor;
                    vAnterior = ob.getValor(actual.valor);

                }
            } else if (vAnterior.isTipoIgual(ConstantesFs.VECTOR_HOMOGENEO)) {
                if (actual.isEtiquetaIgual(ConstantesFs.LLAMADAS_METODOS_NATIVOS)) {
                    switch (actual.getConstTipo()) {
                        case ConstantesFs.DESCENDENTE: {
                            if (vAnterior.isVectorHomogeneo(ConstantesFs.TIPO_CADENA)) {
                                ArrayList<Valor> var = vAnterior.getVector();
                                Collections.sort(var, new Comparator<Valor>() {
                                    @Override
                                    public int compare(Valor v1, Valor v2) {
                                        return v2.getString().compareTo(v1.getString());
                                    }
                                });
                                vAnterior.setValor(var);
                            } else if (vAnterior.isVectorHomogeneo(ConstantesFs.TIPO_BOOLEANO)) {
                                ArrayList<Valor> var = vAnterior.getVector();
                                Collections.sort(var, new Comparator<Valor>() {
                                    @Override
                                    public int compare(Valor v1, Valor v2) {
                                        return new Integer((int) v2.getNumber()).compareTo((int) v1.getNumber());
                                    }
                                });
                                vAnterior.setValor(var);
                            }
                            break;
                        }
                        case ConstantesFs.ASCENDENTE: {
                            if (vAnterior.isVectorHomogeneo(ConstantesFs.TIPO_CADENA)) {
                                ArrayList<Valor> var = vAnterior.getVector();
                                Collections.sort(var, new Comparator<Valor>() {
                                    @Override
                                    public int compare(Valor v1, Valor v2) {
                                        return v1.getString().compareTo(v2.getString());
                                    }
                                });
                                vAnterior.setValor(var);
                            } else if (vAnterior.isVectorHomogeneo(ConstantesFs.TIPO_NUMERO)) {
                                ArrayList<Valor> var = vAnterior.getVector();
                                Collections.sort(var, new Comparator<Valor>() {
                                    @Override
                                    public int compare(Valor v1, Valor v2) {
                                        return new Integer((int) v1.getNumber()).compareTo((int) v2.getNumber());
                                    }
                                });
                                vAnterior.setValor(var);
                            }
                            break;
                        }
                        case ConstantesFs.INVERTIR: {
                            ArrayList<Valor> var = vAnterior.getVector();
                            ArrayList<Valor> nuevo = new ArrayList();
                            int tamV = var.size();
                            for (int j = 0; j < tamV; j++) {
                                nuevo.add(0, vAnterior);
                            }
                            vAnterior.setValor(nuevo);
                            break;
                        }
                        case ConstantesFs.MAXIMO: {

                            ArrayList<Valor> var = vAnterior.getVector();
                            if (vAnterior.isVectorHomogeneo(ConstantesFs.TIPO_NUMERO)) {

                                int tamV = var.size();
                                double numSalida = 0;
                                if (tamV > 0) {
                                    numSalida = var.get(0).getNumber();
                                    for (int j = 1; j < tamV; j++) {
                                        numSalida = (numSalida < var.get(j).getNumber()) ? var.get(j).getNumber() : numSalida;
                                    }
                                    vAnterior.setValor(numSalida);
                                } else {
                                    vAnterior = new Valor("", ConstantesFs.TIPO_NULL);
                                }
                            }
                            vAnterior = new Valor("", ConstantesFs.TIPO_NULL);
                            break;
                        }
                        case ConstantesFs.MINIMO: {
                            ArrayList<Valor> var = vAnterior.getVector();
                            if (vAnterior.isVectorHomogeneo(ConstantesFs.TIPO_NUMERO)) {

                                int tamV = var.size();
                                double numSalida = 0;
                                if (tamV > 0) {
                                    numSalida = var.get(0).getNumber();
                                    for (int j = 1; j < tamV; j++) {
                                        numSalida = (numSalida > var.get(j).getNumber()) ? var.get(j).getNumber() : numSalida;
                                    }
                                    vAnterior.setValor(numSalida);
                                } else {
                                    vAnterior = new Valor("", ConstantesFs.TIPO_NULL);
                                }
                            }
                            vAnterior = new Valor("", ConstantesFs.TIPO_NULL);
                            break;
                        }
                        case ConstantesFs.FILTER: {
                            break;
                        }
                        case ConstantesFs.BUSCAR: {
                            break;
                        }
                        case ConstantesFs.MAP: {
                            break;
                        }
                        case ConstantesFs.REDUCE: {
                            break;
                        }
                        case ConstantesFs.TODOS: {
                            break;
                        }
                        case ConstantesFs.ALGUNO: {
                            break;
                        }
                    }
                }
            }

        }
        return vAnterior;

    }

    // <editor-fold defaultstate="collapsed" desc="FUNCIONES VARIAS Y ERRORES">
    public int getTipoVector(Valor v) {
        boolean band = false;
        if (v.getValor() instanceof ArrayList) {
            ArrayList<Valor> arr = (ArrayList<Valor>) v.getValor();
            int tamArr = arr.size();
            int tipo = 0;

            if (tamArr > 0) {
                tipo = arr.get(0).etqTipo;
            }
            for (int i = 1; i < tamArr; i++) {
                if (tipo != arr.get(i).etqTipo) {
                    band = true;
                }
            }
        }
        if (band) {
            return ConstantesFs.VECTOR_HETEROGENEO;
        } else {
            return ConstantesFs.VECTOR_HOMOGENEO;
        }
    }

    public int boolToInt(Object valor) {

        Map<Boolean, Integer> map = new HashMap<Boolean, Integer>() {
            {
                put(true, 1);
                put(false, 0);
            }
        };
        return map.get((boolean) valor);
    }

    public String boolToString(Object valor) {

        Map<Boolean, String> map = new HashMap<Boolean, String>() {
            {
                put(true, "verdadero");
                put(false, "falso");
            }
        };
        return map.get(valor.toString());
    }

    public Valor detectarError(Valor vI, Valor vD, TablaAmbientes ambientes, NodoArbol nodoError) {
        if (vI.isTipoIgual(ConstantesFs.TIPO_NULL) || vD.isTipoIgual(ConstantesFs.TIPO_NULL)) {
            String error = "la operacion \"" + nodoError.getTextoConst(nodoError.constEtiqueta) + "\" no es valida ya que alguno de los operandos es null";
            ErrorEjecucion error1 = new ErrorEjecucion(nombreArchivo, error, ambientes.getUltimo().getAmbiente(), nodoError.getLinea(), nodoError.getColumna());
            error1.envio();
            return new Valor("", ConstantesFs.TIPO_NULL);
        } else {
            String error = "la operacion \"" + nodoError.getTextoConst(nodoError.constEtiqueta) + "\" no es valida ";
            ErrorEjecucion error1 = new ErrorEjecucion(nombreArchivo, error, ambientes.getUltimo().getAmbiente(), nodoError.getLinea(), nodoError.getColumna());
            error1.envio();
            return new Valor("", ConstantesFs.TIPO_NULL);
        }
    }

    public Valor detectarError(Valor vI, TablaAmbientes ambientes) {
        if (vI.isTipoIgual(ConstantesFs.TIPO_NULL)) {
            //error
            return new Valor("", ConstantesFs.TIPO_NULL);
        } else {
            //error
            return new Valor("", ConstantesFs.TIPO_NULL);
        }
    }

    public Valor detectarError(String error, TablaAmbientes ambientes, NodoArbol nodoError) {

        ErrorEjecucion error1 = new ErrorEjecucion(nombreArchivo, error, ambientes.getUltimo().getAmbiente(), nodoError.getLinea(), nodoError.getColumna());
        error1.envio();

        return new Valor("", ConstantesFs.TIPO_NULL);
    }

    public boolean isNumero(String valor) {

        String regexp = "(\\d/)+";
        boolean salida = Pattern.matches(regexp, valor);
        return salida;
    }

    public boolean isDateTime(String valor) {

        String regexp = "(\\d{2}/\\d{2}/\\d{4})+";
        boolean salida = Pattern.matches(regexp, valor);

        return salida;
    }

    public boolean isDate(String valor) {

        String regexp = "(\\d{2}/\\d{2}/\\d{4} [012]\\d:[012345]\\d:[012345]\\d)+";
        boolean salida = Pattern.matches(regexp, valor);

        return salida;
    }
    // </editor-fold>

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public ArrayList<ErrorEjecucion> getListaErrores() {
        return listaErrores;
    }

    public void setListaErrores(ArrayList<ErrorEjecucion> listaErrores) {
        this.listaErrores = listaErrores;
    }

    public Map<String, NodoArbol> getMetodos() {
        return metodos;
    }

    public void setMetodos(Map<String, NodoArbol> metodos) {
        this.metodos = metodos;
    }

}
