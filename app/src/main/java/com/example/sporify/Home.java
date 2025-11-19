package com.example.sporify;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.sporify.databinding.ActivityHomeBinding;

public class Home extends AppCompatActivity {

    private ActivityHomeBinding binding;

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

        // Fragment inicial
        replaceFragment(playerFragment);
        toggleMiniPlayer(playerFragment);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment;

            int id = item.getItemId();
            if (id == R.id.home) {
                selectedFragment = homeFragment;
            } else if (id == R.id.profile) {
                selectedFragment = profileFragment;
            } else if (id == R.id.player) {
                selectedFragment = playerFragment;
            } else if (id == R.id.playlist) {
                selectedFragment = playlistFragment;
            } else {
                return false;
            }

            replaceFragment(selectedFragment);
            toggleMiniPlayer(selectedFragment);

            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void toggleMiniPlayer(Fragment fragment) {
        // IMPORTANTE: miniPlayer viene de binding INCLUDE → NO SE DECLARA A MANO
        if (fragment instanceof PlayerFragment) {
            binding.miniPlayer.getRoot().setVisibility(View.GONE);
        } else {
            binding.miniPlayer.getRoot().setVisibility(View.VISIBLE);
        }
    }
}
