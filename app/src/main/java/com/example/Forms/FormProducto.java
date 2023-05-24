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
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Lists.pojos.Usuarios;
import com.example.Lists.pojos.cAlergeno;
import com.example.Lists.pojos.Alergeno;
import com.example.Lists.pojos.Producto;
import com.example.adapters.AdapterCheckAls;
import com.example.adapters.AdapterUsuarios;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class FormProducto extends AppCompatActivity {
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    CollectionReference myRef;
    Producto producto = null;
    EditText xNombre;
    EditText xDescrip ;
    EditText xPrecio;
    TextView txDepartamento;
    ImageView imageview1;
    Spinner spinPrepara;
    Spinner spinServido;
    Switch switch1;

    ArrayList<Usuarios> usuarios;
    ArrayAdapter<Usuarios> adapter1;
    ArrayAdapter<Usuarios> adapter2;


    ArrayList<cAlergeno> cAlergenos; // lista de todos los Alergenos
    AdapterCheckAls adaptercheck;
    TextView tAlergs; // TextView donde se muestran los alergenos seleccionados

    final long MAX_IMAGESIZE = 1024 * 1024;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference imgRef = storage.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_producto);

        cAlergenos = new ArrayList<>();

        ImageButton btnBorrar = (ImageButton) findViewById(R.id.btn_borrarProd);
        Button btnGuardar = (Button) findViewById(R.id.btnGuardarAl);
        ImageButton btnFoto = (ImageButton) findViewById(R.id.btnImgProducto);
        ImageButton btnAlergenos = (ImageButton) findViewById(R.id.btn_pAlergenos);

        txDepartamento = (TextView) findViewById(R.id.tx_nomDepto);
        spinPrepara = (Spinner) findViewById(R.id.spinPrepara);
        spinServido = (Spinner) findViewById(R.id.spinServido);
        xNombre = (EditText) findViewById(R.id.t_pNombre);
        xDescrip = (EditText) findViewById(R.id.t_pDescripcion);
        xPrecio = (EditText) findViewById(R.id.t_pPrecio);
        imageview1 =(ImageView) findViewById(R.id.ImgProducto);
        tAlergs = (TextView) findViewById(R.id.txFprodAls);
        switch1 = (Switch) findViewById(R.id.switch1);

        // set perfiles
        CollectionReference perfilRef = db.collection("usuarios");
        perfilRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot doc: task.getResult()){
                        usuarios.add(doc.toObject(Usuarios.class));
                    }

                    adapter1 = new AdapterUsuarios(getApplicationContext(), usuarios);
                    spinPrepara.setAdapter(adapter1);
                    spinPrepara.setSelection(0);
                    adapter2 = new AdapterUsuarios(getApplicationContext(), usuarios);
                    spinServido.setAdapter(adapter2);
                    spinServido.setSelection(0);

                } else {
                    Toast.makeText(FormProducto.this, "ERROR: No existen perfiles de servicio", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                // recupera Producto a editar
                Intent intent = getIntent();
                if (intent.getExtras() != null) {

                    if (intent.getExtras().containsKey("departamento")) {
                        txDepartamento.setText("Departamento: " + getIntent().getExtras().getString("departamento"));
                    }

                    if (intent.getExtras().containsKey("ref")) {
                        myRef = db.collection(getIntent().getExtras().getString("ref"));
                    } else {
                        finish();
                    }

                    if (intent.getExtras().containsKey("producto")) {
                        producto = getIntent().getExtras().getSerializable("producto", Producto.class);
                        xNombre.setText(producto.getNombre());
                        xDescrip.setText(producto.getDescripcion());
                        xPrecio.setText(producto.getPrecio().toString());
                        switch1.setChecked(producto.getActivo());
                        spinPrepara.setSelection(buscaUsuario(producto.getPrepara_idperfil()));
                        spinServido.setSelection(buscaUsuario(producto.getSirve_idperfil()));

                        // set Alergenos
                        CollectionReference alRef = db.collection("alergenos");
                        alRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Alergeno a = document.toObject(Alergeno.class);
                                        cAlergenos.add(new cAlergeno(a, false));
                                    }
                                }
                            }
                        }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (cAlergeno c : cAlergenos) {
                                    for (Alergeno a : producto.getAlergenos()) {
                                        if (c.getAlergeno().getId().equals(a.getId())) {
                                            c.setChecked(true);
                                        }
                                    }
                                }
                                mostrarAls();
                                // cargar imagen
                                imgRef.child("productos").child(getImgName(producto)).getBytes(MAX_IMAGESIZE)
                                        .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                            @Override
                                            public void onSuccess(byte[] bytes) {
                                                imageview1.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                                            }
                                        });
                            }
                        });
                    }
                }
            }

        });


        // guardar producto
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Producto p = new Producto();

                p.setActivo(switch1.isChecked());
                p.setNombre(xNombre.getText().toString());
                p.setDescripcion(xDescrip.getText().toString());
                p.setPrecio( Float.parseFloat(xPrecio.getText().toString()));
                p.setPrepara_idperfil( usuarios.get(spinPrepara.getSelectedItemPosition()).getId());
                p.setSirve_idperfil( usuarios.get(spinServido.getSelectedItemPosition()).getId());
                p.setAlergenos(getAlergenos(cAlergenos));

                //guardar imagen

                // Update
                if (producto != null) {
                    p.setId(producto.getId());

                    HashMap<String, Object> data = new HashMap<String, Object>() {
                    };
                    data.put("activo", p.getActivo());
                    data.put("nombre",p.getNombre());
                    data.put("descripcion", p.getDescripcion());
                    data.put("precio", p.getPrecio());
                    data.put("id_departamento", p.getPrepara_idperfil());
                    data.put("id_prepara", p.getSirve_idperfil());
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
                                    //bitmap = Bitmap.createScaledBitmap(bitmap,50, 50, false);
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

    public void guardarImagen(Producto p, Bitmap bmp) {
        imgRef = storage.getReference().child("productos/"+getImgName(p));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imgRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });
    }

    private String getImgName(Producto p){
        return p.getId()+".jpg";
    }

    private Integer buscaUsuario(String id){
        Integer pos = 0;
        for(Integer x=0; x<usuarios.size(); x++){
            if(usuarios.get(x).getId().equals(id)){
                pos = x;
                break;
            }
        }

        return pos;
    }
}