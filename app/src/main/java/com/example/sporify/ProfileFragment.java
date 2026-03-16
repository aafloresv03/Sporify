package com.example.sporify;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.sporify.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private final ActivityResultLauncher<Intent> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK &&
                        result.getData() != null &&
                        result.getData().getExtras() != null) {

                    Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");

                    if (binding != null && bitmap != null) {
                        binding.imgAvatar.setImageBitmap(bitmap);
                    }
                }
            });

    private final ActivityResultLauncher<String> requestCameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    abrirCamara();
                } else if (isAdded()) {
                    Toast.makeText(requireContext(),
                            "Permiso de cámara denegado",
                            Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);

        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(binding.toolbar);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cargarDatosUsuario();
        actualizarEstadoSpotify();

        binding.btnChangePhoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED) {
                abrirCamara();
            } else {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        binding.btnSpotifyConnect.setOnClickListener(v -> {
            SpotifyManager.login(requireActivity());
        });
    }

    private void cargarDatosUsuario() {
        if (currentUser == null) {
            binding.txtName.setText("Usuario");
            binding.txtAlias.setText("No autenticado");
            return;
        }

        String name = currentUser.getDisplayName();
        String email = currentUser.getEmail();

        if (name == null || name.trim().isEmpty()) {
            name = "Usuario";
        }

        if (email == null || email.trim().isEmpty()) {
            email = "Sin email";
        }

        binding.txtName.setText(name);
        binding.txtAlias.setText(email);
    }

    private void actualizarEstadoSpotify() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("spotify", 0);
        boolean connected = prefs.getBoolean("connected", false);

        if (connected) {
            binding.tvSpotifyStatus.setText("Spotify conectado");
            binding.btnSpotifyConnect.setText("Reconectar Spotify");
        } else {
            binding.tvSpotifyStatus.setText("Spotify no conectado");
            binding.btnSpotifyConnect.setText("Conectar Spotify");
        }
    }

    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureLauncher.launch(intent);
    }
}