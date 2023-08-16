package com.example.studentapp.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.example.studentapp.MainActivity;
import com.example.studentapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class registro extends AppCompatActivity {

    // Obtiene una referencia al almacenamiento de Firebase
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    private String urlFoto="";
    private String urlPdf="";

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICKER = 2;
    private static final int REQUEST_AUDIO_RECORD = 3;
    private static final int REQUEST_PDF_PICK = 4;
    private boolean FILE_AUDIO_SAVED = false;
    private boolean FILE_IMAGE_SAVED = false;
    private boolean FILE_PDF_CHOOSE = false;
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    private boolean isRecorded = false;


    private EditText editTextUsername,editTextPassword,editTextCedula, editTextNombre, editTextApellido, editTextCorreo, editTextCelular, editTextDireccion;
    private Spinner spinnerCarrera, spinnerSemestre;
    private TextView sectionCredenciales,sectionEducacion,sectionDatosP;
    private Button buttonSelectPhoto, buttonRecordAudio, buttonSelectPDF;
    private Boolean estadoCredenciales=false,estadoEducacion=false,estadoDatosP=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);




        // Obtener referencia del botón "login"
        Button buttonLogin = findViewById(R.id.login);
        // Configurar el evento de clic para el botón "login"
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambiar a la actividad "activity_login"
                Intent intent = new Intent(registro.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Obtener referencia del botón "register"
        Button buttonRegister = findViewById(R.id.btn_register);
        // Configurar el evento de clic para el botón "register"
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> userCredentials = new HashMap<>();
                userCredentials.put("usuario", editTextUsername.getText().toString().trim());
                userCredentials.put("contrasena", editTextPassword.getText().toString().trim());
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
                    Toast.makeText(registro.this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }
                /*if (!(FILE_AUDIO_SAVED && FILE_IMAGE_SAVED && FILE_PDF_CHOOSE)) {
                    Toast.makeText(registro.this, "Por favor sube los archivos requeridos", Toast.LENGTH_SHORT).show();
                    return;
                }*/
                existeUsuario(editTextUsername.getText().toString().trim(), new OnCheckUserExistenceListener() {
                    @Override
                    public void onCheckUserExistence(boolean existe) {
                        if (existe) {
                            Toast.makeText(registro.this, "Nombre de usuario registrado, Intente con otro", Toast.LENGTH_SHORT).show();
                            return;
                        }else{
                            // Mostrar el cuadro de diálogo de carga
                            ProgressDialog progressDialog = ProgressDialog.show(registro.this, "", "Cargando...", true);

                            // Ruta en Firebase Storage donde se guardará el archivo de audio
                            String audioStoragePath = "audios/" + editTextUsername.getText().toString().trim() + "/audio_recording.mp3";

                            String localAudioFilePath = getExternalCacheDir().getAbsolutePath() + "/audio_recording.mp3"; // Reemplaza con la ruta real

                            StorageReference audioRef = storageRef.child(audioStoragePath);

                            UploadTask audioUploadTask = audioRef.putFile(Uri.fromFile(new File(localAudioFilePath)));

                            audioUploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot audioTaskSnapshot) {
                                    // El archivo de audio se subió exitosamente, obtén la URL de descarga
                                    Task<Uri> audioDownloadUrlTask = audioRef.getDownloadUrl();
                                    audioDownloadUrlTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri audioUri) {
                                            // La URL de descarga del archivo de audio en Firebase Storage
                                            String urlAudioFb = audioUri.toString();
                                            // Subir la imagen a Firebase Storage y obtener su URL
                                            String imageStoragePath = "imagenes/" + editTextUsername.getText().toString().trim() + "/perfil.png";
                                            StorageReference imageRef = storageRef.child(imageStoragePath);
                                            UploadTask imageUploadTask = imageRef.putFile(Uri.parse(urlFoto));

                                            imageUploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot imageTaskSnapshot) {
                                                    // El archivo de imagen se subió exitosamente, obtén la URL de descarga
                                                    Task<Uri> imageDownloadUrlTask = imageRef.getDownloadUrl();

                                                    imageDownloadUrlTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri imageUri) {
                                                            // La URL de descarga del archivo de imagen en Firebase Storage
                                                            String urlFotoFb = imageUri.toString();

                                                            // Subir el PDF a Firebase Storage y obtener su URL
                                                            String pdfStoragePath = "pdfs/" + editTextUsername.getText().toString().trim() + "/titulo.pdf";
                                                            StorageReference pdfRef = storageRef.child(pdfStoragePath);
                                                            UploadTask pdfUploadTask = pdfRef.putFile(Uri.parse(urlPdf));

                                                            pdfUploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                @Override
                                                                public void onSuccess(UploadTask.TaskSnapshot pdfTaskSnapshot) {
                                                                    // El archivo PDF se subió exitosamente, obtén la URL de descarga
                                                                    Task<Uri> pdfDownloadUrlTask = pdfRef.getDownloadUrl();

                                                                    pdfDownloadUrlTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                        @Override
                                                                        public void onSuccess(Uri pdfUri) {
                                                                            // La URL de descarga del archivo PDF en Firebase Storage
                                                                            String urlPdfFb = pdfUri.toString();
                                                                            // Llamar a la función para guardar en Firestore
                                                                            guardarEnFirestore(urlAudioFb, urlFotoFb, urlPdfFb,userCredentials, progressDialog);
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    }
                });

            }
        });


        // Inicializar vistas
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
                    editTextCelular.setVisibility(View.GONE);
                    editTextDireccion.setVisibility(View.GONE);
                }else{
                    estadoDatosP=true;
                    editTextCedula.setVisibility(View.VISIBLE);
                    editTextNombre.setVisibility(View.VISIBLE);
                    editTextApellido.setVisibility(View.VISIBLE);
                    editTextCorreo.setVisibility(View.VISIBLE);
                    editTextCelular.setVisibility(View.VISIBLE);
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
                    editTextPassword.setVisibility(View.GONE);
                }else{
                    estadoCredenciales=true;
                    editTextUsername.setVisibility(View.VISIBLE);
                    editTextPassword.setVisibility(View.VISIBLE);
                }
            }
        });

        // Configurar botón para seleccionar una foto
        Button buttonSelectPhoto = findViewById(R.id.buttonSelectPhoto);
        buttonSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mostrar un diálogo para permitir al usuario elegir entre cámara y galería
                showPhotoPickerDialog();
            }
        });

        // Configurar botón para grabar el saludo del estudiante
        buttonRecordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isRecorded){
                    // Solicitar permiso de grabación de audio si no está otorgado
                    if (ContextCompat.checkSelfPermission(registro.this, Manifest.permission.RECORD_AUDIO)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(registro.this,
                                new String[]{Manifest.permission.RECORD_AUDIO},
                                REQUEST_AUDIO_RECORD);
                    } else {
                        recordAudio();
                    }
                }else{
                    Toast.makeText(registro.this, "Ya ha guardado un audio !", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // Configurar botón para seleccionar el título en PDF
        buttonSelectPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPDF();
            }
        });
    }

    private void existeUsuario(String user, final OnCheckUserExistenceListener listener) {
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
    interface OnCheckUserExistenceListener {
        void onCheckUserExistence(boolean existe);
    }

    private void showPhotoPickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar Foto");
        builder.setItems(new CharSequence[]{"Tomar Foto", "Elegir de Galería"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        // Tomar Foto - Solicitar permiso de cámara si no está otorgado
                        if (ContextCompat.checkSelfPermission(registro.this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(registro.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    REQUEST_IMAGE_CAPTURE);
                        } else {
                            openCamera();
                        }
                        break;
                    case 1:
                        // Elegir de Galería - Abrir la galería de imágenes
                        openGallery();
                        break;
                }
            }
        });
        builder.show();
    }

    /***************************************************************************************************************/

    private void guardarEnFirestore(String urlAudio,String urlFoto,String urlPdf, Map<String, Object> userCredentials,ProgressDialog progressDialog) {
        // Obtén una referencia a la base de datos Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Agregar usuario y contraseña a la colección 'credenciales'
        Map<String, Object> credencialesData = new HashMap<>();
        credencialesData.put("usuario", userCredentials.get("usuario"));
        credencialesData.put("contrasena", userCredentials.get("contrasena"));

        // Agregar la credencial a la colección 'credenciales'
        db.collection("credenciales")
                .document(editTextUsername.getText().toString().trim())
                .set(credencialesData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Map<String, Object> userDataRegister = new HashMap<>();
                        userDataRegister.put("cedula", editTextCedula.getText().toString().trim());
                        userDataRegister.put("usuario", editTextUsername.getText().toString().trim());
                        userDataRegister.put("nombre", editTextNombre.getText().toString().trim());
                        userDataRegister.put("apellido", editTextApellido.getText().toString().trim());
                        userDataRegister.put("correo", editTextCorreo.getText().toString().trim());
                        userDataRegister.put("direccion", editTextDireccion.getText().toString().trim());
                        userDataRegister.put("carrera", spinnerCarrera.getSelectedItem().toString().trim());
                        userDataRegister.put("semestre", spinnerSemestre.getSelectedItem().toString().trim());
                        userDataRegister.put("urlAudio", urlAudio);
                        userDataRegister.put("urlFoto", urlFoto);
                        userDataRegister.put("urlPdf", urlPdf);
                        db.collection("estudiante")
                                .document(editTextUsername.getText().toString().trim())
                                .set(userDataRegister)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Los datos del estudiante se agregaron exitosamente a Firestore
                                        // Ahora procedemos a guardar la URL de audio
                                        Toast.makeText(registro.this, "REGISTRADO EXITOSAMENTE !", Toast.LENGTH_LONG).show();
                                        // Cambiar a la actividad "activity_login"
                                        Intent intent = new Intent(registro.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Manejar el caso de fallo al agregar datos del estudiante en Firestore
                                        Toast.makeText(registro.this, "ERROR ! NO SE REGISTRÓ USUARIO.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Manejar el caso de fallo al agregar la credencial en Firestore
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
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

    private boolean camposVacios(String word) {
        if ((word instanceof String && word.isEmpty()) || word == null) {
            return true;
        }
        return false;
    }

    private void guardarEstudiante(Map<String, Object> userDataRegister) {
        // Obtén una referencia a la colección 'audios' en Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference estudiante = db.collection("estudiante");

        // Crea un nuevo documento
        Map<String, Object> userData = userDataRegister;

        estudiante.add(userData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(registro.this, "REGISTRADO EXITOSAMENTE !", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(registro.this, "ERROR ! NO SE REGISTRÓ USUARIO.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /***************************************************************************************************************/

    private void openCamera() {
        // Lógica para abrir la cámara aquí
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void openGallery() {
        // Lógica para abrir la galería aquí
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICKER);
    }


    private void showPermissionDeniedDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permiso denegado")
                .setMessage("No se puede abrir la cámara sin permiso.")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Cerrar la actividad o realizar otra acción al denegar el permiso
                        // finish();
                    }
                })
                .show();
    }

    /***************************************************************************************************************/

    // Método para grabar audio
    private void recordAudio() {
        if (isRecording) {
            // Detener la grabación si ya está en progreso
            stopRecording();
        } else {
            // Iniciar la grabación si no está en progreso
            startRecording();
        }
    }

    private void startRecording() {
        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
        }
        try {
            // Configurar la fuente de audio y el formato de salida
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setOutputFile(getExternalCacheDir().getAbsolutePath() + "/audio_recording.mp3");
            // Iniciar la grabación
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            // Cambiar el texto del botón a "Detener Grabación"
            buttonRecordAudio.setText("Detener Grabación");

            // Mostrar el diálogo de "Grabando"
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Grabando...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            // Iniciar temporizador para detener la grabación después de 5 segundos
            new CountDownTimer(5000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    // Actualizar el mensaje del diálogo con el tiempo restante
                    int secondsRemaining = (int) (millisUntilFinished / 1000);
                    progressDialog.setMessage("Grabando... " + secondsRemaining + " segundos restantes");
                }

                @Override
                public void onFinish() {
                    progressDialog.dismiss();
                    stopRecording();
                    showPlaybackDialog();
                }
            }.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void stopRecording() {
        if (isRecording) {
            // Detener la grabación y liberar los recursos
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
            // Cambiar el texto del botón a "Grabar Saludo"
            buttonRecordAudio.setText("Grabar Saludo");
        }
    }

    private void showPlaybackDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reproducir y Guardar");
        builder.setMessage("¿Deseas reproducir o guardar el audio grabado?");

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNeutralButton("Reproducir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final ProgressDialog progressDialog = new ProgressDialog(registro.this);
                progressDialog.setMessage("Reproduciendo...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                playAudio(progressDialog);

            }
        });
        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String audioFilePath = getExternalCacheDir().getAbsolutePath() + "/audio_recording.mp3";
                MediaPlayer mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(audioFilePath);
                    //saveAudioToFirebaseStorage(audioFilePath);
                    buttonRecordAudio.setText("Audio Guardado");
                    isRecorded=true;
                    buttonRecordAudio.setBackgroundResource(R.drawable.button_background_saved);
                    buttonRecordAudio.setTextColor(getResources().getColor(R.color.black));
                    FILE_AUDIO_SAVED = true;
                    dialog.dismiss();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.show();
    }


    private void playAudio(ProgressDialog progressDialog) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(getExternalCacheDir().getAbsolutePath() + "/audio_recording.mp3");
            mediaPlayer.prepare();
            mediaPlayer.start();

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    progressDialog.dismiss();
                    showPlaybackDialog();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void showAlertAudio() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permiso Denegado");
        builder.setMessage("No se ha otorgado el permiso de grabación de audio. Por favor, habilite el permiso desde la configuración de la aplicación.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    /***************************************************************************************************************/

    // Método para seleccionar un archivo PDF
    private void selectPDF() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, REQUEST_PDF_PICK);
    }
    /***************************************************************************************************************/
    /* GUARDAR PDF EN ALMACENAMIENTO DE DISPOSITIVO */
    private String savePdfToInternalStorage(Uri pdfUri, String filename) {
        // Obtiene el directorio de almacenamiento interno de la aplicación
        File directory = getApplicationContext().getDir("pdfs", Context.MODE_PRIVATE);

        // Crea un archivo en el directorio con el nombre "titulo.pdf"
        File file = new File(directory, filename);

        try {
            // Abre un flujo de entrada desde la URI del archivo PDF
            InputStream inputStream = getContentResolver().openInputStream(pdfUri);

            // Abre un flujo de salida en el archivo
            FileOutputStream outputStream = new FileOutputStream(file);

            // Copia el contenido del flujo de entrada al flujo de salida
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            // Cierra los flujos
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Obtiene la ruta completa del archivo
        return file.getAbsolutePath();
    }



    private String saveImageToInternalStorage(Bitmap bitmap) {
        // Obtiene el directorio de caché interno de la aplicación
        File cacheDir = getApplicationContext().getCacheDir();
        // Crea un archivo en el directorio de caché con el nombre "perfil.png"
        File file = new File(cacheDir,"imagen.png");

        try {
            // Abre un flujo de salida en el archivo
            FileOutputStream outputStream = new FileOutputStream(file);

            // Comprime la imagen en formato PNG y la guarda en el flujo de salida
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

            // Cierra el flujo de salida
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Obtiene la ruta completa del archivo en la caché
        return file.getAbsolutePath();
    }



    /* GENERAL PARA TODOS LOS BOTONES */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_PICKER && resultCode == RESULT_OK && data != null) {
                Uri selectedImageUri = data.getData();

                // Obtiene la URL de la imagen guardada y la almacena en la variable urlFoto
                urlFoto = selectedImageUri.toString();
                // Actualiza el botón u otro elemento de la interfaz según sea necesario
                buttonSelectPhoto.setBackgroundResource(R.drawable.button_background_saved);
                buttonSelectPhoto.setTextColor(getResources().getColor(R.color.black));
            }
            else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
                // Obtiene la imagen tomada con la cámara
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap imageBitmap = (Bitmap) extras.get("data");

                    // Guarda la imagen en la galería de tu aplicación con el nombre "perfil.png"
                    String imagePath = saveImageToInternalStorage(imageBitmap);

                    // Obtiene la URL de la imagen guardada y la almacena en la variable urlFoto
                    urlFoto = imagePath;
                    FILE_IMAGE_SAVED = (urlFoto != null || urlFoto.isEmpty()) ? true : false;
                    // Actualiza el botón u otro elemento de la interfaz según sea necesario
                    buttonSelectPhoto.setBackgroundResource(R.drawable.button_background_saved);
                    buttonSelectPhoto.setTextColor(getResources().getColor(R.color.black));
                }
            } else if (requestCode == REQUEST_AUDIO_RECORD) {
                // Procesar el audio grabado
                Toast.makeText(this, "Audio grabado", Toast.LENGTH_SHORT).show();
            } else if (requestCode == REQUEST_PDF_PICK) {
                // Obtener la URI del archivo PDF seleccionado
                if (data != null) {
                    Uri pdfUri = data.getData();

                    // Establecer la URL del archivo PDF guardado en la variable urlPdf
                    urlPdf = pdfUri.toString();
                    FILE_PDF_CHOOSE = (urlPdf != null || urlPdf.isEmpty()) ? true : false;
                    // Procesar el archivo PDF seleccionado
                    buttonSelectPDF.setBackgroundResource(R.drawable.button_background_saved);
                    buttonSelectPDF.setTextColor(getResources().getColor(R.color.black));

                }
            }

        }

    }

    /* GENERAL PARA TODOS LOS BOTONES */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            // Verificar si el permiso ha sido otorgado
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso otorgado, abrir la cámara
                openCamera();
            } else {
                // Permiso denegado, mostrar un mensaje al usuario
                showPermissionDeniedDialog();
            }
        }else if (requestCode == REQUEST_AUDIO_RECORD) {
            // Verificar si el permiso ha sido otorgado
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso otorgado, iniciar la grabación
                recordAudio();
            } else {
                // Permiso denegado, mostrar un mensaje al usuario
                showAlertAudio();
            }
        }
    }
    /***************************************************************************************************************/

    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alerta")
                .setMessage("Esto es una alerta de ejemplo.")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Acciones a realizar cuando se presiona el botón "Aceptar"
                        // Puedes dejarlo vacío si no quieres hacer nada adicional.
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Acciones a realizar cuando se presiona el botón "Cancelar"
                        // Puedes dejarlo vacío si no quieres hacer nada adicional.
                    }
                })
                .show();
    }

}