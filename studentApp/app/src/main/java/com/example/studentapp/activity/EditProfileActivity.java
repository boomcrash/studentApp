package com.example.studentapp.activity;

import static com.example.studentapp.activity.ListActivity.contrasena;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studentapp.MainActivity;
import com.example.studentapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {
    private EditText editTextUsername,editTextPassword,editTextCedula, editTextNombre, editTextApellido, editTextCorreo, editTextCelular, editTextDireccion;
    private Spinner spinnerCarrera, spinnerSemestre;
    private TextView editTextRegistrate, sectionCredenciales,sectionEducacion,sectionDatosP;
    private Button buttonSelectPhoto, buttonRecordAudio, buttonSelectPDF, buttonGuardar, buttonIniciar;
    private Boolean estadoCredenciales=false,estadoEducacion=false,estadoDatosP=false;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Intent intent = getIntent();
        if (intent != null) {
            //usuario = intent.getStringExtra("usuario");
            // Ahora puedes usar usuario y contrasena en esta actividad
        }
        // Inicializar vistas
        editTextRegistrate = findViewById(R.id.textViewRegister);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextCedula = findViewById(R.id.editTextCedula);
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextApellido = findViewById(R.id.editTextApellido);
        editTextCorreo = findViewById(R.id.editTextCorreo);
        editTextCelular = findViewById(R.id.editTextCelular);
        editTextDireccion = findViewById(R.id.editTextDireccion);
        spinnerCarrera = findViewById(R.id.spinnerCarrera);
        spinnerSemestre = findViewById(R.id.spinnerSemestre);
        buttonSelectPhoto = findViewById(R.id.buttonSelectPhoto);
        buttonRecordAudio = findViewById(R.id.buttonRecordAudio);
        buttonSelectPDF = findViewById(R.id.buttonSelectPDF);
        sectionCredenciales = findViewById(R.id.sectionCredenciales);
        sectionEducacion = findViewById(R.id.sectionEducacion);
        sectionDatosP = findViewById(R.id.sectionDatosP);
        buttonGuardar = findViewById(R.id.btn_actualizar);


        //
        editTextPassword.setVisibility(View.GONE);
        //cambiar visibilidad de sectionDatosP
        editTextCedula.setVisibility(View.GONE);
        editTextNombre.setVisibility(View.GONE);
        editTextApellido.setVisibility(View.GONE);
        editTextCorreo.setVisibility(View.GONE);
        editTextCelular.setVisibility(View.GONE);
        editTextDireccion.setVisibility(View.GONE);

        //cambiar visibilidad de sectionEducacion
        spinnerCarrera.setVisibility(View.GONE);
        spinnerSemestre.setVisibility(View.GONE);

        //
        editTextRegistrate.setText("ACTUALIZAR PERFIL");

        editTextUsername.setText(intent.getStringExtra("usuario"));
        editTextCedula.setText(intent.getStringExtra("cedula"));
        editTextNombre.setText(intent.getStringExtra("nombre"));
        editTextApellido.setText(intent.getStringExtra("apellido"));
        editTextCorreo.setText(intent.getStringExtra("correo"));
        editTextCelular.setText(intent.getStringExtra("celular"));
        editTextDireccion.setText(intent.getStringExtra("direccion"));

        loadSpinner(spinnerCarrera, intent.getStringExtra("carrera"));
        loadSpinner(spinnerSemestre, intent.getStringExtra("semestre"));

        editTextUsername.setEnabled(false);
        editTextUsername.setBackgroundColor(getResources().getColor(R.color.secondary_text));

        //cambiar estado de visibilidad para seccion datos personales:
        sectionDatosP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(estadoDatosP){
                    estadoDatosP=false;
                    editTextCedula.setVisibility(View.GONE);
                    editTextNombre.setVisibility(View.GONE);
                    editTextApellido.setVisibility(View.GONE);
                    editTextCorreo.setVisibility(View.GONE);
                    //editTextCelular.setVisibility(View.GONE);
                    editTextDireccion.setVisibility(View.GONE);
                }else{
                    estadoDatosP=true;
                    editTextCedula.setVisibility(View.VISIBLE);
                    editTextNombre.setVisibility(View.VISIBLE);
                    editTextApellido.setVisibility(View.VISIBLE);
                    editTextCorreo.setVisibility(View.VISIBLE);
                    //editTextCelular.setVisibility(View.VISIBLE);
                    editTextDireccion.setVisibility(View.VISIBLE);
                }
            }
        });

        //cambiar estado de visibilidad para seccion educacion:
        sectionEducacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(estadoEducacion){
                    estadoEducacion=false;
                    spinnerCarrera.setVisibility(View.GONE);
                    spinnerSemestre.setVisibility(View.GONE);
                }else{
                    estadoEducacion=true;
                    spinnerCarrera.setVisibility(View.VISIBLE);
                    spinnerSemestre.setVisibility(View.VISIBLE);
                }
            }
        });

        //cambiar estado de visibilidad para seccion credenciales:
        sectionCredenciales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(estadoCredenciales){
                    estadoCredenciales=false;
                    //editTextUsername.setVisibility(View.GONE);
                    //editTextPassword.setVisibility(View.GONE);
                }else{
                    estadoCredenciales=true;
                    //editTextUsername.setVisibility(View.VISIBLE);
                    //editTextPassword.setVisibility(View.VISIBLE);
                }
            }
        });

        buttonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> userCredentials = new HashMap<>();
                userCredentials.put("usuario", editTextUsername.getText().toString().trim());
                Map<String, Object> userDataRegister = new HashMap<>();
                userDataRegister.put("cedula", editTextCedula.getText().toString().trim());
                userDataRegister.put("usuario", editTextUsername.getText().toString().trim());
                userDataRegister.put("nombre", editTextNombre.getText().toString().trim());
                userDataRegister.put("apellido", editTextApellido.getText().toString().trim());
                userDataRegister.put("correo", editTextCorreo.getText().toString().trim());
                userDataRegister.put("direccion", editTextDireccion.getText().toString().trim());
                userDataRegister.put("carrera", spinnerCarrera.getSelectedItem().toString().trim());
                userDataRegister.put("semestre", spinnerSemestre.getSelectedItem().toString().trim());
                if (camposVacios(userCredentials) || camposVacios(userDataRegister)) {
                    Toast.makeText(EditProfileActivity.this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }
                existeUsuario(editTextUsername.getText().toString().trim(), new registro.OnCheckUserExistenceListener() {
                    @Override
                    public void onCheckUserExistence(boolean existe) {
                        if (existe) {
                            ProgressDialog progressDialog = ProgressDialog.show(EditProfileActivity.this, "", "Cargando...", true);
                            actualizarEnFirestore(userCredentials, progressDialog);
                        }else{
                            // Mostrar el cuadro de diálogo de carga
                            Toast.makeText(EditProfileActivity.this, "Nombre de usuario registrado, Intente con otro", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
            }
        });
    }

    private void existeUsuario(String user, final registro.OnCheckUserExistenceListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("credenciales")
                .whereEqualTo("usuario", user)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean existe = !task.getResult().isEmpty();
                        listener.onCheckUserExistence(existe);
                    } else {
                        // Ocurrió un error al obtener los documentos
                        listener.onCheckUserExistence(false);
                    }
                });
    }

    private boolean camposVacios(Map<String, Object> userData) {
        for (Map.Entry<String, Object> entry : userData.entrySet()) {
            if ((entry.getValue() instanceof String && TextUtils.isEmpty((String) entry.getValue())) || entry.getValue() == null) {
                return true;
            }
        }
        return false;
    }

    private void loadSpinner(Spinner spinner, Object palabra) {
        int posicionEncontrada = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            String elementoSpinner = (String) spinner.getItemAtPosition(i);
            if (elementoSpinner.equals(palabra)) {
                posicionEncontrada = i; // Guarda la posición del elemento encontrado
                break; // Sale del bucle una vez que se encuentra el elemento
            }
        }
        if (posicionEncontrada != -1) {
            spinner.setSelection(posicionEncontrada);
        }else{
            spinner.setSelection(0);
        }
    }

    private void actualizarEnFirestore(Map<String, Object> userCredentials,ProgressDialog progressDialog) {
        // Obtén una referencia a la base de datos Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Agregar usuario y contraseña a la colección 'credenciales'
        Map<String, Object> credencialesData = new HashMap<>();
        credencialesData.put("usuario", userCredentials.get("usuario"));

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("cedula", editTextCedula.getText().toString().trim()); // Nuevo valor para cédula
        updatedData.put("nombre", editTextNombre.getText().toString().trim()); // Nuevo valor para nombre
        updatedData.put("apellido", editTextApellido.getText().toString().trim()); // Nuevo valor para apellido
        updatedData.put("correo", editTextCorreo.getText().toString().trim()); // Nuevo valor para correo
        updatedData.put("direccion", editTextCorreo.getText().toString().trim()); // Nuevo valor para dirección
        updatedData.put("carrera", spinnerCarrera.getSelectedItem().toString().trim()); // Nuevo valor para carrera
        updatedData.put("semestre", spinnerSemestre.getSelectedItem().toString().trim());

        db.collection("estudiante")
                .document(userCredentials.get("usuario").toString()) // Documento a actualizar con nombre de usuario "jorge"
                .update(updatedData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Datos actualizados exitosamente en Firestore
                        Toast.makeText(EditProfileActivity.this, "ACTUALIZADO EXITOSAMENTE !", Toast.LENGTH_LONG).show();
                        // Puedes realizar acciones adicionales aquí si es necesario
                        progressDialog.dismiss();
                        // Abre la nueva actividad y cierra todas las demás actividades
                        Intent intent = new Intent(EditProfileActivity.this, ListActivity.class); // Cambia "NuevaActividad" al nombre de tu nueva actividad
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Manejar el caso de fallo al actualizar datos en Firestore
                        Toast.makeText(EditProfileActivity.this, "ERROR ! NO SE ACTUALIZÓ USUARIO.", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
    }

}
