package com.example.sporify;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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

        // Punto de entrada principal del Activity. Inicializa la interfaz y los recursos base.
        // Carga el listado de canciones, prepara el reproductor inicial, configura navegación
        // inferior y asigna comportamiento para cada sección.
        loadSongs();
        prepareSongAt(0);

        replaceFragment(playerFragment);
        toggleMiniPlayer(playerFragment);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            // Listener que gestiona la navegación interna con BottomNavigation.
            // Cambia el fragment, actualiza estado visual y si se ingresa al reproductor
            // aplica un fondo dinámico basado en la carátula.
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
        // Carga y registra en memoria la lista local de canciones incluida en el proyecto.
        // Limpia la lista previa y agrega cada pista con audio, portada, título y artista.
        songs.clear();

        songs.add(new Song(R.raw.song01_wake_me_up, R.drawable.portada_hurry_up_tomorrow,"Wake Me Up","TheWeeknd"));
        songs.add(new Song(R.raw.song02_cry_for_me, R.drawable.portada_hurry_up_tomorrow,"Cry For Me","TheWeeknd"));
        songs.add(new Song(R.raw.song03_i_cant_fucking_sing, R.drawable.portada_hurry_up_tomorrow,"I Can't Fucking Sing","TheWeeknd"));
        songs.add(new Song(R.raw.song04_the_god_of_lying, R.drawable.portada_the_montain,"The God of Lying","Gorillaz ft. IDLES"));
        songs.add(new Song(R.raw.song05_god_is, R.drawable.portada_jesus_is_king,"God Is","Kanye West"));
        songs.add(new Song(R.raw.song06_nominao, R.drawable.portada_madrilenio,"Nominao","C. Tangana"));
        songs.add(new Song(R.raw.song07_nube_negra, R.drawable.portada_nube_negra,"Nube Negra","The Gardener"));
    }

    /* ------------------ NAVEGACIÓN ------------------ */

    private void replaceFragment(Fragment fragment) {
        // Inyección de fragment en el contenedor principal del Activity.
        // No mantiene historial; simplemente reemplaza la vista activa.
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void toggleMiniPlayer(Fragment fragment) {
        // Control de visibilidad del mini reproductor. Si el usuario abre la pantalla
        // principal del reproductor, este componente se oculta para evitar redundancia UI.
        boolean hide = fragment instanceof PlayerFragment;
        binding.miniPlayer.getRoot().setVisibility(hide ? View.GONE : View.VISIBLE);
    }

    /* ------------------ REPRODUCTOR ------------------ */

    private void prepareSongAt(int index) {
        // Carga una canción en el MediaPlayer sin reproducirla directamente.
        // Reinicia la instancia anterior para liberar recursos y configura evento al finalizar.
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
        // Prepara una canción específica y la reproduce inmediatamente.
        // Actualiza visualmente el mini reproductor.
        prepareSongAt(index);
        mediaPlayer.start();
        updateMiniPlayerUI();
    }

    public void playSong(Song song) {
        // Recibe una canción directamente y la reproduce creando un nuevo MediaPlayer.
        // También refresca el mini reproductor y el fondo dinámico.
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
        // Alterna entre reproducir y pausar según el estado actual del player.
        // Refleja el cambio en el icono del mini reproductor.
        if (mediaPlayer == null) return;

        if (mediaPlayer.isPlaying()) mediaPlayer.pause();
        else mediaPlayer.start();

        updateMiniPlayerUI();
    }

    public void playNext() {
        // Salta automáticamente a la siguiente pista del catálogo.
        // Si está en la última, regresa al inicio (comportamiento circular).
        currentSongIndex++;
        if (currentSongIndex >= songs.size()) currentSongIndex = 0;
        playSongAt(currentSongIndex);
    }

    public void playPrevious() {
        // Retrocede a la pista anterior. Si se encuentra al inicio del listado,
        // salta a la última posición disponible.
        currentSongIndex--;
        if (currentSongIndex < 0) currentSongIndex = songs.size() - 1;
        playSongAt(currentSongIndex);
    }

    private void updateMiniPlayerUI() {
        // Actualiza título, artista, portada y estado del botón play/pause del mini player.
        // Vincula acciones del botón para mantener controles operativos.
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
        // Genera un fondo degradado en tiempo real usando colores dominantes de la carátula.
        // Mejora la experiencia visual del PlayerFragment.
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), coverId);

        Palette.from(bitmap).generate(palette -> {

            int colorTop = palette.getDominantColor(0xFF444444);
            int colorMiddle = palette.getVibrantColor(0xFF111111);
            int colorBottom = Color.parseColor("#2A2A2A");

            GradientDrawable gradient = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{colorTop, colorMiddle, colorBottom}
            );

            View bg = findViewById(R.id.playerBackground);
            if (bg != null) bg.setBackground(gradient);
        });
    }

    /* ------------------ GETTERS ------------------ */

    public ArrayList<Song> getSongs() {
        // Devuelve el listado completo de canciones disponibles.
        return songs;
    }

    public MediaPlayer getMediaPlayer() {
        // Expone el reproductor actual para permitir control externo.
        return mediaPlayer;
    }

    public boolean isPlaying() {
        // Informa si existe media activa en reproducción.
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public int getCurrentSongIndex() {
        // Retorna el índice del track que se está reproduciendo actualmente.
        return currentSongIndex;
    }
}
