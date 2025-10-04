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

        String tipo = itemActual.getTipo();
        holder.tvTipo.setText("Tipo: " + tipo);

        holder.tvNombreEquipo.setText(itemActual.getNombreEquipo());
        holder.tvNumeroActivo.setText("Activo: " + itemActual.getNumeroActivo());
        holder.tvAgencia.setText("Agencia: " + itemActual.getAgencia());
        holder.tvEncargado.setText("Encargado: " + itemActual.getEncargado());

        if ("COMPUTADORA".equals(tipo)) {
            holder.tvIp.setText("IP: " + itemActual.getIp());
            holder.tvRam.setText("Tamaño Memoria RAM: " + itemActual.getRam());
            holder.tvWindows.setText("Versión Windows: " + itemActual.getWindows());
            holder.tvAntivirus.setText("Antivirus: " + itemActual.getAntivirus());
            holder.tvFechaCambio.setText("Fecha Registro: " + itemActual.getFechaCambio());



            holder.tvMarca.setVisibility(View.GONE);
            holder.tvModelo.setVisibility(View.GONE);
            holder.tvSerie.setVisibility(View.GONE);
            holder.tvEstado.setVisibility(View.GONE);
            holder.tvFechaRegistro.setVisibility(View.GONE);
            holder.tvFechaAdquisicion.setVisibility(View.GONE);
            holder.separatorUtilitarios.setVisibility(View.GONE);


        } else {
            holder.tvIp.setVisibility(View.GONE);
            holder.tvRam.setVisibility(View.GONE);
            holder.tvWindows.setVisibility(View.GONE);
            holder.tvAntivirus.setVisibility(View.GONE);
            holder.tvFechaCambio.setVisibility(View.GONE);
            holder.separatorUtilitarios.setVisibility(View.GONE);


            holder.tvMarca.setVisibility(View.VISIBLE);
            holder.tvMarca.setText("Marca: " + (itemActual.getMarca() !=null ? itemActual.getMarca() : "N/A"));
            holder.tvModelo.setVisibility(View.VISIBLE);
            holder.tvModelo.setText("Modelo: " + (itemActual.getModelo() !=null ? itemActual.getModelo() : "N/A"));
            holder.tvSerie.setVisibility(View.VISIBLE);
            holder.tvSerie.setText("Serie: " + (itemActual.getSerie() !=null ? itemActual.getSerie() : "N/A"));
            holder.tvEstado.setVisibility(View.VISIBLE);
            holder.tvEstado.setText("Estado: " + (itemActual.getEstado() !=null ? itemActual.getEstado() : "N/A"));
            holder.tvFechaRegistro.setVisibility(View.VISIBLE);
            holder.tvFechaRegistro.setText("Fecha Registro: " + (itemActual.getFechaRegistro() !=null ? itemActual.getFechaRegistro() : "N/A"));
            holder.tvFechaAdquisicion.setVisibility(View.VISIBLE);
            holder.tvFechaAdquisicion.setText("Fecha Adquisición: " + (itemActual.getFechaAdquisicion() !=null ? itemActual.getFechaAdquisicion() : "N/A"));


        }
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
        TextView tvNombreEquipo, tvNumeroActivo, tvAgencia, tvEncargado, tvIp, tvRam, tvWindows, tvAntivirus, tvFechaCambio,
                    tvTipo, tvMarca, tvModelo, tvSerie, tvEstado, tvFechaRegistro, tvFechaAdquisicion;
        View separatorUtilitarios;
        HistorialViewHolder(View itemView) {
            super(itemView);
            separatorUtilitarios = itemView.findViewById(R.id.separatorUtilitarios);
            tvTipo = itemView.findViewById(R.id.tv_card_tipo);
            tvNombreEquipo = itemView.findViewById(R.id.tv_card_equipo_nombre);
            tvNumeroActivo = itemView.findViewById(R.id.tv_card_activo_numero);
            tvAgencia = itemView.findViewById(R.id.tv_card_agencia);
            tvEncargado = itemView.findViewById(R.id.tv_card_encargado);
            tvIp = itemView.findViewById(R.id.tv_card_ip);
            tvRam = itemView.findViewById(R.id.tv_card_ram);
            tvWindows = itemView.findViewById(R.id.tv_card_windows);
            tvAntivirus = itemView.findViewById(R.id.tv_card_antivirus);
            tvFechaCambio = itemView.findViewById(R.id.tv_card_fecha_cambio);

            tvMarca = itemView.findViewById(R.id.tv_card_marca);
            tvModelo = itemView.findViewById(R.id.tv_card_modelo);
            tvSerie = itemView.findViewById(R.id.tv_card_serie);
            tvEstado = itemView.findViewById(R.id.tv_card_estado);
            tvFechaRegistro=itemView.findViewById(R.id.tv_card_fecha_registro);
            tvFechaAdquisicion = itemView.findViewById(R.id.tv_card_fecha_adquisicion);
        }
        }
    }
