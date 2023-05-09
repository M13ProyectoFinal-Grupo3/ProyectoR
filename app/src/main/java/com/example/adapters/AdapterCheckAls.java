package com.example.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.Lists.pojos.Alergeno;
import com.example.Lists.pojos.cAlergeno;
import com.example.proyector.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterCheckAls extends ArrayAdapter<cAlergeno> {
    CheckBox check1;
    ArrayList<cAlergeno> alergenos;

    public AdapterCheckAls(Context context, ArrayList<cAlergeno> alergenos) {
        super( context,0,alergenos);
        this.alergenos = alergenos;
    }

    private static class ViewHolder {
        TextView nombre;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        cAlergeno a = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_check, parent, false);
        }
        check1 = (CheckBox) convertView.findViewById(R.id.check1);
        check1.setText(a.getAlergeno().getNombre());
        check1.setChecked(alergenos.get(position).getChecked());

        check1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alergenos.get(position).setChecked(!alergenos.get(position).getChecked());
            }
        });

        return convertView;
    }

    public ArrayList<cAlergeno> getcAlergenos(){
        return alergenos;
    }

    public ArrayList<String> getCheckedS(){
        ArrayList<String> result =new ArrayList<>();
        for (cAlergeno a : alergenos) {
            if(a.getChecked()) result.add(a.getAlergeno().getNombre());
        }
        return result;
    }

}

