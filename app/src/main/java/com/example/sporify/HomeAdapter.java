package com.example.sporify;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private final ArrayList<Song> songs;
    private final OnSongClick listener;

    public interface OnSongClick { void onClick(Song song); }

    public HomeAdapter(ArrayList<Song> songs, OnSongClick listener) {
        // Constructor del adaptador. Recibe lista de canciones y un listener para manejar clics.
        // Su función es suministrar datos al RecyclerView y recibir eventos de interacción.
        this.songs = songs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla el layout de cada tarjeta del listado y crea el ViewHolder que gestionará la vista.
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.component_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Vincula los datos de una canción con la tarjeta visual correspondiente.
        // Asigna portada, título, artista y registra acción de clic al elemento.
        Song s = songs.get(position);

        holder.cover.setImageResource(s.getCoverId());
        holder.title.setText(s.getTitle());
        holder.subtitle.setText(s.getArtist());

        holder.itemView.setOnClickListener(v -> listener.onClick(s));
    }

    @Override
    public int getItemCount() {
        // Retorna la cantidad total de elementos a mostrar en el listado.
        return songs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView cover;
        TextView title, subtitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Cachea referencias a las vistas internas para mejorar rendimiento
            // al reutilizar celdas en el RecyclerView.
            cover = itemView.findViewById(R.id.albumCover);
            title = itemView.findViewById(R.id.albumTitle);
            subtitle = itemView.findViewById(R.id.albumSubtitle);
        }
    }
}
