package AnalizadorGxml;

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

%cupsym symGxml
%cup
%class scannerGxml
%public
%line
%char
%column
%full
%ignorecase

etq_importar    = importar 
etq_ventana     = ventana
etq_contenedor  = contenedor
etq_texto       = texto
etq_control     = control 
etq_listadatos  = listadatos 
etq_datos       = dato
etq_defecto     = defecto 
etq_multimedia  = multimedia
etq_boton       = boton
etq_enviar      = enviar

id              = id
tipo            = tipo
color           = color
accioninicial   = accioninicial
accionfinal     = accionfinal 
x               = x
y               = y
alto            = alto
ancho           = ancho
borde           = borde 
nombre          = nombre 
fuente          = fuente 
tam             = tam 
negrita         = negrita 
cursiva         = cursiva 
maximo          = maximo 
minimo          = minimo 
accion          = accion 
referencia      = referencia 
path            = path
auto_reprodu    = auto-reproduccion
verdadero   = verdadero
falso    = falso

abrird          = "</"
abrir           = "<"
cerrar          = ">"
igual           = "="

digito=[0-9]

numero 	= {digito}+("\."{digito}+)*
numero 	= {digito}+("\."{digito}+)*
letra 	= [a-zA-ZñÑ]
exp_id 		= {letra}+({letra}|{digito}|"_")*


cadenat        	= "\""([^"\""])* "\""
codigo        	= "{"([^"}"])* "}"
NUMERAL    		= [#]

COMENTARIO2     = "#$" ~"$#"
COMENTARIO1     = "##" [^\n]*{ENTER}?
textoXml       	= [^\ \r\t\f\t\n<>#] ([^<>#](#[^$<#])?)*


SPACE   = [\ \r\t\f\t]
ENTER   = [\ \n]

%state PALABRASTAG
%state PALABRASFINALESTAG
%state NUMERALTAG
  

%%

<YYINITIAL>{
    {abrir}  { System.out.println("Token:|"+yytext() + "|");  yybegin(PALABRASTAG); return new Symbol(symGxml.abrir, yyline, yycolumn, yytext()); } 
    {abrird}  { System.out.println("Token:|"+yytext() + "|"); yybegin(PALABRASFINALESTAG); return new Symbol(symGxml.abrird, yyline, yycolumn, yytext().trim()); }   
    {textoXml} {System.out.println("TEXTO1:"+ yytext()); return new Symbol(symGxml.textoXml, yyline, yycolumn, yytext().trim());}
	{COMENTARIO1}   {System.out.println("comentario una linea:"+yytext());}    
    {COMENTARIO2}   {System.out.println("comentario una multilinea:"+yytext());}   
	
}


<PALABRASTAG>{
    {etq_importar}        { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symGxml.etq_importar, yyline, yycolumn, yytext()); }           
    {etq_ventana}         { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symGxml.etq_ventana, yyline, yycolumn, yytext()); }      
    {etq_contenedor}      { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symGxml.etq_contenedor, yyline, yycolumn, yytext()); } 
    {etq_texto}           { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symGxml.etq_texto, yyline, yycolumn, yytext()); } 
    {etq_control}         { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symGxml.etq_control, yyline, yycolumn, yytext()); } 
    {etq_listadatos}      { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symGxml.etq_listadatos, yyline, yycolumn, yytext()); } 
    {etq_datos}           { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symGxml.etq_datos, yyline, yycolumn, yytext()); } 
    {etq_defecto}         { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symGxml.etq_defecto, yyline, yycolumn, yytext()); } 
    {etq_multimedia}      { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symGxml.etq_multimedia, yyline, yycolumn, yytext()); } 
    {etq_boton}           { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symGxml.etq_boton, yyline, yycolumn, yytext()); } 
    {etq_enviar}          { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symGxml.etq_enviar, yyline, yycolumn, yytext()); } 
    {id}                  { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symGxml.atb_id, yyline, yycolumn, yytext()); } 
    {tipo}                { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symGxml.atb_tipo, yyline, yycolumn, yytext()); } 
    {color}               { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symGxml.atb_color, yyline, yycolumn, yytext()); } 
    {accioninicial}       { System.out.println("Token1:|"+yytext() + "|");return new Symbol(symGxml.atb_accioninicial, yyline, yycolumn, yytext()); } 
    {accionfinal}         { System.out.println("Token1:|"+yytext() + "|");return new Symbol(symGxml.atb_accionfinal, yyline, yycolumn, yytext()); } 
    {x}                   { System.out.println("Token1:|"+yytext() + "|");return new Symbol(symGxml.atb_x, yyline, yycolumn, yytext()); }   
    {y}                   { System.out.println("Token1:|"+yytext() + "|");return new Symbol(symGxml.atb_y, yyline, yycolumn, yytext()); } 
    {alto}                { System.out.println("Token1:|"+yytext() + "|");return new Symbol(symGxml.atb_alto, yyline, yycolumn, yytext()); } 
    {ancho}               { System.out.println("Token1:|"+yytext() + "|");return new Symbol(symGxml.atb_ancho, yyline, yycolumn, yytext()); } 
    {borde}               { System.out.println("Token1:|"+yytext() + "|");return new Symbol(symGxml.atb_borde, yyline, yycolumn, yytext()); } 
    {nombre}              { System.out.println("Token1:|"+yytext() + "|");return new Symbol(symGxml.atb_nombre, yyline, yycolumn, yytext()); } 
    {fuente}              { System.out.println("Token1:|"+yytext() + "|");return new Symbol(symGxml.atb_fuente, yyline, yycolumn, yytext()); } 
    {tam}                 { System.out.println("Token1:|"+yytext() + "|");return new Symbol(symGxml.atb_tam, yyline, yycolumn, yytext()); } 
    {negrita}             { System.out.println("Token1:|"+yytext() + "|");return new Symbol(symGxml.atb_negrita, yyline, yycolumn, yytext()); } 
    {cursiva}             { System.out.println("Token1:|"+yytext() + "|");return new Symbol(symGxml.atb_cursiva, yyline, yycolumn, yytext()); } 
    {maximo}              { System.out.println("Token1:|"+yytext() + "|");return new Symbol(symGxml.atb_maximo, yyline, yycolumn, yytext()); } 
    {minimo}              { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symGxml.atb_minimo, yyline, yycolumn, yytext()); } 
    {accion}              { System.out.println("Token1:|"+yytext() + "|"); return new Symbol(symGxml.atb_accion, yyline, yycolumn, yytext()); } 
    {referencia}          { System.out.println("Token1:|"+yytext() + "|");return new Symbol(symGxml.atb_referencia, yyline, yycolumn, yytext()); } 
    {path}                { System.out.println("Token1:|"+yytext() + "|");return new Symbol(symGxml.atb_path, yyline, yycolumn, yytext()); } 
    {auto_reprodu}        { System.out.println("Token1:|"+yytext() + "|");return new Symbol(symGxml.atb_auto_reprodu, yyline, yycolumn, yytext()); } 

    {igual}             { System.out.println("Token1:|"+yytext() + "|");return new Symbol(symGxml.igual, yyline, yycolumn, yytext()); }
    {cadenat}           { System.out.println("Token1:|"+yytext() + "|");String txt = yytext();return new Symbol(symGxml.cadenaXml, yyline, yycolumn, new String (txt.substring(1,txt.length()-1))); }
	{codigo}           	{ System.out.println("Token1:|"+yytext() + "|");String txt = yytext();return new Symbol(symGxml.codigo, yyline, yycolumn, new String (txt.substring(1,txt.length()-1))); }
	{numero}           	{ System.out.println("Token1:|"+yytext() + "|");return new Symbol(symGxml.numero, yyline, yycolumn, yytext()); }
	{verdadero}         { System.out.println("Token1:|"+yytext() + "|");return new Symbol(symGxml.verdadero, yyline, yycolumn, yytext()); }
	{falso}           	{ System.out.println("Token1:|"+yytext() + "|");return new Symbol(symGxml.falso, yyline, yycolumn, yytext()); }

    {exp_id}                  { System.out.println("Token1:|"+yytext() + "|");return new Symbol(symGxml.exp_id, yyline, yycolumn, yytext()); } 
    {cerrar}            { System.out.println("Token1:|"+yytext() + "|"); yybegin(YYINITIAL); return new Symbol(symGxml.cerrar, yyline, yycolumn, yytext()); }
	
}

