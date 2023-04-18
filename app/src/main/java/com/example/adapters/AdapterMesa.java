package com.example.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.pojos.Mesa;
import com.example.proyector.R;

import java.util.ArrayList;

public class AdapterMesa extends ArrayAdapter<Mesa> {
    public AdapterMesa(Context context, ArrayList<Mesa> mesas) {
        super(context, 0, mesas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Mesa a = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_mesa, parent, false);
        }
        TextView xNumMesa = (TextView) convertView.findViewById(R.id.txANumMesa);
        xNumMesa.setText("Mesa número: " +a.getNum_mesa());

        return convertView;
    }
}