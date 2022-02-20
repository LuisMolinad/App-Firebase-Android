package com.example.redsocialfirebase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button LoginBTN,RegistrarBTN;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoginBTN=findViewById(R.id.LoginBTN);
        RegistrarBTN=findViewById(R.id.RegistrarBTN);

        LoginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Envia el control a la clase Login, para hacerlo necesita el contexto y la clase destino
                startActivity(new Intent( MainActivity.this, Login.class));
            }
        });

        RegistrarBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Envia el control a la clase registro, para hacerlo necesita el contexto y la clase destino
                startActivity(new Intent( MainActivity.this, Registro.class));
            }
        });

    }
}