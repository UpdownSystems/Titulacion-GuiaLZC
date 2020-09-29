$(document).ready(function()
{
    var Init = false;
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
    function CargarDatos(){
        document.getElementById('listado').innerHTML = '';
        if(!Init){
            firebase.initializeApp(firebaseConfig);
            Init = true;
        }


        var database = firebase.database();
        // Fijarse que la ruta de partida ahora es la colección productos:
        var referencia=database.ref("usuarios");
        var categoria={};

        referencia.on('value',function(datos)
        {
            categoria=datos.val();
            var cat='<div class="table-responsive">';
            cat+='<table class="table table-striped table-hover" id="TablaClientes">';
            cat+='<tr>';
            cat+='<th>ID</th>';
            cat+='<th>Nombre</th>';
            cat+='<th>Negocio</th>';
            cat+='<th>Ciudad</th>';
            cat+='<th>Localidad</th>';
            cat+='<th>Direccion</th>';
            cat+='<th>Telefono</th>';
            cat+='<th>Email</th>';
            cat+='<th>Categoria</th>';
            cat+='<th>Opciones</th>';
            cat+='</tr>';
            // Recorremos las categorias y los mostramos
            $.each(categoria, function(indice,valor)
            {
                cat+='<tr>';
                cat+='<td>'+indice+'</td>';
                cat+='<td>'+valor.nombreJefe+'</td>';
                cat+='<td>'+valor.nombreNegocio+'</td>';
                cat+='<td>'+valor.ciudad+'</td>';
                cat+='<td>'+valor.localidad+'</td>';
                cat+='<td>'+valor.direccion+'</td>';
                cat+='<td>'+valor.telefono+'</td>';
                cat+='<td>'+valor.email+'</td>';
                cat+='<td>'+valor.categoria+'</td>';
                cat+='<td id="Botones">';
                
                if(valor.sesion == 1){
                    cat+='<button data="' + indice + '@1' + '" href="Clientes.php?aksi=delete&nik='+indice+'" title="Inhabilitar" class="btn btn-danger btn-sm"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></button>';
                }else{
                    cat+='<button data="' + indice + '@0' + '" href="Clientes.php?aksi=delete&nik='+indice+'" title="Habilitar" class="btn btn-primary btn-sm"><span class="glyphicon glyphicon-ok" aria-hidden="true"></span></button>';
                }
                cat+='<a class="btn btn-primary btn-sm" href="Editar_clientes.php?id='+indice+'&nombre='+valor.nombreJefe+'&negocio='+valor.nombreNegocio+'&ciudad='+valor.ciudad+'&localidad='+valor.localidad+'&direccion='+valor.direccion+'&telefono='+valor.telefono+'&email='+valor.email+'&categoria='+valor.categoria+'" title="Editar"><i class="glyphicon glyphicon-pencil" aria-hidden="true"></i></a>';

                cat+='</td>';
                cat+='</tr>';
            });
            cat+='</table>';
            cat+='</div>';
            $(cat).appendTo('#listado');
            var Filas = document.getElementById('TablaClientes').getElementsByTagName('tr');
            for(var i = 1; i < Filas.length; i++){
                Filas[i].getElementsByTagName('td')[9].getElementsByTagName('button')[0].addEventListener('click', function(){
                    var Accion = this.attributes.data.value.split('@')[1];
                    var Valor;
                    if(Accion == 1){ Valor = "0";}
                    if(Accion == 0){ Valor = "1";}
                    //firebase.initializeApp(firebaseConfig);

                    var database = firebase.database();

                    var categoria;
                    var id = this.attributes.data.value.split('@')[0].toString().trim();

                    var referencia=database.ref("usuarios");

                    firebase.database().ref().child('/usuarios/' + id)
                    .update({ sesion: Valor }).then(function(){
                        document.getElementById('listado').innerHTML = '';
                        CargarDatos();
                    });
                });
            }
            

        },function(objetoError){
            cat+='<tr><td colspan="8">No hay datos.</td></tr>';
            $(cat).appendTo('#listado');
        });
    }

    CargarDatos();
    

});

function MantenerSesion() {
    try{
        Interv = setInterval(function(){
            var AjaxObj = null;
            try{
                if (window.XMLHttpRequest) {
                    AjaxObj = new XMLHttpRequest();
                } else {
                    AjaxObj = new ActiveXObject("Microsoft.XMLHTTP");
                }
                AjaxObj.onreadystatechange = function() {
                    if (AjaxObj.readyState == 0){
                        console.log("No inicializada");
                    }
                    if (AjaxObj.readyState == 1){
                        console.log("Leyendo (Conexion con el servidor establecida)");
                    }
                    if (AjaxObj.readyState == 2){
                        console.log("Leido (Solicitud recibida)");
                    }
                    if (AjaxObj.readyState == 3){
                        console.log("Interactiva (Procesando solicitud)");
                    }
                    if (AjaxObj.readyState == 4){
                        console.log("Completo (Solicitud finalizada y respuesta recibida)");
                        if(AjaxObj.status == 200){
                            console.log("OK");
                        }else{
                            console.log("Problema con la peticion : " + AjaxObj.status);
                        }
                    }
                }
                AjaxObj.open("POST", "php/MantenerSesion.php" , false);
                AjaxObj.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
                AjaxObj.send();
            }catch(Ex){
                console.log(Ex);
            }
        }, 60000);
    }catch(Ex){
        console.error(Ex);
    }
}

window.onload = MantenerSesion();