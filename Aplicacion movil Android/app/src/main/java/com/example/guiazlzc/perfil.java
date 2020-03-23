package com.example.guiazlzc;
/*
 * Echo por: Updown Systems
 * Programado por: Jorge Luis Pérez Medina
 */
import android.content.Intent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class perfil extends AppCompatActivity {
    private TextView inCategoriaC, inCategoria, inNameNegocioC, inNameNegocio, inEmail, inNameJefe, inCiudad, inTelefono, inDireccion, inLocalidad;
    private FloatingActionButton btnEditarPerfil;

    String userId;
    private FragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference db;
    private FirebaseDatabase ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // Vista XML
        inEmail = (TextView)findViewById(R.id.setEmailU);
        inCategoriaC = (TextView)findViewById(R.id.txtCategoria);
        inNameNegocioC = (TextView)findViewById(R.id.txtNameNegocio);
        inTelefono = (TextView)findViewById(R.id.setTelefono);
        Toolbar toolbar = findViewById(R.id.toolbar_misc);
        inCategoria = (TextView)findViewById(R.id.setNameCategoria);
        inLocalidad = (TextView)findViewById(R.id.setLocalidad);
        inNameNegocio = (TextView)findViewById(R.id.setNameNegocio);
        inNameJefe = (TextView)findViewById(R.id.setNameJefe);
        inCiudad = (TextView)findViewById(R.id.txtCiudad);
        inDireccion = (TextView)findViewById(R.id.txtDireccion);
        btnEditarPerfil = (FloatingActionButton)findViewById(R.id.floatingEditarPerfil);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance();

        // Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnEditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(perfil.this, editPerfil.class));
            }
        });

        db = ref.getReference("usuarios").child(user.getUid());
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User usuarios = dataSnapshot.getValue(User.class);

                inNameNegocioC.setText(usuarios.nombreNegocio);
                inCategoriaC.setText(usuarios.categoria);
                inNameNegocio.setText(usuarios.nombreNegocio);
                inCategoria.setText(usuarios.categoria);
                inNameJefe.setText(usuarios.nombreJefe);
                inEmail.setText(usuarios.email);
                inCiudad.setText(usuarios.ciudad);
                inTelefono.setText(usuarios.telefono);
                inLocalidad.setText(usuarios.localidad);
                inDireccion.setText(usuarios.direccion);

                Log.d("perfil", "nombre:" + usuarios.nombreNegocio);
                Log.d("perfil", "apaterno:" + usuarios.categoria);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Error", "No se puedo obtener los datos de la BD de Firebase :(");
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflar el menú; esto agrega elementos a la barra de acción si está presente.
        getMenuInflater().inflate(R.menu.menu_perfil, menu);
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
        } else if (id == R.id.fabResetPassword) {
            startActivity(new Intent(perfil.this, ResetPasswordActivity.class));
            return true;
        } else if (id == R.id.fabExitApp) {
            mAuth.getInstance().signOut();
            startActivity(new Intent(perfil.this, MainActivity.class));
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
