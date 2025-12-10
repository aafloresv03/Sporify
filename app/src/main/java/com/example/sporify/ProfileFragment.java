package com.example.sporify;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
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

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    // Launcher para captura de imagen con cámara. Recibe el resultado y asigna el bitmap al avatar.
    private final ActivityResultLauncher<Intent> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                // Procesa el retorno de la cámara. Verifica éxito y asigna foto al ImageView.
                if (result.getResultCode() == Activity.RESULT_OK &&
                        result.getData() != null &&
                        result.getData().getExtras() != null) {

                    Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");

                    if (binding != null && bitmap != null) {
                        binding.imgAvatar.setImageBitmap(bitmap);
                    }
                }
            });

    // Launcher para solicitar permiso de cámara. En caso de aprobación, abre la cámara.
    private final ActivityResultLauncher<String> requestCameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                // Si concede permiso se procede con la camara, si no, se notifica rechazo.
                if (isGranted) {
                    abrirCamara();
                } else if (isAdded()) {
                    Toast.makeText(requireContext(),
                            "Permiso de cámara denegado",
                            Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla la vista del perfil, habilita binding y asigna la toolbar al activity.
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(binding.toolbar);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // Configura eventos UI tras la creación. Si el permiso existe toma foto,
        // si no, lanza solicitud de autorización.
        super.onViewCreated(view, savedInstanceState);

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
    }

    private void abrirCamara() {
        // Inicia intent para capturar foto con cámara usando ActivityResultLauncher.
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureLauncher.launch(intent);
    }
}
