package com.example.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.Lists.pojos.Lineas_Ticket;
import com.example.Lists.pojos.Ticket;
import com.example.proyector.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdapterLineaTicket extends ArrayAdapter<Lineas_Ticket> {
    public AdapterLineaTicket(Context context, ArrayList<Lineas_Ticket> lineasticket) {
        super(context, 0, lineasticket);
    }

    private AdapterLineaTicket.OnClickListener onClickListener;
    /*
    private static class ViewHolder {
        TextView nombre;
    }
    */

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Lineas_Ticket lineaTicket = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_linea_ticket, parent, false);
        }
        TextView xNombre = (TextView) convertView.findViewById(R.id.tvProducto);
        xNombre.setText(lineaTicket.getProducto().getNombre());

        TextView xCantidad = (TextView) convertView.findViewById(R.id.tvAmount);
        xCantidad.setText("" + lineaTicket.getCantidad() + "");

        ImageButton botonSumar = (ImageButton) convertView.findViewById(R.id.mas);
        ImageButton botonRestar = (ImageButton) convertView.findViewById(R.id.menos);
        ImageButton botonBorrar = (ImageButton) convertView.findViewById(R.id.borrar);


        botonSumar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cantidad = Integer.parseInt(xCantidad.getText().toString());
                int sum = cantidad + 1;
                lineaTicket.setCantidad(sum);
                notifyDataSetChanged();
            }
        });

        botonRestar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cantidad = Integer.parseInt(xCantidad.getText().toString());
                int resta = cantidad - 1;
                if (resta < 1) {
                    lineaTicket.setCantidad(1);
                    notifyDataSetChanged();
                } else {
                    lineaTicket.setCantidad(resta);
                    notifyDataSetChanged();
                }
            }
        });

        botonBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(lineaTicket);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    public void setOnClickListener(AdapterLineaTicket.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position, Ticket ticket);
    }
}
