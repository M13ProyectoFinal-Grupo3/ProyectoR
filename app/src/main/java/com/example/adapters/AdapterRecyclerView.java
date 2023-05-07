package com.example.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.Lists.pojos.Lineas_Ticket;
import com.example.proyector.R;

import java.util.List;

public class AdapterRecyclerView extends RecyclerView.Adapter<AdapterRecyclerView.ViewHolder> {

    int contador = 0;

    private List<Lineas_Ticket> list;
    private static OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nombre, cantidad, observaciones;
        private ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            nombre = (TextView) itemView.findViewById(R.id.nombre);
            cantidad = (TextView) itemView.findViewById(R.id.cantidad);
            observaciones = (TextView) itemView.findViewById(R.id.observaciones);
            imageView = itemView.findViewById(R.id.activo_image_view);

            // Establecer el listener en el ImageView
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        // Obtener la posición del elemento pulsado
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            // Llamar al método onItemClick del listener
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public List<Lineas_Ticket> lineasTicketLista;

    public AdapterRecyclerView(List<Lineas_Ticket> lineasTicketLista) {
        this.lineasTicketLista = lineasTicketLista;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gestion_comandas, parent, false);
        view.setId(contador);
        contador++;
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //holder.nombre.setText("NOMBRE: " + (String.valueOf(lineasTicketLista.get(position).getId_producto())));
        holder.cantidad.setText("Cantidad: " + (String.valueOf(lineasTicketLista.get(position).getCantidad())));
        holder.observaciones.setText("Observaciones: " + lineasTicketLista.get(position).getObservaciones());
    }

    @Override
    public int getItemCount() {
        return lineasTicketLista.size();
    }


}
