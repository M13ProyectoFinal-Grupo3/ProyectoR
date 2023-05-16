package com.example.proyector;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class StoreImage {

    ImageView imageview1;

    public StoreImage(ImageView image){
        imageview1 = image;
    }

    public void cargarImagen(String imageName, String myRef){
        final long MAX_IMAGESIZE = 1024 * 1024;
        imageName = imageName.replace(" ","")+".jpg";
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imgRef = storage.getReference();
        imgRef.child("productos").child(imageName).getBytes(MAX_IMAGESIZE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap  = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imageview1.setImageBitmap(bitmap);
                    }
                });
    }

    public void guardarImagen(String imageName, String myRef){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imgRef = storage.getReference().child(myRef);
        final imageName= imageName.replace(" ","")+".jpg";

        // comprobar si existe imagen asociada al producto y eliminarla antes de subir la nueva
        imgRef.listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
            @Override
            public void onComplete(@NonNull Task<ListResult> task) {
                if(task.getResult().getItems().size()>0) {
                    for(StorageReference file:task.getResult().getItems()){
                        if(file.getName().equals(imageName)){
                            StorageReference delRef = storage.getReference().child(myRef+"/"+imageName);
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
        imgRef = storage.getReference().child(myRef+"/"+imageName);

        Bitmap bitmap = getBitmapFromView(imageview1);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
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

    Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(
                view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
}
