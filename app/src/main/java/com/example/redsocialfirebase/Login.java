package com.example.redsocialfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
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

public class Login extends AppCompatActivity {

    EditText CorreoLogin, PasswordLogin;
    Button INGRESAR;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Action bar asignando un titulo y habilitando el boton de retroceso
        ActionBar actionBar = getSupportActionBar();
        assert actionBar !=null; //afirmamos que el titulo no es nulo
        actionBar.setTitle("Login");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //capptura
        CorreoLogin=findViewById(R.id.CorreoLogin);
        PasswordLogin=findViewById(R.id.PasswordLogin);
        INGRESAR=findViewById(R.id.INGRESAR);

        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(Login.this);//Inicializamos el progressdialog
        dialog = new Dialog(Login.this);//Iniciamos el dialog

        /* Se le assigna un evento al boton ingresar*/

        INGRESAR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Convertimos en string el correo y contrasena

                String correo=CorreoLogin.getText().toString();
                String pass = PasswordLogin.getText().toString();

                if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
                    CorreoLogin.setError("Correo Invalido");
                    CorreoLogin.setFocusable(true);

                }
                else if (pass.length()<6){
                    PasswordLogin.setError("La contraseña debe ser mayor o igual a 6 carácteres");
                    PasswordLogin.setFocusable(true);
                }
                else{
                    LOGINUSUARIO(correo,pass);
                }


            }
        });


    }
    //metodo para loguear un usuario con correo y contrasena
    private void LOGINUSUARIO(String correo, String pass) {
        progressDialog.setCancelable(false);
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(correo,pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //SI SE INICIA SESION CORRECTAMENTE
                        if (task.isSuccessful()){
                            progressDialog.dismiss();//progress se cierra
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            //Cuando iniciemos sesion que nos envie al activity inicio
                            startActivity(new Intent(Login.this, Inicio.class));
                            assert user != null; //Se necesita afiramar que el usuario no es vacio
                            Toast.makeText(Login.this, "Bienvenido(a), "+user.getEmail() +"!",Toast.LENGTH_LONG).show();
                            finish();


                        }else{
                            progressDialog.dismiss();
                            //Remplazado queda el toast por el nuevo dialog
                            Dialog_No_Inicio();
                            //Toast.makeText(Login.this, "ALGO HA SALIDO MAL",Toast.LENGTH_LONG).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                //Nos muestra el error
                Toast.makeText(Login.this, e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });


    }


    //metodo para crear el dialog personalizado
    private void Dialog_No_Inicio(){
        Button ok_no_inicio;

        dialog.setContentView(R.layout.no_session);//se relaciona con la vista creada

        ok_no_inicio = dialog.findViewById(R.id.ok_no_inicia);
        //al presionar en ok se cerrar[a el cuadro de dialogo
        ok_no_inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //al dar click se cierra
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false);//al presionar fuera de laanimacion esta seguira mostrandose
        dialog.show();
    }


    //se habilita la opcion de regresar a la activity anterior
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }




}