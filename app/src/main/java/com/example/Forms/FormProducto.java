package com.example.Forms;

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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Lists.pojos.cAlergeno;
import com.example.adapters.AdapterAlergeno;
import com.example.Lists.pojos.Alergeno;
import com.example.Lists.pojos.Producto;
import com.example.adapters.AdapterCheckAls;
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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FormProducto extends AppCompatActivity {
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    CollectionReference myRef;
    Producto producto = null;
    EditText xNombre;
    EditText xDescrip ;
    EditText xPrecio;
    ImageView imageview1;

    ArrayList<cAlergeno> cAlergenos; // lista de todos los Alergenos
    AdapterCheckAls adaptercheck;
    TextView tAlergs; // TextView donde se muestran los alergenos seleccionados

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_producto);

        cAlergenos = new ArrayList<>();

        ImageButton btnBorrar = (ImageButton) findViewById(R.id.btn_borrarProd);
        Button btnGuardar = (Button) findViewById(R.id.btnGuardarAl);
        ImageButton btnFoto = (ImageButton) findViewById(R.id.btn_pFoto);
        ImageButton btnAlergenos = (ImageButton) findViewById(R.id.btn_pAlergenos);
        TextView txDepartamento = (TextView) findViewById(R.id.tx_nomDepto);

        xNombre = (EditText) findViewById(R.id.t_pNombre);
        xDescrip = (EditText) findViewById(R.id.t_pDescripcion);
        xPrecio = (EditText) findViewById(R.id.t_pPrecio);
        imageview1 =(ImageView) findViewById(R.id.imagen1);
        tAlergs = (TextView) findViewById(R.id.txFprodAls);

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
                // set Alergenos
                CollectionReference alRef = db.collection("alergenos");
                alRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Alergeno a = document.toObject(Alergeno.class);
                                cAlergenos.add(new cAlergeno(a,false));
                            }
                        }
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(cAlergeno c: cAlergenos){
                            for(Alergeno a: producto.getAlergenos()){
                                if(c.getAlergeno().getId().equals(a.getId())){
                                    c.setChecked(true);
                                }
                            }
                        }
                        mostrarAls();
                        cargarImagen(producto);
                    }
                });
            }
        } else { finish();}

        // guardar producto
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // recupera la información introducida por el usuario
                EditText xNombre = (EditText) findViewById(R.id.t_pNombre);
                EditText xDescrip = (EditText) findViewById(R.id.t_pDescripcion);
                EditText xPrecio = (EditText) findViewById(R.id.t_pPrecio);
                if(xPrecio.getText().toString().equals("")) xPrecio.setText("0");

                Producto p = new Producto(true, xNombre.getText().toString(), xDescrip.getText().toString(), Float.parseFloat(xPrecio.getText().toString()),getAlergenos(cAlergenos));

                guardarImagen(p);

                // Update
                if (producto != null) {
                    p.setId(producto.getId());

                    HashMap<String, Object> data = new HashMap<String, Object>() {
                    };
                    data.put("nombre",p.getNombre());
                    data.put("activo", p.getActivo());
                    data.put("descripcion", p.getDescripcion());
                    data.put("precio", p.getPrecio());
                    data.put("id_departamento", producto.getPrepara_idperfil());
                    data.put("id_prepara", producto.getPrepara_idperfil());
                    data.put("alergenos",p.getAlergenos());
                    myRef.document(producto.getId()).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
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

        // Alergenos

        btnAlergenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // generar Dialog
                AlertDialog.Builder dialog1 = new AlertDialog.Builder(FormProducto.this);
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

                View layout = inflater.inflate(R.layout.dialog_list, (ViewGroup) findViewById(R.id.layout_root));
                TextView text1 = (TextView) layout.findViewById(R.id.texthead1);
                ListView listview2 = (ListView) layout.findViewById(R.id.listviewCheck);
                text1.setText("Asignar Alérgenos e intolerancias");

                adaptercheck = new AdapterCheckAls(getApplicationContext(), cAlergenos);
                listview2.setAdapter(adaptercheck);

                dialog1.setView(layout);
                dialog1.setPositiveButton("ASIGNAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        cAlergenos = adaptercheck.getcAlergenos();
                        mostrarAls();
                        dialog.dismiss();
                    }

                });
                dialog1.create();
                dialog1.show();
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

    ArrayList<Alergeno> getAlergenos(ArrayList<cAlergeno> cAlergenos){
        ArrayList<Alergeno> result = new ArrayList<>();
        for(cAlergeno c: cAlergenos){
            if(c.getChecked()) result.add(c.getAlergeno());
        }
        return result;
    }

    //mostrar Alergenos en el TextBox

    void mostrarAls(){
        String s = "";
        ArrayList<String> result =new ArrayList<>();
        for (cAlergeno a : cAlergenos) {
            Log.d("als",a.getChecked()+" "+a.getAlergeno().getNombre().toString());
            if(a.getChecked()) result.add(a.getAlergeno().getNombre());
        }
        if(result.size()>0) s = TextUtils.join(",", result);
        tAlergs.setText(s);
    }
}