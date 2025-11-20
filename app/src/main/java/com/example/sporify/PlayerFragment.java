package com.example.sporify;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sporify.databinding.FragmentPlayerBinding;

import java.util.ArrayList;

public class PlayerFragment extends Fragment {

    private FragmentPlayerBinding binding;
    private Home home;
    private ArrayList<Song> songs;

    private final Handler handler = new Handler();
    private Runnable progressRunnable;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPlayerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        home = (Home) requireActivity();
        songs = home.getSongs();

        setupControls();
        syncUI();
        startProgressUpdates();
    }

    private void setupControls() {
        binding.btnPlay.setOnClickListener(v -> {
            home.togglePlayPause();
            syncUI();
        });

        binding.btnNext.setOnClickListener(v -> {
            home.playNext();
            syncUI();
        });

        binding.btnPrev.setOnClickListener(v -> {
            home.playPrevious();
            syncUI();
        });
    }

    private void syncUI() {
        if (binding == null) return;

        int index = home.getCurrentSongIndex();
        Song s = songs.get(index);

        binding.albumArt.setImageResource(s.getCoverId());

        binding.btnPlay.setImageResource(
                home.isPlaying() ? R.drawable.pause_icon : R.drawable.play_icon
        );
    }

    private void startProgressUpdates() {
        handler.removeCallbacksAndMessages(null);

        progressRunnable = new Runnable() {
            @Override
            public void run() {
                if (binding == null) return;

                MediaPlayer mp = home.getMediaPlayer();

                if (mp != null && mp.isPlaying()) {
                    int pos = mp.getCurrentPosition();
                    int dur = mp.getDuration();

                    if (dur > 0) {
                        int progress = (int) ((pos * 100f) / dur);
                        binding.progressBar.setProgress(progress);
                    }
                }

                handler.postDelayed(this, 100);
            }
        };

        handler.post(progressRunnable);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(progressRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        startProgressUpdates();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(progressRunnable);
        binding = null;
    }
}
