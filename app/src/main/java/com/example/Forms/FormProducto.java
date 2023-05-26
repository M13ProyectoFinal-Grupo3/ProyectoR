package com.example.Forms;

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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import com.example.Lists.pojos.Departamento;
import com.example.Lists.pojos.Restaurante;
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
import java.util.ArrayList;
import java.util.HashMap;

public class FormProducto extends AppCompatActivity {
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    CollectionReference rootRef;
    Restaurante restaurante1;
    Departamento departamento1;

    Producto producto = null;
    EditText xNombre;
    EditText xDescrip ;
    EditText xPrecio;
    TextView txDepartamento;
    ImageView imageview1;
    Spinner spinPrepara;
    Spinner spinServido;
    Switch switch1;

    Usuarios userPrepara;
    Usuarios userSirve;

    ArrayList<Usuarios> usuarios;
    ArrayAdapter adapter1;
    ArrayAdapter adapter2;
    ArrayList<String> sUsers;


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
        getSupportActionBar().hide();

        cAlergenos = new ArrayList<>();

        ImageButton btnBorrar = (ImageButton) findViewById(R.id.btn_borrarProd);
        Button btnGuardar = (Button) findViewById(R.id.btnGuardarDepto);
        ImageButton btnFoto = (ImageButton) findViewById(R.id.btnImgDepto);
        ImageButton btnAlergenos = (ImageButton) findViewById(R.id.btn_pAlergenos);

        txDepartamento = (TextView) findViewById(R.id.TxDepartamento);
        spinPrepara = (Spinner) findViewById(R.id.spinner1);
        spinServido = (Spinner) findViewById(R.id.spinner2);
        xNombre = (EditText) findViewById(R.id.TexNomDepto);
        xDescrip = (EditText) findViewById(R.id.t_pDescripcion);
        xPrecio = (EditText) findViewById(R.id.t_pPrecio);
        imageview1 =(ImageView) findViewById(R.id.ImgDepto);
        tAlergs = (TextView) findViewById(R.id.txFprodAls);
        switch1 = (Switch) findViewById(R.id.switch1);

        usuarios = new ArrayList<>();

        Intent intent = getIntent();
        if (intent.getExtras() != null) {

            if (intent.getExtras().containsKey("departamento")) {
                departamento1 = getIntent().getSerializableExtra("departamento", Departamento.class);
                txDepartamento.setText("Departamento: " + departamento1.getnombre());
            }

            if (intent.getExtras().containsKey("restaurante")) {
                restaurante1 = getIntent().getSerializableExtra("restaurante", Restaurante.class);
            } else {
                finish();
            }

            rootRef = db.collection("restaurante").document(restaurante1.getId()).collection("Carta").document("carta")
                    .collection("Departamentos").document(departamento1.getId()).collection("productos");

            if (intent.getExtras().containsKey("producto")) {
                producto = getIntent().getExtras().getSerializable("producto", Producto.class);
            }
        }

        // set perfiles
        CollectionReference perfilRef = db.collection("usuarios");
        perfilRef.whereEqualTo("Restaurante",restaurante1.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot doc: task.getResult()){
                        usuarios.add(doc.toObject(Usuarios.class));
                        Log.d("usuario",doc.toObject(Usuarios.class).toString());
                    }

                    sUsers = new ArrayList<>();
                    for(Usuarios u: usuarios){
                        sUsers.add(u.getPerfil());
                    }

                    adapter1 = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_spinner, sUsers);
                    spinPrepara.setAdapter(adapter1);
                    spinPrepara.setSelection(0);
                    adapter2 = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_spinner, sUsers);
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

                if (producto != null) {
                    switch1.setChecked(producto.getActivo());
                    xNombre.setText(producto.getNombre());
                    xDescrip.setText(producto.getDescripcion());
                    xPrecio.setText(producto.getPrecio().toString());
                    switch1.setChecked(producto.getActivo());
                    Log.d("preapra",""+buscaUsuario(producto.getPrepara_idperfil()));
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
                } else {
                    btnBorrar.setEnabled(false);
                    switch1.setChecked(true);
                }
            }
        });


        // guardar producto
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPrepara = usuarios.get(spinPrepara.getSelectedItemPosition());
                userSirve = usuarios.get(spinServido.getSelectedItemPosition());
/*                Log.d("spin",spinPrepara.getText().toString());
                for( Integer i=0;i<usuarios.size();i++){
                    if(usuarios.get(i).getPerfil().equals(spinPrepara.getText().toString())){
                        userPrepara = usuarios.get(i);
                        break;
                    }
                }

                for( Integer i=0;i<usuarios.size();i++){
                    if(usuarios.get(i).getPerfil().equals(spinPrepara.getText().toString())){
                        userSirve = usuarios.get(i);
                        break;
                    }
                }*/
                Log.d("userP",userPrepara.toString());
                if(userPrepara == null || userSirve == null){
                    Toast.makeText(FormProducto.this, "ERROR: Debe seleccionar los perfiles de preparación y servicio", Toast.LENGTH_SHORT).show();
                    return;
                }

                Producto p = new Producto();

                p.setActivo(switch1.isChecked());
                p.setNombre(xNombre.getText().toString());
                p.setDescripcion(xDescrip.getText().toString());
                p.setPrecio( Float.parseFloat(xPrecio.getText().toString()));
                p.setPrepara_idperfil( userPrepara.getId());
                p.setSirve_idperfil( userSirve.getId());
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
                    rootRef.document(producto.getId()).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                    rootRef.whereNotEqualTo("nombre",p.getNombre())
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    rootRef.add(p).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            p.setId(task.getResult().getId());
                                            rootRef.document(p.getId()).update("id",p.getId());

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
                rootRef.document( producto.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
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

    private Integer buscaPerfilUsuario(String perfil){
        Integer pos = 0;
        for(Integer x=0; x<usuarios.size(); x++){
            if(usuarios.get(x).getPerfil().equals(perfil)){
                pos = x;
                break;
            }
        }

        return pos;
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