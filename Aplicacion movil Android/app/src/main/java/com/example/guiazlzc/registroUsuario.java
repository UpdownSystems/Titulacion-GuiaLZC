package com.example.guiazlzc;
/*
 * Echo por: Updown Systems
 * Programado por: Jorge Luis Pérez Medina
 */
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class registroUsuario extends AppCompatActivity {

    private ImageButton btnBack;
    private EditText enterEmail, enterPassword, enterTelefono, enterNomCompleto, enterCiudad, confirmPassword, enterDireccion, enterNameNegocio;
    private Button signUp;
    private AutoCompleteTextView inputLocalidad, inputCategoria;
    private ProgressBar progressBar;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference dtbLocation;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener aStateListener;

    // Class User
    private User usuario;

    // Varible para el guardado de datos de los usuarios al registrarse
    final FirebaseDatabase database = FirebaseDatabase.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);

        btnBack = (ImageButton)findViewById(R.id.btn_back);
        enterEmail = (EditText)findViewById(R.id.setEmailU);
        enterPassword = (EditText)findViewById(R.id.password);
        signUp = (Button)findViewById(R.id.sign_up_button);
        inputLocalidad = (AutoCompleteTextView)findViewById(R.id.selectLocalidad);
        inputCategoria = (AutoCompleteTextView)findViewById(R.id.selectCategoria);
        confirmPassword = (EditText)findViewById(R.id.confirm_password);
        //progressBar = (ProgressBar)findViewById(R.id.progressBar);
        enterTelefono = (EditText)findViewById(R.id.setTelefono);
        enterNomCompleto = (EditText)findViewById(R.id.setNameCompletJefe);
        enterCiudad = (EditText)findViewById(R.id.setCiudad);
        enterDireccion = (EditText)findViewById(R.id.setDireccion);
        enterNameNegocio = (EditText)findViewById(R.id.nameNegocio);


        // Firebase
        mAuth = FirebaseAuth.getInstance();
        dtbLocation = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();


        // Actividad Regresar
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // Obtiene los datos de la base de datos del AutoComplete para seleccionar una Localidad
        final ArrayAdapter<String> autoCompleteLocalidades = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        dtbLocation.child("Localidades").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> nombreL = new ArrayList<String>();

                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                    String areaName = areaSnapshot.child("name").getValue(String.class);
                    nombreL.add(areaName);
                }

                AutoCompleteTextView autoComplete = (AutoCompleteTextView) findViewById(R.id.selectLocalidad);
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(registroUsuario.this, android.R.layout.simple_spinner_item, nombreL);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                autoComplete.setAdapter(areasAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Obtener","No se puede obtener los datos de localidades");
            }
        });


        // Obtiene los datos de la base de datos del AutoComplete para seleccionar una Categoria
        final ArrayAdapter<String> autoCompleteCategoria = new ArrayAdapter<>(this,android.R.layout.simple_list_item_2);
        dtbLocation.child("categorias").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> nombreC = new ArrayList<String>();

                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                    String areaName = areaSnapshot.child("name").getValue(String.class);
                    nombreC.add(areaName);
                }

                AutoCompleteTextView autoComplete = (AutoCompleteTextView) findViewById(R.id.selectCategoria);
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(registroUsuario.this, android.R.layout.simple_spinner_item, nombreC);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                autoComplete.setAdapter(areasAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Obtener","No se puede obtener los datos de categorias");
            }
        });


        // Programacion del boton registro
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = enterEmail.getText().toString().trim();
                String password = enterPassword.getText().toString().trim();
                String conPassword = confirmPassword.getText().toString().trim();
                final String nombreJefe = enterNomCompleto.getText().toString().trim();
                final String localidad = inputLocalidad.getText().toString().trim();
                final String categoria = inputCategoria.getText().toString().trim();
                final String telefono = enterTelefono.getText().toString().trim();
                final String ciudad = enterCiudad.getText().toString().trim();
                final String direccion = enterDireccion.getText().toString().trim();
                final String nombreNegocio = enterNameNegocio.getText().toString().trim();


                // Expresion regular para verificar el Email
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                // Condicionales para el validamiento de datos del formulario de registro
                if(!email.matches(emailPattern)) {
                    Toast.makeText(getApplicationContext(), "Correo Electrónico no válido", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Introduzca una Contraseña", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 8) {
                    Toast.makeText(getApplicationContext(), "Contraseña corta, introduzca al menos 8 caracteres!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(conPassword)) {
                    Toast.makeText(getApplicationContext(), "Confirme la contraseña", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!conPassword.equals(password)) {
                    Toast.makeText(getApplicationContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(localidad)) {
                    Toast.makeText(getApplicationContext(), "Seleccione una localidad!", Toast.LENGTH_SHORT).show();
                    return;
                }


                //Crea el usuario
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(registroUsuario.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //Snackbar.make(view, "Se ah registrado con éxito", Snackbar.LENGTH_LONG).show();
                        Toast.makeText(registroUsuario.this, "Se ah registrado con éxito", Toast.LENGTH_SHORT).show();
                        //progressBar.setVisibility(View.VISIBLE);

                        if (!task.isSuccessful()) {
                            Toast.makeText(registroUsuario.this, "Falló al autenticarse" + task.getException(), Toast.LENGTH_SHORT).show();
                        } else {
                            // Variable para enviar los datos a la BD de los usuarios que se registran
                            usuario = new User(nombreJefe, localidad, categoria, telefono, ciudad, direccion, nombreNegocio, email);
                            dtbLocation= database.getReference("usuarios").child(mAuth.getUid());
                            dtbLocation.setValue(usuario);
                            Log.d("usuarioo","nombre:" + usuario.nombreJefe);
                            aStateListener.onAuthStateChanged(mAuth);
                            /*user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener <Void>() {
                                @Override
                                public void onComplete(@NonNull Task <Void> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Le enviamos un Email para verificar su cuenta", Toast.LENGTH_SHORT).show();


                                    }
                                }
                            });*/
                        }
                        //progressBar.setVisibility(View.GONE);
                    }
                });

                //Oyente para verificar si el Email se a verificado
                aStateListener = new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        FirebaseUser usuario = mAuth.getCurrentUser();

                        if(!usuario.isEmailVerified() && usuario != null) {
                            //Toast.makeText(RegisterEmailPasswordActivity.this, "Correo Electronico no verificado", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            startActivity(new Intent(registroUsuario.this, MainActivity.class));
                            finish();
                        }
                    }
                };
            }
        });
    }
}


class User {
    public String nombreJefe;
    public String localidad;
    public String categoria;
    public String telefono;
    public String ciudad;
    public String direccion;
    public String nombreNegocio;
    public String email;

    public User(){}

    public User(String nombreJefe, String localidad, String categoria, String telefono, String ciudad, String direccion, String nombreNegocio, String email) {
        this.nombreJefe = nombreJefe;
        this.localidad = localidad;
        this.categoria = categoria;
        this.email = email;
        this.telefono = telefono;
        this.ciudad = ciudad;
        this.direccion = direccion;
        this.nombreNegocio = nombreNegocio;
    }
}