package com.pymespace.guiazlzc;
/*
 * Echo por: Updown Systems
 * Programado por: Jorge Luis Pérez Medina
 */

import androidx.annotation.NonNull;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ResetPasswordActivity extends BaseActivity {
    private MaterialButton btnRestablecerContra;
    private EditText inputRestablecerContra;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        Toolbar toolbar = findViewById(R.id.toolbar_perfil);
        inputRestablecerContra = (EditText)findViewById(R.id.email);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        btnRestablecerContra = (MaterialButton)findViewById(R.id.btn_reset_password);

        final TextInputLayout emailTextInput = findViewById(R.id.emailTexInput);

        // Firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();

        // Acciones del Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Programacion del boton ResetPassword
        btnRestablecerContra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String txtemail = inputRestablecerContra.getText().toString().trim();


                // Expresion regular para verificar el Email
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if(!txtemail.matches(emailPattern) && inputRestablecerContra != null) {
                    emailTextInput.setError(getString(R.string.error_email));
                    return;
                } else {
                    emailTextInput.setError(null);
                }

                showProgressDialog();

                mAuth.sendPasswordResetEmail(txtemail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Snackbar.make(v, "Verifique su bandeja de entrada!", Snackbar.LENGTH_LONG).show();
                            inputRestablecerContra.setText("");
                        } else {
                            Snackbar.make(v, "Intentelo de nuevo!", Snackbar.LENGTH_LONG).show();
                        }
                        hideProgressDialog();
                    }
                });
            }
        });
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
