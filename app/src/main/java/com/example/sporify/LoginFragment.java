package com.example.sporify;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sporify.databinding.FragmentLoginBinding;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        mAuth = FirebaseAuth.getInstance();

        binding.btnLogin.setOnClickListener(v -> handleLogin());

        binding.btnRegister.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainerView, new RegisterFragment())
                        .addToBackStack(null)
                        .commit()
        );

        return binding.getRoot();
    }

    private void handleLogin() {
        String email = binding.nameInput.getText().toString().trim();
        String password = binding.passsInput.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    Toast.makeText(getContext(), "Login correcto", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(requireContext(), Home.class);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}