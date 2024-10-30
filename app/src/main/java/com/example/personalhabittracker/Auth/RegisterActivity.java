package com.example.personalhabittracker.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.personalhabittracker.Model.User;
import com.example.personalhabittracker.R;
import com.example.personalhabittracker.databinding.ActivityRegisterBinding;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refUsers = database.getReference("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        onClickListener();
    }
    void onClickListener() {
        binding.loginAccount.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.btnRegister.setOnClickListener(v -> {
            validationInput();
        });
    }

    void validationInput() {

        binding.progressCircle.setVisibility(View.VISIBLE);

        String inputUserName = Objects.requireNonNull(binding.inputUserName.getText()).toString().trim();
        if (inputUserName.isEmpty()) {
            binding.inputUserName.setError(getString(R.string.nameIsRequired));
            binding.inputUserName.requestFocus();
            binding.progressCircle.setVisibility(View.INVISIBLE);
            return;
        }

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

        User user = new User();
        user.setUserName(inputUserName);
        user.setuEmail(inputEmail);
        user.setuPassword(inputPassword);
        Register(user);
    }

    public void Register(User user) {
        firebaseAuth.createUserWithEmailAndPassword(user.getuEmail(), user.getuPassword()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser CurrentUser = firebaseAuth.getCurrentUser();
                assert CurrentUser != null;
                user.setuID(CurrentUser.getUid());

                refUsers.child(user.getuID()).setValue(user).addOnSuccessListener(unused -> {
                    binding.progressCircle.setVisibility(View.INVISIBLE);
                    Toast.makeText(this, getString(R.string.successRegistered), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();

                }).addOnFailureListener(e -> {
                    binding.progressCircle.setVisibility(View.INVISIBLE);
                    if (e instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(this, getString(R.string.AlreadyRegistered), Toast.LENGTH_SHORT).show();
                    } else if (e instanceof FirebaseNetworkException) {
                        Toast.makeText(this, getString(R.string.noConnection), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Exception -> " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(e -> {
            binding.progressCircle.setVisibility(View.INVISIBLE);
            if (e instanceof FirebaseAuthUserCollisionException) {
                Toast.makeText(this, getString(R.string.AlreadyRegistered), Toast.LENGTH_SHORT).show();
            } else if (e instanceof FirebaseNetworkException) {
                Toast.makeText(this, getString(R.string.noConnection), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Exception -> " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}