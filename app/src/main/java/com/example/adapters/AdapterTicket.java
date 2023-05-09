package com.example.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.Lists.pojos.Alergeno;
import com.example.Lists.pojos.Ticket;
import com.example.proyector.R;

import java.util.ArrayList;

public class AdapterTicket extends ArrayAdapter<Ticket> {
    public AdapterTicket(Context context, ArrayList<Ticket> tickets) {
        super( context,0,tickets);
    }

    private static class ViewHolder {
        TextView nombre;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Ticket t = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_ticket, parent, false);
        }
        TextView xNombre = (TextView) convertView.findViewById(R.id.txDescrip);
        xNombre.setText("Ticket mesa " + t.getNum_mesa() +  "");

        return convertView;
    }
}
