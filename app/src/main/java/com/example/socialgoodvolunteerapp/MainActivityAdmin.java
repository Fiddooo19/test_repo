package com.example.socialgoodvolunteerapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;  // Ensure Glide is imported
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.socialgoodvolunteerapp.model.User;
import com.example.socialgoodvolunteerapp.sharedpref.SharedPrefManager;

public class MainActivityAdmin extends AppCompatActivity {

    private TextView cdUsername;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mainadmin);

        // Handle system insets (for edge-to-edge UI)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        cdUsername = findViewById(R.id.cdUsername);
        ImageView profileImageView = findViewById(R.id.profileImage);

        // Retrieve user data from SharedPreferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        user = spm.getUser();  // Get the current user

        // Check if the user is logged in, else navigate to LoginActivity
        if (user == null || !spm.isLoggedIn()) {
            // Stop this MainActivity and forward to Login Page
            finish();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            // If user is logged in, update the username TextView
            cdUsername.setText("Hi, " + user.getUsername());  // Display username

            // Log user data for debugging
            Log.d("ProfileImage", "User profile image path: " + user.getProfileImagePath());

            // Check if the profile image is assigned
            if (user.getProfileImagePath() != null && !user.getProfileImagePath().isEmpty()) {
                // Load the user's profile image
                Glide.with(this)
                        .load(user.getProfileImagePath())
                        .diskCacheStrategy(DiskCacheStrategy.NONE)  // No caching
                        .skipMemoryCache(true)  // Skip memory cache
                        .into(profileImageView);
            } else {
                // Load default profile image if no custom profile image is available
                Glide.with(this)
                        .load(R.drawable.default_profile_image)  // Default profile image resource
                        .into(profileImageView);
            }
            User user = spm.getUser();
            Log.d("ProfileImage", "Retrieved profile image path: " + user.getProfileImagePath());

        }

    }

    // Logout function
    public void logoutClicked(View view) {
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();

        // Display message
        Toast.makeText(getApplicationContext(),
                "You have successfully logged out.",
                Toast.LENGTH_LONG).show();

        // Navigate to Login Page
        finish();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    // View Event function
    public void viewEventClicked(View view) {
        // Navigate to EventListActivityAdmin
        Intent intent = new Intent(this, EventListActivityAdmin.class);
        startActivity(intent);
    }

    // View Profile function
    public void viewProfileClicked(View view) {
        // Navigate to ProfileActivity
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
}
