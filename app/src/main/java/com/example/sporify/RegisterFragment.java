package com.example.sporify;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sporify.databinding.FragmentRegisterBinding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);

        binding.btnRegister.setOnClickListener(v -> handleRegister());

        return binding.getRoot();
    }

    private void handleRegister() {

        String name = binding.inputName.getEditText().getText().toString().trim();
        String email = binding.inputEmail.getEditText().getText().toString().trim();
        String pass = binding.inputPassword.getEditText().getText().toString().trim();
        String confirm = binding.inputConfirmPassword.getEditText().getText().toString().trim();

        boolean errors = false;

        if (name.isEmpty()) {
            binding.inputName.setError("Introduzca su nombre");
            errors = true;
        } else {
            binding.inputName.setError(null);
            binding.inputName.setErrorEnabled(false);
        }

        if (email.isEmpty()) {
            binding.inputEmail.setError("Introduzca un email");
            errors = true;
        } else if (!email.contains("@") || !email.contains(".")) {
            binding.inputEmail.setError("Email no válido");
            errors = true;
        } else {
            binding.inputEmail.setError(null);
            binding.inputEmail.setErrorEnabled(false);
        }

        if (pass.isEmpty()) {
            binding.inputPassword.setError("Introduzca una contraseña");
            errors = true;
        } else if (pass.length() < 6) {
            binding.inputPassword.setError("La contraseña debe tener al menos 6 caracteres");
            errors = true;
        } else {
            binding.inputPassword.setError(null);
            binding.inputPassword.setErrorEnabled(false);
        }

        if (confirm.isEmpty()) {
            binding.inputConfirmPassword.setError("Repita la contraseña");
            errors = true;
        } else if (!confirm.equals(pass)) {
            binding.inputConfirmPassword.setError("Las contraseñas no coinciden");
            errors = true;
        } else {
            binding.inputConfirmPassword.setError(null);
            binding.inputConfirmPassword.setErrorEnabled(false);
        }

        if (errors) return;

        File archivo = new File(requireContext().getFilesDir(), "usuarios.txt");

        if (archivo.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    String[] partes = linea.split(";");
                    if (partes.length >= 2 && email.equalsIgnoreCase(partes[0].trim())) {
                        binding.inputEmail.setError("El correo ya está registrado");
                        return;
                    }
                }
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Error al leer usuarios", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        try (FileWriter writer = new FileWriter(archivo, true)) {
            writer.append(email).append(";")
                    .append(pass).append(";")
                    .append(name)
                    .append("\n");
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error al guardar usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(requireContext(), "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView, new LoginFragment())
                .commit();
    }
}
