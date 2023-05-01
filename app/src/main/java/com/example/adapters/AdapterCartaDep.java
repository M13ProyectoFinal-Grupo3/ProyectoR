package com.example.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Lists.pojos.Departamento;
import com.example.proyector.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterCartaDep extends RecyclerView.Adapter<AdapterCartaDep.MyHolder> {

    ArrayList<Departamento> data;
    Context context;

    private OnClickListener onClickListener;
    public AdapterCartaDep(ArrayList<Departamento> data) {
        this.data = data;
    }

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
        holder.cardImg.setImageDrawable(context.getDrawable(R.drawable.platocomida));

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

}