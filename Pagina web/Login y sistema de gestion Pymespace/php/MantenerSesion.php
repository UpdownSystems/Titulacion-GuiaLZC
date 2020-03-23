<?php
	try{
		ini_set('session.gc_maxlifetime', 36000);
		session_start();
		echo '{"Exito":true}';
	}catch(Exception $Ex){
		echo '{"Exito":false}';
	}
?>