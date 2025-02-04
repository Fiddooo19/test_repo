package com.example.socialgoodvolunteerapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.socialgoodvolunteerapp.remote.ApiUtils;
import com.example.socialgoodvolunteerapp.sharedpref.SharedPrefManager;
import com.example.socialgoodvolunteerapp.model.User;
import com.example.socialgoodvolunteerapp.remote.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUsername, tvEmail, tvPassword;
    private Button btnLogout, btnChangePicture;
    private ImageView imgProfilePicture;
    private User user;
    private UserService userService;  // Declare the UserService

    private static final int PICK_IMAGE_REQUEST = 1;  // Request code for selecting an image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize the UserService
        userService = ApiUtils.getUserService();  // Use ApiUtils to get the UserService instance


        // Get references to the UI elements
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvPassword = findViewById(R.id.tvPassword);
        btnLogout = findViewById(R.id.btnLogout);
        imgProfilePicture = findViewById(R.id.imgProfilePicture);
        btnChangePicture = findViewById(R.id.btnChangePicture);

        // Retrieve user data from SharedPreferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        user = spm.getUser();

        // Display user details
        if (user != null) {
            tvUsername.setText(user.getUsername());
            tvEmail.setText(user.getEmail());
            tvPassword.setText(user.getPassword());

            // Load the profile picture if available
            String imagePath = user.getImagePath();  // Get the image path from the user object
            if (imagePath != null && !imagePath.isEmpty()) {
                Glide.with(this).load(imagePath).into(imgProfilePicture);  // Load the image using Glide
            }
        }

        // Set logout button functionality
        btnLogout.setOnClickListener(v -> logoutClicked(v));

        // Change profile picture functionality
        btnChangePicture.setOnClickListener(v -> openFileChooser());

        // Back button click listener
        ImageView backButton = findViewById(R.id.btnBack);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivityUser.class);
            startActivity(intent);
            finish();
        });
    }

    // Logout function
    public void logoutClicked(View view) {
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();
        Toast.makeText(getApplicationContext(), "You have logged out successfully.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // Open the file chooser for selecting a profile picture
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Handle the result of the file chooser
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            uploadProfilePicture(imageUri);  // Upload the selected image
        }
    }

    // Upload the selected profile picture
    private void uploadProfilePicture(Uri imageUri) {
        // Assuming the image is uploaded successfully, we get the image path or URL
        String imagePath = imageUri.toString();  // Replace with actual server URL or local path

        // Update the image path in the database
        updateUserImagePath(imagePath);
    }

    // Update the image path in the database (using Retrofit or SQLite)
    private void updateUserImagePath(String imagePath) {
        String userId = String.valueOf(user.getId());  // Get the user ID

        // Call API to update the user's profile picture
        updateUserInDatabase(userId, imagePath);
    }

    // After updating the image path in the database
    private void updateUserInDatabase(String userId, String imagePath) {
        // Use Retrofit to make the API request using the userService instance
        Call<Void> call = userService.updateUserProfilePicture(userId, imagePath);  // Call method on userService instance
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Update the local user object with the new image path
                    user.setImagePath(imagePath);

                    // Display the updated profile picture using Glide
                    Glide.with(ProfileActivity.this)
                            .load(imagePath)  // Image URL or file path
                            .into(imgProfilePicture);  // Set the image into the ImageView

                    Toast.makeText(ProfileActivity.this, "Profile picture updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to update profile picture", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error updating profile picture", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
