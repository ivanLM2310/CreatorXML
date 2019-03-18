package AnalizadorFs.gdato;


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
           ErrorEjecucion err = new ErrorEjecucion("Lexico Gxml",error,"lexico","lexico",linea,columna);
           Main.errores.add(err);
        }
	public String contenidoActual;

%}

%cupsym symGdato
%cup
%class scannerGdato
%public
%line
%char
%column
%full
%ignorecase

etq_lista    = lista
etq_principal = principal
 



abrird          = "</"
abrir           = "<"
cerrar          = ">"

digito=[0-9]

numero 	= {digito}+("\."{digito}+)*

letra 	= [a-zA-ZñÑ]
exp_id 		= {letra}+({letra}|{digito}|"_")*


cadenat        	= "\""([^"\""])* "\""
COMENTARIO2     = "#$" ~"$#"
COMENTARIO1     = "##" [^\n]*{ENTER}?


SPACE   = [\ \r\t\f\t]
ENTER   = [\ \n]

%state PALABRASTAG
%state PALABRASFINALESTAG
%state NUMERALTAG
  

%%

<YYINITIAL>{
    {abrir}  { System.out.println("Token:|"+yytext() + "|");  yybegin(PALABRASTAG); return new Symbol(symGdato.abrir, yyline, yycolumn, yytext()); } 
    {abrird}  { System.out.println("Token:|"+yytext() + "|"); yybegin(PALABRASTAG); return new Symbol(symGdato.abrird, yyline, yycolumn, yytext()); }   
    {cadenat} { System.out.println("Token1:|"+yytext() + "|"); String txt = yytext();return new Symbol(symGdato.cadenaXml, yyline, yycolumn, new String (txt.substring(1,txt.length()-1))); }
    {numero}  { System.out.println("Token1:|"+yytext() + "|");return new Symbol(symGdato.numero, yyline, yycolumn, yytext()); }


    {COMENTARIO1}   {System.out.println("comentario una linea:"+yytext());}    
    {COMENTARIO2}   {System.out.println("comentario una multilinea:"+yytext());}   
	
}



<PALABRASTAG>{
    {etq_lista}        { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symGdato.etq_lista, yyline, yycolumn, yytext()); }           
    {etq_principal} { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symGdato.etq_principal, yyline, yycolumn, yytext()); }      
    {exp_id}            { System.out.println("Token1:|"+yytext() + "|");return new Symbol(symGdato.exp_id, yyline, yycolumn, yytext()); } 
    {cerrar}            { System.out.println("Token1:|"+yytext() + "|"); yybegin(YYINITIAL); return new Symbol(symGdato.cerrar, yyline, yycolumn, yytext()); }
	
}

   

[\t]			{ yycolumn+=4;}
{SPACE}         { /*Espacios en blanco, ignorados*/ }
{ENTER}         { /*Saltos de linea, ignorados*/}

.   { 
        agregarerror(yyline+1,yycolumn,"lexico","caracter irreconocible: "+yytext());
        System.out.println("error Lex:"+yytext());  
    }
