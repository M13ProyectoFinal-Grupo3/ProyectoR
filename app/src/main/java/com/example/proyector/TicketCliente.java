package com.example.proyector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Lists.ListTicket;
import com.example.Lists.pojos.Lineas_Ticket;
import com.example.Lists.pojos.Ticket;
import com.example.adapters.AdapterLineaTicket;
import com.example.adapters.AdapterLineaTicketCliente;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class TicketCliente extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionTickets = db.collection("tickets");

    Ticket ticketOriginal = new Ticket();

    ArrayList<Lineas_Ticket> arrayLineas = new ArrayList<>();

    float precioTotal = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_cliente);
        getSupportActionBar().hide();

        // recupera ticket a editar
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            if (intent.getExtras().containsKey("ticket")) {
                ticketOriginal = intent.getSerializableExtra("ticket", Ticket.class);
            }
        } else {
            Toast.makeText(this, "Ticket erróneo", Toast.LENGTH_SHORT).show();
            finish();
        }

        TextView nombreRestaurante = (TextView) findViewById(R.id.tx_nombrerest2);
        nombreRestaurante.setText(ticketOriginal.getRestaurante().getNombre());

        if (ticketOriginal.getLineas_ticket() != null) {
            if (!ticketOriginal.getLineas_ticket().isEmpty()) {
                for (Lineas_Ticket linea : ticketOriginal.getLineas_ticket()) {
                    arrayLineas.add(linea);
                }
            }
        }

        // Definimos el adaptador
        AdapterLineaTicketCliente lineasAdapter = new AdapterLineaTicketCliente(this, arrayLineas);
        // Attach the adapter to a ListView
        ListView lineasProductos = (ListView) findViewById(R.id.lvListaDeLineas);
        lineasProductos.setAdapter(lineasAdapter);

        for (Lineas_Ticket linea : arrayLineas) {
            precioTotal += (linea.getProducto().getPrecio() * linea.getCantidad());
        }

        Log.d("COMPROBARRRR", precioTotal+"");

        TextView xTotal = (TextView) findViewById(R.id.tvPrecioFinal);
        xTotal.setText("" + precioTotal + "");

        ImageButton backButton = findViewById(R.id.backBtn);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

}