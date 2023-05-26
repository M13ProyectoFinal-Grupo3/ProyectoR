package com.example.Forms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Lists.pojos.Lineas_Ticket;
import com.example.Lists.pojos.Mesa;
import com.example.Lists.pojos.Producto;
import com.example.Lists.pojos.Restaurante;
import com.example.Lists.pojos.Ticket;
import com.example.Lists.pojos.Usuarios;
import com.example.adapters.AdapterMesa;
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

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class FormTicket extends AppCompatActivity {

    FirebaseUser currentUser;

    final String OLD_FORMAT = "dd/MM/yyyy";

    Usuarios usuario;
    Restaurante restauranteFB;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference ticketCollection = db.collection("tickets");

    CollectionReference collectionRef = db.collection("usuarios");
    CollectionReference collectionRest = db.collection("restaurante");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    ArrayList<String> listaMesas = new ArrayList<>();
    ArrayAdapter<String> adapter;

    Spinner mesas;

    Ticket ticket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_ticket);
        getSupportActionBar().hide();

        /*
        Producto producto1 = new Producto("Cocacola", 2f);
        Producto producto2 = new Producto("Patatas", 4f);
        Producto producto3 = new Producto("Olivas", 2f);
        Producto producto4 = new Producto("Jamon", 8f);
        Producto producto5 = new Producto("Anchoas", 2f);

        Lineas_Ticket linea1 = new Lineas_Ticket(producto1, 2);
        Lineas_Ticket linea2 = new Lineas_Ticket(producto2, 2);
        Lineas_Ticket linea3 = new Lineas_Ticket(producto3, 2);
        Lineas_Ticket linea4 = new Lineas_Ticket(producto4, 2);
        Lineas_Ticket linea5 = new Lineas_Ticket(producto5, 2);

        List<Lineas_Ticket> listaLineas = new ArrayList<Lineas_Ticket>();
        listaLineas.add(linea1);
        listaLineas.add(linea2);
        listaLineas.add(linea3);
        listaLineas.add(linea4);
        listaLineas.add(linea5);
         */

        //inicializamos las variables
        mesas = findViewById(R.id.spMesa);

        adapter = new ArrayAdapter<String>(FormTicket.this, android.R.layout.simple_spinner_dropdown_item, listaMesas);
        mesas.setAdapter(adapter);

        Button btnGuardar = (Button) findViewById(R.id.btnGuardar);
        btnGuardar.setEnabled(false);

        ImageButton backButton = findViewById(R.id.backBtn);

        currentUser = mAuth.getCurrentUser();

        collectionRef.whereEqualTo("UID", currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    usuario = task.getResult().getDocuments().get(0).toObject(Usuarios.class);
                    collectionRest.document(usuario.getRestaurante()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                restauranteFB = task.getResult().toObject(Restaurante.class);
                                collectionRest.document(usuario.getRestaurante()).collection("mesa").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()){
                                            for(DocumentSnapshot document : task.getResult()){
                                                Mesa mesa = document.toObject(Mesa.class);
                                                listaMesas.add(mesa.getNum_mesa());
                                            }
                                            ticketCollection.whereEqualTo("restaurante.id",restauranteFB.getId())
                                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if(task.isSuccessful()){
                                                                for (DocumentSnapshot document : task.getResult()) {
                                                                    Ticket t = document.toObject(Ticket.class);
                                                                    Iterator it = listaMesas.iterator();
                                                                    while(it.hasNext()){
                                                                        Object m = it.next();
                                                                        if(m.equals(t.getNum_mesa())){
                                                                            listaMesas.remove(m);
                                                                            break;
                                                                        }
                                                                    }
                                                                }
                                                                if (listaMesas.size() == 0){
                                                                    Toast.makeText(FormTicket.this, "Todas las mesas estan ocupadas.", Toast.LENGTH_SHORT).show();
                                                                    finish();
                                                                }else {
                                                                    btnGuardar.setEnabled(true);
                                                                    adapter.notifyDataSetChanged();
                                                                }
                                                            }
                                                        }
                                                    });
                                            }
                                        }
                                });

                            } else {
                                Log.d("ERROR", "No se pudo obtener la lista de mesas");
                            }
                        }
                    }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            //cargar nombre restaurante logeado
                            TextView restaurante = (TextView) findViewById(R.id.tvNombreRest);
                            restaurante.setText(restauranteFB.getNombre());

                            //cargar nif restaurante logeado
                            TextView nif = (TextView) findViewById(R.id.tvNifRest);
                            nif.setText(restauranteFB.getNif());

                            //añadir fecha automáticamente
                            TextView fecha = (TextView) findViewById(R.id.tvFecha);
                            SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
                            fecha.setText(sdf.format(new Date()));
                        }
                    });

                }
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ticket = new Ticket();
                ticket.setNum_mesa(mesas.getSelectedItem().toString());
                ticket.setFecha(new Date());
                ticket.setRestaurante(restauranteFB);
                ticket.setId_camarero(4);
                //ticket.setLineas_ticket(listaLineas);
                // Nuevo Ticket
                ticketCollection.add(ticket).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        ticket.setId(task.getResult().getId());
                        ticketCollection.document(ticket.getId()).update("id", ticket.getId());
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("new", ticket);
                        setResult(RESULT_OK, resultIntent);
                        Toast.makeText(FormTicket.this, "El Ticket se añadio correctamente", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
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