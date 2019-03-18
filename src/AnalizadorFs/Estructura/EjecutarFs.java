/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorFs.Estructura;

import AnalizadorFs.Interfaz.ComponenteGenerico;
import AnalizadorFs.Interfaz.InterfazContenedor;
import AnalizadorFs.Interfaz.InterfazVentana;
import AnalizadorFs.Interfaz.ObjInterfaz;
import AnalizadorFs.Interfaz.PantallaVideo;
import AnalizadorGxml.ErrorEjecucion;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import creatorxml.Main;
import java.awt.Component;

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

    String direccionE = "";
    Gdato gDato = new Gdato();

    public EjecutarFs(DocumentoFs doc, String direccionE) {
        this.direccionE = direccionE;
        gDato.setDirI(direccionE);
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

    public Valor ejecutarMetodo(NodoArbol raizMetodo, TablaAmbientes ambientes, ArrayList<Valor> paramValor) {
        //ver que tipo de parametros son los que se van a valuar

        ArrayList<Valor> parametros = null;
        if (paramValor != null) {
            parametros = new ArrayList();
            for (NodoArbol parametro : raizMetodo.getElemento(0).getHijosNodo()) {
                //valores que tiene  cada parametro
                parametros.add(evaluarExp(parametro, ambientes));
            }
        } else {
            parametros = paramValor;
        }

        //encontrar metodo en la lista
        NodoArbol met = null;
        // buscar el metodo
        if (metodos.containsKey("fun_" + raizMetodo.getValor())) {

            String tempAmbiente = strAmbito;
            strAmbito = "fun_" + raizMetodo.getValor();
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
        int i = 0;
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
                ejecutarMetodo(nSentencia, ambientes, null);
            } else if (nSentencia.isEtiquetaIgual(ConstantesFs.IMPRIMIR)) {
                ejecutarImprimir(nSentencia, ambientes);
            } else if (nSentencia.isEtiquetaIgual(ConstantesFs.LLAMADA_ID)) {
                ejecutarLlamadaId(nSentencia, ambientes);
            }

            if (v.isTipoIgual(ConstantesFs.RETORNO_BREAK)) {
                return v;
            }

            if (v.getProveniente() == ConstantesFs.RETORNO_VALOR) {
                return v;
            }
            i += 1;
        }
        return new Valor("metodo", ConstantesFs.RETORNO_METODO);
    }

    public void ejecutarLlamadaId(NodoArbol raiz, TablaAmbientes ambientes) {

        if (raiz.getElemento(0).isTipoIgual(ConstantesFs.ID)) {
            if (raiz.getElemento(0).getTamañoH() == 1
                    && raiz.getElemento(0).getElemento(0).isEtiquetaIgual(ConstantesFs.LISTA_PUNTO)) {
                Valor v = evaluarElemento(raiz.getElemento(0), ambientes);
            } else {
                //error
            }
        } else if (raiz.getElemento(0).isTipoIgual(ConstantesFs.LLAMADA_METODO)) {
            if (raiz.getElemento(0).getTamañoH() == 1
                    && raiz.getElemento(0).getElemento(0).isEtiquetaIgual(ConstantesFs.LISTA_PUNTO)) {
                Valor v = evaluarElemento(raiz.getElemento(0), ambientes);
            } else {
                //error
            }
        } else {
            //error
        }
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
            //solo declaracion sin valor al final
            for (NodoArbol v : raiz.getElemento(0).getHijosNodo()) {
                if (!ambientes.getUltimo().variables.containsKey(v.getValor())) {
                    ambientes.add_a_Ambiente(v.getValor(), new Valor("", ConstantesFs.TIPO_INDEFINIDO));
                } else {
                    detectarError("ya existe una variable con el nombre:" + v.getValor(), ambientes, v);
                }
            }
        } else if (raiz.getTamañoH() == 2) {
            //declaracion con una asignacion final
            NodoArbol ultimo = null;
            int i = 0;
            for (i = 0; i < raiz.getElemento(0).getTamañoH() - 1; i++) {
                NodoArbol v = raiz.getElemento(0).getElemento(i);
                if (!ambientes.getUltimo().variables.containsKey(v.getValor())) {
                    ambientes.add_a_Ambiente(v.getValor(), new Valor("", ConstantesFs.TIPO_INDEFINIDO));
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
            case ConstantesFs.TIPO_NULL:
            case ConstantesFs.TIPO_BOOLEANO:
            case ConstantesFs.TIPO_CADENA:
            case ConstantesFs.TIPO_NUMERO:
                return new Valor(raizOperacion.getValor(), raizOperacion.getConstTipo());
            case ConstantesFs.ID: {
                for (int i = ambientes.getNumeroAmbientes() - 1; i >= 0; i--) {
                    if (ambientes.getElemento(i).variables.containsKey(raizOperacion.getValor())) {
                        if (raizOperacion.getTamañoH() == 0) {
                            return ambientes.getElemento(i).variables.get(raizOperacion.getValor());
                        } else if (raizOperacion.getTamañoH() == 1) {
                            Valor val = ambientes.getElemento(i).variables.get(raizOperacion.getValor());
                            if (val.getValor() instanceof ComponenteGenerico) {
                                NodoArbol nodoLlamada = raizOperacion.getElemento(0);
                                return evaluarFunInterfaz(nodoLlamada, ambientes, val);
                            } else if (raizOperacion.getElemento(0).isEtiquetaIgual(ConstantesFs.LISTA_PUNTO)) {

                                Valor valPunto = evaluarPunto(raizOperacion.getElemento(0), ambientes, val);
                                return valPunto;
                            }
                        }
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
                                    if (raizOperacion.getTamañoH() == 1) {
                                        return indiceVector;
                                    } else if (raizOperacion.getTamañoH() == 2) {
                                        if (raizOperacion.getElemento(1).isEtiquetaIgual(ConstantesFs.LISTA_PUNTO)) {
                                            Valor valPunto = evaluarPunto(raizOperacion.getElemento(0), ambientes, indiceVector);
                                            return valPunto;
                                        }
                                    }

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
            case ConstantesFs.VECTOR_DECLARAR: {
                ArrayList<Valor> o = new ArrayList();
                int totalO = raizOperacion.getTamañoH();
                for (int num = 0; num < totalO; num++) {
                    o.add(evaluarExp(raizOperacion.getElemento(num), ambientes));
                }
                Valor v = new Valor(o, ConstantesFs.TIPO_OBJETO);
                int tipoA = (v.isVectorHomogeneo()) ? ConstantesFs.VECTOR_HOMOGENEO : ConstantesFs.VECTOR_HETEROGENEO;
                v.setEtqTipo(tipoA);

                return v;
            }
            case ConstantesFs.OBJETO_DECLARAR: {
                Objeto o = new Objeto();
                int totalO = raizOperacion.getTamañoH();
                for (int num = 0; num < totalO; num++) {
                    o.addAtributoValor(raizOperacion.getElemento(num).valor, evaluarExp(raizOperacion.getElemento(num).getElemento(0), ambientes));
                }
                return new Valor(o, ConstantesFs.TIPO_OBJETO);
            }
            case ConstantesFs.LLAMADAS_METODOS_NATIVOS: {
                //creardesdearchivo
                //lleergxml

                break;
            }
            case ConstantesFs.LLAMADA_METODO: {
                switch (raizOperacion.getValor()) {
                    case "crearventana": {
                        if (raizOperacion.getElemento(0).getTamañoH() == 4) {
                            ArrayList<Valor> parametrosLlamada = new ArrayList();
                            for (NodoArbol parametro : raizOperacion.getElemento(0).getHijosNodo()) {
                                //valores que tiene  cada parametro
                                parametrosLlamada.add(evaluarExp(parametro, ambientes));
                            }
                            if (parametrosLlamada.get(0).isTipoIgual(ConstantesFs.TIPO_CADENA)
                                    && parametrosLlamada.get(1).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                                    && parametrosLlamada.get(2).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                                    && parametrosLlamada.get(3).isTipoIgual(ConstantesFs.TIPO_CADENA)) {
                                /////////////////////////////////////////////////////////
                                InterfazVentana vent = new InterfazVentana(parametrosLlamada.get(0).getString(), (int) parametrosLlamada.get(1).getNumber(), (int) parametrosLlamada.get(2).getNumber(), parametrosLlamada.get(3).getString());
                                vent.show();
                                Object oVent = vent;
                                return new Valor(oVent, ConstantesFs.INTERFAZ_VENTANA);
                            } else {
                                //error
                            }
                        }
                        break;
                    }
                    case "leergxml": {
                        if (raizOperacion.getElemento(0).getTamañoH() == 1) {

                        }
                        break;
                    }
                    case "creardesdearchivo": {
                        if (raizOperacion.getElemento(0).getTamañoH() == 1) {
                            Valor valorN = evaluarExp(raizOperacion.getElemento(0).getElemento(0), ambientes);
                            if (valorN.isTipoIgual(ConstantesFs.TIPO_CADENA)) {
                                Object vect = gDato.getVectorGdato(valorN.getString());
                                if (vect != null) {
                                    return new Valor(vect, ConstantesFs.VECTOR_HOMOGENEO);
                                } else {
                                    //error
                                }
                            }
                        } 
                        break;
                    }
                    default: {
                        Valor retornoMetodo = ejecutarMetodo(raizOperacion, ambientes, null);
                        if (!retornoMetodo.isTipoIgual(ConstantesFs.RETORNO_METODO)) {
                            return retornoMetodo;
                        } else {
                            return detectarError("el metodo \"" + raizOperacion.getValor() + "\" no retorna ningun valor", ambientes, raizOperacion);
                        }
                    }
                }
            }
        }
        return new Valor("", ConstantesFs.TIPO_NULL);

    }

    public Valor evaluarFunInterfaz(NodoArbol lista, TablaAmbientes ambientes, Valor primerValor) {

        NodoArbol actual;
        Valor valorSalida = primerValor;
        int tamTotal = lista.getTamañoH();
        for (int i = 0; i < tamTotal; i++) {
            actual = lista.getElemento(i);
            if (valorSalida.isTipoIgual(ConstantesFs.INTERFAZ_VENTANA)) {
                if (actual.getValor().equals("crearcontenedor") && actual.getElemento(0).getTamañoH() == 6) {
                    ArrayList<Valor> parametrosLlamada = new ArrayList();
                    for (NodoArbol parametro : actual.getElemento(0).getHijosNodo()) {
                        //valores que tiene  cada parametro
                        parametrosLlamada.add(evaluarExp(parametro, ambientes));
                    }
                    if (parametrosLlamada.get(0).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                            && parametrosLlamada.get(1).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                            && parametrosLlamada.get(2).isTipoIgual(ConstantesFs.TIPO_CADENA)
                            && parametrosLlamada.get(3).isTipoIgual(ConstantesFs.TIPO_BOOLEANO)
                            && parametrosLlamada.get(4).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                            && parametrosLlamada.get(5).isTipoIgual(ConstantesFs.TIPO_NUMERO)) {
                        /////////////////////////////////////////////////////////
                        InterfazContenedor cont = new InterfazContenedor(
                                (int) parametrosLlamada.get(0).getNumber(),
                                (int) parametrosLlamada.get(1).getNumber(),
                                parametrosLlamada.get(2).getString(),
                                parametrosLlamada.get(3).getBoolean(),
                                (int) parametrosLlamada.get(4).getNumber(),
                                (int) parametrosLlamada.get(5).getNumber()
                        );
                        InterfazVentana vent = (InterfazVentana) valorSalida.valor;
                        Object oVent = cont;
                        vent.add(cont);
                        valorSalida = new Valor(oVent, ConstantesFs.INTERFAZ_CONTENEDOR);
                    } else {
                        //error
                    }
                }else if(actual.getValor().equals("creararraydesdearchivo") && actual.getElemento(0).getTamañoH() == 0){
                    InterfazVentana contenedor = (InterfazVentana)valorSalida.valor;
                    String salidaGdato = contenedor.getGdato();
                    abc
                }
            } else if (valorSalida.isTipoIgual(ConstantesFs.INTERFAZ_CONTENEDOR)) {
                if (actual.getTamañoH() == 1) {
                    ObjInterfaz ob = new ObjInterfaz();
                    ArrayList<Valor> parametrosLlamada = new ArrayList();

                    for (int n = 0; n < actual.getElemento(0).getTamañoH(); n++) {
                        //valores que tiene  cada parametro

                        if (actual.getValor().equals("crearboton") && n == 5 && actual.getElemento(0).getElemento(n).isEtiquetaIgual(ConstantesFs.LLAMADA_METODO)) {
                            parametrosLlamada.add(new Valor(actual.getElemento(0).getElemento(n).valor, ConstantesFs.TIPO_CADENA));
                        } else {
                            parametrosLlamada.add(evaluarExp(actual.getElemento(0).getElemento(n), ambientes));
                        }
                    }

                    if (actual.getValor().equals("creartexto")
                            && actual.getElemento(0).getTamañoH() == 8) {
                        if (parametrosLlamada.get(0).isTipoIgual(ConstantesFs.TIPO_CADENA)
                                && parametrosLlamada.get(1).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                                && parametrosLlamada.get(2).isTipoIgual(ConstantesFs.TIPO_CADENA)
                                && parametrosLlamada.get(3).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                                && parametrosLlamada.get(4).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                                && parametrosLlamada.get(5).isTipoIgual(ConstantesFs.TIPO_BOOLEANO)
                                && parametrosLlamada.get(6).isTipoIgual(ConstantesFs.TIPO_BOOLEANO)
                                && parametrosLlamada.get(7).isTipoIgual(ConstantesFs.TIPO_CADENA)) {
                            /////////////////////////////////////////////////////////

                            Object oVent = ob.crearTexto(parametrosLlamada.get(0).getString(),
                                    (int) parametrosLlamada.get(1).getNumber(),
                                    parametrosLlamada.get(2).getString(),
                                    (int) parametrosLlamada.get(3).getNumber(),
                                    (int) parametrosLlamada.get(4).getNumber(),
                                    parametrosLlamada.get(5).getBoolean(),
                                    parametrosLlamada.get(6).getBoolean(),
                                    parametrosLlamada.get(7).getString());

                            InterfazContenedor vent = (InterfazContenedor) valorSalida.valor;
                            vent.add(ob, (Component) oVent);
                            valorSalida = new Valor(oVent, ConstantesFs.TIPO_INDEFINIDO);
                        } else {
                            //error
                        }
                    } else if (actual.getValor().equals("crearcajatexto") && actual.getElemento(0).getTamañoH() == 11) {
                        if (parametrosLlamada.get(0).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                                && parametrosLlamada.get(1).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                                && parametrosLlamada.get(2).isTipoIgual(ConstantesFs.TIPO_CADENA)
                                && parametrosLlamada.get(3).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                                && parametrosLlamada.get(4).isTipoIgual(ConstantesFs.TIPO_CADENA)
                                && parametrosLlamada.get(5).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                                && parametrosLlamada.get(6).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                                && parametrosLlamada.get(7).isTipoIgual(ConstantesFs.TIPO_BOOLEANO)
                                && parametrosLlamada.get(8).isTipoIgual(ConstantesFs.TIPO_BOOLEANO)
                                && parametrosLlamada.get(9).isTipoIgual(ConstantesFs.TIPO_CADENA)
                                && parametrosLlamada.get(10).isTipoIgual(ConstantesFs.TIPO_CADENA)) {
                            /////////////////////////////////////////////////////////

                            Object oVent = ob.crearCajaTexto(
                                    (int) parametrosLlamada.get(0).getNumber(),
                                    (int) parametrosLlamada.get(1).getNumber(),
                                    parametrosLlamada.get(2).getString(),
                                    (int) parametrosLlamada.get(3).getNumber(),
                                    parametrosLlamada.get(4).getString(),
                                    (int) parametrosLlamada.get(5).getNumber(),
                                    (int) parametrosLlamada.get(6).getNumber(),
                                    parametrosLlamada.get(7).getBoolean(),
                                    parametrosLlamada.get(8).getBoolean(),
                                    parametrosLlamada.get(9).getString(),
                                    parametrosLlamada.get(10).getString());

                            InterfazContenedor vent = (InterfazContenedor) valorSalida.valor;
                            vent.add(ob, (Component) oVent);
                            valorSalida = new Valor(oVent, ConstantesFs.TIPO_INDEFINIDO);
                        } else {
                            //error
                        }
                    } else if (actual.getValor().equals("crearareatexto") && actual.getElemento(0).getTamañoH() == 11) {
                        if (parametrosLlamada.get(0).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                                && parametrosLlamada.get(1).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                                && parametrosLlamada.get(2).isTipoIgual(ConstantesFs.TIPO_CADENA)
                                && parametrosLlamada.get(3).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                                && parametrosLlamada.get(4).isTipoIgual(ConstantesFs.TIPO_CADENA)
                                && parametrosLlamada.get(5).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                                && parametrosLlamada.get(6).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                                && parametrosLlamada.get(7).isTipoIgual(ConstantesFs.TIPO_BOOLEANO)
                                && parametrosLlamada.get(8).isTipoIgual(ConstantesFs.TIPO_BOOLEANO)
                                && parametrosLlamada.get(9).isTipoIgual(ConstantesFs.TIPO_CADENA)
                                && parametrosLlamada.get(10).isTipoIgual(ConstantesFs.TIPO_CADENA)) {
                            /////////////////////////////////////////////////////////

                            Object oVent = ob.crearAreaTexto(
                                    (int) parametrosLlamada.get(0).getNumber(),
                                    (int) parametrosLlamada.get(1).getNumber(),
                                    parametrosLlamada.get(2).getString(),
                                    (int) parametrosLlamada.get(3).getNumber(),
                                    parametrosLlamada.get(4).getString(),
                                    (int) parametrosLlamada.get(5).getNumber(),
                                    (int) parametrosLlamada.get(6).getNumber(),
                                    parametrosLlamada.get(7).getBoolean(),
                                    parametrosLlamada.get(8).getBoolean(),
                                    parametrosLlamada.get(9).getString(),
                                    parametrosLlamada.get(10).getString());

                            InterfazContenedor vent = (InterfazContenedor) valorSalida.valor;
                            vent.add(ob, (Component) oVent);
                            valorSalida = new Valor(oVent, ConstantesFs.TIPO_INDEFINIDO);
                        } else {
                            //error
                        }
                    } else if (actual.getValor().equals("crearcontrolnumerico") && actual.getElemento(0).getTamañoH() == 8) {
                        if (parametrosLlamada.get(0).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                                && parametrosLlamada.get(1).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                                && parametrosLlamada.get(2).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                                && parametrosLlamada.get(3).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                                && parametrosLlamada.get(4).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                                && parametrosLlamada.get(5).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                                && parametrosLlamada.get(6).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                                && parametrosLlamada.get(7).isTipoIgual(ConstantesFs.TIPO_CADENA)) {
                            /////////////////////////////////////////////////////////

                            Object oVent = ob.crearControNumerico(
                                    (int) parametrosLlamada.get(0).getNumber(),
                                    (int) parametrosLlamada.get(1).getNumber(),
                                    (int) parametrosLlamada.get(2).getNumber(),
                                    (int) parametrosLlamada.get(3).getNumber(),
                                    (int) parametrosLlamada.get(4).getNumber(),
                                    (int) parametrosLlamada.get(5).getNumber(),
                                    (int) parametrosLlamada.get(6).getNumber(),
                                    parametrosLlamada.get(7).getString());

                            InterfazContenedor vent = (InterfazContenedor) valorSalida.valor;

                            vent.add(ob, (Component) oVent);
                            valorSalida = new Valor(oVent, ConstantesFs.TIPO_INDEFINIDO);
                        } else {
                            //error
                        }
                    } else if (actual.getValor().equals("creardesplegable") && actual.getElemento(0).getTamañoH() == 7) {
                        if (parametrosLlamada.get(0).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                                && parametrosLlamada.get(1).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                                && parametrosLlamada.get(2).isTipoIgual(ConstantesFs.VECTOR_HOMOGENEO)
                                && parametrosLlamada.get(3).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                                && parametrosLlamada.get(4).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                                && parametrosLlamada.get(5).isTipoIgual(ConstantesFs.TIPO_CADENA)
                                && parametrosLlamada.get(6).isTipoIgual(ConstantesFs.TIPO_CADENA)) {
                            /////////////////////////////////////////////////////////

                            Object oVent = ob.crearDesplegable(
                                    (int) parametrosLlamada.get(0).getNumber(),
                                    (int) parametrosLlamada.get(1).getNumber(),
                                    parametrosLlamada.get(2).getVector(),
                                    (int) parametrosLlamada.get(3).getNumber(),
                                    (int) parametrosLlamada.get(4).getNumber(),
                                    parametrosLlamada.get(5).getString(),
                                    parametrosLlamada.get(6).getString());

                            InterfazContenedor vent = (InterfazContenedor) valorSalida.valor;

                            vent.add(ob, (Component) oVent);
                            valorSalida = new Valor(oVent, ConstantesFs.TIPO_INDEFINIDO);
                        } else {
                            //error
                        }
                    } else if (actual.getValor().equals("crearboton") && actual.getElemento(0).getTamañoH() == 9) {
                        if (parametrosLlamada.get(0).isTipoIgual(ConstantesFs.TIPO_CADENA)
                                && parametrosLlamada.get(1).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                                && parametrosLlamada.get(2).isTipoIgual(ConstantesFs.TIPO_CADENA)
                                && parametrosLlamada.get(3).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                                && parametrosLlamada.get(4).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                                && parametrosLlamada.get(5).isTipoIgual(ConstantesFs.TIPO_CADENA)
                                && parametrosLlamada.get(6).isTipoIgual(ConstantesFs.TIPO_CADENA)
                                && parametrosLlamada.get(7).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                                && parametrosLlamada.get(8).isTipoIgual(ConstantesFs.TIPO_NUMERO)) {
                            /////////////////////////////////////////////////////////

                            Object oVent = ob.crearBoton(parametrosLlamada.get(0).getString(),
                                    (int) parametrosLlamada.get(1).getNumber(),
                                    parametrosLlamada.get(2).getString(),
                                    (int) parametrosLlamada.get(3).getNumber(),
                                    (int) parametrosLlamada.get(4).getNumber(),
                                    parametrosLlamada.get(5).getString(),
                                    parametrosLlamada.get(6).getString(),
                                    (int) parametrosLlamada.get(7).getNumber(),
                                    (int) parametrosLlamada.get(8).getNumber());

                            InterfazContenedor vent = (InterfazContenedor) valorSalida.valor;

                            vent.add(ob, (Component) oVent);
                            valorSalida = new Valor(oVent, ConstantesFs.INTERFAZ_BOTON);
                        } else {
                            //error
                        }
                    } else if (actual.getValor().equals("crearimagen") && actual.getElemento(0).getTamañoH() == 5) {
                        if (parametrosLlamada.get(0).isTipoIgual(ConstantesFs.TIPO_CADENA)
                                && parametrosLlamada.get(1).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                                && parametrosLlamada.get(2).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                                && parametrosLlamada.get(3).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                                && parametrosLlamada.get(4).isTipoIgual(ConstantesFs.TIPO_NUMERO)) {
                            /////////////////////////////////////////////////////////
                            String dirRelativa = parametrosLlamada.get(0).getString();
                            if (dirRelativa.length() > 0) {
                                dirRelativa = (dirRelativa.charAt(0) != '\\' || dirRelativa.charAt(0) != '/') ? "\\" + dirRelativa : dirRelativa;
                            }
                            Object oVent = ob.crearImagen(direccionE + dirRelativa,
                                    (int) parametrosLlamada.get(1).getNumber(),
                                    (int) parametrosLlamada.get(2).getNumber(),
                                    (int) parametrosLlamada.get(3).getNumber(),
                                    (int) parametrosLlamada.get(4).getNumber());

                            InterfazContenedor vent = (InterfazContenedor) valorSalida.valor;
                            vent.add(ob, (Component) oVent);
                            ((PantallaVideo) oVent).start();
                            valorSalida = new Valor(oVent, ConstantesFs.TIPO_INDEFINIDO);
                        } else {
                            //error
                        }
                    } else if (actual.getValor().equals("crearreproductor") && actual.getElemento(0).getTamañoH() == 6) {
                        if (parametrosLlamada.get(0).isTipoIgual(ConstantesFs.TIPO_CADENA)
                                && parametrosLlamada.get(1).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                                && parametrosLlamada.get(2).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                                && parametrosLlamada.get(3).isTipoIgual(ConstantesFs.TIPO_BOOLEANO)
                                && parametrosLlamada.get(4).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                                && parametrosLlamada.get(5).isTipoIgual(ConstantesFs.TIPO_NUMERO)) {
                            /////////////////////////////////////////////////////////
                            String dirRelativa = parametrosLlamada.get(0).getString();
                            if (dirRelativa.length() > 0) {
                                dirRelativa = (dirRelativa.charAt(0) != '\'' || dirRelativa.charAt(0) != '/') ? "\\" + dirRelativa : dirRelativa;
                            }
                            Object oVent = ob.crearReproductor(direccionE + dirRelativa,
                                    (int) parametrosLlamada.get(1).getNumber(),
                                    (int) parametrosLlamada.get(2).getNumber(),
                                    parametrosLlamada.get(3).getBoolean(),
                                    (int) parametrosLlamada.get(4).getNumber(),
                                    (int) parametrosLlamada.get(5).getNumber());

                            InterfazContenedor vent = (InterfazContenedor) valorSalida.valor;

                            vent.add(ob, (Component) oVent);
                            ((PantallaVideo) oVent).start();
                            valorSalida = new Valor(oVent, ConstantesFs.TIPO_INDEFINIDO);
                        } else {
                            //error
                        }
                    } else if (actual.getValor().equals("crearvideo") && actual.getElemento(0).getTamañoH() == 6) {
                        if (parametrosLlamada.get(0).isTipoIgual(ConstantesFs.TIPO_CADENA)
                                && parametrosLlamada.get(1).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                                && parametrosLlamada.get(2).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                                && parametrosLlamada.get(3).isTipoIgual(ConstantesFs.TIPO_BOOLEANO)
                                && parametrosLlamada.get(4).isTipoIgual(ConstantesFs.TIPO_NUMERO)
                                && parametrosLlamada.get(5).isTipoIgual(ConstantesFs.TIPO_NUMERO)) {
                            /////////////////////////////////////////////////////////
                            String dirRelativa = parametrosLlamada.get(0).getString();
                            if (dirRelativa.length() > 0) {
                                dirRelativa = (dirRelativa.charAt(0) != '\'' || dirRelativa.charAt(0) != '/') ? "\\" + dirRelativa : dirRelativa;
                            }
                            Object oVent = ob.crearVideo(direccionE + dirRelativa,
                                    (int) parametrosLlamada.get(1).getNumber(),
                                    (int) parametrosLlamada.get(2).getNumber(),
                                    parametrosLlamada.get(3).getBoolean(),
                                    (int) parametrosLlamada.get(4).getNumber(),
                                    (int) parametrosLlamada.get(5).getNumber());

                            InterfazContenedor vent = (InterfazContenedor) valorSalida.valor;
                            vent.add(ob, (Component) oVent);
                            ((PantallaVideo) oVent).start();
                            valorSalida = new Valor(oVent, ConstantesFs.TIPO_INDEFINIDO);
                        } else {
                            //error
                        }
                    }
                }
            } else {
                //error
            }
        }
        return valorSalida;
    }

    public Valor evaluarPunto(NodoArbol raizOperacion, TablaAmbientes ambientes, Valor primerValor) {
        int tamTotal = raizOperacion.getTamañoH();
        NodoArbol actual;
        Valor vAnterior = primerValor.getCopia();
        for (int i = 0; i < tamTotal; i++) {
            actual = raizOperacion.getElemento(i);
            if (vAnterior.isTipoIgual(ConstantesFs.TIPO_OBJETO)) {
                if (actual.isEtiquetaIgual(ConstantesFs.ID)) {
                    Objeto ob = (Objeto) vAnterior.valor;
                    vAnterior.copiarValor(ob.getValor(actual.valor));
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
                            } else {
                                //error
                                return detectarError("El Vector No Valido Para La Operacion Nativa \"" + actual.getTextoConst(actual.constTipo) + "\" Para La Variable \"" + raizOperacion.valor + "\"", ambientes, raizOperacion);
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
                            } else {
                                return detectarError("El Vector No Valido Para La Operacion Nativa \"" + actual.getTextoConst(actual.constTipo) + "\" Para La Variable \"" + raizOperacion.valor + "\"", ambientes, raizOperacion);
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
                                    vAnterior.setEtqTipo(ConstantesFs.TIPO_NUMERO);
                                    break;
                                }
                            } else {
                                return detectarError("El Vector No Valido Para La Operacion Nativa \"" + actual.getTextoConst(actual.constTipo) + "\" Para La Variable \"" + raizOperacion.valor + "\"", ambientes, raizOperacion);
                            }
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
                                    vAnterior.setEtqTipo(ConstantesFs.TIPO_NUMERO);
                                    break;
                                }
                            } else {
                                return detectarError("El Vector No Valido Para La Operacion Nativa \"" + actual.getTextoConst(actual.constTipo) + "\" Para La Variable \"" + raizOperacion.valor + "\"", ambientes, raizOperacion);
                            }
                            break;
                        }
                        case ConstantesFs.FILTER: {

                            if (metodos.containsKey("fun_" + actual.getValor())) {
                                NodoArbol metodoA = metodos.get("fun_" + actual.getValor());
                                if (metodoA.getTamañoH() == 1) {
                                    ArrayList<Valor> var = vAnterior.getVector();
                                    ArrayList<Valor> resultado = new ArrayList();
                                    ArrayList<Valor> valoresV = new ArrayList();
                                    valoresV.add(null);
                                    for (Valor item : var) {
                                        valoresV.set(0, item);
                                        Valor v = ejecutarMetodo(metodoA, ambientes, valoresV);
                                        if (v.isTipoIgual(ConstantesFs.TIPO_BOOLEANO)) {
                                            if (v.getBoolean()) {
                                                resultado.add(item);
                                            }
                                        } else {
                                            return detectarError("El Metodo Que Se Quiere Ejecutar Para FILTER, retorna algo diferente a Booleano", ambientes, raizOperacion);
                                        }
                                    }
                                    vAnterior.setValorTipo(resultado, ConstantesFs.VECTOR_HOMOGENEO);

                                } else {
                                    return detectarError("El Metodo Que Se Quiere Ejecutar Para FILTER, no tiene el numero de parametros correctos", ambientes, raizOperacion);
                                }
                            } else {
                                //el metodo no existe
                                return detectarError("El Metodo Que Se Quiere Ejecutar Para FILTER, no existe", ambientes, raizOperacion);
                            }
                            break;
                        }
                        case ConstantesFs.BUSCAR: {
                            if (metodos.containsKey("fun_" + actual.getValor())) {
                                NodoArbol metodoA = metodos.get("fun_" + actual.getValor());
                                if (metodoA.getTamañoH() == 1) {
                                    ArrayList<Valor> var = vAnterior.getVector();
                                    Valor resultado = null;
                                    ArrayList<Valor> valoresV = new ArrayList();
                                    valoresV.add(null);
                                    for (Valor item : var) {
                                        valoresV.set(0, item);
                                        Valor v = ejecutarMetodo(metodoA, ambientes, valoresV);
                                        if (v.isTipoIgual(ConstantesFs.TIPO_BOOLEANO)) {
                                            if (v.getBoolean()) {
                                                resultado = item;
                                                break;
                                            }
                                        } else {
                                            return detectarError("El Metodo Que Se Quiere Ejecutar Para BUSCAR, retorna algo diferente a Booleano", ambientes, raizOperacion);
                                        }
                                    }
                                    if (resultado != null) {
                                        vAnterior.setValorTipo(resultado, resultado.etqTipo);
                                    } else {
                                        return detectarError("El Metodo Que Se Quiere Ejecutar Para BUSCAR, No encontro ninguna coincidencia, indefinido", ambientes, raizOperacion);
                                    }
                                } else {
                                    return detectarError("El Metodo Que Se Quiere Ejecutar Para BUSCAR, el metodo no tiene el numero de parametros correctos", ambientes, raizOperacion);
                                }
                            } else {
                                return detectarError("El Metodo Que Se Quiere Ejecutar Para BUSCAR, No Existe", ambientes, raizOperacion);
                            }
                            break;
                        }
                        case ConstantesFs.MAP: {
                            if (metodos.containsKey("fun_" + actual.getValor())) {
                                NodoArbol metodoA = metodos.get("fun_" + actual.getValor());
                                if (metodoA.getTamañoH() == 1) {
                                    ArrayList<Valor> var = vAnterior.getVector();
                                    ArrayList<Valor> resultado = new ArrayList();
                                    ArrayList<Valor> valoresV = new ArrayList();
                                    valoresV.add(null);
                                    for (Valor item : var) {
                                        valoresV.set(0, item);
                                        Valor v = ejecutarMetodo(metodoA, ambientes, valoresV);
                                        resultado.add(v);
                                    }
                                    vAnterior.setValorTipo(resultado, ConstantesFs.VECTOR_HOMOGENEO);
                                } else {
                                    return detectarError("El Metodo Que Se Quiere Ejecutar Para MAP, el metodo no tiene el numero de parametros correctos", ambientes, raizOperacion);
                                }
                            } else {
                                return detectarError("El Metodo Que Se Quiere Ejecutar Para MAP, No Existe", ambientes, raizOperacion);
                            }
                            break;
                        }
                        case ConstantesFs.REDUCE: {
                            if (vAnterior.isVectorHomogeneo(ConstantesFs.TIPO_NUMERO)) {

                                if (metodos.containsKey("fun_" + actual.getValor())) {
                                    NodoArbol metodoA = metodos.get("fun_" + actual.getValor());
                                    if (metodoA.getTamañoH() == 2) {
                                        ArrayList<Valor> var = vAnterior.getVector();
                                        double resultado = 0;
                                        ArrayList<Valor> valoresV = new ArrayList();
                                        Object obN = 0;
                                        valoresV.add(new Valor(obN, ConstantesFs.TIPO_NUMERO));
                                        valoresV.add(null);
                                        for (Valor item : var) {
                                            valoresV.set(1, item);
                                            Valor v = ejecutarMetodo(metodoA, ambientes, valoresV);
                                            if (v.isTipoIgual(ConstantesFs.TIPO_NUMERO)) {
                                                resultado = v.getNumber();
                                                valoresV.set(0, new Valor(resultado, ConstantesFs.TIPO_NUMERO));
                                            } else {
                                                return detectarError("El Metodo Que Se Quiere Ejecutar Para REDUCE, Retorna Algo invalido", ambientes, raizOperacion);
                                            }
                                        }
                                        vAnterior.setValorTipo(resultado, ConstantesFs.TIPO_NUMERO);
                                    } else {
                                        return detectarError("El Metodo Que Se Quiere Ejecutar Para REDUCE, no tiene el numero de parametros correctos", ambientes, raizOperacion);
                                    }
                                } else {
                                    return detectarError("El Metodo Que Se Quiere Ejecutar Para REDUCE, No Existe", ambientes, raizOperacion);
                                }

                            } else if (vAnterior.isVectorHomogeneo(ConstantesFs.TIPO_CADENA)) {
                                if (metodos.containsKey("fun_" + actual.getValor())) {
                                    NodoArbol metodoA = metodos.get("fun_" + actual.getValor());
                                    if (metodoA.getTamañoH() == 2) {
                                        ArrayList<Valor> var = vAnterior.getVector();
                                        double resultado = 0;
                                        ArrayList<Valor> valoresV = new ArrayList();
                                        Object obN = "";
                                        valoresV.add(new Valor(obN, ConstantesFs.TIPO_CADENA));
                                        valoresV.add(null);
                                        for (Valor item : var) {
                                            valoresV.set(1, item);
                                            Valor v = ejecutarMetodo(metodoA, ambientes, valoresV);
                                            if (v.isTipoIgual(ConstantesFs.TIPO_CADENA)) {
                                                resultado = v.getNumber();
                                                valoresV.set(0, new Valor(resultado, ConstantesFs.TIPO_CADENA));
                                            } else {
                                                return detectarError("El Metodo Que Se Quiere Ejecutar Para REDUCE, Retorna Algo invalido", ambientes, raizOperacion);
                                            }
                                        }
                                        vAnterior.setValorTipo(resultado, ConstantesFs.TIPO_CADENA);
                                    } else {
                                        return detectarError("El Metodo Que Se Quiere Ejecutar Para REDUCE, no tiene el numero de parametros correctos", ambientes, raizOperacion);
                                    }
                                } else {
                                    return detectarError("El Metodo Que Se Quiere Ejecutar Para REDUCE, No Existe", ambientes, raizOperacion);
                                }
                            }
                            break;
                        }
                        case ConstantesFs.TODOS: {
                            if (metodos.containsKey("fun_" + actual.getValor())) {
                                NodoArbol metodoA = metodos.get("fun_" + actual.getValor());
                                if (metodoA.getTamañoH() == 1) {
                                    ArrayList<Valor> var = vAnterior.getVector();
                                    Valor resultado = null;
                                    ArrayList<Valor> valoresV = new ArrayList();
                                    valoresV.add(null);
                                    for (Valor item : var) {
                                        valoresV.set(0, item);
                                        Valor v = ejecutarMetodo(metodoA, ambientes, valoresV);
                                        if (v.isTipoIgual(ConstantesFs.TIPO_BOOLEANO)) {
                                            if (!v.getBoolean()) {
                                                resultado = item;
                                                break;
                                            }
                                        } else {
                                            return detectarError("El Metodo Que Se Quiere Ejecutar Para TODOS, Retorna algo invalido", ambientes, raizOperacion);
                                        }
                                    }
                                    if (resultado != null) {
                                        vAnterior.setValorTipo(false, ConstantesFs.TIPO_BOOLEANO);
                                    } else {
                                        vAnterior.setValorTipo(true, ConstantesFs.TIPO_BOOLEANO);
                                    }
                                } else {
                                    return detectarError("El Metodo Que Se Quiere Ejecutar Para TODOS, Numero de Parametros invalidos", ambientes, raizOperacion);
                                }
                            } else {
                                return detectarError("El Metodo Que Se Quiere Ejecutar Para TODOS, No Existe", ambientes, raizOperacion);
                            }
                            break;
                        }
                        case ConstantesFs.ALGUNO: {
                            if (metodos.containsKey("fun_" + actual.getValor())) {
                                NodoArbol metodoA = metodos.get("fun_" + actual.getValor());
                                if (metodoA.getTamañoH() == 1) {
                                    ArrayList<Valor> var = vAnterior.getVector();
                                    Valor resultado = null;
                                    ArrayList<Valor> valoresV = new ArrayList();
                                    valoresV.add(null);
                                    for (Valor item : var) {
                                        valoresV.set(0, item);
                                        Valor v = ejecutarMetodo(metodoA, ambientes, valoresV);
                                        if (v.isTipoIgual(ConstantesFs.TIPO_BOOLEANO)) {
                                            if (v.getBoolean()) {
                                                resultado = item;
                                                break;
                                            }
                                        } else {
                                            return detectarError("El Metodo Que Se Quiere Ejecutar Para ALGUNO, Retorno algo invalido", ambientes, raizOperacion);
                                        }
                                    }
                                    if (resultado != null) {
                                        vAnterior.setValorTipo(false, ConstantesFs.TIPO_BOOLEANO);
                                    } else {
                                        vAnterior.setValorTipo(true, ConstantesFs.TIPO_BOOLEANO);
                                    }
                                } else {
                                    return detectarError("El Metodo Que Se Quiere Ejecutar Para ALGUNO, Numero de Parametros invalidos", ambientes, raizOperacion);
                                }
                            } else {
                                return detectarError("El Metodo Que Se Quiere Ejecutar Para ALGUNO, No Existe", ambientes, raizOperacion);
                            }
                            break;
                        }
                    }
                }
            } else if (vAnterior.isTipoIgual(ConstantesFs.LLAMADA_METODO)) {

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
