package com.example.socialgoodvolunteerapp;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.socialgoodvolunteerapp.model.Event;
import com.example.socialgoodvolunteerapp.model.Participation;
import com.example.socialgoodvolunteerapp.model.User;
import com.example.socialgoodvolunteerapp.remote.ApiUtils;
import com.example.socialgoodvolunteerapp.remote.EventService;
import com.example.socialgoodvolunteerapp.sharedpref.SharedPrefManager;

import java.text.SimpleDateFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailsActivityAdminViewOnly extends AppCompatActivity {

    private EventService eventService;

    private Event currentPos;


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


        // Back button click listener
        ImageView backButton = findViewById(R.id.btnBack);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(EventDetailsActivityAdminViewOnly.this, EventListActivityAdminViewOnly.class);
            startActivity(intent);
            finish();
        });
        // retrieve borrow details based on selected id

        // get book id sent by BookListActivity, -1 if not found
        Intent intent = getIntent();
        int event_id = intent.getIntExtra("event_id", -1);

        // get user info from SharedPreferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // get book service instance
        eventService = ApiUtils.getEventService();

        // execute the API query. send the token and book id
        eventService.getEvent(token, event_id).enqueue(new Callback<Event>() {

            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 200) {
                    // server return success

                    // get event object from response
                    currentPos = response.body();

                    // get references to the view elements
                    TextView eventName = findViewById(R.id.tvEventName);
                    TextView tvDate = findViewById(R.id.tvDate);
                    TextView tvCategory = findViewById(R.id.tvCategory);
                    TextView tvLocation = findViewById(R.id.tvLocation);
                    TextView tvDescription = findViewById(R.id.tvDescription);

                    // set values
                    eventName.setText(currentPos.getEvent_name());
                    tvDate.setText(currentPos.getDate());
                    tvCategory.setText(currentPos.getCategory());
                    tvLocation.setText(currentPos.getLocation());
                    tvDescription.setText(currentPos.getDescription());

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

    }

    public void clearSessionAndRedirect() {
        // clear the shared preferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();

        // terminate this MainActivity
        finish();

        // forward to Login Page
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }
    public void JoinEvent(View view) {
        // get values in form
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();

        int user_id = user.getId();
        int event_id = currentPos.getEvent_id();

        // send request to add new borrow record to the REST API
        EventService eventService = ApiUtils.getEventService();
        Call<Participation> call = eventService.addParticipation(user.getToken(), user_id, event_id);

        // execute
        call.enqueue(new Callback<Participation>() {
            @Override
            public void onResponse(Call<Participation> call, Response<Participation> response) {

                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 201) {
                    // borrow record added successfully
                    Participation addedParticipation = response.body();
                    // display message
                    Toast.makeText(getApplicationContext(),
                            addedParticipation.getEvent().getEvent_name() + " successfully joined!.",
                            Toast.LENGTH_LONG).show();

                    // end this activity and forward user to BookListActivity
                    Intent intent = new Intent(getApplicationContext(), MainActivityUser.class);
                    startActivity(intent);
                    finish();
                }
                else if (response.code() == 401) {
                    // invalid token, ask user to relogin
                    Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    // server return other error
                    Log.e("MyApp: ", response.toString());
                }
            }

            @Override
            public void onFailure(Call<Participation> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Error [" + t.getMessage() + "]",
                        Toast.LENGTH_LONG).show();
                // for debug purpose
                Log.d("MyApp:", "Error: " + t.getCause().getMessage());
            }
        });
    }
}