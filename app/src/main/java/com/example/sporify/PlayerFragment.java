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
        // Infla la vista del player y habilita acceso al binding de la interfaz.
        binding = FragmentPlayerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Se ejecuta tras crear la vista. Obtiene referencia al Activity principal, lista de canciones,
        // configura controles de reproducción, sincroniza el estado visual e inicia actualización del progreso.
        super.onViewCreated(view, savedInstanceState);

        home = (Home) requireActivity();
        songs = home.getSongs();

        setupControls();
        syncUI();
        startProgressUpdates();
    }

    private void setupControls() {
        // Asigna acciones a los botones del reproductor: play/pause, siguiente y anterior.
        // Tras cada acción se sincroniza la interfaz para reflejar el estado actual.
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
        // Sincroniza la información visual con la canción actual: portada, título,
        // artista y estado del botón de reproducción.
        if (binding == null) return;

        int index = home.getCurrentSongIndex();
        Song s = songs.get(index);

        binding.albumArt.setImageResource(s.getCoverId());
        binding.songArtist.setText(s.getArtist());
        binding.songTitle.setText(s.getTitle());

        binding.btnPlay.setImageResource(
                home.isPlaying() ? R.drawable.pause_icon : R.drawable.play_icon
        );
    }

    private void startProgressUpdates() {
        // Inicia un ciclo periódico que actualiza la barra de progreso del reproductor.
        // Verifica posición actual versus duración y refresca la UI cada 100 ms.
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
}
