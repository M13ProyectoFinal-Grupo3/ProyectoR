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

import com.example.Forms.FormMesa;
import com.example.adapters.AdapterMesa;
import com.example.Lists.pojos.Mesa;
import com.example.proyector.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ListMesa extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference myRef = db.collection("restaurante");

    String nombreRestaurante;
    String idDocumentoRestaurante;
    CollectionReference idColec;
    ListView listview1;
    ArrayList<Mesa> lista = new ArrayList<>();
    AdapterMesa adapter;
    Integer pos = 0;

    ActivityResultLauncher<Intent> activityForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_mesa);

        listview1 = (ListView) findViewById(R.id.list_mesas);
        adapter = new AdapterMesa(ListMesa.this, lista);
        listview1.setAdapter(adapter);

        Intent intent = getIntent();
        if(intent.getExtras()!=null) {
            nombreRestaurante = (String) getIntent().getExtras().get("nombreRestaurante");
            idDocumentoRestaurante = "ID" + nombreRestaurante;
            idColec = db.collection("restaurante/" + idDocumentoRestaurante + "/mesa");
        }

        listview1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
                Intent intent = new Intent(ListMesa.this, FormMesa.class);
                intent.putExtra("mesa", lista.get(position));
                intent.putExtra("btnBorrarHabilitado", true);
                intent.putExtra("idDocumentoRestaurante", idDocumentoRestaurante);
                activityForm.launch(intent);
                return false;
            }
        });

        idColec.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                lista.add(document.toObject(Mesa.class));
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
                                Mesa a_dev = (Mesa) intent.getSerializableExtra("update");
                                lista.set(pos, a_dev);
                                adapter.notifyDataSetChanged();
                            } else if (intent.getSerializableExtra("new") != null) {
                                Mesa a_dev = (Mesa) intent.getSerializableExtra("new");
                                lista.add(a_dev);
                                adapter.notifyDataSetChanged();
                            } else if (intent.getSerializableExtra("delete") != null) {
                                ArrayList<Mesa> mesas = new ArrayList<>();
                                for (Mesa mesa : lista) {
                                    int posicion = lista.indexOf(mesa);
                                    if(posicion != pos) {
                                        mesas.add(mesa);
                                    }
                                }
                                lista.clear();
                                lista.addAll(mesas);
                                adapter.notifyDataSetChanged();
                            }

                        }
                    }
                });

        Button btnNuevo = (Button) findViewById(R.id.btn_listaNuevo);
        btnNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(ListMesa.this, FormMesa.class);
                intent.putExtra("idDocumentoRestaurante", idDocumentoRestaurante);
                startActivity(intent);
            }
        });
    }
}
