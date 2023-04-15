package com.example.proyector;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.adapters.ImageAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SelectStorage extends Fragment {
    private RecyclerView recyclerView;


    FirebaseStorage storage = FirebaseStorage.getInstance();
    ArrayList<String> imagelist;

    ImageAdapter adapter;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_select_storage, container, false);

        Button btnGaleria = (Button) view.findViewById(R.id.btn_galeria);

        StorageReference listRef = storage.getReference().child("productos");

        imagelist = new ArrayList<>();

        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {


                        if(listResult.getItems().size()>0) {
                            for(StorageReference file:listResult.getItems()){
                                file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        imagelist.add(uri.toString());
                                        Log.e("Itemvalue",uri.toString());
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        recyclerView = view.findViewById(R.id.recyclerview);
                                        progressBar=view.findViewById(R.id.progress);
                                        progressBar.setVisibility(View.VISIBLE);
                                        adapter=new ImageAdapter(imagelist,getContext());
                                        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                                        recyclerView.setAdapter(adapter);
                                        progressBar.setVisibility(View.GONE);

                                    }
                                });
                            }
                        }
                    }});

        btnGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        return view;
    }


}

