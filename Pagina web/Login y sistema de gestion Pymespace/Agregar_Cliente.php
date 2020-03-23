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
	<meta http-equiv="X-UA-Compatible" >
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>Guia Comercial</title>
	<link href="css/bootstrap.min.css" rel="stylesheet">
	<link href="css/bootstrap-datepicker.css" rel="stylesheet">
	<link href="css/style_nav.css" rel="stylesheet">
	<style>
		.content {
			margin-top: 80px;
		}
	</style>
<script src="https://www.gstatic.com/firebasejs/7.6.2/firebase.js"></script>
    <script src="https://www.gstatic.com/firebasejs/7.7.0/firebase-auth.js"></script>
    <script src="https://www.gstatic.com/firebasejs/7.7.0/firebase-firestore.js"></script>
    <script src="https://www.gstatic.com/firebasejs/7.7.0/firebase-database.js"></script>
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
					<li><a href="Clientes.php">Lista de Clientes</a></li>
					<li><a href="Agregar_categoria.php">Agregar Categorias</a></li>
					<li class="active"><a href="Agregar_Cliente.php">Agregar Clientes</a></li>
					<li><a href="Configuracion.php">Reestablecer contraseña</a></li>
					<li><a href="php/CerrarSesion.php">Cerrar sesión</a></li>
				</ul>
			</div><!--/.nav-collapse -->
	</div>
</nav>

	<div class="container">
		<div class="content">
			<h2>Clientes &raquo; Agregar clientes</h2>
			<hr />

			<form method="#" action="#" onsubmit="registro();" class="form-horizontal">
				
			<div class="form-group">
					<label class="col-sm-3 control-label">Nombre de la persona encargada</label>
					<div class="col-sm-4">
						<input type="text" id="nombreJefe" class="form-control" placeholder="Alberto" required>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-3 control-label">Nombre del negocio</label>
					<div class="col-sm-4">
						<input type="text" id="nombreNegocio" class="form-control" placeholder="La pacanda" required>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-3 control-label">Categoria del negocio</label>
					<div class="col-sm-4">
						<input type="text" id="categoria" class="form-control" placeholder="Fruteria" required>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-3 control-label">Ciudad</label>
					<div class="col-sm-4">
						<input type="text" id="ciudad" class="form-control" placeholder="Lazaro Cardenas" required>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-3 control-label">Localidad</label>
					<div class="col-sm-4">
						<input type="text" id="localidad" class="form-control" placeholder="La Mira" required>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-3 control-label">Direccion</label>
					<div class="col-sm-4">
						<input type="text" id="direccion" class="form-control" placeholder="Avenida Universitaria Numero 32" required>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-3 control-label">Teléfono</label>
					<div class="col-sm-4">
						<input type="text" id="telefono" class="form-control" placeholder="7531234567" required>
					</div>
				</div>

				<div class="form-group">
					<label class="col-sm-3 control-label">Email</label>
					<div class="col-sm-4">
						<input type="text" id="email" class="form-control" placeholder="asd@hotmail.com" required>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-3 control-label">Contraseña</label>
					<div class="col-sm-4">
						<input type="password" id="password" class="form-control" placeholder="1234" required>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-3 control-label">Confirmar Contraseña</label>
					<div class="col-sm-4">
						<input type="password" id="confpassword" class="form-control" placeholder="1234" required>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-3 control-label">Tipo de usuario</label>
					<div class="col-sm-4">
						Usuario comun
						<input type="radio" name="TipoUsuario">
						Adminsitrador
						<input type="radio" name="TipoUsuario">
					</div>
				</div>
				

				<div class="form-group">
					<label class="col-sm-3 control-label">&nbsp;</label>
					<div class="col-sm-6">
						<input type="submit" class="btn btn-sm btn-primary" value="Agregar cliente">
						<a href="Clientes.php" class="btn btn-sm btn-danger">Cancelar</a>
					</div>
				</div>

			</form>
		</div>
	</div>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script src="js/bootstrap.min.js"></script>

<!--Firebase-->

<script src ="js/Agregar_cliente.js"></script>
<!--===============================================================================================-->
</body>
</html>
