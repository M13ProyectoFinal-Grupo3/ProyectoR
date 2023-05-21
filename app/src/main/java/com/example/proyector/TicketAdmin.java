package com.example.proyector;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.Forms.FormTicket;
import com.example.Lists.ListTicket;
import com.example.Lists.pojos.Lineas_Ticket;
import com.example.Lists.pojos.Producto;
import com.example.Lists.pojos.Restaurante;
import com.example.Lists.pojos.Ticket;
import com.example.adapters.AdapterLineaTicket;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TicketAdmin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_admin);

        Restaurante restPrueba = new Restaurante("Bar Paco", "Comedor Social SL", "123456789k", "Barcelona", "Vilanova", "08720", "+34123456789");

        Producto producto1 = new Producto("Cocacola", 2f);
        Producto producto2 = new Producto("Patatas", 4f);
        Producto producto3 = new Producto("Olivas", 2f);
        Producto producto4 = new Producto("Jamon", 8f);
        Producto producto5 = new Producto("Anchoas", 2f);

        Lineas_Ticket linea1 = new Lineas_Ticket(producto1, 1);
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

        Date fechaPrueba = new Date();

        Ticket ticketPrueba = new Ticket(restPrueba, "idxffffff", fechaPrueba, 4, 4, listaLineas);


        // Definimos el adaptador frutas
        AdapterLineaTicket lineasAdapter = new AdapterLineaTicket(this, (ArrayList) ticketPrueba.getLineas_ticket());

        // Attach the adapter to a ListView
        ListView lineasProductos = (ListView) findViewById(R.id.lvListaDeLineas);
        lineasProductos.setAdapter(lineasAdapter);

        //TODO - getIntent

        Button btnGenQR = (Button) findViewById(R.id.mostrarQr);

        btnGenQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setClass(TicketAdmin.this, VerQR.class);
                //intent.putExtra("id", id); id con numero mesa que venga del primer intent
                startActivity(intent);

                //TODO - hacerlo como un dialog?
            }
        });

    }


}