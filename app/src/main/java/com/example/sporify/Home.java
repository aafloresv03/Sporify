package com.example.sporify;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;

import com.example.sporify.databinding.ActivityHomeBinding;
import com.example.sporify.databinding.ComponentMiniPlayerBinding;

import java.util.ArrayList;

public class Home extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private MediaPlayer mediaPlayer;
    private int currentSongIndex = 0;

    private final ArrayList<Song> songs = new ArrayList<>();

    private final Fragment homeFragment = new HomeFragment();
    private final Fragment profileFragment = new ProfileFragment();
    private final Fragment playlistFragment = new PlaylistFragment();
    private final Fragment playerFragment = new PlayerFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadSongs();
        prepareSongAt(0);

        replaceFragment(playerFragment);
        toggleMiniPlayer(playerFragment);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment f;

            int id = item.getItemId();
            if (id == R.id.home) f = homeFragment;
            else if (id == R.id.profile) f = profileFragment;
            else if (id == R.id.player) f = playerFragment;
            else if (id == R.id.playlist) f = playlistFragment;
            else return false;

            replaceFragment(f);
            toggleMiniPlayer(f);

            if (f instanceof PlayerFragment) {
                Song s = songs.get(currentSongIndex);
                updatePlayerGradient(s.getCoverId());
            }

            return true;
        });
    }

    /* ------------------ CARGA DE DATOS ------------------ */

    private void loadSongs() {
        songs.clear();

        songs.add(new Song(
                R.raw.song01_wake_me_up,
                R.drawable.portada_hurry_up_tomorrow,
                "Wake Me Up",
                "TheWeeknd"
        ));

        songs.add(new Song(
                R.raw.song02_cry_for_me,
                R.drawable.portada_hurry_up_tomorrow,
                "Cry For Me",
                "TheWeeknd"
        ));

        songs.add(new Song(
                R.raw.song03_i_cant_fucking_sing,
                R.drawable.portada_hurry_up_tomorrow,
                "I Can't Fucking Sing",
                "TheWeeknd"
        ));

        songs.add(new Song(
                R.raw.song04_the_god_of_lying,
                R.drawable.portada_the_montain,
                "The God of Lying",
                "Gorillaz"
        ));
    }

    /* ------------------ NAVEGACIÓN ------------------ */

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void toggleMiniPlayer(Fragment fragment) {
        boolean hide = fragment instanceof PlayerFragment;
        binding.miniPlayer.getRoot().setVisibility(hide ? View.GONE : View.VISIBLE);
    }

    /* ------------------ REPRODUCTOR ------------------ */

    private void prepareSongAt(int index) {
        if (index < 0 || index >= songs.size()) return;

        currentSongIndex = index;

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        mediaPlayer = MediaPlayer.create(this, songs.get(index).getRawId());
        mediaPlayer.setOnCompletionListener(mp -> playNext());

        updateMiniPlayerUI();
        updatePlayerGradient(songs.get(index).getCoverId());
    }

    public void playSongAt(int index) {
        prepareSongAt(index);
        mediaPlayer.start();
        updateMiniPlayerUI();
    }

    public void playSong(Song song) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        mediaPlayer = MediaPlayer.create(this, song.getRawId());
        mediaPlayer.start();

        updateMiniPlayerUI();
        updatePlayerGradient(song.getCoverId());
    }

    public void togglePlayPause() {
        if (mediaPlayer == null) return;

        if (mediaPlayer.isPlaying()) mediaPlayer.pause();
        else mediaPlayer.start();

        updateMiniPlayerUI();
    }

    public void playNext() {
        currentSongIndex++;
        if (currentSongIndex >= songs.size()) currentSongIndex = 0;
        playSongAt(currentSongIndex);
    }

    public void playPrevious() {
        currentSongIndex--;
        if (currentSongIndex < 0) currentSongIndex = songs.size() - 1;
        playSongAt(currentSongIndex);
    }

    private void updateMiniPlayerUI() {
        ComponentMiniPlayerBinding mini = binding.miniPlayer;
        if (mediaPlayer == null) return;

        Song current = songs.get(currentSongIndex);

        mini.title.setText(current.getTitle());
        mini.subtitle.setText(current.getArtist());
        mini.art.setImageResource(current.getCoverId());

        mini.btnPlay.setOnClickListener(v -> togglePlayPause());
        mini.btnPlay.setImageResource(
                mediaPlayer.isPlaying() ? R.drawable.pause_icon : R.drawable.play_icon
        );
    }

    /* ------------------ GRADIENTE DINÁMICO ------------------ */

    private void updatePlayerGradient(int coverId) {

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), coverId);

        Palette.from(bitmap).generate(palette -> {

            int colorTop = palette.getDominantColor(0xFF444444);
            int colorBottom = palette.getDarkMutedColor(0xFF111111);

            GradientDrawable gradient = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{colorTop, colorBottom}
            );

            View bg = findViewById(R.id.playerBackground);
            if (bg != null) bg.setBackground(gradient);
        });
    }

    /* ------------------ GETTERS ------------------ */

    public ArrayList<Song> getSongs() { return songs; }
    public MediaPlayer getMediaPlayer() { return mediaPlayer; }
    public boolean isPlaying() { return mediaPlayer != null && mediaPlayer.isPlaying(); }
    public int getCurrentSongIndex() { return currentSongIndex; }
}
