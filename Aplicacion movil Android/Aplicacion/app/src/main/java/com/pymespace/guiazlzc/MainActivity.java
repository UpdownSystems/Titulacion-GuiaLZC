package com.pymespace.guiazlzc;
/*
 * Echo por: Updown Systems
 * Programado por: Jorge Luis Pérez Medina
 */
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "InicioDeSesionActivity";

    private Button btnInvitado, mSignInButton;
    private EditText mEmailField,mPasswordField;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener aStateListener;

    private DrawerLayout drawer;
    private ImageView img_page_start;
    private RelativeLayout relative_main;

    // Page Start
    private static boolean isShowPageStart = true;
    private final int MESSAGE_SHOW_DRAWER_LAYOUT = 0x001;
    private final int MESSAGE_SHOW_START_PAGE = 0x002;

    /*public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SHOW_DRAWER_LAYOUT:
                    drawer.openDrawer(GravityCompat.START);
                    SharedPreferences sharedPreferences = getSharedPreferences("app", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isFirst", false);
                    editor.apply();
                    break;

                case MESSAGE_SHOW_START_PAGE:
                    AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
                    alphaAnimation.setDuration(300);
                    alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            relative_main.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    relative_main.startAnimation(alphaAnimation);
                    break;
            }
        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        /*relative_main = (RelativeLayout)findViewById(R.id.relative_main);
        img_page_start = (ImageView)findViewById(R.id.img_page_start);*/

        // Vistas
        mEmailField = findViewById(R.id.email);
        mPasswordField = findViewById(R.id.password);
        mSignInButton = findViewById(R.id.sign_up_button);
        btnInvitado = findViewById(R.id.btn_link_invitado);

        mSignInButton.setOnClickListener(this);
        btnInvitado.setOnClickListener(this);


        /*SharedPreferences sharedPreferences = getSharedPreferences("app", MODE_PRIVATE);

        if (isShowPageStart) {
            relative_main.setVisibility(View.VISIBLE);
            Glide.with(MainActivity.this).load(R.drawable.ic_location_on_pink_24dp).into(img_page_start);
            if (sharedPreferences.getBoolean("isFirst", true)) {
                mHandler.sendEmptyMessageDelayed(MESSAGE_SHOW_START_PAGE, 2000);
            } else {
                mHandler.sendEmptyMessageDelayed(MESSAGE_SHOW_START_PAGE, 1000);
            }
            isShowPageStart = false;
        }

        if (sharedPreferences.getBoolean("isFirst", true)) {
            mHandler.sendEmptyMessageDelayed(MESSAGE_SHOW_DRAWER_LAYOUT, 2500);
        }*/
    }

    @Override
    public void onStart() {
        super.onStart();
        // Comprobar autenticación al inicio de la actividad
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }

    private void signIn() {
        Log.d(TAG, "signIn");
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(MainActivity.this, "Falló al iniciar sesion", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signInInvitado() {
        Log.d(TAG, "signInInvitado");

        showProgressDialog();

        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    // Inicia sesión correctamente, actualiza la interfaz de usuario con la información del usuario que inició sesión
                    Log.d(TAG, "signInAnonymously:success");
                    onAuthSuccess(task.getResult().getUser());
                } else {
                    Toast.makeText(MainActivity.this, "Fallo al iniciar sesion como invitado", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void onAuthSuccess(FirebaseUser user) {
        startActivity(new Intent(MainActivity.this, navigationDrawer.class));
        finish();
    }

    private boolean validateForm() {
        boolean result = true;
        final TextInputLayout passwordTextInput = findViewById(R.id.passTextInputLayout);
        final TextInputEditText passwordEditText = findViewById(R.id.password);
        final TextInputLayout emailTextInput = findViewById(R.id.emailTextInputLayout);

        // Expresion regular para verificar el Email
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        // Verifica que el Email sea válido
        if(TextUtils.isEmpty(mEmailField.getText().toString())) {
            emailTextInput.setError(getString(R.string.error_email));
            result = false;
        } else {
            emailTextInput.setError(null);
        }

        // Verifica que la Contraseña sea válido
        if(!isPasswordValid(passwordEditText.getText())){
            passwordTextInput.setError(getString(R.string.error_password));
            result = false;
        } else {
            passwordTextInput.setError(null); // Limpia el error
        }

        return result;
    }

    private boolean isPasswordValid(@Nullable Editable text) {
        return text != null && text.length() >= 8;
    }

    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sign_up_button) {
            signIn();
        } else if (i == R.id.btn_link_invitado) {
            signInInvitado();
            //startActivity(new Intent(MainActivity.this, registroUsuario.class));
        }
    }
}




