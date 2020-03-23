<?php
	session_start();
	session_unset("Logueado");
	session_destroy();
	header("Location:../index.html");
?>
