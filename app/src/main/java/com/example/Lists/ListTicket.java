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

import com.example.Forms.FormTicket;
import com.example.Lists.pojos.Departamento;
import com.example.Lists.pojos.Ticket;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListTicket extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference myRef = db.collection("tickets");
    CollectionReference collectionRef = db.collection("usuarios");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String restaurante;
    FirebaseUser currentUser;

    ActivityResultLauncher<Intent> activityForm;
    
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_tickets);
        getSupportActionBar().hide();

        ListView listview1;
        AdapterTicket adapter;
        ArrayList<Ticket> lista = new ArrayList<>();

        Ticket ticket;

        int pos = -1;

        ImageButton backButton = findViewById(R.id.backBtn);


        listview1 = (ListView) findViewById(R.id.listTickets);
        adapter = new AdapterTicket(ListTicket.this, lista);
        listview1.setAdapter(adapter);
        listview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
             Log.i("HelloListView", "You clicked Item: " + id + " at position:" + position);
             // Then you start a new Activity via Intent
             Intent intent = new Intent();
             intent.setClass(ListTicket.this, TicketAdmin.class);
             intent.putExtra("position", position);
             // Or / And
             intent.putExtra("id", id);
             startActivity(intent);
         }
     });


        // Mostrar tickets
        myRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Ticket t = document.toObject(Ticket.class);
                        lista.add(t);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d("ERROR", "No se pudo obtener la lista de tickets");
                }

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

    //COMPROBAR USUARIO
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
                        restaurante = snapshot.getString("Restaurante");
                    }
                }
            }
        });


    }



}