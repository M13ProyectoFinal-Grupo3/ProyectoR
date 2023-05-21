package com.example.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Lists.pojos.Departamento;
import com.example.proyector.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterCartaDep extends RecyclerView.Adapter<AdapterCartaDep.MyHolder> {

    ArrayList<Departamento> data;
    Context context;

    private OnClickListener onClickListener;
    public AdapterCartaDep(ArrayList<Departamento> data) {
        this.data = data;
    }

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference imgRef = storage.getReference();
    ImageView imageview1;

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dep_carta, parent, false);
        context = parent.getContext();
        return new MyHolder(view);
    }

    public interface OnClickListener {
        void onClick(int position, Departamento departamento);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        holder.cardText.setText(data.get(holder.getAdapterPosition()).getnombre());
        Drawable d =(Drawable) context.getDrawable(R.drawable.platocomida);
        // cargar imagen
        final long MAX_IMAGESIZE = 1024 * 1024;
        imgRef.child("departamentos").child(getImgName(data.get(holder.getAdapterPosition()))).getBytes(MAX_IMAGESIZE)
                .addOnCompleteListener(new OnCompleteListener<byte[]>() {
                    @Override
                    public void onComplete(@NonNull Task<byte[]> task) {
                        Bitmap bmp  = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                        holder.cardImg.setImageBitmap(bmp);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        holder.cardImg.setImageDrawable(context.getDrawable(R.drawable.icon_depto));
                    }
                });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null) {
                    onClickListener.onClick(holder.getAdapterPosition(), data.get(holder.getAdapterPosition()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        TextView cardText;
        ImageView cardImg;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            cardText = itemView.findViewById(R.id.cardText);
            cardImg = itemView.findViewById(R.id.cardImg);
        }
    }

    private String getImgName(Departamento d){
        return d.getId()+".jpg";
    }

}