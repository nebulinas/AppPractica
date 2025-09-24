package com.example.apppractica;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder> {

    private List<ComputadoraHistorial> listaHistorial;
    public HistorialAdapter(List<ComputadoraHistorial> listaHistorial) {
        this.listaHistorial = listaHistorial;
    }

    @NonNull
    @Override
    public HistorialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_historial_card, parent, false);
        return new HistorialViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HistorialViewHolder holder, int position) {
        ComputadoraHistorial itemActual = listaHistorial.get(position);
        holder.tvNombreEquipo.setText(itemActual.getNombreEquipo());
        holder.tvNumeroActivo.setText("Activo: " + itemActual.getNumeroActivo());
        holder.tvAgencia.setText("Agencia: " + itemActual.getAgencia());
        holder.tvEncargado.setText("Encargado: " + itemActual.getEncargado());
        holder.tvIp.setText("IP: " + itemActual.getIp());
        holder.tvRam.setText("Tamaño Memoria RAM: " + itemActual.getRam());
        holder.tvWindows.setText("Versión Windows: " + itemActual.getWindows());
        holder.tvAntivirus.setText("Antivirus: " + itemActual.getAntivirus());
        holder.tvFechaCambio.setText("Fecha Cambio: " + itemActual.getFechaCambio());
    }

    @Override
    public int getItemCount() {
        return listaHistorial != null ? listaHistorial.size() : 0;
    }

    // Para actualizar la lista desde el Activity
    public void actualizarLista(List<ComputadoraHistorial> nuevaLista) {
        this.listaHistorial = nuevaLista;
        notifyDataSetChanged(); // Notificar al adaptador que los datos cambiaron
    }


    static class HistorialViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreEquipo, tvNumeroActivo, tvAgencia, tvEncargado, tvIp, tvRam, tvWindows, tvAntivirus, tvFechaCambio;

        HistorialViewHolder(View itemView) {
            super(itemView);
            tvNombreEquipo = itemView.findViewById(R.id.tv_card_equipo_nombre);
            tvNumeroActivo = itemView.findViewById(R.id.tv_card_activo_numero);
            tvAgencia = itemView.findViewById(R.id.tv_card_agencia);
            tvEncargado = itemView.findViewById(R.id.tv_card_encargado);
            tvIp = itemView.findViewById(R.id.tv_card_ip);
            tvRam = itemView.findViewById(R.id.tv_card_ram);
            tvWindows = itemView.findViewById(R.id.tv_card_windows);
            tvAntivirus = itemView.findViewById(R.id.tv_card_antivirus);
            tvFechaCambio = itemView.findViewById(R.id.tv_card_fecha_cambio);
        }
    }
}