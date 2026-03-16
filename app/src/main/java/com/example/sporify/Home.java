package com.example.sporify;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.sporify.databinding.ActivityHomeBinding;
import com.example.sporify.databinding.ComponentMiniPlayerBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private MediaPlayer mediaPlayer;

    private final Fragment homeFragment = new HomeFragment();
    private final Fragment profileFragment = new ProfileFragment();
    private final Fragment playlistFragment = new PlaylistFragment();
    private final Fragment playerFragment = new PlayerFragment();

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private FirebaseTrack currentRemoteTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            finish();
            return;
        }

        loadUserProfile();
        ensureDefaultPlaylist();

        replaceFragment(homeFragment);
        toggleMiniPlayer(homeFragment);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment f;
            int id = item.getItemId();

            if (id == R.id.home) {
                f = homeFragment;
            } else if (id == R.id.profile) {
                f = profileFragment;
            } else if (id == R.id.player) {
                f = playerFragment;
            } else if (id == R.id.playlist) {
                f = playlistFragment;
            } else {
                return false;
            }

            replaceFragment(f);
            toggleMiniPlayer(f);
            notifyPlayerFragment();
            return true;
        });
    }

    /* ------------------ FIREBASE ------------------ */

    private void loadUserProfile() {
        db.collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error cargando perfil", Toast.LENGTH_SHORT).show()
                );
    }

    private void ensureDefaultPlaylist() {
        db.collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(userDoc -> {
                    Boolean hasDefaultPlaylist = userDoc.getBoolean("hasDefaultPlaylist");

                    if (hasDefaultPlaylist == null || !hasDefaultPlaylist) {
                        Map<String, Object> playlist = new HashMap<>();
                        playlist.put("name", "Favoritos");
                        playlist.put("isDefault", true);
                        playlist.put("createdAt", FieldValue.serverTimestamp());

                        db.collection("users")
                                .document(currentUser.getUid())
                                .collection("playlists")
                                .document("favorites")
                                .set(playlist)
                                .addOnSuccessListener(unused -> {
                                    db.collection("users")
                                            .document(currentUser.getUid())
                                            .update("hasDefaultPlaylist", true);
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Error creando playlist", Toast.LENGTH_SHORT).show()
                                );
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error comprobando usuario", Toast.LENGTH_SHORT).show()
                );
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

    private void notifyPlayerFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment instanceof PlayerFragment) {
            ((PlayerFragment) fragment).syncUI();
        }
    }

    /* ------------------ REPRODUCTOR REMOTO ------------------ */

    public void playRemoteTrack(FirebaseTrack track) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }

            currentRemoteTrack = track;

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(track.getPreviewUrl());
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                updateMiniPlayerUI();
                notifyPlayerFragment();
                updatePlayerGradientFromUrl(track.getCoverUrl());
            });
            mediaPlayer.prepareAsync();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error reproduciendo audio", Toast.LENGTH_SHORT).show();
        }
    }

    public void togglePlayPause() {
        if (mediaPlayer == null) return;

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }

        updateMiniPlayerUI();
        notifyPlayerFragment();
    }

    private void updateMiniPlayerUI() {
        ComponentMiniPlayerBinding mini = binding.miniPlayer;
        if (mediaPlayer == null || currentRemoteTrack == null) return;

        mini.title.setText(currentRemoteTrack.getTitle());
        mini.subtitle.setText(currentRemoteTrack.getArtist());

        if (currentRemoteTrack.getCoverUrl() != null && !currentRemoteTrack.getCoverUrl().isEmpty()) {
            Glide.with(this)
                    .load(currentRemoteTrack.getCoverUrl())
                    .into(mini.art);
        } else {
            mini.art.setImageResource(R.mipmap.logo_img_round);
        }

        mini.btnPlay.setOnClickListener(v -> {
            togglePlayPause();
            notifyPlayerFragment();
        });

        mini.btnPlay.setImageResource(
                mediaPlayer.isPlaying() ? R.drawable.pause_icon : R.drawable.play_icon
        );
    }

    private void updatePlayerGradientFromUrl(String url) {
        if (url == null || url.isEmpty()) return;

        Glide.with(this)
                .asBitmap()
                .load(url)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap,
                                                @Nullable Transition<? super Bitmap> transition) {

                        Palette.from(bitmap).generate(palette -> {
                            if (palette == null) return;

                            int colorTop = palette.getDominantColor(0xFF222222);
                            int colorMid = palette.getVibrantColor(0xFF111111);
                            int colorBot = palette.getDarkMutedColor(0xFF000000);

                            GradientDrawable gradient = new GradientDrawable(
                                    GradientDrawable.Orientation.TOP_BOTTOM,
                                    new int[]{colorTop, colorMid, colorBot}
                            );

                            View bg = findViewById(R.id.playerBackground);
                            if (bg != null) {
                                bg.setBackground(gradient);
                            }
                        });
                    }

                    @Override
                    public void onLoadCleared(@Nullable android.graphics.drawable.Drawable placeholder) {
                    }
                });
    }

    /* ------------------ FAVORITOS ------------------ */

    public void addTrackToFavorites(FirebaseTrack track) {
        if (currentUser == null) return;

        db.collection("users")
                .document(currentUser.getUid())
                .collection("favorites")
                .document(track.getTrackId())
                .set(track);
    }

    /* ------------------ GETTERS ------------------ */

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public FirebaseTrack getCurrentRemoteTrack() {
        return currentRemoteTrack;
    }
}