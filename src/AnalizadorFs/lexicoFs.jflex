package AnalizadorFs;

import AnalizadorGxml.ErrorEjecucion;
import java_cup.runtime.Symbol;
import creatorxml.Main;

%%
%{
    /*
    //Código de usuario
    public String archivo="null";
    public  ArrayList<NodoError> lexico= new ArrayList<NodoError>();
    
     public  ArrayList<NodoError> lex()
    {
        return lexico;
    }
    */
        public void agregarerror(int linea,int columna,String tipo,String men)
        {
           String error = "el token \"" + men + "\" no pudo ser reconocido";
           ErrorEjecucion err = new ErrorEjecucion("Lexico Fs",error,"lexico","lexico",linea,columna);
           Main.errores.add(err);
        }
    public String contenidoActual;

%}

%cupsym symFs
%cup
%class scannerFs
%public
%line
%char
%column
%full
%ignorecase


digito=[0-9]

numero  = {digito}+("\."{digito}+)*

letra   = [a-zA-ZñÑ]
exp_id      = {letra}+({letra}|{digito}|"_")*


cadenat         = "\""([^"\""])* "\""


SPACE   = [\ \r\t\f\t]
ENTER   = [\ \n]

Comment = {TraditionalComment} | {EndOfLineComment} 

TraditionalComment = "/*" [^*] ~"*/" | "/*" "*"+ "/"

EndOfLineComment = "//" ([^\r\n])* (\r|\n|\r\n)?
  

%%

<YYINITIAL>{
    "importar" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.importar, yyline, yycolumn, yytext()); } 
    "var" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.var, yyline, yycolumn, yytext()); } 
    "imprimir" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.imprimir, yyline, yycolumn, yytext()); } 
    "detener" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.detener, yyline, yycolumn, yytext()); } 
    "retornar" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.retornar, yyline, yycolumn, yytext()); } 
    "selecciona" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.selecciona, yyline, yycolumn, yytext()); } 
    "caso" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.caso, yyline, yycolumn, yytext()); } 
    "si" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.si, yyline, yycolumn, yytext()); } 
    "si" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.si, yyline, yycolumn, yytext()); } 
    "sino" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.sino, yyline, yycolumn, yytext()); } 
    "funcion" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.funcion, yyline, yycolumn, yytext()); } 
    "verdadero" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.t_true, yyline, yycolumn, yytext()); } 
    "falso" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.t_false, yyline, yycolumn, yytext()); } 
    "descendente" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.descendente, yyline, yycolumn, yytext()); } 
    "ascendente" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.ascendente, yyline, yycolumn, yytext()); } 
    "maximo" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.maximo, yyline, yycolumn, yytext()); } 
    "minimo" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.minimo, yyline, yycolumn, yytext()); } 
    "invertir" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.invertir, yyline, yycolumn, yytext()); } 
    "filter" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.filter, yyline, yycolumn, yytext()); } 
    "buscar" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.buscar, yyline, yycolumn, yytext()); } 
    "map" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.map, yyline, yycolumn, yytext()); } 
    "reduce" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.reduce, yyline, yycolumn, yytext()); } 
    "todos" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.todos, yyline, yycolumn, yytext()); } 
    "defecto" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.defecto, yyline, yycolumn, yytext()); } 
    "alguno" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.alguno, yyline, yycolumn, yytext()); } 
    "nulo" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.nulo, yyline, yycolumn, yytext()); } 
    "+=" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.masigual, yyline, yycolumn, yytext()); } 
    "-=" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.menosigual, yyline, yycolumn, yytext()); } 
    "*=" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.porigual, yyline, yycolumn, yytext()); } 
    "/=" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.divigual, yyline, yycolumn, yytext()); } 
    "++" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.masmas, yyline, yycolumn, yytext()); } 
    "--" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.menosmenos, yyline, yycolumn, yytext()); } 
    ">=" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.mayorigual, yyline, yycolumn, yytext()); } 
    "<=" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.menorigual, yyline, yycolumn, yytext()); } 
    "==" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.igualigual, yyline, yycolumn, yytext()); } 
    "!=" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.diferente, yyline, yycolumn, yytext()); } 
    ">" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.mayorq, yyline, yycolumn, yytext()); } 
    "<" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.menorq, yyline, yycolumn, yytext()); } 
    "||" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.or, yyline, yycolumn, yytext()); } 
    "&&" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.and, yyline, yycolumn, yytext()); } 
    "!" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.var, yyline, yycolumn, yytext()); } 
    
    "+" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.mas, yyline, yycolumn, yytext()); } 
    "-" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.menos, yyline, yycolumn, yytext()); } 
    "*" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.por, yyline, yycolumn, yytext()); } 
    "/" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.dividir, yyline, yycolumn, yytext()); } 
    "^"    { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.potencia, yyline, yycolumn, yytext()); } 
    
    "?" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.ternario, yyline, yycolumn, yytext()); } 

    "(" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.parentesisa, yyline, yycolumn, yytext()); } 
    ")" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.parentesisc, yyline, yycolumn, yytext()); } 
    ";" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.puntoycoma, yyline, yycolumn, yytext()); } 
    "=" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.igual, yyline, yycolumn, yytext()); } 
    "," { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.coma, yyline, yycolumn, yytext()); } 
    "{" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.llavea, yyline, yycolumn, yytext()); } 
    "}" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.llavec, yyline, yycolumn, yytext()); } 
    ":" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.dospuntos, yyline, yycolumn, yytext()); } 
    "[" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.corchetea, yyline, yycolumn, yytext()); } 
    "]" { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.corchetec, yyline, yycolumn, yytext()); } 
    "." { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.punto, yyline, yycolumn, yytext()); } 
    {numero} { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symFs.numero, yyline, yycolumn, yytext()); } 
    {cadenat} { System.out.println("Token1:|"+yytext() + "|"); String txt = yytext();return new Symbol(symFs.cadena, yyline, yycolumn, new String (txt.substring(1,txt.length()-1)));} 
    {exp_id} { System.out.println("Token1id:|"+yytext() + "|"); return new Symbol(symFs.id, yyline, yycolumn, yytext().toLowerCase()); } 





}

[\t]            { yycolumn+=4;}
{Comment}       { }
{SPACE}         { /*Espacios en blanco, ignorados*/ }
{ENTER}         { /*Saltos de linea, ignorados*/}

.   { 
        agregarerror(yyline+1,yycolumn,"lexico","caracter irreconocible: "+yytext());
        System.out.println("error Lex:"+yytext());  
    }