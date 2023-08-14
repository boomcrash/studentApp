package com.example.studentapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.studentapp.R;
import com.example.studentapp.adapters.CustomListAdapter;
import com.example.studentapp.model.Persona;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Persona> personas;
    private Persona p;
    private RecyclerView recyclerView;
    private CustomListAdapter adapter;
    public static String usuario;
    public static String contrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //recibir credenciales:
        Intent intent = getIntent();
        if (intent != null) {
            usuario = intent.getStringExtra("usuario");
            contrasena = intent.getStringExtra("contrasena");
            // Ahora puedes usar usuario y contrasena en esta actividad
        }
        getQueryCollecion("estudiante");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CustomListAdapter(personas, this);
        recyclerView.setAdapter(adapter);

    // Configurar el SearchView para filtrar por cédula
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterPersonasByCedula(newText);
                return true;
            }
        });
    }

    // Método para obtener los datos de ejemplo de las personas (simulado)
    /*private List<Persona> getPersonasFromDatabase() {
        // Aquí implementarías la lógica para obtener los datos de las personas desde tu base de datos
        // y las devolverías como una lista de objetos Persona
        // Por ahora, lo simularemos con datos de ejemplo:
        List<Persona> personas = new ArrayList<>();
        personas.add(new Persona("1234567890", "0987654321", "Juan", "Pérez",
                "juan.perez@example.com", "Calle 123, Ciudad", "Medicina", 4, "https://th.bing.com/th/id/R.90416da5ee535ccf90054a578d47ba5f?rik=V7t8uoK9rvRv5A&pid=ImgRaw&r=0",
                "https://www.redalyc.org/pdf/706/70645811001.pdf", "/storage/emulated/0/Audio/hola1.mp3"));
        personas.add(new Persona("0987654321", "0987654321", "María", "Gómez",
                "maria.gomez@example.com", "Avenida XYZ, Ciudad", "Derecho", 3, "https://th.bing.com/th/id/OIP.kC-wGXKn1iwD1r8jxbFtrgHaF7?pid=ImgDet&w=474&h=379&rs=1",
                "https://www.redalyc.org/pdf/706/70645811001.pdf", "/storage/emulated/0/Audio/hola1.mp3"));
        personas.add(new Persona("9876543210", "0987654321", "Pedro", "Ramírez",
                "pedro.ramirez@example.com", "Calle ABC, Ciudad", "Ingeniería", 5, "https://th.bing.com/th/id/OIP.2cOe4ej-roywKGJXcvQQUQHaF7?pid=ImgDet&rs=1",
                "https://www.redalyc.org/pdf/706/70645811001.pdf", "/storage/emulated/0/Audio/hola2.mp3"));
        personas.add(new Persona("1111111111", "0987654321", "Laura", "López",
                "laura.lopez@example.com", "Carrera 45, Ciudad", "Arquitectura", 2, "https://th.bing.com/th/id/OIP.W1K0q6M0KMl1GLB0m8sZZAHaF7?pid=ImgDet&w=474&h=379&rs=1",
                "https://www.redalyc.org/pdf/706/70645811001.pdf", "/storage/emulated/0/Audio/hola1.mp3"));
        personas.add(new Persona("2222222222", "0987654321", "Carlos", "García",
                "carlos.garcia@example.com", "Avenida 12, Ciudad", "Contabilidad", 1, "https://th.bing.com/th/id/OIP.-q7IHuZDil85587OrkdkUAHaF6?pid=ImgDet&w=474&h=378&rs=1",
                "https://www.redalyc.org/pdf/706/70645811001.pdf", "/storage/emulated/0/Audio/hola3.mp3"));
        personas.add(new Persona("3333333333", "0987654321", "Sofía", "Hernández",
                "sofia.hernandez@example.com", "Calle 78, Ciudad", "Psicología", 3, "https://th.bing.com/th/id/OIP.ATP8XIchpWYEF2CDoAi7owHaHb?pid=ImgDet&w=474&h=475&rs=1",
                "https://www.redalyc.org/pdf/706/70645811001.pdf", "/storage/emulated/0/Audio/hola1.mp3"));
        personas.add(new Persona("4444444444", "0987654321", "Andrés", "Rojas",
                "andres.rojas@example.com", "Avenida ABC, Ciudad", "Artes Visuales", 5, "https://cdn.domestika.org/c_limit,dpr_auto,f_auto,q_auto,w_820/v1576497340/content-items/003/518/329/_MG_5442-original.jpg?1576497340",
                "https://www.redalyc.org/pdf/706/70645811001.pdf", "/storage/emulated/0/Audio/hola4.mp3"));
        personas.add(new Persona("5555555555", "0987654321", "Ana", "Mendoza",
                "ana.mendoza@example.com", "Carrera XYZ, Ciudad", "Ingeniería Civil", 4, "https://th.bing.com/th/id/OIP.PwNJ534FKy6wjdahikAr6wHaHc?pid=ImgDet&w=474&h=476&rs=1",
                "https://www.redalyc.org/pdf/706/70645811001.pdf", "/storage/emulated/0/Audio/hola1.mp3"));
        personas.add(new Persona("6666666666", "0987654321", "José", "Sánchez",
                "jose.sanchez@example.com", "Calle 789, Ciudad", "Medicina", 2, "https://th.bing.com/th/id/OIP.110SBK7MS7srv1pNV1_kEQHaHc?pid=ImgDet&w=474&h=476&rs=1",
                "https://www.redalyc.org/pdf/706/70645811001.pdf", "/storage/emulated/0/Audio/hola1.mp3"));
        personas.add(new Persona("7777777777", "0987654321", "Carolina", "Vargas",
                "carolina.vargas@example.com", "Avenida 34, Ciudad", "Derecho", 5, "https://c.pxhere.com/photos/e8/60/smile_profile_face_male_portrait_young_person_glasses-451653.jpg!d",
                "https://www.redalyc.org/pdf/706/70645811001.pdf", "/storage/emulated/0/Audio/hola4.mp3"));

        return personas;
    }*/
    public void getQueryCollecion(String collectionName){
        db.collection(collectionName).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                personas = new ArrayList<>();
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    p = new Persona(
                            documentSnapshot.getString("cedula"),
                            documentSnapshot.getString("cedula"),
                            documentSnapshot.getString("cedula"),
                            documentSnapshot.getString("cedula"),
                            documentSnapshot.getString("cedula"),
                            documentSnapshot.getString("cedula"),
                            documentSnapshot.getString("cedula"),
                            1,
                            documentSnapshot.getString("cedula"),
                            documentSnapshot.getString("cedula"),
                            documentSnapshot.getString("cedula")
                    );
                    personas.add(p);
                }

                // Aquí tienes la lista de personas
                // Puedes realizar acciones con la lista, como mostrarla en un RecyclerView
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Manejar el error si ocurre
            }
        });
    }

    // Método para filtrar las personas por cédula
    private void filterPersonasByCedula(String cedula) {
        List<Persona> filteredList = new ArrayList<>();
        for (Persona persona : personas) {
            if (persona.getCedula().contains(cedula)) {
                filteredList.add(persona);
            }
        }
        adapter = new CustomListAdapter(filteredList, this);
        recyclerView.setAdapter(adapter);
    }
}
