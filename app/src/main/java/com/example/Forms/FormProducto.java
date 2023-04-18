package com.example.Forms;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
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
import com.example.adapters.AdapterAlergeno;
import com.example.pojos.Alergeno;
import com.example.pojos.Producto;
import com.example.proyector.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class FormProducto extends AppCompatActivity {
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    CollectionReference myRef;
    Producto producto = null;
    EditText xNombre;
    EditText xDescrip ;
    EditText xPrecio;
    ListView xListAlgs;
    ImageView imageview1;

    ArrayList<Alergeno> lista_algs;
    AdapterAlergeno adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_producto);

        Button btnBorrar = (Button) findViewById(R.id.btn_selecImg);
        Button btnGuardar = (Button) findViewById(R.id.btnGuardarCarta);
        ImageButton btnFoto = (ImageButton) findViewById(R.id.btn_pFoto);
        ImageButton btnAlergenos = (ImageButton) findViewById(R.id.btn_pAlergenos);
        TextView txDepartamento = (TextView) findViewById(R.id.tx_nomDepto);

        xNombre = (EditText) findViewById(R.id.t_pNombre);
        xDescrip = (EditText) findViewById(R.id.t_pDescripcion);
        xPrecio = (EditText) findViewById(R.id.t_pPrecio);
        xListAlgs = (ListView) findViewById(R.id.list_pAls);
        imageview1 =(ImageView) findViewById(R.id.imagen1);
        lista_algs =new ArrayList<>();

        // recupera Producto a editar
        Intent intent = getIntent();
        if(intent.getExtras()!=null) {
            if(intent.getExtras().containsKey("departamento")){
                txDepartamento.setText("Departamento: "+getIntent().getExtras().getString("departamento"));
            }
            if(intent.getExtras().containsKey("ref")){
                myRef = db.collection(getIntent().getExtras().getString("ref"));
                Log.d("myRef",myRef.getPath());
            } else { finish();}
            if(intent.getExtras().containsKey("producto")) {
                producto = getIntent().getExtras().getSerializable("producto", Producto.class);
                Log.d("producto",""+producto.toString());
                xNombre.setText(producto.getNombre());
                xDescrip.setText(producto.getDescripcion());
                xPrecio.setText(producto.getPrecio().toString());
                cargarImagen(producto);
            }
        } else { finish();}


        //mostrar Alergenos

        adapter = new AdapterAlergeno(FormProducto.this,lista_algs);
        xListAlgs.setAdapter(adapter);

        myRef.document(producto.getId()).collection("Alergenos").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot doc: task.getResult()){
                        lista_algs.add(doc.toObject(Alergeno.class));
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });

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

                Producto p = new Producto(true, xNombre.getText().toString(), xDescrip.getText().toString(), Float.parseFloat(xPrecio.getText().toString()));
                p.setId(producto.getId());
                guardarImagen(p);
                // Update
                if (producto != null) {
                    // Actualizar
                    Log.d("save myRef", myRef.getPath());

                    HashMap<String, Object> data = new HashMap<String, Object>() {
                    };
                    data.put("nombre",p.getNombre());
                    data.put("activo", p.getActivo());
                    data.put("descripcion", p.getDescripcion());
                    data.put("precio", p.getPrecio());
                    data.put("id_departamento", producto.getPrepara_idperfil());
                    data.put("id_prepara", producto.getPrepara_idperfil());
                    myRef.document(producto.getId()).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            guardarAlergenos(p);

                            Toast.makeText(FormProducto.this, "El producto se modificó correctamente", Toast.LENGTH_SHORT).show();
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("update", p);
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        }
                    });

                } else {
                    // Nuevo Producto
                    myRef.whereNotEqualTo("nombre",p.getNombre())
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    myRef.add(p).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            p.setId(task.getResult().getId());
                                            myRef.document(p.getId()).update("id",p.getId());

                                            guardarAlergenos(p);

                                            Intent resultIntent = new Intent();
                                            resultIntent.putExtra("new",p);
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
                myRef.document( producto.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(FormProducto.this, "El Producto ha sido eliminado correctamente", Toast.LENGTH_SHORT).show();
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("delete", producto);
                        setResult(RESULT_OK, resultIntent);
                        finish();
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
                                Alergeno a= intent.getSerializableExtra("alergeno", Alergeno.class);
                                lista_algs.add(a);
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

        ActivityResultLauncher<Intent> startActivityGaleria  = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if (data != null){
                            Uri selectedImageUri = data.getData();
                            if (selectedImageUri != null){
                                try {
                                    InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                    imageview1.setImageBitmap(bitmap);
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

    }

    private void cargarImagen(Producto p){
        final long MAX_IMAGESIZE = 1024 * 1024;
        String name = p.getNombre().replace(" ","")+".jpg";
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imgRef = storage.getReference();
        imgRef.child("productos").child(name).getBytes(MAX_IMAGESIZE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap  = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imageview1.setImageBitmap(bitmap);
                    }
                });
    }

    private void guardarImagen(Producto p){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imgRef = storage.getReference().child("productos");
        String name = p.getNombre().replace(" ","")+".jpg";

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

        Bitmap bitmap = getBitmapFromView(imageview1);
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

    private void guardarAlergenos(Producto p){
        CollectionReference alRef = myRef.document(producto.getId()).collection("Alergenos");
        alRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot doc: task.getResult()){
                        alRef.document(doc.getId()).delete();
                    }
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(Alergeno a: lista_algs){
                    alRef.add(a);
                }
            }
        });
    }

}