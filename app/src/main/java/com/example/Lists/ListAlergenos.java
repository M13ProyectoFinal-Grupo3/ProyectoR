package com.example.Lists;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.Forms.FormAlergenos;
import com.example.adapters.AdapterAlergeno;
import com.example.Lists.pojos.Alergeno;
import com.example.proyector.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ListAlergenos extends AppCompatActivity {
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    CollectionReference myRef = db.collection("alergenos");

    ListView listview1;
    ArrayList<Alergeno> lista = new ArrayList<>();
    AdapterAlergeno adapter;
    Integer pos=0;

    ActivityResultLauncher<Intent> activityForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_alergenos);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        listview1 = (ListView) findViewById(R.id.list_productos);
        adapter = new AdapterAlergeno(ListAlergenos.this, lista);
        listview1.setAdapter(adapter);


        myRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        lista.add(document.toObject(Alergeno.class));
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        // form alergeno
        activityForm = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent intent = result.getData();
                            if (intent.getSerializableExtra("update", Alergeno.class) != null) {
                                Alergeno a_dev = (Alergeno) intent.getSerializableExtra("update", Alergeno.class);
                                Log.d("update", pos + " " + a_dev.getNombre());
                                lista.set(pos, a_dev);
                                adapter.notifyDataSetChanged();
                            } else if (intent.getSerializableExtra("new", Alergeno.class) != null) {
                                Alergeno a_dev = (Alergeno) intent.getSerializableExtra("new", Alergeno.class);
                                Log.d("new", pos + " " + a_dev.getNombre());
                                lista.add(a_dev);
                                adapter.notifyDataSetChanged();
                            } else if (intent.getSerializableExtra("delete", Alergeno.class) != null) {
                                Alergeno a_dev = (Alergeno) intent.getSerializableExtra("delete", Alergeno.class);
                                Log.d("delete", pos + " " + a_dev.getNombre());
                                lista.remove(a_dev);
                                adapter.notifyDataSetChanged();
                            }

                        }
                    }
                });

        // editar alérgeno
        listview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
                Intent intent = new Intent(ListAlergenos.this, FormAlergenos.class);
                intent.putExtra("alergeno", lista.get(position));
                activityForm.launch(intent);
            }
        });

        // Nuevo alergenos
        Button btnNuevo = (Button) findViewById(R.id.btn_listaNuevo);
        btnNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListAlergenos.this, FormAlergenos.class);
                activityForm.launch(intent);
            }
        });
    }
}