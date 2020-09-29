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
<?php
$id = $_GET['id'];
$nombre=$_GET['nombre'];
$negocio = $_GET['negocio'];
$ciudad = $_GET['ciudad'];
$localidad = $_GET['localidad'];
$direccion = $_GET['direccion'];
$telefono = $_GET['telefono'];
$email = $_GET['email'];
$categoria = $_GET['categoria'];
?>
<!DOCTYPE html>
<html lang="es">
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
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
</head>
<body>
<nav class="navbar navbar-default navbar-fixed-top">
	<div class="container">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
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
					<li><a href="Agregar_Cliente.php?TipoUsuario=on#">Agregar Clientes</a></li>
					<li><a href="Configuracion.php">Reestablecer contraseña</a></li>
					<li><a href="php/CerrarSesion.php">Cerrar sesión</a></li>
				</ul>
			</div><!--/.nav-collapse -->
	</div>
	</nav>
	<div class="container">
		<div class="content">
			<h2>Clientes &raquo; Editar clientes</h2>
			<hr />

			<form class="form-horizontal" id="formularioAlta">
			<div class="form-group">
					<label class="col-sm-3 control-label">ID</label>
					<div class="col-sm-4">
						<input type="text" id="id" class="form-control" required readonly="readonly" value="<?php echo $id;?>" >
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-3 control-label">Nombre</label>
					<div class="col-sm-4">
						<input type="text" id="nombre" class="form-control" placeholder="Alfredo" required value="<?php echo $nombre;?>">
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-3 control-label">Negocio</label>
					<div class="col-sm-4">
						<input type="text" id="negocio" class="form-control" placeholder="La pacanda" required value="<?php echo $negocio;?>">
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-3 control-label">Ciudad</label>
					<div class="col-sm-4">
						<input type="text" id="ciudad" class="form-control" placeholder="Lazaro Cardenas" required value="<?php echo $ciudad;?>">
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-3 control-label">Localidad</label>
					<div class="col-sm-4">
						<input type="text" id="localidad" class="form-control" placeholder="Lazaro Cardenas" required value="<?php echo $localidad;?>">
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-3 control-label">Direccion</label>
					<div class="col-sm-4">
						<input type="text" id="direccion" class="form-control" placeholder="Lazaro Cardenas" required value="<?php echo $direccion;?>">
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-3 control-label">Telefono</label>
					<div class="col-sm-4">
						<input type="text" id="telefono" class="form-control" placeholder="7894561230" required value="<?php echo $telefono;?>">
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-3 control-label">Email</label>
					<div class="col-sm-4">
						<input type="text" id="email" class="form-control" placeholder="asd@hotmail.com" required readonly="readonly" value="<?php echo $email;?>">
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-3 control-label">Categoria</label>
					<div class="col-sm-4">
						<input type="text" id="categoria" class="form-control" placeholder="Fruteria" required value="<?php echo $categoria;?>">
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-3 control-label">&nbsp;</label>
					<div class="col-sm-6">
					
						<input type="submit" id="editar" class="btn btn-sm btn-primary" value="Editar cliente">
						<a href="Clientes.php" class="btn btn-sm btn-danger">Cancelar</a>
					</div>
				</div>
			</form>
		</div>
	</div>

	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
	<script src="js/bootstrap.min.js"></script>

		<!--Firebase-->
<script src="https://www.gstatic.com/firebasejs/7.6.2/firebase.js"></script>
<script src="js/Editar_clientes.js"></script>
<!--===============================================================================================-->
</body>
</html>
