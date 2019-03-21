\\seccion 1 -------------------------------------
Var Ven_VentanaPrincipal = CrearVentana( "#000000" , 100 , 100 );
Var Contenedor1_VentanaPrincipal = Ven_VentanaPrincipal.CrearContenedor(400,400,"#F3EEED",verdadero,10,10 );
Contenedor1_VentanaPrincipal.CrearTexto(500,500,"#F3EEED",10,10,verdadero,falso,"Haga clic en el siguiente boton para iniciar la evaluacion");
Var btnEvaluacion_VentanaPrincipal = Contenedor1_VentanaPrincipal.CrearBoton(500,500,"#F3EEED",60,40,CargarVentana_VentanaAritmetica(),"Iniciar Evaluacion",500,500);
btnEvaluacion_VentanaPrincipal.AlClic( Ejecutar_btnEvaluacion_VentanaPrincipal());
Contenedor1_VentanaPrincipal.CrearTexto(500,500,"#F3EEED",10,250,falso,verdadero,"Haga clic en el siguiente boton para iniciar el area de reportes");
Var btnReportes_VentanaPrincipal = Contenedor1_VentanaPrincipal.CrearBoton(500,500,"#F3EEED",60,300,CargarVentana_VentanaReportes(),"Iniciar Reportes",500,500);
btnReportes_VentanaPrincipal.AlClic( Ejecutar_btnReportes_VentanaPrincipal());
Var btnEnviar_VentanaPrincipal = Contenedor1_VentanaPrincipal.CrearBoton(500,500,"#F3EEED",60,350,nulo,"Sin funcionalidad",500,500);
btnEnviar_VentanaPrincipal.AlClic( Ejecutar_btnEnviar_VentanaPrincipal());
\\seccion 2 -------------------------------------
funcion Ejecutar_btnEvaluacion_VentanaPrincipal(){
	Bienvenido();
}
funcion Ejecutar_btnReportes_VentanaPrincipal(){
	BienvenidoReporte();
}
funcion Ejecutar_btnEnviar_VentanaPrincipal(){
	Var_VentanaPrincipal.crearArrayDesdeArchivo();
	EnviarSinFuncionalidad();
}
\\seccion 3 -------------------------------------
Ven_VentanaPrincipal.AlCargar();
Ven_VentanaPrincipal.AlCerrar();
funcion CargarVentana_VentanaPrincipal(){
Ven_VentanaPrincipal.AlCargar();
}