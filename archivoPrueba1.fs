importar("FuncionesEvaluacion.fs");
/*+++++++++++++++++++ventana VentanaPrincipal++++++++++++++++++++++++*/
var Ven_VentanaPrincipal=CrearVentana("#ffffff",720,1280,"VentanaPrincipal");
var Contenedor1_VentanaPrincipal= Ven_VentanaPrincipal.CrearContenedor(400, 400, "#66FFB2", verdadero, 10, 10);
Contenedor1_VentanaPrincipal.CrearTexto("Arial", 14, "#3519BD", 10, 10, verdadero, falso, "Haga clic en el siguiente boton para iniciar la evaluacion");
var btnEvaluacion_VentanaPrincipal=Contenedor1_VentanaPrincipal.CrearBoton("Arial", 14, "#000000", 60, 40, Cargar_VentanaPrincipal_VentanaAritmetica(), "", 100, 100);
Contenedor1_VentanaPrincipal.CrearImagen("oceano.jpg", 300, 850, falso, 100, 100);
