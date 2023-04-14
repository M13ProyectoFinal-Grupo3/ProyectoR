package com.example.Forms;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.Lists.ListAlergenos;
import com.example.adapters.AdapterAlergeno;
import com.example.pojos.Alergeno;
import com.example.pojos.Producto;
import com.example.proyector.ImgInterface;
import com.example.proyector.R;
import com.example.proyector.SelectImage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class FormProducto extends AppCompatActivity implements ImgInterface {
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    CollectionReference myRef;
    Producto anterior = null;
    Producto nuevo = null;
    EditText xNombre;
    EditText xDescrip ;
    EditText xPrecio;
    ListView xListAlgs;
    ImageView xImagen;

    ArrayList<Alergeno> lista_algs;
    AdapterAlergeno adapter;

    FragmentManager fm = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_producto);
        
        xNombre = (EditText) findViewById(R.id.t_pNombre);
        xDescrip = (EditText) findViewById(R.id.t_pDescripcion);
        xPrecio = (EditText) findViewById(R.id.t_pPrecio);
        xListAlgs = (ListView) findViewById(R.id.list_pAls);
        xImagen =(ImageView) findViewById(R.id.imagen1);
        lista_algs =new ArrayList<>();

        Button btnBorrar = (Button) findViewById(R.id.btn_selecImg);
        Button btnGuardar = (Button) findViewById(R.id.btnGuardarCarta);
        ImageButton btnFoto = (ImageButton) findViewById(R.id.btn_pFoto);
        ImageButton btnAlergenos = (ImageButton) findViewById(R.id.btn_pAlergenos);

        // fragment elegir imagen
        fm.setFragmentResultListener("request", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                //User u = result.getSerializable("user",User.class);
                /*if(u!=null){
                    //cargar fragment
                }*/
            }
        });

        // recupera Producto a editar
        Intent intent = getIntent();
        if(intent.getExtras()!=null) {
            if(intent.getExtras().containsKey("ref")){
                myRef = db.collection(getIntent().getExtras().getString("ref"));
                Log.d("myRef",myRef.getPath());
            }
            if(intent.getExtras().containsKey("producto")) {
                anterior = getIntent().getExtras().getSerializable("producto", Producto.class);
                xNombre.setText(anterior.getNombre());
                xDescrip.setText(anterior.getDescripcion());
                xPrecio.setText(anterior.getPrecio().toString());
            }
        }

        //verAlergenos

        adapter = new AdapterAlergeno(FormProducto.this,lista_algs);
        xListAlgs.setAdapter(adapter);
        //borrar alérgeno
        xListAlgs.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(FormProducto.this);
                dlgAlert.setMessage("¿Desea eliminar el alérgeno de la lista?")
                        .setTitle("Eliminar alérgeno")
                        .setCancelable(true)
                        .setNegativeButton("NO", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                     dialog.cancel();
                            }
                        })
                        .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                lista_algs.remove(lista_algs.get(position));
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .create().show();
                return false;
            }
        });

        // guardar producto
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // recupera la información introducida por el usuario
                EditText xNombre = (EditText) findViewById(R.id.t_pNombre);
                EditText xDescrip = (EditText) findViewById(R.id.t_pDescripcion);
                EditText xPrecio = (EditText) findViewById(R.id.t_pPrecio);

                nuevo = new Producto(true, xNombre.getText().toString(),xDescrip.getText().toString(),Float.parseFloat( xPrecio.getText().toString()));
                guardarImagen();
                // Update
                if(anterior!=null) {
                    // Actualizar
                    Log.d("save myRef",myRef.getPath());
                    myRef.whereNotEqualTo("nombre", nuevo.getNombre())
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    myRef.whereEqualTo("nombre",anterior.getNombre())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        if(task.getResult().getDocuments().size()>0) {
                                                            DocumentSnapshot d = task.getResult().getDocuments().get(task.getResult().size()-1);
                                                            HashMap<String,Object> data = new HashMap<String,Object>(){};
                                                            data.put("nombre",nuevo.getNombre());
                                                            data.put("activo", nuevo.getActivo());
                                                            data.put("descripcion",nuevo.getDescripcion());
                                                            data.put("precio",nuevo.getPrecio());
                                                            data.put("id_departamento",anterior.getPrepara_idperfil());
                                                            data.put("id_prepara",anterior.getPrepara_idperfil());
                                                            myRef.document( d.getId()).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    Toast.makeText(FormProducto.this, "El producto se modificó correctamente", Toast.LENGTH_SHORT).show();
                                                                    Intent resultIntent = new Intent();
                                                                    resultIntent.putExtra("update", nuevo);
                                                                    setResult(RESULT_OK, resultIntent);
                                                                    finish();
                                                                }
                                                            });

                                                        } else {
                                                            Log.d(TAG,"Documento Producto no econtrado para su modificación");
                                                        }
                                                    } else {
                                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                                    }
                                                }
                                            });
                                }
                            });

                } else {
                    // Nuevo Producto
                    myRef.whereNotEqualTo("nombre", nuevo.getNombre())
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    myRef.document().set(nuevo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Intent resultIntent = new Intent();
                                            resultIntent.putExtra("new", nuevo);
                                            setResult(RESULT_OK, resultIntent);
                                            Toast.makeText(FormProducto.this, "El Producto se añadio correctamente", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    });
                                }
                            });
                }


            }
        });

        // borrar producto
        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.whereEqualTo("nombre",anterior.getNombre())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if(task.getResult().getDocuments().size()>0) {
                                        DocumentSnapshot d = task.getResult().getDocuments().get(task.getResult().size()-1);
                                        myRef.document( d.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(FormProducto.this, "El Producto ha sido eliminado correctamente", Toast.LENGTH_SHORT).show();
                                                Intent resultIntent = new Intent();
                                                resultIntent.putExtra("delete", anterior);
                                                setResult(RESULT_OK, resultIntent);
                                                finish();
                                            }
                                        });
                                    } else {
                                        Log.d(TAG,"Documento Alergeno no econtrado para su modificación");
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });

        // activity alergeno
        ActivityResultLauncher<Intent> startActivityAlergeno= registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // Recibe objeto alergeno
                            Intent intent =  result.getData();
                            if(intent.getExtras() != null) {
                                Alergeno alerg = (Alergeno) intent.getSerializableExtra("alergeno");
                                lista_algs.add(alerg);
                                adapter.notifyDataSetChanged();
                            }
                        }else{
                            //No recibe información.

                        }
                    }
                });

        btnAlergenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FormProducto.this, ListAlergenos.class);
                startActivityAlergeno.launch(intent);
            }
        });

        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new SelectImage());
            }
        });

    }

    private void loadFragment(Fragment fragment) {
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        // replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.framelayout_p1, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit(); // save the changes
    }

    // imagen que devuelve fragment seleccionar imagen
    public void devData(Object data){
        if(data!=null) {
            Bitmap b = (Bitmap) data;
            xImagen.setImageBitmap(b);
        }
    }

    private void guardarImagen(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imgRef = storage.getReference().child("productos");
        String name = nuevo.getNombre().replace(" ","")+".jpg";

        // comprobar si existe imagen asociada al producto y eliminarla antes de subir la nueva
        imgRef.listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
            @Override
            public void onComplete(@NonNull Task<ListResult> task) {
                if(task.getResult().getItems().size()>0) {
                    for(StorageReference file:task.getResult().getItems()){
                        if(file.getName().equals(name)){
                            StorageReference delRef = storage.getReference().child("productos/"+name);
                            delRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // File deleted successfully
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Uh-oh, an error occurred!
                                }
                            });

                        }
                    }
                }
            }
        });

        // subir la nueva imagen
        imgRef = storage.getReference().child("productos/"+name);

        Bitmap bitmap = getBitmapFromView(xImagen);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imgRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });
    }

    Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(
                view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

}