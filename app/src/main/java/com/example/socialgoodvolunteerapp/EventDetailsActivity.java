package com.example.socialgoodvolunteerapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
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
import com.example.socialgoodvolunteerapp.model.Participation;
import com.example.socialgoodvolunteerapp.model.User;
import com.example.socialgoodvolunteerapp.remote.ApiUtils;
import com.example.socialgoodvolunteerapp.remote.EventService;
import com.example.socialgoodvolunteerapp.sharedpref.SharedPrefManager;

import java.util.List;

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

        // Initialize UI components
        ImageView backButton = findViewById(R.id.btnBack);

        Button btnJoinEvent = findViewById(R.id.btnJoinEvent);

        // Back button click listener
        backButton.setOnClickListener(view -> {
            finish();
        });

        // Join button click listener (example placeholder)
        btnJoinEvent.setOnClickListener(view -> {
            // Code to join the event (to be implemented)
        });

        // retrieve event details based on selected id

        // get event id sent by EventListActivity, -1 if not found
        Intent intent = getIntent();
        int eventId = intent.getIntExtra("event_id", -1);

        // get user info from SharedPreferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();

        // get event service instance
        eventService = ApiUtils.getEventService();

        // execute the API query. send the token and event id
        eventService.getEvent(token, eventId).enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 200) {
                    // server return success

                    // get event object from response
                    Event event = response.body();

                    // get references to the view elements
                    TextView eventName = findViewById(R.id.tvEventName);
                    TextView tvDate = findViewById(R.id.tvDate);
                    TextView tvCategory = findViewById(R.id.tvCategory);
                    TextView tvLocation = findViewById(R.id.tvLocation);
                    TextView tvDescription = findViewById(R.id.tvDescription);

                    // set values
                    eventName.setText(event.getEvent_name());
                    tvDate.setText(event.getdate());
                    tvCategory.setText(event.getcategory());
                    tvLocation.setText(event.getlocation());
                    tvDescription.setText(event.getdescription());

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Get the menu inflater
        MenuInflater inflater = getMenuInflater();
        // Inflate the menu using our XML menu file id, options_menu
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    public void clearSessionAndRedirect() {
        // clear the shared preferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();

        // terminate this activity
        finish();

        // forward to Login Page
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }
    public void JoinEvent(View view) {
// Get event details from the intent
        Intent intent = getIntent();
        int eventId = intent.getIntExtra("event_id", -1);

        // Get user info
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();

        // Check if the event ID is valid
        if (eventId != -1) {
            // Check if user is already participating in the event using the getAllParticipationsForUser method
            eventService.getAllParticipationsForUser(user.getId()).enqueue(new Callback<List<Participation>>() {
                @Override
                public void onResponse(Call<List<Participation>> call, Response<List<Participation>> response) {
                    if (response.code() == 200) {
                        List<Participation> participations = response.body();
                        boolean isParticipating = false;

                        // Check if the user is already participating in the event
                        for (Participation participation : participations) {
                            if (participation.getEvent_id() == participation.getEvent_id()) {
                                isParticipating = true;
                                break;
                            }
                        }

                        if (isParticipating) {
                            // If already participating, show a message
                            Toast.makeText(getApplicationContext(), "You are already participating in this event.", Toast.LENGTH_LONG).show();
                        } else {
                            // If not participating, join the event by adding user to participants
                            // You need to add the user ID to the event's participant list and update the event
                            eventService.getEvent(token, eventId).enqueue(new Callback<Event>() {
                                @Override
                                public void onResponse(Call<Event> call, Response<Event> response) {
                                    if (response.code() == 200) {
                                        Event event = response.body();
                                        if (event != null) {
                                            // Add the user to the event participants list
                                            event.getParticipants().add(String.valueOf(user.getId()));

                                            // Update the event with the new participants list
                                            eventService.updateEvent(token, eventId, event.getEvent_name(), event.getdescription(),
                                                            event.getlocation(), event.getcategory(), event.getdate(), event.getOrganizer_id())
                                                    .enqueue(new Callback<Event>() {
                                                        @Override
                                                        public void onResponse(Call<Event> call, Response<Event> response) {
                                                            if (response.code() == 200) {
                                                                Toast.makeText(getApplicationContext(), "Successfully joined the event!", Toast.LENGTH_LONG).show();

                                                                // After joining the event, navigate to ViewMyEventsActivity
                                                                Intent myEventsIntent = new Intent(EventDetailsActivity.this, ViewMyEventsActivity.class);
                                                                myEventsIntent.putExtra("filter_my_events", true); // This flag will show only joined events
                                                                startActivity(myEventsIntent);
                                                                finish();
                                                            } else {
                                                                Toast.makeText(getApplicationContext(), "Failed to join the event", Toast.LENGTH_LONG).show();
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(Call<Event> call, Throwable t) {
                                                            Toast.makeText(getApplicationContext(), "Error connecting to the server", Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Event not found", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Event> call, Throwable t) {
                                    Toast.makeText(getApplicationContext(), "Error connecting to the server", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Error fetching participation info", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Participation>> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Error fetching participation info", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Invalid event", Toast.LENGTH_LONG).show();
        }
    }
}