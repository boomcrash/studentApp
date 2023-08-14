package com.example.studentapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.studentapp.activity.ListActivity;
import com.example.studentapp.activity.registro;


//import firestore
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtener referencia del botón "register"
        Button buttonRegister = findViewById(R.id.register);
        // Obtener referencia del botón "register"
        Button buttonLogin = findViewById(R.id.buttonLogin);

        EditText editTextUsername= findViewById(R.id.editTextUsername);
        EditText editTextPassword= findViewById(R.id.editTextPassword);


        // Configurar el evento de clic para el botón "register"
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambiar a la actividad "activity_registro"
                Intent intent = new Intent(MainActivity.this, registro.class);
                startActivity(intent);
                finish();
            }
        });
        // Configurar el evento de clic para el botón "register"
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambiar a la actividad "activity_login"
                //Toast.makeText(MainActivity.this, editTextUsername.getText().toString().trim()+" "+editTextPassword.getText().toString().trim(), Toast.LENGTH_SHORT).show();

                //validar existe usuario y contrasena
                // Obtén una referencia a la colección 'credenciales'
                if(validarCredenciales(editTextUsername.getText().toString().trim(),editTextPassword.getText().toString().trim())){
                    verificarCredenciales(editTextUsername.getText().toString().trim(),editTextPassword.getText().toString().trim());
                }else{
                    Toast.makeText(MainActivity.this, "INGRESE SUS CREDENCIALES", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean validarCredenciales(String... args){
        for (String arg: args) {
            System.out.println(arg);
            if(arg == null || arg.isEmpty()){
                return false;
            }
        }
        return true;
    }

    private void verificarCredenciales(String usuario, String contrasena) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference credencialesRef = db.collection("credenciales");

        Task<QuerySnapshot> queryTask = credencialesRef
                .whereEqualTo("usuario", usuario)
                .whereEqualTo("contrasena", contrasena)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                // Verifica si el documento actual existe en Firestore
                                if (documentSnapshot.exists()) {
                                    // El documento con el usuario y contraseña existe
                                    // Realiza las acciones que necesites
                                    Intent intent = new Intent(MainActivity.this, ListActivity.class);
                                    intent.putExtra("usuario", usuario);
                                    intent.putExtra("contrasena", contrasena);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // El documento no existe con los valores proporcionados
                                    // Maneja este caso según tus necesidades
                                    Toast.makeText(MainActivity.this, "CREDENCIALES INCORRECTAS", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }else{
                            Toast.makeText(MainActivity.this, "CREDENCIALES INCORRECTAS", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "ERROR DE CONSULTA DE USUARIO", Toast.LENGTH_SHORT).show();
                    }
                });


    }
}