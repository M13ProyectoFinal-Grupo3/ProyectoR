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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.android.material.slider.Slider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TicketAdmin extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference ticketCollection = db.collection("tickets");

    Ticket ticketOriginal = new Ticket();

    ArrayList<Lineas_Ticket> arrayLineas = new ArrayList<>();

    float precioTotal = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_admin);
        getSupportActionBar().hide();

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

        TextView nombreRestaurante = (TextView) findViewById(R.id.tx_nombrerest2);
        nombreRestaurante.setText(ticketOriginal.getRestaurante().getNombre());

        if (ticketOriginal.getLineas_ticket() != null){
            if(!ticketOriginal.getLineas_ticket().isEmpty()){
                for (Lineas_Ticket linea : ticketOriginal.getLineas_ticket()) {
                    arrayLineas.add(linea);
                }

            }
        }

        // Definimos el adaptador
        AdapterLineaTicket lineasAdapter = new AdapterLineaTicket(this, (ArrayList) arrayLineas);
        // Attach the adapter to a ListView
        ListView lineasProductos = (ListView) findViewById(R.id.lvListaDeLineas);
        lineasProductos.setAdapter(lineasAdapter);

        for (Lineas_Ticket linea : arrayLineas) {
            precioTotal += (linea.getProducto().getPrecio() * linea.getCantidad());
        }

        //TODO - que se actualice al cambiar las cantidades
        TextView xTotal = (TextView) findViewById(R.id.tvPrecioFinal);
        xTotal.setText(""+precioTotal+"");

        ImageButton backButton = findViewById(R.id.backBtn);

        Button btnGenQR = (Button) findViewById(R.id.mostrarQr);

        Button btnFinalizar = (Button) findViewById(R.id.btnFinTicket);

        Button btnGuardar = (Button) findViewById(R.id.btnGuardar);

        /*
        lineasAdapter.setOnClickListener(new AdapterLineaTicket.OnClickListener() {
            @Override
            public void onClick(int position, Ticket ticket) {
                for (Lineas_Ticket linea : arrayLineas) {
                    precioTotal += (linea.getProducto().getPrecio() * linea.getCantidad());
                    xTotal.setText(""+precioTotal+"");
                }
                lineasAdapter.notifyDataSetChanged();
            }
        });
        */

        /*
        lineasProductos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (Lineas_Ticket linea : arrayLineas) {
                    precioTotal += (linea.getProducto().getPrecio() * linea.getCantidad());
                    xTotal.setText("" + precioTotal + "");
                }
            }
        });
        */


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
                ticketCollection.document(ticketOriginal.getId()).update("lineas_ticket", arrayLineas).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(TicketAdmin.this, "Ticket modificado", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        }));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }



}