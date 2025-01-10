package com.example.socialgoodvolunteerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EventDetailsActivity extends AppCompatActivity {

    private ImageView backButton, favoriteIcon, eventImage, cameraIcon;
    private TextView pageTitle, eventName, tvDate, tvTime, tvCategory, tvLocation, tvDescription;
    private Button joinButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        backButton = findViewById(R.id.btnBack);
        cameraIcon = findViewById(R.id.camera_icon);
        eventImage = findViewById(R.id.event_image);
        favoriteIcon = findViewById(R.id.favorite_icon);
        pageTitle = findViewById(R.id.page_title);
        eventName = findViewById(R.id.event_name);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.event_time);
        tvCategory = findViewById(R.id.tvCategory);
        tvLocation = findViewById(R.id.tvLocation);
        tvDescription = findViewById(R.id.tvDescription);
        joinButton = findViewById(R.id.join_button);

        // Back button click listener
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(EventDetailsActivity.this, MainActivityAdmin.class);
            startActivity(intent);
            finish();
        });

        // Join button click listener (example placeholder)
        joinButton.setOnClickListener(view -> {
            // Code to join the event (to be implemented)
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Get the menu inflater
        MenuInflater inflater = getMenuInflater();
        // Inflate the menu using our XML menu file id, options_menu
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }
}
