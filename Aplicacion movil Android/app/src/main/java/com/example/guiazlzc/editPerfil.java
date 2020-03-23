package com.example.guiazlzc;
/*
 * Echo por: Updown Systems
 * Programado por: Jorge Luis Pérez Medina
 */
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


//import static com.google.firebase.internal.FirebaseAppHelper.getUid;

public class editPerfil extends BaseActivity {
    private static final String TAG = "editPerfil";

    private EditText setNombreEncargado, setNombreNegocio, setEmail, setTelefono, setCiudad, setDireccion;
    private AutoCompleteTextView setLocalidad, setCategoria;
    private FloatingActionButton btnSubmit;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference db, dbLocation;
    private FirebaseDatabase ref;

    // Class User
    private User usuario;

    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_perfil);

        // Vista XML
        Toolbar toolbar = findViewById(R.id.toolbar_perfil);
        setNombreEncargado = (EditText)findViewById(R.id.fieldNombreEncargado);
        setNombreNegocio = (EditText)findViewById(R.id.fieldNegocio);
        setEmail = (EditText)findViewById(R.id.fieldEmail);
        setTelefono = (EditText)findViewById(R.id.fieldTelefono);
        setCiudad = (EditText)findViewById(R.id.fieldCiudad);
        setDireccion = (EditText)findViewById(R.id.fieldDireccion);
        setLocalidad = (AutoCompleteTextView)findViewById(R.id.fieldLocalidad);
        setCategoria = (AutoCompleteTextView)findViewById(R.id.fieldCategoria);
        btnSubmit = (FloatingActionButton)findViewById(R.id.fabSubmitEditPerfil);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance();
        dbLocation = FirebaseDatabase.getInstance().getReference();

        // Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = ref.getReference("usuarios").child(user.getUid());
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User usuarios = dataSnapshot.getValue(User.class);

                setNombreNegocio.setText(usuarios.nombreNegocio);
                setCategoria.setText(usuarios.categoria);
                setNombreEncargado.setText(usuarios.nombreJefe);
                setEmail.setText(usuarios.email);
                setCiudad.setText(usuarios.ciudad);
                setTelefono.setText(usuarios.telefono);
                setLocalidad.setText(usuarios.localidad);
                setDireccion.setText(usuarios.direccion);

                Log.d("perfil", "nombre:" + usuarios.nombreNegocio);
                Log.d("perfil", "apaterno:" + usuarios.categoria);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Error", "No se puedo obtener los datos de la BD de Firebase :(");
            }
        });

        // Obtiene los datos de la base de datos del AutoComplete para seleccionar una Localidad
        final ArrayAdapter<String> autoCompleteLocalidades = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        dbLocation.child("Localidades").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> nombreL = new ArrayList<String>();

                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                    String areaName = areaSnapshot.child("name").getValue(String.class);
                    nombreL.add(areaName);
                }

                AutoCompleteTextView autoComplete = (AutoCompleteTextView) findViewById(R.id.fieldLocalidad);
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(editPerfil.this, android.R.layout.simple_spinner_item, nombreL);
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
        dbLocation.child("categorias").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> nombreC = new ArrayList<String>();

                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                    String areaName = areaSnapshot.child("name").getValue(String.class);
                    nombreC.add(areaName);
                }

                AutoCompleteTextView autoComplete = (AutoCompleteTextView) findViewById(R.id.fieldCategoria);
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(editPerfil.this, android.R.layout.simple_spinner_item, nombreC);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                autoComplete.setAdapter(areasAdapter);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Obtener","No se puede obtener los datos de categorias");
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });

    }

    /*private boolean validateForm() {
        final TextInputLayout titleTextInput = findViewById(R.id.fieldTitleInput);
        final TextInputLayout bodyTextInput = findViewById(R.id.fieldBodyInput);
        final TextInputLayout phoneTextInput = findViewById(R.id.fieldPhoneInput);
        final TextInputLayout emailTextInput = findViewById(R.id.fieldEmailInput);

        boolean result = true;


        if (TextUtils.isEmpty(mTitleField.getText())) {
            titleTextInput.setError("Introduzca un nombre");
            result = false;
        } else {
            titleTextInput.setError(null);
        }

        if (TextUtils.isEmpty(mBodyField.getText())) {
            bodyTextInput.setError("Introduzca una dirección");
            result = false;
        } else {
            bodyTextInput.setError(null);
        }

        if (TextUtils.isEmpty(mPhoneField.getText())) {
            phoneTextInput.setError("Introduzca un teléfono");
            result = false;
        } else {
            phoneTextInput.setError(null);
        }

        if (TextUtils.isEmpty(mCategoriaField.getText())) {
            mCategoriaField.setError("");
            result = false;
        }

        if (!validarEmail(mEmailField.getText().toString())) {
            emailTextInput.setError("Introduzca un email");
            result = false;
        } else {
            emailTextInput.setError(null);
        }

        return result;
    }*/

    private boolean validarEmail(String email) {
        Pattern patterns = Patterns.EMAIL_ADDRESS;
        return patterns.matcher(email).matches();
    }

    private void submitPost() {
        final String email = setEmail.getText().toString().trim();
        final String nombreJefe = setNombreEncargado.getText().toString().trim();
        final String localidad = setLocalidad.getText().toString().trim();
        final String categoria = setCategoria.getText().toString().trim();
        final String telefono = setTelefono.getText().toString().trim();
        final String ciudad = setCiudad.getText().toString().trim();
        final String direccion = setDireccion.getText().toString().trim();
        final String nombreNegocio = setNombreNegocio.getText().toString().trim();

        /*if (!validateForm()) {
            return;
        }*/

        // [COMIENZO single_value_read]
        final String userId = getUid();
        dbLocation.child("usuarios").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Obtener valor de usuario
                        User user = dataSnapshot.getValue(User.class);

                        if (user == null) {
                            // El usuario es nulo, error fuera
                            Log.e(TAG, "User " + userId + "is unexpectedly null");
                            Toast.makeText(editPerfil.this,
                                    "Error: no se pudo recuperar el usuario.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Actualiza los campos
                            actualizarCampos(nombreJefe, localidad, categoria, telefono, ciudad, direccion, nombreNegocio, email);
                        }

                        // Termina esta actividad, regresa a la actividad anterior
                        //finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
        // [FIN single_value_read]
    }



    private void actualizarCampos(String nombreJefe, String localidad, String categoria, String telefono, String ciudad, String direccion, String nombreNegocio, String email) {
        usuario = new User(nombreJefe, localidad, categoria, telefono, ciudad, direccion, nombreNegocio, email);
        dbLocation = ref.getReference("usuarios").child(mAuth.getUid());

        dbLocation.setValue(usuario).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                 Snackbar.make(coordinatorLayout, "Su perfil fue editado correctamente", Snackbar.LENGTH_LONG).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(coordinatorLayout, "Lo sentimos, intentelo de nuevo", Snackbar.LENGTH_LONG).show();
            }
        });

        Log.d("usuarioo","nombre:" + usuario.nombreJefe);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Maneje los elementos de la barra de acción haciendo clic aquí La barra de acción
        // maneja automáticamente los clics en el botón Inicio / Arriba, tanto tiempo
        // al especificar una actividad principal en AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
