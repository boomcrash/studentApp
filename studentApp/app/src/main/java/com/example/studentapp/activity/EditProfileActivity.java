package com.example.studentapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.studentapp.R;

public class EditProfileActivity extends AppCompatActivity {
    private EditText editTextUsername,editTextPassword,editTextCedula, editTextNombre, editTextApellido, editTextCorreo, editTextCelular, editTextDireccion;
    private Spinner spinnerCarrera, spinnerSemestre;
    private TextView editTextRegistrate, sectionCredenciales,sectionEducacion,sectionDatosP;
    private Button buttonSelectPhoto, buttonRecordAudio, buttonSelectPDF, buttonGuardar, buttonIniciar;
    private Boolean estadoCredenciales=false,estadoEducacion=false,estadoDatosP=false;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

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
        buttonGuardar = findViewById(R.id.btn_register);
        buttonIniciar = findViewById(R.id.login);

        //cambiar visibilidad de sectionCredenciales
        editTextUsername.setVisibility(View.GONE);
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
        buttonGuardar.setText("GUARDAR");
        buttonIniciar.setVisibility(View.GONE);

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
                    editTextUsername.setVisibility(View.GONE);
                    //editTextPassword.setVisibility(View.GONE);
                }else{
                    estadoCredenciales=true;
                    editTextUsername.setVisibility(View.VISIBLE);
                    //editTextPassword.setVisibility(View.VISIBLE);
                }
            }
        });

        buttonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
}
