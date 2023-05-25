package com.example.Lists;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Lists.pojos.Lineas_Ticket;
import com.example.Lists.pojos.Producto;
import com.example.adapters.AdapterRecyclerView;
import com.example.proyector.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CardViewGestionComandas extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference myRef1 = db.collection("tickets");
    FirebaseUser currentUser;
    FirebaseFirestore userDb;
    List<Lineas_Ticket> listaGestionComandas = new ArrayList<>();
    int idRestauranteSesionIniciada = 0;
    String nombreRestauranteUsuarioLogeado;
    String perfilUsuarioLogeado;
    String idPerfil;
    private FirebaseAuth mAuth;
    private CollectionReference collectionRef;
    private RecyclerView recyclerViewGestionComandas;
    private AdapterRecyclerView adaptadorGestionComandas;
    private Handler handler;
    private Runnable runnable;
    private final int intervaloTiempo = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_view_gestion_comandas);

        getSupportActionBar().hide();
        //ImageButton backButton = findViewById(R.id.backBtn);
        Button logOutBtn = (Button) findViewById(R.id.logOutBtn);

        mAuth = FirebaseAuth.getInstance();
        userDb = FirebaseFirestore.getInstance();
        collectionRef = db.collection("usuarios");

        recyclerViewGestionComandas = (RecyclerView) findViewById(R.id.recyclerGestionComandas);
        recyclerViewGestionComandas.setLayoutManager(new LinearLayoutManager(this));

        adaptadorGestionComandas = new AdapterRecyclerView(listaGestionComandas);

        recyclerViewGestionComandas.setAdapter(adaptadorGestionComandas);

        updateLinearLayout();

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                updateLinearLayout();
                handler.postDelayed(this, intervaloTiempo);
            }
        };

        handler.postDelayed(runnable, intervaloTiempo);

        adaptadorGestionComandas.setOnItemClickListener(new AdapterRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String idLineaTicket = listaGestionComandas.get(position).getIdLineaTicket();

                String documentTicket = idLineaTicket.substring(0, idLineaTicket.indexOf("/"));
                int caracteresDespuesDeBarra = Integer.parseInt(idLineaTicket.substring(idLineaTicket.indexOf("/") + 1));

                DocumentReference documentRef = db.collection("tickets").document(documentTicket);

                if (perfilUsuarioLogeado.equals("Camarero")) {
                    documentRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                ArrayList<Map<String, Object>> lineas_ticket = (ArrayList<Map<String, Object>>) document.get("lineas_ticket");
                                Map<String, Object> lineaTicket = lineas_ticket.get(caracteresDespuesDeBarra);
                                lineaTicket.put("sirve_idperfil", "");

                                documentRef.update("lineas_ticket", lineas_ticket)
                                        .addOnSuccessListener(aVoid -> {
                                            listaGestionComandas.clear();
                                            updateLinearLayout();
                                        }).addOnFailureListener(e -> {
                                        });
                            }
                        }
                    });

                } else if (perfilUsuarioLogeado.equals("Cocinero")) {
                    documentRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                ArrayList<Map<String, Object>> lineas_ticket = (ArrayList<Map<String, Object>>) document.get("lineas_ticket");
                                Map<String, Object> lineaTicket = lineas_ticket.get(caracteresDespuesDeBarra);
                                lineaTicket.put("prepara_idperfil", "");

                                documentRef.update("lineas_ticket", lineas_ticket)
                                        .addOnSuccessListener(aVoid -> {
                                            listaGestionComandas.clear();
                                            updateLinearLayout();
                                        }).addOnFailureListener(e -> {
                                        });
                            }
                        }
                    });
                }
            }
        });

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(runnable);
                mAuth.signOut();
                finish();
            }
        });
    }

    private void updateLinearLayout() {

        listaGestionComandas.clear();
        currentUser = mAuth.getCurrentUser();

        String uid = null;
        if (currentUser != null) {
            uid = currentUser.getUid();
        }
        String finalUid = uid;

        collectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot snapshot : snapshotList) {
                    if (finalUid.equals(snapshot.getString("UID"))) {
                        nombreRestauranteUsuarioLogeado = snapshot.getString("Restaurante");
                        perfilUsuarioLogeado = snapshot.getString("Perfil");
                        idPerfil = snapshot.getString("id");
                    }
                }

                myRef1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Long cantidadLineaTicket = null;
                                String observacionesLineaTicket = null;
                                String productoNombreLineaTicket = null;
                                Producto productoLineaTicket = null;
                                String preparaIdPerfilLineaTicket = null;
                                String sirveIdPerfilLineaTicket = null;
                                int indice = 0;
                                String nombreRestaurante = null;

                                Map<String, Object> mapa = (Map<String, Object>) document.get("restaurante");
                                for (Map.Entry<String, Object> entry : mapa.entrySet()) {
                                    if (entry.getKey().equals("nombre")) {
                                        nombreRestaurante = (String) entry.getValue();
                                    }
                                }

                                ArrayList<Map<String, Object>> lineas_ticket = (ArrayList<Map<String, Object>>) document.get("lineas_ticket");

                                for (Map<String, Object> mapaFila : lineas_ticket) {

                                    // Recorrer el mapa de cada fila
                                    for (Map.Entry<String, Object> entry : mapaFila.entrySet()) {
                                        if (entry.getKey().equals("cantidad")) {
                                            cantidadLineaTicket = (Long) entry.getValue();
                                        }
                                        if (entry.getKey().equals("observaciones")) {
                                            observacionesLineaTicket = entry.getValue() == null ? "N/A" : (String) entry.getValue();
                                        }
                                        if (entry.getKey().equals("producto")) {
                                            Map<String, Object> mapaProducto = (Map<String, Object>) entry.getValue();
                                            for (Map.Entry<String, Object> entryProducto : mapaProducto.entrySet()) {
                                                if (entryProducto.getKey().equals("nombre")) {
                                                    productoNombreLineaTicket = (String) entryProducto.getValue();
                                                    productoLineaTicket = new Producto();
                                                    productoLineaTicket.setNombre(productoNombreLineaTicket);
                                                }
                                            }
                                        }
                                        if (entry.getKey().equals("prepara_idperfil")) {
                                            preparaIdPerfilLineaTicket = (String) entry.getValue();
                                        }
                                        if (entry.getKey().equals("sirve_idperfil")) {
                                            sirveIdPerfilLineaTicket = (String) entry.getValue();
                                        }
                                        indice = lineas_ticket.indexOf(mapaFila);

                                    }
                                    Lineas_Ticket agregarComanda = new Lineas_Ticket(cantidadLineaTicket.intValue(), observacionesLineaTicket, productoLineaTicket);
                                    agregarComanda.setIdLineaTicket(document.getId() + "/" + indice);

                                    if(perfilUsuarioLogeado.equals("Camarero") && idPerfil.equals(sirveIdPerfilLineaTicket) && (preparaIdPerfilLineaTicket == null || preparaIdPerfilLineaTicket.equals(""))){
                                        listaGestionComandas.add(agregarComanda);
                                    }else if(perfilUsuarioLogeado.equals("Cocinero") && idPerfil.equals(preparaIdPerfilLineaTicket)){
                                        listaGestionComandas.add(agregarComanda);
                                    }
                                }
                            }
                            adaptadorGestionComandas.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
            }
        });
    }

}