<PALABRASFINALESTAG>{
    {etq_importar}        { System.out.println("Token2:|"+yytext() + "|"); return new Symbol(symGxml.etq_importar, yyline, yycolumn, yytext()); }           
    {etq_ventana}         { System.out.println("Token2:|"+yytext() + "|");return new Symbol(symGxml.etq_ventana, yyline, yycolumn, yytext()); }      
    {etq_contenedor}      { System.out.println("Token2:|"+yytext() + "|");return new Symbol(symGxml.etq_contenedor, yyline, yycolumn, yytext()); } 
    {etq_texto}           { System.out.println("Token2:|"+yytext() + "|");return new Symbol(symGxml.etq_texto, yyline, yycolumn, yytext()); } 
    {etq_control}         { System.out.println("Token2:|"+yytext() + "|");return new Symbol(symGxml.etq_control, yyline, yycolumn, yytext()); } 
    {etq_listadatos}      { System.out.println("Token2:|"+yytext() + "|");return new Symbol(symGxml.etq_listadatos, yyline, yycolumn, yytext()); } 
    {etq_datos}           { System.out.println("Token2:|"+yytext() + "|");return new Symbol(symGxml.etq_datos, yyline, yycolumn, yytext()); } 
    {etq_defecto}         { System.out.println("Token2:|"+yytext() + "|");return new Symbol(symGxml.etq_defecto, yyline, yycolumn, yytext()); } 
    {etq_multimedia}      { System.out.println("Token2:|"+yytext() + "|");return new Symbol(symGxml.etq_multimedia, yyline, yycolumn, yytext()); } 
    {etq_boton}           { System.out.println("Token2:|"+yytext() + "|");return new Symbol(symGxml.etq_boton, yyline, yycolumn, yytext()); } 
    {etq_enviar}          { System.out.println("Token2:|"+yytext() + "|");return new Symbol(symGxml.etq_enviar, yyline, yycolumn, yytext()); } 
    {exp_id}              { System.out.println("Token1:|"+yytext() + "|");return new Symbol(symGxml.exp_id, yyline, yycolumn, yytext()); } 
	{cerrar}              { System.out.println("Token2:|"+yytext() + "|");System.out.println("<2" ); yybegin(YYINITIAL); return new Symbol(symGxml.cerrar, yyline, yycolumn, yytext()); }
    	
}       

[\t]			{ yycolumn+=4;}
{SPACE}         { /*Espacios en blanco, ignorados*/ }
{ENTER}         { /*Saltos de linea, ignorados*/}

.   { 
        agregarerror(yyline+1,yycolumn,"lexico","caracter irreconocible: "+yytext());
        System.out.println("error Lex:"+yytext());  
    }
