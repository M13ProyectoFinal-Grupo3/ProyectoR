package com.example.Lists;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.adapters.AdapterAlergeno;
import com.example.pojos.Alergeno;
import com.example.proyector.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
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

        listview1 = (ListView) findViewById(R.id.list_alergenos);
        adapter = new AdapterAlergeno(ListAlergenos.this,lista);
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

        // seleccionar alergeno
        listview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("alergeno", lista.get(position));
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        // editar/eliminar alergeno
        listview1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ListAlergenos.this);
                builder.setTitle("Editar alérgeno");

                final EditText input = new EditText(ListAlergenos.this);

                input.setInputType(InputType.TYPE_CLASS_TEXT );
                String anterior = lista.get(position).getNombre();
                input.setText(anterior);
                builder.setView(input);

                builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Alergeno nuevo = new Alergeno(input.getText().toString());

                        //comprueba que el alergeno no haya sido creado anteriormente
                        myRef.whereNotEqualTo("nombre",nuevo.getNombre())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()) {
                                            myRef.whereEqualTo("nombre", anterior)
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                if (task.getResult().getDocuments().size() > 0) {
                                                                    DocumentSnapshot d = task.getResult().getDocuments().get(task.getResult().size() - 1);
                                                                    myRef.document(d.getId()).update("nombre", nuevo.getNombre()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            Toast.makeText(ListAlergenos.this, "El Alérgeno se modificó correctamente", Toast.LENGTH_SHORT).show();
                                                                            lista.set(position, nuevo);
                                                                            adapter.notifyDataSetChanged();
                                                                        }
                                                                    });

                                                                } else {
                                                                    Log.d(TAG, "Documento Alérgeno no econtrado para su modificación");
                                                                }
                                                            }
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(ListAlergenos.this, "Este alérgeno ya existe", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }
                });

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.setNeutralButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Alergeno anterior = lista.get(position);
                        myRef.whereEqualTo("nombre",lista.get(position).getNombre())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()) {
                                            DocumentSnapshot d = task.getResult().getDocuments().get(task.getResult().size()-1);
                                            myRef.document(d.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(ListAlergenos.this,"El alérgeno se eliminó correctament",Toast.LENGTH_LONG).show();
                                                    lista.remove(anterior);
                                                    adapter.notifyDataSetChanged();
                                                }
                                            });
                                        } else {
                                            Log.d(TAG, "Documento no ha podido ser eiliminado");
                                        }
                                    }
                                });
                    }
                });

                builder.show();
                return false;
            }
        });

        // Nuevo alergenos
        Button btnNuevo = (Button) findViewById(R.id.btn_listaNuevo);
        btnNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ListAlergenos.this);
                builder.setTitle("Nuevo alérgeno ó intolerancia");

                final EditText input = new EditText(ListAlergenos.this);

                input.setInputType(InputType.TYPE_CLASS_TEXT );
                builder.setView(input);

                builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Alergeno nuevo = new Alergeno(input.getText().toString());

                        myRef.whereNotEqualTo("nombre",nuevo.getNombre())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()) {
                                            myRef.document().set(nuevo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    lista.add(nuevo);
                                                    adapter.notifyDataSetChanged();
                                                }
                                            });
                                        } else {
                                            Toast.makeText(ListAlergenos.this, "Este alérgeno ya existe", Toast.LENGTH_SHORT).show();
                                        }
                                    }});
                    }
                });

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

    }


}

/*        activityForm= registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent intent = result.getData();
                            if(intent.getSerializableExtra("update",Alergeno.class)!=null){
                                Alergeno a_dev = (Alergeno) intent.getSerializableExtra("update",Alergeno.class);
                                Log.d("update",pos+" "+a_dev.getNombre());
                                lista.set(pos,a_dev);
                                adapter.notifyDataSetChanged();
                            } else if(intent.getSerializableExtra("new",Alergeno.class)!=null){
                                Alergeno a_dev = (Alergeno) intent.getSerializableExtra("new",Alergeno.class);
                                Log.d("new",pos+" "+a_dev.getNombre());
                                lista.add(a_dev);
                                adapter.notifyDataSetChanged();
                            } else if(intent.getSerializableExtra("delete",Alergeno.class)!=null){
                                Alergeno a_dev = (Alergeno) intent.getSerializableExtra("delete",Alergeno.class);
                                Log.d("delete",pos+" "+a_dev.getNombre());
                                lista.remove(edit_item);
                                adapter.notifyDataSetChanged();
                            }

                        }
                    }
                });*/