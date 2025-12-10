package com.example.sporify;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sporify.databinding.FragmentLoginBinding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private final Fragment registerFragment = new RegisterFragment();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla el layout asociado al fragmento y habilita el binding para manipular la UI.
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Se ejecuta tras la creación visual. Configura listeners de botones:
        // login, registro y recuperación (aún vacío).
        super.onViewCreated(view, savedInstanceState);

        binding.btnLogin.setOnClickListener(v -> handleLogin());
        binding.btnRegister.setOnClickListener(v -> cambioFragment(registerFragment));
        binding.btnForgotPassword.setOnClickListener(v -> {});
    }

    private void handleLogin() {
        // Valida los campos de email y contraseña. Si no hay errores,
        // verifica credenciales contra el archivo interno de usuarios.
        // Si coincide, redirige a Home; si falla, muestra mensaje de error.

        String email = binding.inputEmail.getEditText().getText().toString().trim();
        String password = binding.inputPassword.getEditText().getText().toString().trim();

        boolean errores = false;

        // Validación campos vacíos
        if (email.isEmpty()) {
            binding.inputEmail.setError("Introduzca su correo");
            errores = true;
        } else {
            binding.inputEmail.setError(null);
        }

        if (password.isEmpty()) {
            binding.inputPassword.setError("Introduzca su contraseña");
            errores = true;
        } else {
            binding.inputPassword.setError(null);
        }

        if (errores) return;

        File file = new File(requireContext().getFilesDir(), "usuarios.txt");

        // Verifica existencia de archivo de credenciales
        if (!file.exists()) {
            binding.inputEmail.setError("No existe registro de usuarios");
            return;
        }

        boolean loginCorrecto = false;

        // Lectura y comparación de credenciales
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;

            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(";");

                if (partes.length >= 2) {
                    if (email.equals(partes[0].trim()) && password.equals(partes[1].trim())) {
                        loginCorrecto = true;
                        break;
                    }
                }
            }

        } catch (Exception e) {
            binding.inputEmail.setError("Error leyendo datos");
            return;
        }

        // Resultado final del login
        if (loginCorrecto) {
            Toast.makeText(requireContext(), "Inicio de sesión correcto", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(requireContext(), Home.class));
            requireActivity().finish();
        } else {
            binding.inputEmail.setError("Email o contraseña incorrectos");
            binding.inputPassword.setError("Email o contraseña incorrectos");
        }
    }

    private void cambioFragment(Fragment fragment) {
        // Permite navegación hacia el fragment indicado y añade la transacción al backstack.
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroyView() {
        // Libera el binding al destruir la vista evitando fugas de memoria.
        super.onDestroyView();
        binding = null;
    }
}
