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
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Lists.ListMesa;
import com.example.Lists.ListRestaurante;
import com.example.Lists.pojos.Mesa;
import com.example.Lists.pojos.Restaurante;
import com.example.Lists.pojos.Ticket;
import com.example.adapters.AdapterTicket;
import com.example.proyector.R;
import com.example.proyector.RegistroActivity;
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
    CollectionReference myRef = db.collection("tickets");
    static String coleccion = "tickets";
    static String documento = "mesa";
    Ticket a_anterior = null;
    Ticket a_nuevo = null;
    Mesa a_nuevo2 = null;

    Boolean btnBorrarHabilitado = true;

    AdapterTicket adaptador;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_ticket);
        getSupportActionBar().hide();

        //cargar nombre restaurante logeado
        TextView restaurante = (TextView) findViewById(R.id.tvNombreRest);
        //restaurante.setText();

        //cargar nif restaurante logeado
        TextView nif = (TextView) findViewById(R.id.tvNifRest);
        //nif.setText();

        //añadir fecha automáticamente
        TextView fecha = (TextView) findViewById(R.id.tvFecha);
        String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
        fecha.setText(currentDateTimeString);



        Button btnGuardar = (Button) findViewById(R.id.btnGuardar);
        Button btnBorrar = (Button) findViewById(R.id.btnBorrar);

        Spinner mesas;
        mesas = (Spinner) findViewById(R.id.spMesa);
        //adaptador = new AdapterTicket(this,android.R.layout.simple_spinner_item, mesas);

        mesas.setAdapter(adaptador);

        ImageButton backButton = findViewById(R.id.backBtn);

        //generacion qr. TODO falta que sea solo al seleccionar una mesa. está empezado más abajo pero no puedo rellenar el spinner
        ImageView imageView1= (ImageView) findViewById(R.id.imageQR);
        imageView1.setImageBitmap(generateQRCodeImage("12324567890"));


        //TODO - cambiar los size??
        Intent intent = getIntent();
        if (intent.getExtras() != null && intent.getExtras().size() > 3) {
            a_anterior = (Ticket) getIntent().getExtras().get("ticket");
            btnBorrarHabilitado = (Boolean) getIntent().getExtras().get("btnBorrarHabilitado");
        } else if (intent.getExtras() != null && intent.getExtras().size() < 4) {
            btnBorrarHabilitado = (Boolean) getIntent().getExtras().get("btnBorrarHabilitado");
        }

        if (btnBorrarHabilitado == false) {
            btnBorrar.setVisibility(View.INVISIBLE);
        }


        //le indicamos lo que tiene que hacer cada vez que el spinner cambie de posición
        mesas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //TODO - hacer que los cases sean = al numero de mesas del rest. es posible?
                switch (position) {
                    case 0:

                        break;

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


/*
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                a_nuevo = new Ticket();
                // Actualizar restaurante o añadir nuevo
                if (a_anterior != null) {
                    // Actualizar
                    myRef.whereEqualTo("nombre", a_anterior.getNombre())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().getDocuments().size() > 0) {
                                            DocumentSnapshot d = task.getResult().getDocuments().get(task.getResult().size() - 1);
                                            myRef.document(d.getId()).update("nombre", a_nuevo.getNombre());
                                            myRef.document(d.getId()).update("razonSocial", a_nuevo.getRazonSocial());
                                            myRef.document(d.getId()).update("nif", a_nuevo.getNif());
                                            myRef.document(d.getId()).update("provincia", a_nuevo.getProvincia());
                                            myRef.document(d.getId()).update("ciudad", a_nuevo.getCiudad());
                                            myRef.document(d.getId()).update("codPostal", a_nuevo.getCodPostal());
                                            myRef.document(d.getId()).update("telefono", a_nuevo.getTelefono()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(FormTicket.this, "El Restaurante se modificó correctamente", Toast.LENGTH_SHORT).show();
                                                    Intent resultIntent = new Intent();
                                                    resultIntent.putExtra("update", a_nuevo);
                                                    Log.d("return update", "ok");
                                                    setResult(RESULT_OK, resultIntent);
                                                    finish();
                                                }
                                            });
                                        } else {
                                            Log.d(TAG, "Restaurante no encontrado para su modificación");
                                        }
                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                    }
                                }
                            });

                } else {
                    // Nuevo Restaurante
                    DocumentReference idDocumento = db.collection(coleccion).document("ID"+a_nuevo.getNombre());
                    idDocumento.set(a_nuevo).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("new", a_nuevo);
                            setResult(RESULT_OK, resultIntent);
                            Toast.makeText(FormTicket.this, "El Restaurante se añadió correctamente", Toast.LENGTH_SHORT).show();
                            finish();
                            Intent intent = new Intent(FormTicket.this, ListRestaurante.class);
                            startActivity(intent);
                        }
                    });
                }
            }
        });

        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.whereEqualTo("nombre", a_anterior.getNombre())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().getDocuments().size() > 0) {
                                        DocumentSnapshot d = task.getResult().getDocuments().get(task.getResult().size() - 1);
                                        myRef.document(d.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(FormTicket.this, "El Restaurante ha sido eliminado correctamente", Toast.LENGTH_SHORT).show();
                                                Intent resultIntent = new Intent();
                                                resultIntent.putExtra("delete", a_anterior);
                                                setResult(RESULT_OK, resultIntent);
                                                Intent intent = new Intent(FormTicket.this, ListRestaurante.class);
                                                startActivity(intent);
                                            }
                                        });
                                    } else {
                                        Log.d(TAG, "Restaurante no encontrado para su modificación");
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });

        btnMesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FormTicket.this, ListMesa.class);
                intent.putExtra("nombreRestaurante", a_anterior.getNombre());
                startActivity(intent);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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