package com.example.proyector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Forms.FormTicket;
import com.example.Lists.ListTicket;
import com.example.Lists.pojos.Lineas_Ticket;
import com.example.Lists.pojos.Producto;
import com.example.Lists.pojos.Restaurante;
import com.example.Lists.pojos.Ticket;
import com.example.adapters.AdapterLineaTicket;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TicketAdmin extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference ticketCollection = db.collection("tickets");

    Ticket ticketOriginal;

    ArrayList<Lineas_Ticket> arrayLineas = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_admin);

        // recupera ticket a editar
        Intent intent = getIntent();
        if(intent.getExtras()!=null) {
            if (intent.getExtras().containsKey("ticket")) {
                ticketOriginal = new Ticket();
                ticketOriginal = intent.getSerializableExtra("ticket", Ticket.class);
            }
        }else {
            Toast.makeText(this, "Ticket erróneo", Toast.LENGTH_SHORT).show();
            finish();
        }

        Date fechaPrueba = new Date();

        float precioTotal = 0f;

        //TODO - que se actualice al cambiar las cantidades
        TextView xTotal = (TextView) findViewById(R.id.tvPrecioFinal);
        xTotal.setText(""+precioTotal+"");

        for (Lineas_Ticket linea : ticketOriginal.getLineas_ticket()) {
            arrayLineas.add(linea);
        }

        // Definimos el adaptador frutas
        AdapterLineaTicket lineasAdapter = new AdapterLineaTicket(this, (ArrayList) arrayLineas);

        // Attach the adapter to a ListView
        ListView lineasProductos = (ListView) findViewById(R.id.lvListaDeLineas);
        lineasProductos.setAdapter(lineasAdapter);

        for (Lineas_Ticket linea : arrayLineas) {
            precioTotal += (float) linea.getProducto().getPrecio();
        }

        Button btnGenQR = (Button) findViewById(R.id.mostrarQr);

        Button btnFinalizar = (Button) findViewById(R.id.btnFinTicket);

        Button btnGuardar = (Button) findViewById(R.id.btnGuardar);


        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DELETE", ticketOriginal.getId());
                ticketCollection.document(ticketOriginal.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(TicketAdmin.this, "Ticket finalizado", Toast.LENGTH_SHORT).show();
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("delete", ticketOriginal);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
                });
            }
        });

        btnGenQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(TicketAdmin.this, VerQR.class);
                intent.putExtra("ticket", ticketOriginal);
                startActivity(intent);
            }
        });

        btnGuardar.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("GUARDAR", arrayLineas.toString());
                ticketCollection.document(ticketOriginal.getId()).update("lineas_ticket", arrayLineas).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //ticketOriginal.setLineas_ticket((arrayLineas));
                        Toast.makeText(TicketAdmin.this, "Ticket modificado", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        }));


    }


}


/*
PARA ELIMINAR:
necesito el id de la linea de ticket (para borrar lineas) y del ticket (para borrar el ticket entero)

 */