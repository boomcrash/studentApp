package com.example.studentapp.adapters;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.studentapp.R;
import com.example.studentapp.activity.FullScreenImageActivity;
import com.example.studentapp.activity.ListActivity;
import com.example.studentapp.activity.PersonaDetailActivity;
import com.example.studentapp.model.Persona;

import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;


import java.io.IOException;
import java.util.List;

public class CustomListAdapter extends RecyclerView.Adapter<CustomListAdapter.ViewHolder> {

    private List<Persona> personas;
    private Context context;

    public CustomListAdapter(List<Persona> personas, Context context) {
        this.personas = personas;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Persona persona = personas.get(position);
        holder.textViewNombre.setText(persona.getNombre());
        holder.textViewApellido.setText(persona.getApellido());
        holder.textViewCedula.setText(persona.getCedula());
        //showImageURLDialog(persona.getFotoUrl());
        // Cargar la foto de cada persona en el ImageView usando Glide
        Glide.with(context)
                .load(persona.getFotoUrl())
                .placeholder(R.drawable.person_placeholder)
                .into(holder.imageViewFoto);

        // Abrir la imagen en pantalla completa
        holder.imageViewFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullScreenImageActivity.class);
                intent.putExtra("image_url", persona.getFotoUrl());
                context.startActivity(intent);
            }
        });

        // Botón para mostrar información de la persona
        holder.buttonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PersonaDetailActivity.class);
                intent.putExtra("cedula", persona.getCedula());
                intent.putExtra("celular", persona.getCelular());
                intent.putExtra("nombre", persona.getNombre());
                intent.putExtra("apellido", persona.getApellido());
                intent.putExtra("correo", persona.getCorreo());
                intent.putExtra("direccion", persona.getDireccion());
                intent.putExtra("carrera", persona.getCarrera());
                intent.putExtra("nivelCarrera", persona.getNivelCarrera());
                intent.putExtra("imagen", persona.getFotoUrl());
                context.startActivity(intent);
            }
        });
        // Botón para mostrar el pdf de la persona
        holder.buttonPdf.setOnClickListener(new View.OnClickListener() {
            // Dentro del método onClick del botón PDF
            @Override
            public void onClick(View v) {
                String pdfUrl = persona.getPdfUrl();

                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(pdfUrl), "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    // Verificar si hay una aplicación de PDF instalada
                    if (intent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, "No se encontró una aplicación para abrir PDF", Toast.LENGTH_SHORT).show();
                    }
                } catch (ActivityNotFoundException e) {
                    // Manejar excepción si no se encuentra una aplicación de lectura de PDF
                    Toast.makeText(context, "No se encontró una aplicación para abrir PDF", Toast.LENGTH_SHORT).show();
                }
            }
        });


// Botón para mostrar el audio de la persona
        holder.buttonAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String audioUrl = persona.getAudioUrl();

                // Verificar y solicitar permisos si es necesario
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // Solicitar permiso para leer el almacenamiento externo
                    ActivityCompat.requestPermissions((ListActivity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    return;
                }

                // Si los permisos ya están concedidos, puedes reproducir el audio
                playAudio(audioUrl);
            }
        });


    }

    // Método para reproducir el audio
    private void playAudio(String audioUrl) {
        // Mostrar ProgressDialog mientras se carga y reproduce el audio
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Cargando audio...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Reproducir el audio en segundo plano usando AsyncTask
        new PlayAudioTask(progressDialog).execute(audioUrl);
    }

    private class PlayAudioTask extends AsyncTask<String, Void, Boolean> {

        private ProgressDialog progressDialog;

        public PlayAudioTask(ProgressDialog progressDialog) {
            this.progressDialog = progressDialog;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                // Configurar el reproductor de audio para reproducir desde la ruta proporcionada
                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(strings[0]);

                // Configurar atributos de audio para asegurar una reproducción adecuada
                mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                        .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                        .build());

                // Preparar y reproducir el audio
                mediaPlayer.prepare();
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // Liberar recursos después de la reproducción
                        mediaPlayer.release();
                    }
                });

                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            progressDialog.dismiss();
            if (!success) {
                Toast.makeText(context, "Error al reproducir audio", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showImageURLDialog(String imageURL) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("URL de la imagen");
        builder.setMessage(imageURL);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    @Override
    public int getItemCount() {
        return personas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewFoto;
        TextView textViewNombre;
        TextView textViewApellido;
        TextView textViewCedula;
        ImageButton buttonPdf;
        ImageButton buttonAudio;
        ImageButton buttonInfo;

        public ViewHolder(View itemView) {
            super(itemView);
            imageViewFoto = itemView.findViewById(R.id.imageViewFoto);
            textViewNombre = itemView.findViewById(R.id.textViewNombre);
            textViewApellido = itemView.findViewById(R.id.textViewApellido);
            textViewCedula = itemView.findViewById(R.id.textViewCedula);
            buttonPdf = itemView.findViewById(R.id.buttonPdf);
            buttonAudio = itemView.findViewById(R.id.buttonAudio);
            buttonInfo = itemView.findViewById(R.id.buttonInfo);
        }
    }
}
