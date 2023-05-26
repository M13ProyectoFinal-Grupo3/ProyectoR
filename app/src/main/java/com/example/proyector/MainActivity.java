package com.example.proyector;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.Lists.pojos.Ticket;
import com.example.Lists.pojos.Usuarios;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class MainActivity extends AppCompatActivity{
    FragmentManager fm = getSupportFragmentManager();
    String RestId=null;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference myRef = db.collection("tickets");

    Ticket ticket1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();


        ImageButton btnAccess = (ImageButton) findViewById(R.id.btnLoginProf);
        ImageView imageView1 = (ImageView) findViewById(R.id.imageView1);


        // acceso profesional
        btnAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // acceso usuario - leer QR
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // leer el primer ticket para test
                myRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ticket1 = task.getResult().toObjects(Ticket.class).get(0);
                            Intent intent = new Intent(MainActivity.this, CartaCliente.class);
                            intent.putExtra("ticket", ticket1);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "Ticket no reconocido", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                /* escanear qr
                ScanOptions options = new ScanOptions();
                options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
                options.setPrompt("ESCANEAR QR");
                options.setCameraId(0);
                options.setOrientationLocked(false);
                options.setBeepEnabled(true);
                options.setCaptureActivity(CapActivity.class);
                options.setBarcodeImageEnabled(false);
                barcodeLauncher.launch(options);
                */
            }
        });

    }

    // acceso cliente - leer qr
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(), result-> {
                if (result.getContents() != null) {
                    String nTicket = result.getContents();
                    myRef.whereEqualTo("id", nTicket)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        ticket1 = task.getResult().toObjects(Ticket.class).get(0);
                                        Intent intent = new Intent(MainActivity.this, CartaCliente.class);
                                        intent.putExtra("ticket", ticket1);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(MainActivity.this, "Ticket no reconocido", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            });


}