package com.example.sporify;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.sporify.databinding.ActivityLoginBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Login extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private static final String USERS_FILE = "users.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ensureUsersFileExists();

        // 🔥 Cargar LoginFragment al iniciar
        loadFragment(new LoginFragment());
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .commit();
    }

    private void ensureUsersFileExists() {
        File file = new File(getFilesDir(), USERS_FILE);

        if (!file.exists()) {
            try {
                FileOutputStream fos = openFileOutput(USERS_FILE, MODE_PRIVATE);
                fos.write("".getBytes());
                fos.close();
                Log.d("FILE_CHECK", "users.txt creado correctamente");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("FILE_CHECK", "Error creando users.txt");
            }
        } else {
            Log.d("FILE_CHECK", "users.txt ya existe");
        }
    }

    // 🔹 Llamado desde los fragments para cambiar de pantalla
    public void switchToRegister() {
        loadFragment(new RegisterFragment());
    }

    public void switchToLogin() {
        loadFragment(new LoginFragment());
    }
}
