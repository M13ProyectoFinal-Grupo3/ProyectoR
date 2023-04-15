package com.example.proyector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.Forms.FormProducto;
import com.example.Lists.ListAlergenos;
import com.example.adapters.AdapterAlergeno;
import com.example.pojos.Alergeno;
import com.example.pojos.Producto;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.*;

import java.io.IOException;
import java.util.ArrayList;


public class SelectImage extends Fragment {

    private ImgInterface listener;

    Uri imageUri;
    ImageView foto_gallery;

    ArrayList<Producto> lista;

    ArrayList<StorageReference> listStorage;
    ActivityResultLauncher<Intent> launchSomeActivity;

    ImageView imageView1;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ImgInterface) {
            listener = (ImgInterface) context;
        }
    }

    @Override
    public void onDetach() {
        listener = null;
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_image, container, false);

        imageView1 = (ImageView) view.findViewById(R.id.imageViewSel1);

// lista de imagenes en storage

        listStorage = new ArrayList<>();
        lista = new ArrayList<>();


        ActivityResultLauncher<Intent> startActivityGaleria= registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // Recibe objeto alergeno
                            Intent data = result.getData();
                            // do your operation from here....
                            if (data != null
                                    && data.getData() != null) {
                                Uri selectedImageUri = data.getData();
                                Bitmap selectedImageBitmap=null;
                                try {
                                    selectedImageBitmap
                                            = MediaStore.Images.Media.getBitmap(
                                            getContext().getContentResolver(),
                                            selectedImageUri);
                                }
                                catch (IOException e) {
                                    e.printStackTrace();
                                }
                                listener.devData(selectedImageBitmap);
                                imageView1.setImageBitmap(
                                        selectedImageBitmap);
                            }
                        }else{
                            //No recibe información.

                        }
                    }
                });

        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityGaleria.launch(i);


        return view;

    }

    public static Bitmap byteToBitmap(byte[] b) {
        return (b == null || b.length == 0) ? null : BitmapFactory
                .decodeByteArray(b, 0, b.length);
    }


}