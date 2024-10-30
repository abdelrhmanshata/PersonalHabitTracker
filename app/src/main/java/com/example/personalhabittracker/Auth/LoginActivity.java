package com.example.personalhabittracker.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.personalhabittracker.Activity.MainActivity;
import com.example.personalhabittracker.R;
import com.example.personalhabittracker.databinding.ActivityLoginBinding;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        onClickListener();
    }

    void onClickListener() {
        binding.registerAccount.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
        binding.btnLogin.setOnClickListener(v -> {
            validationInput();
        });
    }

    void validationInput() {
        binding.progressCircle.setVisibility(View.VISIBLE);

        String inputEmail = Objects.requireNonNull(binding.inputEmail.getText()).toString().trim();
        if (inputEmail.isEmpty()) {
            binding.inputEmail.setError(getString(R.string.emailIsRequired));
            binding.inputEmail.requestFocus();
            binding.progressCircle.setVisibility(View.INVISIBLE);
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches()) {
            binding.inputEmail.setError(getString(R.string.please_enter_valid_email));
            binding.inputEmail.requestFocus();
            binding.progressCircle.setVisibility(View.INVISIBLE);
            return;
        }

        String inputPassword = Objects.requireNonNull(binding.inputPassword.getText()).toString().trim();
        if (inputPassword.isEmpty()) {
            binding.inputPassword.setError(getString(R.string.passwordIsRequired));
            binding.inputPassword.requestFocus();
            binding.progressCircle.setVisibility(View.INVISIBLE);
            return;
        }
        if (inputPassword.length() < 8) {
            binding.inputPassword.setError(getString(R.string.minimumLength));
            binding.inputPassword.requestFocus();
            binding.progressCircle.setVisibility(View.INVISIBLE);
            return;
        }

        login(inputEmail, inputPassword);
    }

    void login(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                binding.progressCircle.setVisibility(View.INVISIBLE);
                Toast.makeText(this, getString(R.string.successLogin), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                ActivityCompat.finishAffinity(this);

            } else if (task.getException() instanceof FirebaseNetworkException) {
                Toast.makeText(this, getString(R.string.noConnection), Toast.LENGTH_SHORT).show();
            } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                Toast.makeText(this, getString(R.string.userNotFound), Toast.LENGTH_SHORT).show();
            } else if ((task.getException() instanceof FirebaseAuthInvalidCredentialsException)) {
                Toast.makeText(this, getString(R.string.passwordIncorrect), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error+->" + task.getException(), Toast.LENGTH_SHORT).show();
            }
            binding.progressCircle.setVisibility(View.INVISIBLE);
        });
    }
}