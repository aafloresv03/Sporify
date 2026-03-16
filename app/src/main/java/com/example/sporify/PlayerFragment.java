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

import com.bumptech.glide.Glide;
import com.example.sporify.databinding.FragmentPlayerBinding;

public class PlayerFragment extends Fragment {

    private FragmentPlayerBinding binding;
    private Home home;

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

        setupControls();
        syncUI();
        startProgressUpdates();
    }

    private void setupControls() {
        binding.btnPlay.setOnClickListener(v -> {
            home.togglePlayPause();
            syncUI();
        });

        binding.btnNext.setVisibility(View.GONE);
        binding.btnPrev.setVisibility(View.GONE);
    }

    public void syncUI() {
        if (binding == null) return;

        FirebaseTrack track = home.getCurrentRemoteTrack();
        if (track == null) return;

        binding.songTitle.setText(track.getTitle());
        binding.songArtist.setText(track.getArtist());

        if (track.getCoverUrl() != null && !track.getCoverUrl().isEmpty()) {
            Glide.with(requireContext())
                    .load(track.getCoverUrl())
                    .into(binding.albumArt);
        } else {
            binding.albumArt.setImageResource(R.mipmap.logo_img_round);
        }

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

                if (mp != null) {
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
}