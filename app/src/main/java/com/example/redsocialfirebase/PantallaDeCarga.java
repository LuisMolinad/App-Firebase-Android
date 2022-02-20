package com.example.redsocialfirebase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class PantallaDeCarga extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_de_carga);
        //tiempo en segundo que demora la pantalla de carga
        final int Duracion  = 2500;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Esto se ejecutara pasa los segundos establecidos
                //se cambio del "mainactivity" a la actividad de "Inicio"
               // Intent intent = new Intent(PantallaDeCarga.this, MainActivity.class);
                 Intent intent = new Intent(PantallaDeCarga.this, Inicio.class);
                startActivity(intent);
                //Nos dirige de esta activity al MainActivity
            }
        },Duracion);
    }


}