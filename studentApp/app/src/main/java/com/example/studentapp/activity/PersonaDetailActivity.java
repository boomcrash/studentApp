package com.example.studentapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.studentapp.R;

public class PersonaDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persona_detail);

        ImageView imageViewFoto = findViewById(R.id.imageViewFoto);
        TextView textViewCedula = findViewById(R.id.textViewCedula);
        TextView textViewCelular = findViewById(R.id.textViewCelular);
        TextView textViewNombre = findViewById(R.id.textViewNombre);
        TextView textViewApellido = findViewById(R.id.textViewApellido);
        TextView textViewCorreo = findViewById(R.id.textViewCorreo);
        TextView textViewDireccion = findViewById(R.id.textViewDireccion);
        TextView textViewCarrera = findViewById(R.id.textViewCarrera);
        TextView textViewNivelCarrera = findViewById(R.id.textViewNivelCarrera);

        Intent intent = getIntent();
        if (intent != null) {
            String cedula = intent.getStringExtra("cedula");
            String celular = intent.getStringExtra("celular");
            String nombre = intent.getStringExtra("nombre");
            String apellido = intent.getStringExtra("apellido");
            String correo = intent.getStringExtra("correo");
            String direccion = intent.getStringExtra("direccion");
            String carrera = intent.getStringExtra("carrera");
            String imagen = intent.getStringExtra("imagen");
            int nivelCarrera = intent.getIntExtra("nivelCarrera", 0);

            textViewCedula.setText("Cédula: " + cedula);
            textViewCelular.setText("Celular: " + celular);
            textViewNombre.setText("Nombre: " + nombre);
            textViewApellido.setText("Apellido: " + apellido);
            textViewCorreo.setText("Correo: " + correo);
            textViewDireccion.setText("Dirección: " + direccion);
            textViewCarrera.setText("Carrera: " + carrera);
            textViewNivelCarrera.setText("Nivel de carrera: " + nivelCarrera);

            // Cargar la foto de la persona en el ImageView usando Glide
            Glide.with(this)
                    .load(imagen)
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // Guarda la imagen en caché
                    .placeholder(R.drawable.person_placeholder) // Imagen de relleno mientras carga
                    .error(R.drawable.person_placeholder) // Imagen de error si no se puede cargar
                    .into(imageViewFoto);
        }
    }
}
