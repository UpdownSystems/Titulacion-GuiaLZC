<?php
	session_start();
	if(isset($_SESSION["Logueado"])){
        if($_SESSION["Logueado"]){
            #
        }else{
            header("Location:Login.php");
        }
    }else{
        header("Location:Login.php");
    }
?>
<!DOCTYPE html>
<html lang="es">
<head>

	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>Guía Comercial</title>

	<!-- Bootstrap -->
	<link href="css/bootstrap.min.css" rel="stylesheet">
	<link href="css/style_nav.css" rel="stylesheet">

	<style>
		.content {
			margin-top: 80px;
		}
	</style>

</head>
<body>
<nav class="navbar navbar-default navbar-fixed-top">
	<div class="container">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
					<span class="sr-only">Toggle navigation</span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
				</button>
				<a class="navbar-brand visible-xs-block visible-sm-block" href="">Inicio</a>
			</div>
			<div id="navbar" class="navbar-collapse collapse">
				<ul class="nav navbar-nav ">
					<li><a href="Categorias.php">Lista de categorias</a></li>
					<li class="active"><a href="Clientes.php">Lista de Clientes</a></li>
					<li><a href="Agregar_categoria.php">Agregar Categorias</a></li>
					<li ><a href="Agregar_Cliente.php?TipoUsuario=on#">Agregar Clientes</a></li>
					<li><a href="Configuracion.php">Reestablecer contraseña</a></li>
					<li><a href="php/CerrarSesion.php">Cerrar sesión</a></li>
				</ul>
			</div><!--/.nav-collapse -->
	</div>
</nav>

	<div class="container">
		<div class="content">
			<h2>Lista de clientes</h2>
			<hr />			
			<br />
			<div id="listado">
			</div>
    </div>
	</div>
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
	<script src="js/bootstrap.min.js"></script>

	<!--Firebase-->
<script src="https://www.gstatic.com/firebasejs/7.6.2/firebase.js"></script>
<script src="js/Cliente.js"></script>
<!--===============================================================================================-->
</body>
</html>
