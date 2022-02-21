package com.example.redsocialfirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Login extends AppCompatActivity {

    EditText CorreoLogin, PasswordLogin;
    Button INGRESAR, INGRESARCONGOOGLE;

    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 12 ;

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

        //INICIO DE GOOGLE
        INGRESARCONGOOGLE = findViewById(R.id.INGRESARCONGOOGLE);


        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(Login.this);//Inicializamos el progressdialog
        dialog = new Dialog(Login.this);//Iniciamos el dialog

        //Creamos la solicitud
        crearSolicitud();


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
            //Evento al presionar boton de iniciar sesion con google
        INGRESARCONGOOGLE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });


    }
    // 1 metodo para crear solicitud
    private void crearSolicitud() {
        //Configuracion de inicio de sesion de google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        //creamos un google sign in client
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso );

    }

    // 2 crear pantalla de google
    private void  signIn(){
        Intent signIntent= mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //resultado devuelto al iniciar la intent con googlesignIn
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                //inicio de sesion fue exitoso autentique con firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                //metodo para loguear con google
                AutenticacionFirebase(account);

            }catch (ApiException e){
                Toast.makeText(this, e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void AutenticacionFirebase(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            //si inicio correctamente
                            //se obtiene el usuario actual que quiere iniciar sesion
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            //si el usuario inicio sesion por primera vez
                            if (task.getResult().getAdditionalUserInfo().isNewUser()){

                                String uid = user.getUid();
                                String correo = user.getEmail();
                                String nombre  = user.getDisplayName();

                                //aca pasamops los parametros

                                //crear un hasmap para enviar los datos a firebase
                                HashMap<Object, String> DatosUsuario = new HashMap<>();

                                DatosUsuario.put("uid",uid);
                                DatosUsuario.put("correo", correo);
                                //DatosUsuario.put("pass", pass);
                                DatosUsuario.put("nombres",nombre);
                                //DatosUsuario.put("apellidos", apellidos);
                                DatosUsuario.put("edad", "");
                                DatosUsuario.put("telefono","");
                                DatosUsuario.put("direccion", "");
                                //imagen vacia de momento
                                DatosUsuario.put("imagen","");

                                //Inicializamos la instancia a la base de datos de firebase
                                FirebaseDatabase database = FirebaseDatabase.getInstance();

                                //se crea la base de datos
                                DatabaseReference reference = database.getReference("USUARIOS_DE_APP");
                                //El nombre de la BD "No relacional es USUARIOS_DE_APP"
                                reference.child(uid).setValue(DatosUsuario);

                            }
                            //nos dirige a la actividad
                            startActivity(new Intent(Login.this, Inicio.class));
                        }
                        else{
                            Dialog_No_Inicio();
                        }
                    }
                });
    }


    //metodo para loguear un usuario con correo y contrasena
    private void LOGINUSUARIO(String correo, String pass) {
        progressDialog.setTitle("Ingresando");
        progressDialog.setMessage("Espere por favor...");
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