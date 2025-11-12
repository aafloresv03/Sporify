package com.example.sporify;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sporify.databinding.ComponentCardBinding;
import com.example.sporify.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    final boolean[] playing = {false};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // Si albumCard YA es ComponentCardBinding (tooltip lo confirma), perfecto:
        ComponentCardBinding card = binding.albumCard;

        // Payload
        card.title.setText("Dark Mode Mix");
        card.author.setText("Andy");
        card.meta.setText("Electronic • 2025 • 14 tracks");
        // card.albumArt.setImageResource(R.drawable.mi_cover);
        card.playFab.setOnClickListener(v -> { /* play */ });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
