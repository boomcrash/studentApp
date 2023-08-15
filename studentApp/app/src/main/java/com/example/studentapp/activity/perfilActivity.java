package com.example.studentapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.studentapp.model.Persona;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studentapp.MainActivity;
import com.example.studentapp.R;

public class perfilActivity extends AppCompatActivity {

    public static String usuario;
    private FirebaseFirestore db;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        Intent intent = getIntent();
        if (intent != null) {
            usuario = intent.getStringExtra("usuario");
            // Ahora puedes usar usuario y contrasena en esta actividad
        }
        ImageView imageViewFoto = findViewById(R.id.imageViewFoto);
        TextView textViewCedula = findViewById(R.id.textViewCedula);
        TextView textViewCelular = findViewById(R.id.textViewCelular);
        TextView textViewNombre = findViewById(R.id.textViewNombre);
        TextView textViewApellido = findViewById(R.id.textViewApellido);
        TextView textViewCorreo = findViewById(R.id.textViewCorreo);
        TextView textViewDireccion = findViewById(R.id.textViewDireccion);
        TextView textViewCarrera = findViewById(R.id.textViewCarrera);
        TextView textViewNivelCarrera = findViewById(R.id.textViewNivelCarrera);


        cargarPerfil(new OnProfileLoadedListener() {
            @Override
            public void onProfileLoaded(Persona p) {
                textViewCedula.setText("Cédula: " + p.getCedula());
                textViewNombre.setText("Nombre: " + p.getNombre());
                textViewApellido.setText("Apellido: " + p.getApellido());
                //textViewCelular.setText("Celular: " + p.getCelular());
                textViewCorreo.setText("Correo: " + p.getCorreo());
                textViewDireccion.setText("Direccion: " + p.getDireccion());
                textViewCarrera.setText("Carrera: " + p.getCarrera());
                textViewNivelCarrera.setText("Nivel de carrera: " + p.getNivelCarrera());
                Glide.with(perfilActivity.this)
                        .load(p.getFotoUrl())  // Utiliza p.getImagen() en lugar de "imagen"
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.person_placeholder)
                        .error(R.drawable.person_placeholder)
                        .into(imageViewFoto);
                imageViewFoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(perfilActivity.this, FullScreenImageActivity.class);
                        intent.putExtra("image_url", p.getFotoUrl());
                        startActivity(intent);
                    }
                });
            }
        });
    }
    public void cargarPerfil(OnProfileLoadedListener listener) {
        db = FirebaseFirestore.getInstance();

        db.collection("estudiante")
                .whereEqualTo("usuario", usuario)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Persona p = new Persona();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            p.setCedula(document.getString("cedula"));
                            p.setNombre(document.getString("nombre"));
                            p.setApellido(document.getString("apellido"));
                            //p.setCelular(document.getString("celular"));
                            p.setCorreo(document.getString("correo"));
                            p.setDireccion(document.getString("direccion"));
                            p.setCarrera(document.getString("carrera"));
                            p.setNivelCarrera(Integer.parseInt(document.getString("semestre")));
                            p.setFotoUrl(document.getString("urlFoto"));
                            if (listener != null) {
                                listener.onProfileLoaded(p);
                            }
                        }
                    }
                });
    }

    interface OnProfileLoadedListener {
        void onProfileLoaded(Persona p);
}
}