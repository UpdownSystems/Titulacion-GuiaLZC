<?php
	session_start();

	$_SESSION["Logueado"] = true;
	echo '{"Exito": true}';
?>