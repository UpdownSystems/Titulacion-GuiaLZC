<?php 
$nombre = $_POST['name']; 
$correo_electronico= $_POST['email']; 
$telefono = $_POST['phone']; 
$negocio=$_POST['negocio']; 
$nota=$_POST['note']; 

$mensaje = "Nombre: " . $nombre . " \r\n"; 
$mensaje .= "Correo: " . $correo_electronico . " \r\n"; 
$mensaje .= "Teléfono: " . $telefono . " \r\n"; 
$mensaje .= "Nombre del negocio: " . $negocio . " \r\n";  
$mensaje .= "Mensaje: " . $nota . " \r\n";  
$mensaje .= "Fecha de solicitud: " . date('d/m/Y', time()); 

$para = 'contacto@pymespace.com.mx'; 

mail($para, "Solicitud de servicio", utf8_decode($mensaje)); 

header('Location: Confirmacion.html');

?> 