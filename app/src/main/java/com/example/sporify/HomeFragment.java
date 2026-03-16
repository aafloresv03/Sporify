package com.example.sporify;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sporify.databinding.FragmentHomeBinding;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FirebaseTrackAdapter adapter;
    private Home home;

    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        home = (Home) requireActivity();

        setupList();
        setupLiveSearch();

        loadItunesTracks("mix");
    }

    private void setupList() {
        binding.rvAlbums.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        );

        adapter = new FirebaseTrackAdapter(track -> {
            home.playRemoteTrack(track);
        });

        binding.rvAlbums.setAdapter(adapter);
    }

    private void setupLiveSearch() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // no-op
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }

                searchRunnable = () -> {
                    String query = s.toString().trim();

                    if (query.length() < 2) {
                        return;
                    }

                    loadItunesTracks(query);
                };

                searchHandler.postDelayed(searchRunnable, 300);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // no-op
            }
        });
    }

    private void loadItunesTracks(String query) {
        ItunesServices.search(requireContext(), query, new ItunesServices.ItunesCallback() {
            @Override
            public void onResult(ArrayList<FirebaseTrack> tracks) {
                adapter.setTracks(tracks);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }

        binding = null;
    }
}