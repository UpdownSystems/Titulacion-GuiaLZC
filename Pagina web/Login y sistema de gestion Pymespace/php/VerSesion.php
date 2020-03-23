<?php
	session_start();

	function ObtenerIpPublica(){
        try{
            if (isset($_SERVER["HTTP_CLIENT_IP"])){
                return $_SERVER["HTTP_CLIENT_IP"];
            }elseif (isset($_SERVER["HTTP_X_FORWARDED_FOR"])){
                return $_SERVER["HTTP_X_FORWARDED_FOR"];
            }elseif (isset($_SERVER["HTTP_X_FORWARDED"])){
                return $_SERVER["HTTP_X_FORWARDED"];
            }elseif (isset($_SERVER["HTTP_FORWARDED_FOR"])){
                return $_SERVER["HTTP_FORWARDED_FOR"];
            }elseif (isset($_SERVER["HTTP_FORWARDED"])){
                return $_SERVER["HTTP_FORWARDED"];
            }else{
                return $_SERVER["REMOTE_ADDR"];
            }
        }catch(Exception $Ex){

        }
    }
    echo "IP : " . ObtenerIpPublica();
    echo '<br>';
    if(isset($_SESSION["Logueado"])){
        if($_SESSION["Logueado"]){
            echo "Hay una sesion activa";
        }else{
            echo "No hay una sesion activa";
        }
    }else{
        echo "No hay sesion definida";
    }
?>
