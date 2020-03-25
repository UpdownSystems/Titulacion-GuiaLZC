package com.pymespace.guiazlzc;
/*
 * Echo por: Updown Systems
 * Programado por: Jorge Luis Pérez Medina
 */
import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.pymespace.guiazlzc.models.Maps;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pymespace.guiazlzc.models.Post;
import com.pymespace.guiazlzc.service.MyDownloadService;
import com.pymespace.guiazlzc.service.MyUploadService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class NewPostActivity extends BaseActivity implements View.OnClickListener {

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

    private DatabaseReference mDatabase, databaseReference;
    private EditText mTitleField, mBodyField, mPhoneField, mEmailField, mTitleFoto; //mLongitudField,//mLatitudField;
    private AutoCompleteTextView mCategoriaField;
    private FloatingActionButton mSubmitButton;
    private DatabaseReference dtbLocation;
    private CoordinatorLayout coordinatorLayout;
    private TextInputEditText txtImageBg;
    private String mPostKey;
    private TextView obtenerLatitud, obtenerLongitud,mLatitud,mLongitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mTitleField = findViewById(R.id.fieldTitle);
        mBodyField = findViewById(R.id.fieldBody);
        mPhoneField = findViewById(R.id.fieldPhone);
        mEmailField = findViewById(R.id.fieldEmail);
        mTitleFoto = findViewById(R.id.pictureDownloadUri);
        //mLatitudField = findViewById(R.id.fieldLatitud);
        //mLongitudField = findViewById(R.id.fieldLongitud);
        mCategoriaField = findViewById(R.id.selectCategoria);
        mSubmitButton = findViewById(R.id.fabSubmitPost);
        Toolbar toolbar = findViewById(R.id.toolbar_perfil);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        txtImageBg = (TextInputEditText)findViewById(R.id.pictureDownloadUri);
        mLatitud = (TextView)findViewById(R.id.fieldLatitud);
        mLongitud = (TextView)findViewById(R.id.fieldLongitud);


        obtenerLatitud = (TextView) findViewById(R.id.txtLatitud);
        obtenerLongitud = (TextView) findViewById(R.id.txtLongitud);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });

        findViewById(R.id.pictureDownloadUri).setOnClickListener(this);

        if (savedInstanceState != null) {
            mFileUri = savedInstanceState.getParcelable(KEY_FILE_URI);
            mDownloadUrl = savedInstanceState.getParcelable(KEY_DOWNLOAD_URL);
        }
        onNewIntent(getIntent());

        // Mapas
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();
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
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(NewPostActivity.this, android.R.layout.simple_spinner_item, nombreC);
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

    // Storage [INICIO]
    public void loadWithGlide() {
        // [START storage_load_with_glide]
        // Reference to an image file in Cloud Storage
        // Inicializamos la Base de datos
        dtbLocation = FirebaseDatabase.getInstance().getReference().child("posts").child(mPostKey);
        dtbLocation = FirebaseDatabase.getInstance().getReference().child("posts").child(dtbLocation.getKey()).child("imageBg");
        //dtbLocation.child("posts").child(mPostReference.getKey()).removeValue();
        //dtbLocation.child("user-posts").child(p.getUid()).child(mPostReference.getKey()).removeValue();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("photos");

        // ImageView in your Activity
        ImageView imageView = findViewById(R.id.imageViewGlide);

        // Download directly from StorageReference using Glide
        // (See MyAppGlideModule for Loader registration)
        Glide.with(this /* context */)
                .load(storageReference)
                .into(imageView);
        // [END storage_load_with_glide]
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // Compruebe si esta actividad se inició haciendo clic en una notificación de carga
        if (intent.hasExtra(MyUploadService.EXTRA_DOWNLOAD_URL)) {
            onUploadResultIntent(intent);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        updateUI(mAuth.getCurrentUser());

        // Registrar receptor para cargas y descargas
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.registerReceiver(mBroadcastReceiver, MyDownloadService.getIntentFilter());
        manager.registerReceiver(mBroadcastReceiver, MyUploadService.getIntentFilter());

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Maps maps = dataSnapshot.getValue(Maps.class);

                //mLatitudField.setText(maps.getLongitud());



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Error", "No se puedo obtener los datos de la BD de Firebase :(");
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();

        // Anular el registro del receptor de descarga
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        out.putParcelable(KEY_FILE_URI, mFileUri);
        out.putParcelable(KEY_DOWNLOAD_URL, mDownloadUrl);
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

    // Metodo para obtener Las coordenadas
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
                    mLatitud.setText(String.valueOf(location.getLatitude()));
                    mLongitud.setText(String.valueOf(location.getLongitude()));
                    // Logic to handle location object
                    Log.e("Longitud: ", +location.getLongitude() + "Latitud: " + location.getLatitude());


                    //obtenerLongitud.setText(Double.toString(location.getLongitude()));
                    //obtenerLatitud.setText(Double.toString(location.getLatitude()));

                    Map<String, Object> latLang = new HashMap<>();
                    latLang.put("latitud", location.getLatitude());
                    latLang.put("longitud", location.getLongitude());
                    databaseReference.child("Mapas").push().setValue(latLang);

                }
            }
        });
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

    private void beginDownload() {
        // Get path
        String path = "photos/" + mFileUri.getLastPathSegment();

        // Kick off MyDownloadService to download the file
        Intent intent = new Intent(this, MyDownloadService.class)
                .putExtra(MyDownloadService.EXTRA_DOWNLOAD_PATH, path)
                .setAction(MyDownloadService.ACTION_DOWNLOAD);
        startService(intent);

        // Show loading spinner
        showProgressDialog(getString(R.string.progress_downloading));
    }

    private void launchCamera() {
        Log.d(TAG, "launchCamera");

        // Seleccionar una foto de la galeria
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(intent, RC_TAKE_PICTURE);
    }

    private void onUploadResultIntent(Intent intent) {
        // Obtuve una nueva intención de MyUploadService con éxito o fracaso
        mDownloadUrl = intent.getParcelableExtra(MyUploadService.EXTRA_DOWNLOAD_URL);
        mFileUri = intent.getParcelableExtra(MyUploadService.EXTRA_FILE_URI);

        updateUI(mAuth.getCurrentUser());
    }

    // Metodo para mostrar y ocultar Botones
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

    private void showMessageDialog(String title, String message) {
        AlertDialog ad = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .create();
        ad.show();
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

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }*/

   /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            updateUI(null);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }*/

    /*@Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.buttonCamera) {
            launchCamera();
        } /*else if (i == R.id.buttonSignIn) {
            signInAnonymously();
        } else if (i == R.id.buttonDownload) {
            beginDownload();
        }
    }    */
    // Storage [FINAL]

    private boolean validateForm() {
        final TextInputLayout titleTextInput = findViewById(R.id.fieldTitleInput);
        final TextInputLayout photoTextInput = findViewById(R.id.fieldTitleInputFoto);
        //final TextInputLayout latitudTextInput = findViewById(R.id.fieldLatitudInput);
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

        /*if(TextUtils.isEmpty(mLatitudField.getText())){
            latitudTextInput.setError("Es necesario la ubicación");
        }*/

        if(TextUtils.isEmpty(mTitleFoto.getText())){
            photoTextInput.setError("Es necesario seleccionar una foto de portada");
        } else {
            phoneTextInput.setError(null);
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
    }

    private boolean validarEmail(String email) {
        Pattern patterns = Patterns.EMAIL_ADDRESS;
        return patterns.matcher(email).matches();
    }

    private void submitPost() {
        final String title = mTitleField.getText().toString();
        final String body = mBodyField.getText().toString();
        final String phone = mPhoneField.getText().toString();
        final String email = mEmailField.getText().toString();
        final String categoria = mCategoriaField.getText().toString();
        final String imageBg = txtImageBg.getText().toString();
        final String latitud = mLatitud.getText().toString();
        final String longitud = mLongitud.getText().toString();


        if (!validateForm()) {
            return;
        }

        // Deshabilitar botón para que no haya publicaciones múltiples
        setEditingEnabled(false);

        // [COMIENZO single_value_read]
        final String userId = getUid();
        //final String categoriaId = FirebaseDatabase.getInstance().getReference().child("categorias").push().getKey();
        mDatabase.child("usuarios").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Obtener valor de usuario
                        User user = dataSnapshot.getValue(User.class);

                        if (user == null) {
                            // El usuario es nulo, error fuera
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(NewPostActivity.this,
                                    "Error: no se pudo recuperar el usuario.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Escribir nueva publicación
                            writeNewPost(userId, user.nombreJefe, title, body, phone, email, categoria, imageBg, latitud, longitud);
                            mTitleField.setText("");
                            mBodyField.setText("");
                            mPhoneField.setText("");
                            mEmailField.setText("");
                            mCategoriaField.setText("");
                            txtImageBg.setText("");
                            /*mLatitudField.setText("");
                            mLongitudField.setText("");*/
                        }

                        // Termina esta actividad, regresa a la actividad anterior
                        setEditingEnabled(true);
                        //finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        setEditingEnabled(true);
                    }
                });
        // [FIN single_value_read]
    }

    private void setEditingEnabled(boolean enabled) {
        mTitleField.setEnabled(enabled);
        mBodyField.setEnabled(enabled);
        mPhoneField.setEnabled(enabled);
        mCategoriaField.setEnabled(enabled);
        mEmailField.setEnabled(enabled);
        if (enabled) {
            mSubmitButton.show();
        } else {
            mSubmitButton.hide();
        }
    }


    private void writeNewPost(String userId, String username, String title, String body, String phone, String email, String categoria, String imageBg, String latitud, String longitud) {
        // Crear nueva publicación en /user-posts/$userid/$postid y en
        // /posts/$postid simultaneamente
        String key = mDatabase.child("posts").push().getKey();
        Log.e("Nueva llave generada: ", key);
        //String KeyCat = mDatabase.child("categorias").child("name").getKey();
        Post post = new Post(userId, username, title, body, email, phone, categoria, imageBg, latitud, longitud);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key + "/" , postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        // AddOnSuccessListener, es un objeto de escucha para saber si los datos se agregaron correctamente
        // a la Base de datos en caso de que haya ocurrido un error al insertar los datos en la BD el objeto addOnFailureListener
        // sirve para eso
        mDatabase.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Snackbar.make(coordinatorLayout, "Su negocio fue creado correctamente", Snackbar.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(coordinatorLayout, "Lo sentimos, intentelo de nuevo", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    // Opciones del Menu de navegacion
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


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.pictureDownloadUri) {
            launchCamera();
        }
        /*if (i == R.id.buttonCamera) {
            launchCamera();
        }*/
    }
}