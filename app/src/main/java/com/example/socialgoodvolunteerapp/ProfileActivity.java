package com.example.socialgoodvolunteerapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProfileActivity extends AppCompatActivity {

    private EditText etUsername, etEmail, etPassword;
    private Button btnUpdateProfile, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        btnLogout = findViewById(R.id.btnLogout);

        // Get user data from SharedPreferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        etUsername.setText(user.getUsername());
        etEmail.setText(user.getEmail());
        etPassword.setText(user.getPassword());

        // Update profile action
        btnUpdateProfile.setOnClickListener(v -> {
            String updatedUsername = etUsername.getText().toString();
            String updatedEmail = etEmail.getText().toString();
            String updatedPassword = etPassword.getText().toString();
            updateProfile(updatedUsername, updatedEmail, updatedPassword);
        });

        // Logout action
        btnLogout.setOnClickListener(v -> logoutUser());
    }

    private void updateProfile(String username, String email, String password) {
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = new User(username, email, password);
        spm.saveUser(user);

        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
    }

    private void logoutUser() {
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();
        Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
