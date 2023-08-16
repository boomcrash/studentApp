package com.example.studentapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.studentapp.MainActivity;
import com.example.studentapp.R;
import com.example.studentapp.adapters.CustomListAdapter;
import com.example.studentapp.model.Persona;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {
    static List<Persona> personas = new ArrayList<>();
    static Persona p;
    static RecyclerView recyclerView;
    static CustomListAdapter adapter;
    public static String usuario;
    public static String contrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
       // String receivedUserUid = getIntent().getStringExtra("Usuario");

        //Ingresar al Perfil
        ImageView imageViewProfile = findViewById(R.id.imageViewProfile);
        // manipular el ImageView como desees
        imageViewProfile.setImageResource(R.drawable.ic_profile);

        // Configurar el evento de click para ingresar al perfil"
        imageViewProfile.setOnClickListener(new View.OnClickListener() {
       @Override
          public void onClick(View v) {
                // Cambiar a la actividad "activity_perfil"
            //  Toast.makeText(ListActivity.this, MainActivity.getUSUARIO(), Toast.LENGTH_SHORT).show();
           Intent intent = new Intent(ListActivity.this, perfilActivity.class);
           intent.putExtra("usuario",MainActivity.getUSUARIO());
            startActivity(intent);
         }
      });

        //recibir credenciales:
        Intent intent = getIntent();
        if (intent != null) {
            usuario = intent.getStringExtra("usuario");
            contrasena = intent.getStringExtra("contrasena");
            // Ahora puedes usar usuario y contrasena en esta actividad
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference estudianteC = db.collection("estudiante");
        Task<QuerySnapshot> queryTask = estudianteC.get();
        queryTask.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot queryDocumentSnapshots = task.getResult();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        personas.add(new Persona(
                                documentSnapshot.getString("cedula"),
                                "",
                                documentSnapshot.getString("nombre"),
                                documentSnapshot.getString("apellido"),
                                documentSnapshot.getString("correo"),
                                documentSnapshot.getString("direccion"),
                                documentSnapshot.getString("carrera"),
                                Integer.parseInt(documentSnapshot.getString("semestre")),
                                documentSnapshot.getString("urlFoto"),
                                documentSnapshot.getString("urlPdf"),
                                documentSnapshot.getString("urlAudio")
                        ));
                    }

                    recyclerView = findViewById(R.id.recyclerView);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                    adapter = new CustomListAdapter(personas, ListActivity.this);
                    recyclerView.setAdapter(adapter);
                } else {
                    Exception exception = task.getException();
                    if (exception != null) {
                        // Manejar el fallo aquí
                    }
                }
            }
        });
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String cedula) {
                filterPersonasByCedula(cedula,adapter);
                return true;
            }
        });

    }
    // Configurar el SearchView para filtrar por cédula


    // Método para filtrar las personas por cédula
    private void filterPersonasByCedula(String cedula, CustomListAdapter _adapter) {
        List<Persona> filteredList = new ArrayList<>();
        for (Persona persona : personas) {
            if (persona.getCedula().contains(cedula)) {
                filteredList.add(persona);
            }
        }
        _adapter = new CustomListAdapter(filteredList, this);
        recyclerView.setAdapter(_adapter);
    }
}
