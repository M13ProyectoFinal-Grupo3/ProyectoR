package com.example.Lists;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Lists.pojos.Lineas_Ticket;
import com.example.adapters.AdapterRecyclerView;
import com.example.proyector.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardViewGestionComandas extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference myRef1 = db.collection("ticket");
    FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private CollectionReference collectionRef;
    FirebaseFirestore userDb;

    private RecyclerView recyclerViewGestionComandas;
    private AdapterRecyclerView adaptadorGestionComandas;
    List<Lineas_Ticket> listaGestionComandas = new ArrayList<>();

    int idRestauranteSesionIniciada = 0;
    String nombreRestaurante;
    String perfilUsuarioLogeado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_view_gestion_comandas);

        mAuth = FirebaseAuth.getInstance();
        userDb = FirebaseFirestore.getInstance();
        collectionRef = db.collection("usuarios");

        recyclerViewGestionComandas = (RecyclerView) findViewById(R.id.recyclerGestionComandas);
        recyclerViewGestionComandas.setLayoutManager(new LinearLayoutManager(this));

        adaptadorGestionComandas = new AdapterRecyclerView(listaGestionComandas);

        recyclerViewGestionComandas.setAdapter(adaptadorGestionComandas);

        updateLinearLayout();

        adaptadorGestionComandas.setOnItemClickListener(new AdapterRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String idLineaTicket = listaGestionComandas.get(position).getIdLineaTicket();

                int index = idLineaTicket.indexOf("/", idLineaTicket.indexOf("/") + 1);
                String documentTicket = idLineaTicket.substring(idLineaTicket.indexOf("/") + 1, index);

                String[] partes = idLineaTicket.split("/");
                String documentLineaTicket = partes[partes.length - 1];

                DocumentReference documentRef = db.collection("ticket").document(documentTicket).collection("lineaTicket").document(documentLineaTicket);

                if (perfilUsuarioLogeado.equals("Camarero")) {
                    documentRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Documento eliminado correctamente");
                            listaGestionComandas.clear();
                            updateLinearLayout();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error al eliminar el documento", e);
                        }
                    });
                } else if (perfilUsuarioLogeado.equals("Cocinero")) {
                    Map<String, Object> actualizacion = new HashMap<>();
                    actualizacion.put("Perfil", "Camarero");

                    documentRef.update(actualizacion).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Documento actualizado correctamente");
                                    listaGestionComandas.clear();
                                    updateLinearLayout();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error al actualizar documento", e);
                                }
                            });
                }
            }
        });
    }

    private void updateLinearLayout() {

        myRef1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        int idRestauranteFirebase = document.getLong("idRestaurante").intValue();
                        if (idRestauranteFirebase == idRestauranteSesionIniciada) { //Extraemos aqui el nombre del restaurante con nombreRestaurante
                            String documentoID = document.getId();
                            String idTicket = "ticket/" + documentoID + "/lineaTicket/";
                            db.collection("ticket").document(documentoID).collection("lineaTicket").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot querySnapshot) {
                                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                        String idLineaTicket = idTicket + documentSnapshot.getId();
                                        //Filtramos aqui segun perfil del personal
                                        listaGestionComandas.add(documentSnapshot.toObject(Lineas_Ticket.class));
                                        listaGestionComandas.get(querySnapshot.getDocuments().indexOf(documentSnapshot)).setIdLineaTicket(idLineaTicket);
                                    }
                                    adaptadorGestionComandas.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

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
                        nombreRestaurante = snapshot.getString("Restaurante");
                        perfilUsuarioLogeado = snapshot.getString("Perfil");
                    }
                }
            }
        });

    }

}
