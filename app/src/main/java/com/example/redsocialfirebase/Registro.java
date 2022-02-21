package com.example.redsocialfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Registro extends AppCompatActivity {
    EditText Correo, Password, Nombre, Apellido, Edad, Telefono, Direccion;
    Button REGISTRARUSUARIO;

    FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        //Action bar asignando un titulo y habilitando el boton de retroceso
        ActionBar actionBar = getSupportActionBar();
        assert actionBar !=null; //afirmamos que el titulo no es nulo
        actionBar.setTitle("Registro");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        Correo= findViewById(R.id.Correo);
        Nombre=findViewById(R.id.Nombre);
        Apellido=findViewById(R.id.Apellidos);
        Edad=findViewById(R.id.Edad);
        Telefono=findViewById(R.id.Telefono);
        Direccion=findViewById(R.id.Direccion);
        Password=findViewById(R.id.Password);
        REGISTRARUSUARIO=findViewById(R.id.REGISTRARUSUARIO);

        //instancia FIREBASE
        firebaseAuth=FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(Registro.this);//Inicializamos el progressdialog

        REGISTRARUSUARIO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String correo=Correo.getText().toString();
                String pass=Password.getText().toString();

                //validando
                if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
                    Correo.setError("Correo no Valido");
                    Correo.setFocusable(true);
                }else if (pass.length()<6){
                    Password.setError("Contraseña  debe ser mayor a 6 carácteres");
                    Password.setFocusable(true);
                }else {
                    REGISTRAR(correo,pass);
                }
            }
        });


    }
//METODO PARA REGISTRAR UN USUARIO
    private void REGISTRAR(String correo, String pass) {
        progressDialog.setTitle("Registrando");
        progressDialog.setMessage("Espere por favor...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(correo, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //si el registro es exitoso
                        if (task.isSuccessful()){
                            progressDialog.dismiss();//progress se cierra
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            //Datos que deseamos registrar
                            //Tomar en cuenta que los string deben diferir de los EditText "correo" es distinto de "Correo"

                            //PARA OBTENER EL UID

                            assert user != null;//Confirmamos usuario distinto de Nulo
                            String uid = user.getUid();

                            String correo = Correo.getText().toString();
                            String pass = Password.getText().toString();
                            String nombre= Nombre.getText().toString();
                            String apellidos = Apellido.getText().toString();
                            String edad= Edad.getText().toString();
                            String telefono = Telefono.getText().toString();
                            String direccion = Direccion.getText().toString();

                            //crear un hasmap para enviar los datos a firebase
                            HashMap<Object, String>DatosUsuario = new HashMap<>();

                            DatosUsuario.put("uid",uid);
                            DatosUsuario.put("correo", correo);
                            DatosUsuario.put("pass", pass);
                            DatosUsuario.put("nombres",nombre);
                            DatosUsuario.put("apellidos", apellidos);
                            DatosUsuario.put("edad", edad);
                            DatosUsuario.put("telefono",telefono);
                            DatosUsuario.put("direccion", direccion);
                            //imagen vacia de momento
                            DatosUsuario.put("imagen","");

                            //Inicializamos la instancia a la base de datos de firebase
                            FirebaseDatabase database = FirebaseDatabase.getInstance();

                            //se crea la base de datos
                            DatabaseReference reference = database.getReference("USUARIOS_DE_APP");
                            //El nombre de la BD "No relacional es USUARIOS_DE_APP"
                            reference.child(uid).setValue(DatosUsuario);
                            Toast.makeText(Registro.this,"Se registro exitosamente",Toast.LENGTH_LONG).show();
                            //Una vez se ha registrado nos envia al apartado de inicio
                            startActivity(new Intent(Registro.this, Inicio.class));
                        }else {
                            progressDialog.dismiss();//progress se cierra
                            Toast.makeText(Registro.this, "Algo ha salido mal", Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();//progress se cierra ojito cuidao
                Toast.makeText(Registro.this,e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    //se habilita la opcion de regresar a la activity anterior
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}