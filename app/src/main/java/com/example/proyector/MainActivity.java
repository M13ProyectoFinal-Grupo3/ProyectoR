package com.example.proyector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.Forms.FormCarta;
import com.example.Lists.pojos.User;
import com.example.Lists.pojos.Ticket;

public class MainActivity extends AppCompatActivity implements AccesoQR.ifAccesoQR {
    FragmentManager fm = getSupportFragmentManager();
    String RestId=null;

    ImageView imageView;

    Ticket ticket1;
    User user1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button btnAccess = (Button) findViewById(R.id.btnAcceso);
        Button btnLeerQR = (Button) findViewById(R.id.btnLeerQR);

        fm.setFragmentResultListener("request", MainActivity.this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                user1 = result.getSerializable("user", User.class);
                ticket1 = result.getSerializable("ticket", Ticket.class);
                if (user1 != null) {
                    //cargar fragment menu profesional
                }
            }
        });

        loadFragment(new AccesoQR());

        btnAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new Profesional());
            }
        });

        btnLeerQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new AccesoQR());
            }
        });

    }


    private void loadFragment(Fragment fragment) {
    // create a FragmentTransaction to begin the transaction and replace the Fragment
        fm.beginTransaction()
            .replace(R.id.frameLayout1, fragment)
            .commit(); // save the changes
    }

    @Override
    public void if_btnCarta() {
        ticket1 = new Ticket("");
        Intent intent = new Intent(this, CartaCliente.class);
        intent.putExtra("ticket",ticket1);
        startActivity(intent);
    }
}


/*
// Create a new user with a first and last name

Map<String, Object> user = new HashMap<>();
user.put("first", "Ada");
user.put("last", "Lovelace");
user.put("born", 1815);

// Add a new document with a generated ID
db.collection("users")
        .add(user)
        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error adding document", e);
            }
        });



        // Create a new user with a first, middle, and last name
Map<String, Object> user = new HashMap<>();
user.put("first", "Alan");
user.put("middle", "Mathison");
user.put("last", "Turing");
user.put("born", 1912);

// Add a new document with a generated ID
db.collection("users")
        .add(user)
        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error adding document", e);
            }
        });

        //READ DATA

        db.collection("users")
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
 */