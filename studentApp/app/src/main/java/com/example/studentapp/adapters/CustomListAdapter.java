package com.example.studentapp.adapters;

import android.Manifest;
import android.app.Activity;
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


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class CustomListAdapter extends RecyclerView.Adapter<CustomListAdapter.ViewHolder> {

    private List<Persona> personas;
    private Context context;
    ProgressDialog progressDialog;

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
                        ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // Solicitar permiso para escribir en el almacenamiento externo
                    ActivityCompat.requestPermissions((ListActivity)context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                    return;
                }

                // Mostrar ProgressDialog
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Cargando...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                // Descargar y reproducir el audio
                new DownloadAudioTask(context).execute(audioUrl);
            }
        });



    }


    private class DownloadAudioTask extends AsyncTask<String, Void, Boolean> {
        private Context context;

        public DownloadAudioTask(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            String audioUrl = urls[0];

            try {
                URL url = new URL(audioUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    return false;
                }

                InputStream input = connection.getInputStream();

                File audioFile = new File(context.getExternalFilesDir(null), "audio_playing.mp3");
                OutputStream output = context.getContentResolver().openOutputStream(Uri.fromFile(audioFile));

                byte[] buffer = new byte[1024];
                int length;
                while ((length = input.read(buffer)) != -1) {
                    output.write(buffer, 0, length);
                }

                output.flush();
                output.close();
                input.close();

                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            // Cerrar ProgressDialog
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (success) {
                // Reproduce el audio desde el archivo descargado
                File audioFile = new File(context.getExternalFilesDir(null), "audio_playing.mp3");
                playAudio(audioFile.getAbsolutePath());
            }
        }
    }

    // Método para reproducir el audio
    private void playAudio(String audioFilePath) {
        MediaPlayer mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(audioFilePath);
            mediaPlayer.prepare();
            mediaPlayer.start();

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    // Liberar los recursos del reproductor cuando se complete la reproducción
                    mp.release();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
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
