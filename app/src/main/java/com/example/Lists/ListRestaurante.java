package com.example.Lists;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Forms.FormRestaurante;
import com.example.adapters.AdapterRestaurante;
import com.example.Lists.pojos.Restaurante;
import com.example.proyector.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ListRestaurante extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference myRef = db.collection("restaurante");

    ListView listview1;
    ArrayList<Restaurante> lista = new ArrayList<>();
    AdapterRestaurante adapter;
    Integer pos = 0;

    ActivityResultLauncher<Intent> activityForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_restaurante);

        listview1 = (ListView) findViewById(R.id.list_restaurantes);
        adapter = new AdapterRestaurante(ListRestaurante.this, lista);
        listview1.setAdapter(adapter);

        listview1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
                Intent intent = new Intent(ListRestaurante.this, FormRestaurante.class); //cambiar a listrestaurante
                intent.putExtra("restaurante", lista.get(position));
                intent.putExtra("btnBorrarHabilitado", true);
                intent.putExtra("btnAgregarMesaHabilitado", true);
                activityForm.launch(intent);
                return false;
            }
        });

        myRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        lista.add(document.toObject(Restaurante.class));
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        activityForm = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent intent = result.getData();
                            if (intent.getSerializableExtra("update") != null) {
                                Restaurante a_dev = (Restaurante) intent.getSerializableExtra("update");
                                Log.d("update", pos + " " + a_dev.getNombre());
                                lista.set(pos, a_dev);
                                adapter.notifyDataSetChanged();
                            } else if (intent.getSerializableExtra("new") != null) {
                                Restaurante a_dev = (Restaurante) intent.getSerializableExtra("new");
                                Log.d("new", pos + " " + a_dev.getNombre());
                                lista.add(a_dev);
                                adapter.notifyDataSetChanged();
                            } else if (intent.getSerializableExtra("delete") != null) {
                                Restaurante a_dev = (Restaurante) intent.getSerializableExtra("delete");
                                Log.d("delete", pos + " " + a_dev.getNombre());
                                lista.remove(pos);
                                adapter.notifyDataSetChanged();
                            }

                        }
                    }
                });

        Button btnNuevo = (Button) findViewById(R.id.btn_listaNuevo);
        btnNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListRestaurante.this, FormRestaurante.class);
                intent.putExtra("btnBorrarHabilitado", false);
                intent.putExtra("btnAgregarMesaHabilitado", false);
                startActivity(intent);
            }
        });
    }
}
