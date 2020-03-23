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
					<li ><a href="Agregar_Cliente.php">Agregar Clientes</a></li>
					<li class="active"><a href="Configuracion.php">Reestablecer contraseña</a></li>
					<li><a href="php/CerrarSesion.php">Cerrar sesión</a></li>
				</ul>
			</div><!--/.nav-collapse -->
	</div>
	</nav>
	<div class="container">
		<div class="content">
			<h2>Usuario &raquo; Reestablecer contraseña</h2>
			<hr />

			<?php
			if(isset($_POST['editar'])){
				$usuario= mysqli_real_escape_string($con,(strip_tags($_POST["Usuario"],ENT_QUOTES)));	
				$contraseña= mysqli_real_escape_string($con,(strip_tags($_POST["Contraseña"],ENT_QUOTES)));		
				$confirmar= mysqli_real_escape_string($con,(strip_tags($_POST["Confirmar"],ENT_QUOTES)));
				if($contraseña==$confirmar)
				{
					$insert = mysqli_query($con, "UPDATE usuario SET usuario='$usuario',password='$contraseña'") or die(mysqli_error());
					if($insert){
						echo '<div class="alert alert-success alert-dismissable"><button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>Bien hecho! se actualizo el usuario con éxito.</div>';
					}else{
						echo '<div class="alert alert-danger alert-dismissable"><button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>Error. No se pudo guardar los datos !</div>';
					}
				}
				else{
					echo '<div class="alert alert-danger alert-dismissable"><button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>Error. La contraseña no coincide con la confirmación!</div>';
				}		
			}
			?>

			<div class="row">
				<div class="col-xs-6 col-xs-offset-3 form-group">
					<label class="control-label">Correo</label>
					<div class="">
						<input type="text" id="Correo" name="Correo" class="form-control" placeholder="correo@ejemplo.com" required >
					</div>
				</div>
				<div class="col-xs-6 col-xs-offset-3 form-group" align="center">
					<label class=" control-label">&nbsp;</label>
					<div class="">
						<button id="BtnEnviarCorreo" class="btn btn-sm btn-primary">Enviar</button>
					</div>
				</div>
			</div>
		</div>
	</div>

	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
	<script src="js/bootstrap.min.js"></script>
	<script src="js/bootstrap-datepicker.js"></script>
	<script src="https://www.gstatic.com/firebasejs/7.6.2/firebase.js"></script>
	<script>
	$('.date').datepicker({
		format: 'dd-mm-yyyy',
	})
	</script>
	<script type="text/javascript">
		function InicializarBD(){
			var firebaseConfig = {
				apiKey: "AIzaSyCGY-4qDPo__8RRK6WYUHgTnnattvYXhtE",
				authDomain: "pyme-space.firebaseapp.com",
				databaseURL: "https://pyme-space.firebaseio.com",
				projectId: "pyme-space",
				storageBucket: "pyme-space.appspot.com",
				messagingSenderId: "499302145560",
				appId: "1:499302145560:web:c590842ff00075b2062d6d",
				measurementId: "G-V0072ERKQB"
			};
			firebase.initializeApp(firebaseConfig);
			AsignarEventos();
		}
		function EnviarCorreo(){
			try{
				var auth = firebase.auth();
				auth.sendPasswordResetEmail(document.getElementById('Correo').value).then(function() {
					alert('Correo enviado correctamente');
					document.getElementById('Correo').value = '';
				}).catch(function(error) {
					alert('Error de envio, verifique el correo y su conexion a internet');
				});
			}catch(Ex){
				console.error(Ex);
			}
		}
		function AsignarEventos(){
			document.getElementById('BtnEnviarCorreo').addEventListener('click',function(){
				EnviarCorreo();
			});
		}
		window.onload = InicializarBD();
	</script>
</body>
</html>
