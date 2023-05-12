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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Lists.pojos.Alergeno;
import com.example.Lists.pojos.Ticket;
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

import java.util.Date;

public class FormTicket extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference ticketCollection = db.collection("tickets");
    CollectionReference restauranteCollection = db.collection("restaurante");

    String nombreRest;
    String nifRest;
    String fecha;

    Ticket t_anterior = null;
    Ticket t_nuevo = null;

    AdapterTicket adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_ticket);
        getSupportActionBar().hide();

        //cargar nombre restaurante logeado
        TextView restaurante = (TextView) findViewById(R.id.tvNombreRest);
        //restaurante.setText();

        //cargar nif restaurante logeado TODO
        TextView nif = (TextView) findViewById(R.id.tvNifRest);
        //nif.setText(restauranteCollection.whereEqualTo(""));

        //añadir fecha automáticamente
        TextView fecha = (TextView) findViewById(R.id.tvFecha);
        String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
        fecha.setText(currentDateTimeString);

        Spinner mesas;
        mesas = (Spinner) findViewById(R.id.spMesa);
        //adaptador = new AdapterTicket(this,android.R.layout.simple_spinner_item, mesas);

        Button btnGuardar = (Button) findViewById(R.id.btnGuardar);
        ImageButton btnBorrar = (ImageButton) findViewById(R.id.btnBorrar);

        mesas.setAdapter(adaptador);

        ImageButton backButton = findViewById(R.id.backBtn);

        //generacion qr. TODO falta que sea solo al seleccionar una mesa. meterlo en la funcion de debajo
        ImageView imageView1= (ImageView) findViewById(R.id.imageQR);
        imageView1.setImageBitmap(generateQRCodeImage("12324567890"));

        //le indicamos lo que tiene que hacer cada vez que el spinner cambie de posición. TODO
        mesas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //TODO - hacer que los cases sean = al numero de mesas del rest. es posible?
                switch (position) {
                    case 0:
                        //nada
                        break;
                    case 1:
                        //generar qr con mesa 1
                        break;
                    case 2:
                        //generar qr con mesa 2
                        break;

                        //....

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //una vez seleccionada la mesa. TODO Es necesario esto??
        Intent intent = getIntent();
        if(intent.getExtras()!=null) {
            if (intent.getExtras().containsKey("ticket")) {
                t_anterior = getIntent().getExtras().getSerializable("ticket", Ticket.class);
            }
        } else {
            t_anterior = null;
        }
/*
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cargar mesa del spinner seleccionada por el user TODO

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
                    // Nuevo Alergeno
                    ticketCollection.add(t_nuevo).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            t_nuevo.setId(task.getResult().getId());
                            ticketCollection.document(t_nuevo.getId()).update("id", t_nuevo.getId());
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("new", t_nuevo);
                            setResult(RESULT_OK, resultIntent);
                            Toast.makeText(FormAlergenos.this, "El Alergeno se añadio correctamente", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }


            }
        });
*/




/*
        btnRegistar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FormTicket.this, RegistroActivity.class);
                intent.putExtra("perfilUsuario", "administrador");
                intent.putExtra("nombreRestaurante", a_anterior.getNombre());
                startActivity(intent);
            }
        });
*/

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // genera codigo QR
    private Bitmap generateQRCodeImage(String text) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        Bitmap bmp = null;
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,500,500);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bmp = barcodeEncoder.createBitmap(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return bmp;
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