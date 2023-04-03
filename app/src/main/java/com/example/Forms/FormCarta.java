package com.example.Forms;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.adapters.AdapterProducto;
import com.example.pojos.Producto;
import com.example.proyector.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class FormCarta extends AppCompatActivity {
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    CollectionReference myRef = db.collection("carta");

    ActivityResultLauncher<Intent> activityForm;

    ListView listview1;
    AdapterProducto adapter;
    ArrayList<Producto> lista = new ArrayList<>();
    Integer pos=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_carta);

        Button btnNuevoDep = (Button) findViewById(R.id.btn_cNuevoDep);
        Button btnGuardar = (Button) findViewById(R.id.btn_dGuardar);

        listview1 = (ListView) findViewById(R.id.lista_cDeps);

        // editar departamento
        listview1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                pos=position;
                Intent intent = new Intent(FormCarta.this   , FormDepartamento.class);
                intent.putExtra("departamento",lista.get(position));
                activityForm.launch(intent);
                return false;
            }
        });

        // nuevo departamento
        btnNuevoDep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FormCarta.this   , FormDepartamento.class);
                startActivity(intent);
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        activityForm= registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent intent = result.getData();
                            if(intent.getSerializableExtra("update")!=null){
                                Producto p_dev = (Producto) intent.getSerializableExtra("update");
                                lista.set(pos,p_dev);
                                adapter.notifyDataSetChanged();
                            } else if(intent.getSerializableExtra("new")!=null){
                                Producto p_dev = (Producto) intent.getSerializableExtra("new");
                                lista.add(p_dev);
                                adapter.notifyDataSetChanged();
                            } else if(intent.getSerializableExtra("delete")!=null){
                                Producto p_dev = (Producto) intent.getSerializableExtra("delete");
                                lista.remove(pos);
                                adapter.notifyDataSetChanged();
                            }

                        }
                    }
                });
    }

    private void verDeps(){

    }


}