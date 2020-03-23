
function Solicitud(Url, Method, FlagAsinc) {
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
    AjaxObj.open(Method, Url , FlagAsinc);
    AjaxObj.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    AjaxObj.send();
  }catch(Ex){
    console.log(Ex);
  }
  return AjaxObj.responseText;
}

function login()
{
var email=document.getElementById('usuario').value;
var password=document.getElementById('password').value;

	

firebase.auth().signInWithEmailAndPassword(email, password).then(function(result) {

	var database = firebase.database();
    // Fijarse que la ruta de partida ahora es la colección productos:
    var referencia=database.ref("usuarios");
    var categoria={};

      referencia.on('value',function(datos)
      {
          categoria=datos.val();

          $.each(categoria, function(indice,valor)
          {
            if(email==valor.email&&valor.Administrador==1&&valor.sesion==1)
            {
              Solicitud("php/IniciarSesion.php", "POST", false);
              location.href="Categorias.php";
            }
            else
            {

            }
          });
  
      },function(objetoError){
      	console.log("Error");
      });

	}).catch(function(error) { });

}
