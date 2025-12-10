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
        // Punto de entrada de la pantalla de autenticación. Activa el EdgeToEdge,
        // inicializa el binding, verifica existencia del archivo de usuarios
        // y muestra por defecto el fragment de login.
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ensureUsersFileExists();
        loadFragment(new LoginFragment());
    }

    private void loadFragment(Fragment fragment) {
        // Sustituye el fragmento actual del contenedor por el fragmento recibido.
        // Facilita navegación entre login y registro sin actividad adicional.
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .commit();
    }

    private void ensureUsersFileExists() {
        // Garantiza que el archivo donde se guardan los usuarios exista.
        // Si no existe, lo crea vacío y registra el estado en log.
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

    public void switchToRegister() {
        // Cambia el fragmento actual para mostrar la pantalla de registro.
        loadFragment(new RegisterFragment());
    }

    public void switchToLogin() {
        // Cambia el fragmento actual para mostrar la pantalla de inicio de sesión.
        loadFragment(new LoginFragment());
    }
}
