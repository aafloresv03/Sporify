package com.example.sporify;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sporify.databinding.FragmentPlaylistBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class PlaylistFragment extends Fragment {

    private FragmentPlaylistBinding binding;
    private FirebaseTrackAdapter adapter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentPlaylistBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        setupRecycler();
        loadFavorites();
    }

    private void setupRecycler() {

        binding.recyclerFavorites.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        adapter = new FirebaseTrackAdapter(track -> {
            Home home = (Home) requireActivity();
            home.playRemoteTrack(track);
        });

        binding.recyclerFavorites.setAdapter(adapter);
    }

    private void loadFavorites() {

        String uid = mAuth.getCurrentUser().getUid();

        db.collection("users")
                .document(uid)
                .collection("favorites")
                .get()
                .addOnSuccessListener(snapshot -> {

                    ArrayList<FirebaseTrack> list = new ArrayList<>();

                    for (var doc : snapshot.getDocuments()) {
                        FirebaseTrack t = doc.toObject(FirebaseTrack.class);
                        if (t != null) list.add(t);
                    }

                    adapter.setTracks(list);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(),
                                "Error cargando favoritos",
                                Toast.LENGTH_SHORT).show());
    }
}