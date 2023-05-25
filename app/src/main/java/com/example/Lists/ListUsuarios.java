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

import com.example.Lists.pojos.Usuarios;
import com.example.adapters.AdapterUsuarios;
import com.example.proyector.R;
import com.example.proyector.RegistroActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ListUsuarios extends AppCompatActivity {
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    CollectionReference myRef = db.collection("usuarios");

    ListView listview1;
    ArrayList<Usuarios> lista = new ArrayList<>();
    AdapterUsuarios adapter;
    Integer pos=0;

    ActivityResultLauncher<Intent> activityForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_usuarios);
        getSupportActionBar().hide();

        listview1 = (ListView) findViewById(R.id.list_productos);
        adapter = new AdapterUsuarios(ListUsuarios.this, lista);
        listview1.setAdapter(adapter);


        myRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        lista.add(document.toObject(Usuarios.class));
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
                            if (intent.getSerializableExtra("update", Usuarios.class) != null) {
                                Usuarios a_dev = (Usuarios) intent.getSerializableExtra("update", Usuarios.class);
                                lista.set(pos, a_dev);
                                adapter.notifyDataSetChanged();
                            } else if (intent.getSerializableExtra("new", Usuarios.class) != null) {
                                Usuarios a_dev = (Usuarios) intent.getSerializableExtra("new", Usuarios.class);
                                lista.add(a_dev);
                                adapter.notifyDataSetChanged();
                            } else if (intent.getSerializableExtra("delete", Usuarios.class) != null) {
                                Usuarios a_dev = (Usuarios) intent.getSerializableExtra("delete", Usuarios.class);
                                lista.remove(a_dev);
                                adapter.notifyDataSetChanged();
                            }

                        }
                    }
                });

        // editar usuario
        listview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
                Intent intent = new Intent(ListUsuarios.this, RegistroActivity.class);
                intent.putExtra("usuario", lista.get(position));
                activityForm.launch(intent);
            }
        });

        // Nuevo usuario
        Button btnNuevo = (Button) findViewById(R.id.btn_listaNuevo);
        btnNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListUsuarios.this,RegistroActivity.class);
                activityForm.launch(intent);
            }
        });
    }
}