package com.example.sporify;

import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sporify.databinding.ItemFirebaseTrackBinding;

import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;

public class FirebaseTrackAdapter extends RecyclerView.Adapter<FirebaseTrackAdapter.TrackViewHolder> {

    public interface OnTrackClickListener {
        void onTrackClick(FirebaseTrack track);
    }

    private final List<FirebaseTrack> tracks = new ArrayList<>();
    private final OnTrackClickListener listener;

    public FirebaseTrackAdapter(OnTrackClickListener listener) {
        this.listener = listener;
    }

    public void setTracks(List<FirebaseTrack> newTracks) {
        tracks.clear();
        tracks.addAll(newTracks);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemFirebaseTrackBinding binding = ItemFirebaseTrackBinding.inflate(inflater, parent, false);
        return new TrackViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder holder, int position) {
        holder.bind(tracks.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    static class TrackViewHolder extends RecyclerView.ViewHolder {

        private final ItemFirebaseTrackBinding binding;

        public TrackViewHolder(ItemFirebaseTrackBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(FirebaseTrack track, OnTrackClickListener listener) {
            binding.tvTitle.setText(track.getTitle() != null ? track.getTitle() : "Sin título");
            binding.tvArtist.setText(track.getArtist() != null ? track.getArtist() : "Sin artista");
            if (track.getCoverUrl() != null && !track.getCoverUrl().isEmpty()) {
                Glide.with(binding.imgCover.getContext())
                        .load(track.getCoverUrl())
                        .into(binding.imgCover);
            } else {
                binding.imgCover.setImageResource(R.mipmap.logo_img_round);
            }

            binding.getRoot().setOnClickListener(v -> listener.onTrackClick(track));

            binding.getRoot().setOnLongClickListener(v -> {
                if (v.getContext() instanceof Home) {
                    Home home = (Home) v.getContext();
                    home.addTrackToFavorites(track);

                    Toast.makeText(v.getContext(),
                            "Añadido a favoritos",
                            Toast.LENGTH_SHORT).show();
                }
                return true;
            });
        }
    }
}