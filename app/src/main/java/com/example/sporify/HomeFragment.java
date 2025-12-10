package com.example.sporify;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sporify.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Home home;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla el layout del fragmento y habilita acceso al binding para manipular la UI.
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        // Se ejecuta después de la creación de la vista. Obtiene referencia al activity,
        // permitiendo acceso a canciones y control del reproductor. Inicializa el listado.
        super.onViewCreated(v, savedInstanceState);
        home = (Home) requireActivity();
        setupList();
    }

    private void setupList() {
        // Configura el RecyclerView del Home en modo vertical, asigna el adaptador y define
        // la acción al pulsar una canción, delegando reproducción al Activity principal.
        binding.rvAlbums.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        );

        HomeAdapter adapter = new HomeAdapter(home.getSongs(), song -> {
            home.playSong(song);
        });

        binding.rvAlbums.setAdapter(adapter);
    }
}
