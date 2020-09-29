$(document).ready(function()
 {
    // Inicializar la base de datos
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

    var database = firebase.database();

    var categoria;
    var id;

    $("#formularioAlta").change(function()
    {
        categoria=$("#categoria").val();
    });


    $("#botonGuardar").click(function()
    {
        categoria=$("#categoria").val();
        id=$("#id").val();
        var referencia=database.ref("categorias");

        firebase.database().ref().child('/categorias/' + id)
        .update({ name: categoria }).then(function(){            
        });
        alert("Editado Correctamente");
        window.history.go(-1)
    });

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