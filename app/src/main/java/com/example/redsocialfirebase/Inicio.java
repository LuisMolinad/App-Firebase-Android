package com.example.redsocialfirebase;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Inicio extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    Button CerrarSesion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        //Action bar asignando un titulo y habilitando el boton de retroceso
        ActionBar actionBar = getSupportActionBar();
        assert actionBar !=null; //afirmamos que el titulo no es nulo
        actionBar.setTitle("Inicio");


        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        CerrarSesion = findViewById(R.id.CerrarSesion);

        CerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creamos un evento para cerrar sesion
                CerrarSesion();
            }
        });


    }
    //llamamos Onstart
    //Este metodo se ejecuta antes que todos
    @Override
    protected void onStart() {
        //Invocamos el metodo de verificacion de inicio de sesion
        VerificacionInicioSesion();
        super.onStart();
    }


    //metodo para verificar que el ususario ya ha iniciado session previamente

    private void VerificacionInicioSesion(){
        //si el usuario ha iniciado sesion nos dirige directamente a esta actividad Inicio
        if (firebaseUser != null){
            Toast.makeText(this,"Se ha iniciado sesión", Toast.LENGTH_SHORT).show();
        }
        //caso contrario nos dirige al main activity
        else{
            startActivity(new Intent( Inicio.this,MainActivity.class));
            finish();
        }
    }
    //METODO PARA CERRAR SESSION
    private void  CerrarSesion(){
        firebaseAuth.signOut();
        Toast.makeText(this,"Ha cerrado sesión", Toast.LENGTH_SHORT).show();
        //luego de cerrar sesion que nos diriga al main activity
        startActivity(new Intent(Inicio.this, MainActivity.class));
    }
    //desabilitando regresar a la activity anterior
   @Override
    public void onBackPressed() {
        // Simply Do noting!
       startActivity(new Intent(this, Inicio.class));
       finish();
    }
}