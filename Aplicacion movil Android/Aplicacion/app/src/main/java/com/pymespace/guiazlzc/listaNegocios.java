package com.pymespace.guiazlzc;
/*
 * Echo por: Updown Systems
 * Programado por: Jorge Luis Pérez Medina
 */
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;

import android.view.View;
import android.widget.ImageButton;

public class listaNegocios extends AppCompatActivity {

    private CardView seleccionNegocio;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_negocios);

        seleccionNegocio = (CardView)findViewById(R.id.selectNegocio);
        btnBack = (ImageButton)findViewById(R.id.btnBack);

        // Regresa a la actividad anterior
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(listaNegocios.this, navigationDrawer.class));
            }
        });

        /*seleccionNegocio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(listaNegocios.this, selectNegocio.class));
            }
        });*/

    }
}


