package com.example.Forms;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Lists.ListTicket;
import com.example.Lists.pojos.Alergeno;
import com.example.Lists.pojos.Departamento;
import com.example.Lists.pojos.Lineas_Ticket;
import com.example.Lists.pojos.Mesa;
import com.example.Lists.pojos.Restaurante;
import com.example.Lists.pojos.Ticket;
import com.example.adapters.AdapterMesa;
import com.example.adapters.AdapterTicket;
import com.example.proyector.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class FormTicket extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference ticketCollection = db.collection("tickets");
    CollectionReference restauranteCollection = db.collection("restaurante");

    String nombreRest;
    String nifRest;
    String fecha;

    Ticket t_anterior = null;
    Ticket t_nuevo = null;

    Restaurante restauranteFB;

    //AdapterTicket adaptador; // TODO - necesario?
    ArrayAdapter<Integer> adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_ticket);
        getSupportActionBar().hide();

        /*///// TODO - cargar restaurante logeado
        Intent intent = getIntent();
        if (intent.getExtras() != null &&
            (intent.getStringExtra("perfilUsuario").equals("administrador") ||
            intent.getStringExtra("perfilUsuario").equals("camarero"))) {
            restauranteFB = getIntent().getExtras().getSerializable("restaurante", Restaurante.class);
            // o
            //nombreRestaurante = intent.getStringExtra("nombreRestaurante");
        }else{
            //opciones = new String[]{"Camarero", "Cocinero"};
        }

        /////*/

        //cargar nombre restaurante logeado
        TextView restaurante = (TextView) findViewById(R.id.tvNombreRest);
        //restaurante.setText(restauranteFB.getNombre());

        //cargar nif restaurante logeado TODO
        TextView nif = (TextView) findViewById(R.id.tvNifRest);
        //nif.setText(restauranteFB.getNif());

        //añadir fecha automáticamente
        TextView fecha = (TextView) findViewById(R.id.tvFecha);
        Date fechaPrueba = new Date();
        fecha.setText(""+fechaPrueba+"");


        Restaurante restPrueba = new Restaurante("Bar Paco", "Comedor Social SL", "123456789k", "Barcelona", "Vilanova", "08720", "+34123456789");
        Ticket ticketPrueba = new Ticket(restPrueba, "idxffffff", fechaPrueba, 4, 4);


        ListView listviewMesas;
        AdapterMesa adapter;
        ArrayList<Mesa> lista = new ArrayList<>();

        listviewMesas = (ListView) findViewById(R.id.listaMesas);
        adapter = new AdapterMesa(FormTicket.this, lista);
        listviewMesas.setAdapter(adapter);


            //definimos el desplegable. PROVISIONAL
            Spinner mesas;
            List<Integer> listaMesas = new ArrayList<>();
            //TODO - meter aquícodigo para recuperar nº de mesas. candidadMesas = lo que venga de firebase
            int cantidadMesas = 8; // 8 solo para pruebas, será lo de firebase
            for (int i = 1; i <= cantidadMesas; i++) {
                listaMesas.add(i);
            }
            //revertimos el orden de la lista, que por defecto es de mayor a menor
            //Collections.reverse(listaMesas);
            adaptador = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, listaMesas);

            //inicializamos las variables
            mesas = findViewById(R.id.spMesa);
            mesas.setAdapter(adaptador);

            Button btnGuardar = (Button) findViewById(R.id.btnGuardar);
            Button btnBorrar = (Button) findViewById(R.id.btnBorrar);

            ImageButton backButton = findViewById(R.id.backBtn);

            //le indicamos lo que tiene que hacer cada vez que el spinner cambie de posición. TODO
            mesas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    int numeroMesa = position + 1;

                    ticketPrueba.setNum_mesa(numeroMesa);


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            btnGuardar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //cargar mesa del spinner seleccionada por el user TODO
/*
                // Actualizar ticket o añadir nuevo
                if(t_anterior !=null) {
                    // Actualizar
                    ticketCollection.whereEqualTo("nombre", t_anterior.getNombre())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if(task.getResult().getDocuments().size()>0) {
                                            DocumentSnapshot d = task.getResult().getDocuments().get(task.getResult().size()-1);
                                            ticketCollection.document( d.getId()).update("nombre", t_nuevo.getNombre()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(FormAlergenos.this, "El Alergeno se modificó correctamente", Toast.LENGTH_SHORT).show();
                                                    Intent resultIntent = new Intent();
                                                    resultIntent.putExtra("update", t_nuevo);
                                                    Log.d("return update","ok");
                                                    setResult(RESULT_OK, resultIntent);
                                                    finish();
                                                }
                                            });

                                        } else {
                                            Log.d(TAG,"Documento Alergeno no econtrado para su modificación");
                                        }
                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                    }
                                }
                            });

                } else {
                    */
                    // Nuevo Ticket
                    ticketCollection.add(ticketPrueba).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            ticketPrueba.setId(task.getResult().getId());
                            ticketCollection.document(ticketPrueba.getId()).update("id", ticketPrueba.getId());
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("new", ticketPrueba);
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










/* codigo viejo, por si acaso hay que recuperarlo


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

 */