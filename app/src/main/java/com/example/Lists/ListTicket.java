package com.example.Lists;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.Forms.FormTicket;
import com.example.Lists.pojos.Departamento;
import com.example.Lists.pojos.Restaurante;
import com.example.Lists.pojos.Ticket;
import com.example.Lists.pojos.Usuarios;
import com.example.adapters.AdapterCartaDep;
import com.example.adapters.AdapterTicket;
import com.example.proyector.R;
import com.example.proyector.TicketAdmin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListTicket extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionTickets = db.collection("tickets");
    CollectionReference collectionRef = db.collection("usuarios");
    CollectionReference collectionRest = db.collection("restaurante");

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser;
    String perfilUsuarioLogeado;
    String nombreRestaurante;

    Usuarios usuario;
    Restaurante restaurante;

    ActivityResultLauncher<Intent> activityForm;

    ArrayList<Ticket> lista = new ArrayList<>();

    AdapterTicket adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_tickets);
        getSupportActionBar().hide();

        ListView listview1;



        int pos = -1;

        ImageButton backButton = findViewById(R.id.backBtn);

        // TODO - getIntent

        listview1 = (ListView) findViewById(R.id.listTickets);
        adapter = new AdapterTicket(ListTicket.this, lista);
        listview1.setAdapter(adapter);

        currentUser = mAuth.getCurrentUser();

        collectionRef.whereEqualTo("UID", currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    usuario = task.getResult().getDocuments().get(0).toObject(Usuarios.class);
                    collectionRest.document(usuario.getRestaurante()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                restaurante = task.getResult().toObject(Restaurante.class);
                                Log.d("PRUEBA 1 - ", restaurante.toString());

                                collectionTickets.whereEqualTo("restaurante.id", restaurante.getId())
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()){
                                                    for (QueryDocumentSnapshot doc : task.getResult()){
                                                        Ticket t = doc.toObject(Ticket.class);
                                                        lista.add(t);
                                                    }
                                                } else {
                                                    Toast.makeText(ListTicket.this, "No hay tickets disponibles", Toast.LENGTH_SHORT).show();
                                                }
                                                adapter.notifyDataSetChanged();
                                            }
                                        });
                            } else {
                                Toast.makeText(ListTicket.this, "No se puede obtener la lista de restaurantes", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });



        listview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
             Intent intent = new Intent();
             intent.setClass(ListTicket.this, TicketAdmin.class);
             intent.putExtra("position", position);
             intent.putExtra("id", id);
             intent.putExtra("ticketId", lista.get(position).getId());
             intent.putExtra("ticket", lista.get(position));
             startActivity(intent);
         }
     });



        // form ticket
        activityForm = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent intent = result.getData();
                            if (intent.getSerializableExtra("update", Ticket.class) != null) {
                                Ticket t_dev = (Ticket) intent.getSerializableExtra("update", Ticket.class);
                                lista.set(pos, t_dev);
                                adapter.notifyDataSetChanged();
                            } else if (intent.getSerializableExtra("new", Ticket.class) != null) {
                                Ticket t_dev = (Ticket) intent.getSerializableExtra("new", Ticket.class);
                                lista.add(t_dev);
                                adapter.notifyDataSetChanged();
                            } else if (intent.getSerializableExtra("delete", Ticket.class) != null) {
                                Ticket t_dev = (Ticket) intent.getSerializableExtra("delete", Ticket.class);
                                lista.remove(t_dev);
                                adapter.notifyDataSetChanged();
                            }

                        }
                    }
                });



        // Nuevo ticket
        Button btnNuevo = (Button) findViewById(R.id.btnNuevo);
        btnNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListTicket.this, FormTicket.class);
                //TODO - putExtra
                activityForm.launch(intent);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

}




     /*
        collectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot snapshot : snapshotList) {
                        nombreRestaurante = snapshot.getString("Restaurante");
                        perfilUsuarioLogeado = snapshot.getString("Perfil");

                }
            }
        });*/
