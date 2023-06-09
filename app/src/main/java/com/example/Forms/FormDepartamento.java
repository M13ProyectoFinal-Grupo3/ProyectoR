package com.example.Forms;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Lists.ListAlergenos;
import com.example.Lists.pojos.Alergeno;
import com.example.Lists.pojos.Restaurante;
import com.example.adapters.AdapterProducto;
import com.example.Lists.pojos.Departamento;
import com.example.Lists.pojos.Producto;
import com.example.proyector.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;

public class FormDepartamento extends AppCompatActivity {
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    CollectionReference rootRef;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference imgRef = storage.getReference();

    Restaurante restaurante1;
    Departamento departamento;

    ArrayList<Producto> listaProductos = new ArrayList<>();
    AdapterProducto adapter;
    ActivityResultLauncher<Intent> activityForm;

    ListView listViewProds;

    int pos=-1;

    ImageView imageview1;
    ImageLib oFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_departamento);
        getSupportActionBar().hide();

        Button btnGuardar= (Button) findViewById(R.id.btnGuardarDepto);
        Button btnNuevoProducto = (Button) findViewById(R.id.btnNuevoProducto);
        TextView txNomRest = (TextView) findViewById(R.id.txNomRest);
        ImageButton btnBorrar = (ImageButton) findViewById(R.id.btnBorrarDep);
        ImageButton btnFoto = (ImageButton) findViewById(R.id.btnImgDepto);
        TextInputEditText txDepto = (TextInputEditText) findViewById(R.id.TexNomDepto);
        imageview1 = (ImageView) findViewById(R.id.ImgDepto);
        listViewProds = (ListView) findViewById(R.id.list_productos);

        oFoto = new ImageLib();

        // recupera Departamento y muestra sus productos
        Intent intent = getIntent();
        if(intent.getExtras()!=null) {
            if(intent.getExtras().containsKey("restaurante")){
                restaurante1 = intent.getSerializableExtra("restaurante",Restaurante.class);
                txNomRest.setText(restaurante1.getNombre());
            } else {
                Toast.makeText(this, "ERROR: Restaurante no encontrado", Toast.LENGTH_SHORT).show();
                finish();
            }

            rootRef = db.collection("restaurante").document(restaurante1.getId()).collection("Carta");

            if(intent.getExtras().containsKey("departamento")){
                departamento = getIntent().getExtras().getSerializable("departamento", Departamento.class);
                txDepto.setText(departamento.getnombre());
                oFoto.leerImagen("departamentos",departamento.getId(),imageview1);
            } else {
                departamento = new Departamento();
                btnBorrar.setEnabled(false);
            }

            // si hay productos los muestra

            if(departamento.getId() != null) {

                rootRef = db.collection("restaurante").document(restaurante1.getId()).collection("Carta").document("carta")
                        .collection("Departamentos");



                CollectionReference productosRef = rootRef.document(departamento.getId()).collection("productos");
                Log.d("ok",productosRef.getPath());
                productosRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                listaProductos.add(doc.toObject(Producto.class));
                            }
                            adapter = new AdapterProducto(getApplicationContext(), listaProductos);
                            listViewProds.setAdapter(adapter);

                            listViewProds.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    pos = position;
                                    Intent intent = new Intent(getApplicationContext(), FormProducto.class);
                                    intent.putExtra("producto", listaProductos.get(position));
                                    intent.putExtra("restaurante", restaurante1);
                                    intent.putExtra("departamento", departamento);
                                    activityForm.launch(intent);
                                }
                            });

                        }
                    }
                });
            } else {
                listViewProds.setEnabled(false);
                btnNuevoProducto.setEnabled(false);
            }

        } else {
            Toast.makeText(this, "ERROR: Restaurante no reconocido", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnNuevoProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FormProducto.class);
                intent.putExtra("restaurante", restaurante1);
                intent.putExtra("departamento", departamento);
                activityForm.launch(intent);
            }
        });

        // form producto
        activityForm = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent intent = result.getData();
                            if (intent.getSerializableExtra("update", Producto.class) != null) {
                                Producto a_dev = (Producto) intent.getSerializableExtra("update", Producto.class);
                                Log.d("update", pos + " " + a_dev.getNombre());
                                listaProductos.set(pos, a_dev);
                                adapter.notifyDataSetChanged();
                            } else if (intent.getSerializableExtra("new", Producto.class) != null) {
                                Producto a_dev = (Producto) intent.getSerializableExtra("new", Producto.class);
                                Log.d("new", pos + " " + a_dev.getNombre());
                                listaProductos.add(a_dev);
                                adapter.notifyDataSetChanged();
                            } else if (intent.getSerializableExtra("delete", Producto.class) != null) {
                                Producto a_dev = (Producto) intent.getSerializableExtra("delete", Producto.class);
                                Log.d("delete", pos + " " + a_dev.getNombre());
                                listaProductos.remove(listaProductos.get(pos));
                                adapter.notifyDataSetChanged();
                            }

                        }
                    }
                });



        // borrar departamento
        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 CollectionReference productoRef = rootRef.document(departamento.getId()).collection("productos");
                productoRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot d: task.getResult()){
                                productoRef.document(d.getId()).delete();
                            }
                        }
                    }
                }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.d("departamento", ""+departamento.getId());
                        rootRef.document(departamento.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(FormDepartamento.this, "El Departamento ha sido eliminado correctamente", Toast.LENGTH_SHORT).show();
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("delete", departamento);
                                setResult(RESULT_OK, resultIntent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(FormDepartamento.this, "El Departamento no ha podido ser eliminado correctamente", Toast.LENGTH_SHORT).show();
                            }
                    });

                    }
                });
            }
        });

        // Guardar Nuevo departamento

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Departamento nuevoDep = new Departamento();
                nuevoDep.setnombre(txDepto.getText().toString());
                CollectionReference deptoRef = rootRef.document("carta").collection("Departamentos");
                deptoRef.whereNotEqualTo("departamento",nuevoDep.getnombre())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                deptoRef.add(nuevoDep).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        nuevoDep.setId(task.getResult().getId());
                                        deptoRef.document(nuevoDep.getId()).update("id",nuevoDep.getId());
                                        oFoto.guardarImagen("departamentos",nuevoDep.getId());

                                        Toast.makeText(FormDepartamento.this, "El Departamento ha sido almacenado correctamente", Toast.LENGTH_SHORT).show();
                                        Intent resultIntent = new Intent();
                                        resultIntent.putExtra("new", nuevoDep);
                                        setResult(RESULT_OK, resultIntent);
                                        finish();
                                    }
                                });
                            }});
            }
        });


        // Galeria fotos

        ActivityResultLauncher<Intent> startActivityGaleria  = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if (data != null){
                            Uri selectedImageUri = data.getData();
                            if (selectedImageUri != null){
                                try {
                                    InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                                    oFoto = new ImageLib(BitmapFactory.decodeStream(inputStream));
                                    imageview1.setImageBitmap(Bitmap.createScaledBitmap(oFoto.getBmp(),50, 50, false));
                                }catch (Exception e){
                                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                });

        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityGaleria.launch(intent);
            }
        });

        ImageButton backButton = findViewById(R.id.backBtn);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


}