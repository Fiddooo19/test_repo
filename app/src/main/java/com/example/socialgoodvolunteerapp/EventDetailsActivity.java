package com.example.socialgoodvolunteerapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.socialgoodvolunteerapp.model.Event;
import com.example.socialgoodvolunteerapp.model.User;
import com.example.socialgoodvolunteerapp.remote.ApiUtils;
import com.example.socialgoodvolunteerapp.remote.EventService;
import com.example.socialgoodvolunteerapp.sharedpref.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailsActivity extends AppCompatActivity {

    private EventService eventService;

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

        /*
        // retrieve book details based on selected id

        // get book id sent by EventListActivity, -1 if not found
        Intent intent = getIntent();
        int bookId = intent.getIntExtra("book_id", -1);

        // get user info from SharedPreferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();

        // get book service instance
        eventService = ApiUtils.getEventService();

        // execute the API query. send the token and book id
        eventService.getBook(token, event_Id).enqueue(new Callback<Event>() {

            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 200) {
                    // server return success

                    // get book object from response
                    Book book = response.body();

                    // get references to the view elements

                    Part ni kena find by id

                    // set values

                    Part ni kena set
                }
                else if (response.code() == 401) {
                    // unauthorized error. invalid token, ask user to relogin
                    Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                }
                else {
                    // server return other error
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    Log.e("MyApp: ", response.toString());
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Toast.makeText(null, "Error connecting", Toast.LENGTH_LONG).show();
            }
        });
    */

        // Initialize UI components
        ImageView backButton = findViewById(R.id.btnBack);
        ImageView cameraIcon = findViewById(R.id.camera_icon);
        ImageView eventImage = findViewById(R.id.event_image);
        ImageView favoriteIcon = findViewById(R.id.favorite_icon);
        TextView pageTitle = findViewById(R.id.page_title);
        TextView eventName = findViewById(R.id.event_name);
        TextView tvDate = findViewById(R.id.tvDate);
        TextView tvTime = findViewById(R.id.event_time);
        TextView tvCategory = findViewById(R.id.tvCategory);
        TextView tvLocation = findViewById(R.id.tvLocation);
        TextView tvDescription = findViewById(R.id.tvDescription);
        Button joinButton = findViewById(R.id.join_button);

        // Back button click listener
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(EventDetailsActivity.this, EventListActivity.class);
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