package com.pymespace.guiazlzc;
/*
 * Echo por: Updown Systems
 * Programado por: Jorge Luis Pérez Medina
 */
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pymespace.guiazlzc.models.Maps;
import com.pymespace.guiazlzc.models.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pymespace.guiazlzc.service.MyDownloadService;
import com.pymespace.guiazlzc.service.MyUploadService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EditPostActivity extends BaseActivity {
    private static final String TAG = "NewPostActivity";
    private static final String REQUIRED = "Requerido";
    public static final String EXTRA_POST_KEY = "post_key";

    //Mapas
    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS;
    private FusedLocationProviderClient fusedLocationClient;

    // Firebase Storage
    private static final int RC_TAKE_PICTURE = 101;
    private static final String KEY_FILE_URI = "key_file_uri";
    private static final String KEY_DOWNLOAD_URL = "key_download_url";

    private Uri mDownloadUrl = null;
    private Uri mFileUri = null;
    private BroadcastReceiver mBroadcastReceiver;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;

    private EditText mTitleField, mBodyField, mPhoneField, mEmailField, mLatitudField, mLongitudField,mTitleFoto;
    private Toolbar toolbar;
    private AutoCompleteTextView mCategoriaField;
    private FloatingActionButton mSubmitButton;
    private DatabaseReference dtbLocation, dbLocation, dtbLocation2;
    private FirebaseDatabase ref, databaseReference;

    private ValueEventListener mPostListener;
    private String mPostKey;
    private DatabaseReference mPostReference;
    private Object Post;

    private CoordinatorLayout coordinatorLayout;
    private TextInputEditText txtImageBg;
    private String id;

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
        mTitleFoto = findViewById(R.id.pictureDownloadUri);
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


        // Mapas
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        obtenerDatosMap();

        // Receptor de difusión local
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive:" + intent);
                hideProgressDialog();

                switch (intent.getAction()) {
                    case MyDownloadService.DOWNLOAD_COMPLETED:
                        // Obtener el número de bytes descargados
                        long numBytes = intent.getLongExtra(MyDownloadService.EXTRA_BYTES_DOWNLOADED, 0);

                        // Alert success
                        showMessageDialog(getString(R.string.success), String.format(Locale.getDefault(),
                                "%d bytes downloaded from %s",
                                numBytes,
                                intent.getStringExtra(MyDownloadService.EXTRA_DOWNLOAD_PATH)));
                        break;
                    case MyDownloadService.DOWNLOAD_ERROR:
                        // Alert failure
                        showMessageDialog("Error", String.format(Locale.getDefault(),
                                "Failed to download from %s",
                                intent.getStringExtra(MyDownloadService.EXTRA_DOWNLOAD_PATH)));
                        break;
                    case MyUploadService.UPLOAD_COMPLETED:
                    case MyUploadService.UPLOAD_ERROR:
                        onUploadResultIntent(intent);
                        break;
                }

                // Obtener la clave de la publicación del Intent
                mPostKey = getIntent().getStringExtra(EXTRA_POST_KEY);
            }
        };


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dtbLocation = FirebaseDatabase.getInstance().getReference();

        // Obtiene los datos de la base de datos del AutoComplete para seleccionar una Categoria
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

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("photos");
        String url = "https://firebasestorage.googleapis.com/v0/b/otros-7b06b.appspot.com/o/photos%2Fimage%3A12209?alt=media&token=65ea422f-2cb4-49cb-902c-b3a4608e8a65";
        // ImageView in your Activity
        ImageView imageView = findViewById(R.id.imageViewGlide);

        // Download directly from StorageReference using Glide
        // (See MyAppGlideModule for Loader registration)
        Glide.with(getApplicationContext())
                .load(url)
                .into(imageView);
    }

    public void esperarYCerrar(int milisegundos) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // acciones que se ejecutan tras los milisegundos
                onBackPressed();
            }
        }, milisegundos);
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
                    id=post.id;
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
                            updatePost(userId, user.nombreJefe, title, body, phone, email, categoria, imageBg, latitud, longitud,id);
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

    private void obtenerDatosMap() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }


        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    mLatitudField.setText(String.valueOf(location.getLatitude()));
                    mLongitudField.setText(String.valueOf(location.getLongitude()));
                    // Logic to handle location object
                    Log.e("Longitud: ", +location.getLongitude() + "Latitud: " + location.getLatitude());


                    //obtenerLongitud.setText(Double.toString(location.getLongitude()));
                    //obtenerLatitud.setText(Double.toString(location.getLatitude()));

                    Map<String, Object> latLang = new HashMap<>();
                    latLang.put("latitud", location.getLatitude());
                    latLang.put("longitud", location.getLongitude());

                }
            }
        });
    }



    public void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        out.putParcelable(KEY_FILE_URI, mFileUri);
        out.putParcelable(KEY_DOWNLOAD_URL, mDownloadUrl);
    }

    private void updatePost(String userId, String username, String title, String body, String phone, String email, String categoria, String imageBg, String latitud, String longitud,String id) {

        // Crear nueva publicación en /user-posts/$userid/$postid y en
        // /posts/$postid simultaneamente
        //String key = dtbLocation.child("posts").push().getKey();
        Map<String, Object> updates = new HashMap<String,Object>();

        dbLocation = ref.getReference("user-posts").child(userId).child(id);
        updates.clear();
        updates.put("author", username);
        updates.put("body", body);
        updates.put("categoria", categoria);
        updates.put("email", email);
        updates.put("imageBg", imageBg);
        updates.put("latitud", latitud);
        updates.put("longitud", longitud);
        updates.put("phone", phone);
        updates.put("starCount", 0);
        updates.put("title", title);
        updates.put("uid", userId);
        updates.put("id", id);

        dbLocation.setValue(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        dbLocation = ref.getReference("posts").child(id);

        updates.put("author", username);
        updates.put("body", body);
        updates.put("categoria", categoria);
        updates.put("email", email);
        updates.put("imageBg", imageBg);
        updates.put("latitud", latitud);
        updates.put("longitud", longitud);
        updates.put("phone", phone);
        updates.put("starCount", 0);
        updates.put("title", title);
        updates.put("uid", userId);
        updates.put("id", id);


        dbLocation.setValue(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Snackbar.make(coordinatorLayout, "Editado correctamente", Snackbar.LENGTH_LONG).show();
                esperarYCerrar(1100);
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

    private void showMessageDialog(String title, String message) {
        AlertDialog ad = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .create();
        ad.show();
    }

    private void onUploadResultIntent(Intent intent) {
        // Obtuve una nueva intención de MyUploadService con éxito o fracaso
        mDownloadUrl = intent.getParcelableExtra(MyUploadService.EXTRA_DOWNLOAD_URL);
        mFileUri = intent.getParcelableExtra(MyUploadService.EXTRA_FILE_URI);

        updateUI(mAuth.getCurrentUser());
    }

    private void updateUI(FirebaseUser user) {
        // Signed in or Signed out
        /*if (user != null) {
            findViewById(R.id.layoutStorage).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.layoutStorage).setVisibility(View.GONE);
        }*/

        // Download URL and Download button
        if (mDownloadUrl != null) {
            ((TextInputEditText)findViewById(R.id.pictureDownloadUri)).setText(mDownloadUrl.toString());

            /*((TextView) findViewById(R.id.pictureDownloadUri)).setText(mDownloadUrl.toString());
            findViewById(R.id.layoutDownload).setVisibility(View.VISIBLE);*/
        } else {
            ((TextInputEditText)findViewById(R.id.pictureDownloadUri)).setInputType(InputType.TYPE_NULL);
            /*((TextView) findViewById(R.id.pictureDownloadUri)).setText(null);
            findViewById(R.id.layoutDownload).setVisibility(View.GONE);*/
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);
        if (requestCode == RC_TAKE_PICTURE) {
            if (resultCode == RESULT_OK) {
                mFileUri = data.getData();

                if (mFileUri != null) {
                    uploadFromUri(mFileUri);
                } else {
                    Log.w(TAG, "File URI is null");
                }
            } else {
                Toast.makeText(this, "No seleccionó ninguna foto", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void uploadFromUri(Uri fileUri) {
        Log.d(TAG, "uploadFromUri:src:" + fileUri.toString());

        // Save the File URI
        mFileUri = fileUri;

        // Clear the last download, if any
        updateUI(mAuth.getCurrentUser());
        mDownloadUrl = null;

        // Start MyUploadService to upload the file, so that the file is uploaded
        // even if this Activity is killed or put in the background
        startService(new Intent(this, MyUploadService.class)
                .putExtra(MyUploadService.EXTRA_FILE_URI, fileUri)
                .setAction(MyUploadService.ACTION_UPLOAD));

        // Show loading spinner
        showProgressDialog(getString(R.string.progress_uploading));
    }

    private void showProgressDialog(String caption) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.setMessage(caption);
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

}


