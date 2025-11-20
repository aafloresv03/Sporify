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
        this.songs = songs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.component_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song s = songs.get(position);

        holder.cover.setImageResource(s.getCoverId());
        holder.title.setText(s.getTitle());
        holder.subtitle.setText(s.getArtist());

        holder.itemView.setOnClickListener(v -> listener.onClick(s));
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView cover;
        TextView title, subtitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.albumCover);
            title = itemView.findViewById(R.id.albumTitle);
            subtitle = itemView.findViewById(R.id.albumSubtitle);
        }
    }
}
