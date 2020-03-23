package com.example.guiazlzc;
/*
 * Echo por: Updown Systems
 * Programado por: Jorge Luis Pérez Medina
 */
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.example.guiazlzc.models.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditPostActivity extends BaseActivity {
    private static final String TAG = "EditPostActivity";

    public static final String EXTRA_POST_KEY = "post_key";

    private EditText mTitleField, mBodyField, mPhoneField, mEmailField, mLatitudField, mLongitudField;
    private Toolbar toolbar;
    private AutoCompleteTextView mCategoriaField;
    private FloatingActionButton mSubmitButton;
    private DatabaseReference dtbLocation, dbLocation, dtbLocation2;
    private FirebaseDatabase ref;

    private ValueEventListener mPostListener;
    private String mPostKey;
    private DatabaseReference mPostReference;
    private Object Post;

    private CoordinatorLayout coordinatorLayout;
    private TextInputEditText txtImageBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        // Obtener la clave de la publicación del Intent
        mPostKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        if (mPostKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        // Inicializamos la Base de datos
        mPostReference = FirebaseDatabase.getInstance().getReference().child("posts").child(mPostKey);

        ref = FirebaseDatabase.getInstance();
        dbLocation = FirebaseDatabase.getInstance().getReference();

        toolbar = findViewById(R.id.toolbar_perfil);
        mTitleField = findViewById(R.id.fieldTitle);
        mBodyField = findViewById(R.id.fieldBody);
        mPhoneField = findViewById(R.id.fieldPhone);
        mEmailField = findViewById(R.id.fieldEmail);
        mLatitudField = findViewById(R.id.fieldLatitud);
        mLongitudField = findViewById(R.id.fieldLongitud);
        mCategoriaField = findViewById(R.id.selectCategoria);
        mSubmitButton = findViewById(R.id.fabSubmitPost);
        txtImageBg = (TextInputEditText)findViewById(R.id.pictureDownloadUri);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        txtImageBg = (TextInputEditText) findViewById(R.id.pictureDownloadUri);

        // Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dtbLocation = FirebaseDatabase.getInstance().getReference();
        dtbLocation2 = FirebaseDatabase.getInstance().getReference();

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
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(EditPostActivity.this, android.R.layout.simple_spinner_item, nombreC);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                autoComplete.setAdapter(areasAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Obtener","No se puede obtener los datos de categorias");
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        // Agregue valor al oyente de eventos a la publicación
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Obtenga el objeto Post y use los valores para actualizar la IU
                Post post = dataSnapshot.getValue(Post.class);

                if(post == null) {
                    toolbar.setTitle("");
                } else {
                    toolbar.setTitle(post.title);
                    mTitleField.setText(post.title);
                    mBodyField.setText(post.body);
                    mEmailField.setText(post.email);
                    txtImageBg.setText(post.imageBg);
                    mCategoriaField.setText(post.categoria);
                    mPhoneField.setText(post.phone);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Error al obtener la publicación, registre un mensaje
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());

                Toast.makeText(EditPostActivity.this, "No se pudieron cargar los datos del negocio", Toast.LENGTH_SHORT).show();

            }
        };
        mPostReference.addValueEventListener(postListener);

        // Guarde una copia del post, escucha para que podamos eliminarla cuando la aplicación se detenga
        mPostListener = postListener;
    }

    private void submitPost() {
        final String title = mTitleField.getText().toString();
        final String body = mBodyField.getText().toString();
        final String phone = mPhoneField.getText().toString();
        final String email = mEmailField.getText().toString();
        final String categoria = mCategoriaField.getText().toString();
        final String imageBg = txtImageBg.getText().toString();
        final String latitud = mLatitudField.getText().toString();
        final String longitud = mLongitudField.getText().toString();

        //Toast.makeText(this, "Publicando...", Toast.LENGTH_SHORT).show();

        // [COMIENZO single_value_read]
        final String userId = getUid();
        //final String categoriaId = FirebaseDatabase.getInstance().getReference().child("categorias").push().getKey();
        dtbLocation.child("usuarios").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Obtener valor de usuario
                        User user = dataSnapshot.getValue(User.class);

                        if (user == null) {
                            // El usuario es nulo, error fuera
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(EditPostActivity.this,
                                    "Error: no se pudo recuperar el usuario.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Escribir nueva publicación
                            updatePost(userId, user.nombreJefe, title, body, phone, email, categoria, imageBg, latitud, longitud);
                            mTitleField.setText("");
                            mBodyField.setText("");
                            mPhoneField.setText("");
                            mEmailField.setText("");
                            mCategoriaField.setText("");
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

    private void updatePost(String userId, String username, String title, String body, String phone, String email, String categoria, String imageBg, String latitud, String longitud) {

        // Crear nueva publicación en /user-posts/$userid/$postid y en
        // /posts/$postid simultaneamente
        String key = dtbLocation.child("posts").push().getKey();

        Post post = new Post(userId, username, title, body, email, phone, categoria, imageBg, latitud, longitud);
        Map<String, Object> postValues = post.toMap();

        final Map<String, Object> childUpdates = new HashMap<>();

        /*dtbLocation.child("posts").child(mPostReference.getKey());
        dtbLocation.child("user-posts").child(post.getUid()).child(mPostReference.getKey());*/

        childUpdates.put("/posts/" + key + "/" , postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        dtbLocation.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Snackbar.make(coordinatorLayout, "Exitooo", Snackbar.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(coordinatorLayout, "Lo sentimos, intentelo de nuevo", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflar el menú; esto agrega elementos a la barra de acción si está presente.
        getMenuInflater().inflate(R.menu.menu_deletepost, menu);
        return true;
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
        } else if(id == R.id.fabDeletePost){
            Post p = new Post();
            p.setUid(getUid());

            dtbLocation.child("posts").child(mPostReference.getKey()).removeValue();
            dtbLocation.child("user-posts").child(p.getUid()).child(mPostReference.getKey()).removeValue();
            Toast.makeText(EditPostActivity.this, "Eliminado correctamente", Toast.LENGTH_LONG).show();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}


