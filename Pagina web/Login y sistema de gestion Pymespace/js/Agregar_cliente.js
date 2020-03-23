
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

    function registro() {
      var auth = firebase.auth();
      var database = firebase.database();
      var email = document.getElementById('email').value;
      var password1 = document.getElementById('password').value;
      var password2 = document.getElementById('confpassword').value;
      var nombre=document.getElementById('nombreJefe').value;
      var negocio=document.getElementById('nombreNegocio').value;
      var categoria=document.getElementById('categoria').value;
      var ciudad=document.getElementById('ciudad').value;
      var localidad=document.getElementById('localidad').value;
      var direccion=document.getElementById('direccion').value;
      var telefono=document.getElementById('telefono').value;
      var Usuario = document.getElementsByName('TipoUsuario');
      var TipoUsuario;
      if(Usuario[0].checked){
        TipoUsuario = 0;
      }else{
        TipoUsuario = 1;
      }


      if (password1 == password2) {
        password = document.getElementById('password').value;
      } else {
        alert("Las contraseñas NO son iguales");
        return 0;
      }
  
  
      firebase.auth().createUserWithEmailAndPassword(email, password).then(function(user) {
          
      }).catch(function(error) {
        var errorCode = error.code;
        var errorMessage = error.message;
        if (errorCode == "auth/email-already-in-use") {
          alert("El correo ya esta en uso");
        }
        if (errorCode == "auth/weak-password") {
          alert("Contraseña muy vurnerable, 6 digitos minimo!.");
        }
      }).then(function(user) {
          var user = firebase.auth().getUid();
  
          firebase.database().ref('usuarios').child(user).set({
            nombreJefe:nombre,
            nombreNegocio:negocio,
            categoria:categoria,
            ciudad:ciudad,
            localidad:localidad,
            direccion:direccion,
            telefono:telefono,
            email: email,
            Administrador:TipoUsuario,
            sesion:"1"
          });
            
          /*user.updateProfile({
              displayName: nombre
            }).then(function() {
              alert("¡Gracias Por Registrarte!");
            }, function(error) {
              alert(error.message);
            });*/
      })
  }
  
  function ingresar() {
    var email = document.getElementById('correo').value;
    var password = document.getElementById('contrasena').value;
  
    firebase.auth().signInWithEmailAndPassword(email, password)
        .then(function (response) {
          console.log("Logueado correctamente");
        })
        .catch(function (error) {
          console.log("No Logueado correctamente");
    });
  }
  
  function datosMostrar(){
    var info = document.getElementById('main');
    //info.innerHTML = '<button class="btn btn-danger" onclick="cerrarSesion()">Cerrar sesion</button>';
  }
  
  function cerrarSesion() {
    firebase.auth().signOut()
        .then(function () {
            console.log("Cerraste sesion correctamente");
        }).catch(function (error) {
            // An error happened.
        });
  }
  
  function observador(){
    firebase.auth().onAuthStateChanged(function (user) {
      var info = document.getElementById('main');
  
        if (user) {
          var email = user.email;
          var uid = user.uid;
          console.log(uid);
          console.log("hay usuarios autenticados");
          //datosMostrar();
        } else {
          console.log("No hay usuarios autenticados");
        }
    });
  }
  
  
  observador();



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