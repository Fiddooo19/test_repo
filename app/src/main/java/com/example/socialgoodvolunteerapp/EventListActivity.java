package com.example.socialgoodvolunteerapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialgoodvolunteerapp.adapter.EventAdapter;
import com.example.socialgoodvolunteerapp.model.Event;
import com.example.socialgoodvolunteerapp.model.User;
import com.example.socialgoodvolunteerapp.remote.ApiUtils;
import com.example.socialgoodvolunteerapp.remote.EventService;
import com.example.socialgoodvolunteerapp.sharedpref.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventListActivity extends AppCompatActivity {

    private EventService eventService;
    private RecyclerView rvEventList;
    private EventAdapter adapter;
    private User user;
    private boolean showMyEventsOnly = false; // Flag to filter events

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Debug: Check if flag is received
        showMyEventsOnly = getIntent().getBooleanExtra("filter_my_events", false);
        Log.d("MyApp", "Filter My Events flag received: " + showMyEventsOnly);

        // Back button click listener
        ImageView backButton = findViewById(R.id.btnBack);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(EventListActivity.this, MainActivityUser.class);
            startActivity(intent);
            finish();
        });

        // Get reference to the RecyclerView
        rvEventList = findViewById(R.id.rvEventList);
        registerForContextMenu(rvEventList);

        // Retrieve user data
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        user = spm.getUser();
        String token = user.getToken();
        Log.d("MyApp", "User ID: " + user.getId() + ", Token: " + token);

        // Get event service instance
        eventService = ApiUtils.getEventService();

        // Fetch events based on user role
        fetchEvent(token);
    }

    private void fetchEvent(String token) {
        Call<List<Event>> eventCall;

        if (user.getRole().equals("user")) {
            eventCall = eventService.getAllEvents(token);
        } else {
            eventCall = eventService.getAllEventsForOrganizer(token, user.getId());
        }

        eventCall.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                Log.d("MyApp", "API Response received. Status Code: " + response.code());

                if (response.code() == 200) {
                    List<Event> events = response.body();
                    Log.d("MyApp", "Total events received: " + (events != null ? events.size() : 0));

                    // If no events received, show a toast
                    if (events == null || events.isEmpty()) {
                        Log.d("MyApp", "No events found.");
                        Toast.makeText(getApplicationContext(), "No events found.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Filter events if "My Events" is selected
                    if (showMyEventsOnly) {
                        events = filterUserJoinedEvents(events);
                    }

                    Log.d("MyApp", "Total events after filtering: " + (events != null ? events.size() : 0));
                    setupRecyclerView(events);
                } else if (response.code() == 401) {
                    Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                } else {
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    Log.e("MyApp", "API Response Error: " + response.toString());
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error connecting to the server", Toast.LENGTH_LONG).show();
                Log.e("MyApp", "API Call Failed: ", t);
            }
        });
    }

    private List<Event> filterUserJoinedEvents(List<Event> allEvents) {
        List<Event> filteredEvents = new ArrayList<>();
        String userId = String.valueOf(user.getId());

        Log.d("MyApp", "Filtering events for user ID: " + userId);

        for (Event event : allEvents) {
            if (event.getParticipants() != null) {
                Log.d("MyApp", "Checking event: " + event.getEvent_name() + ", Participants: " + event.getParticipants());

                if (event.getParticipants().contains(userId)) {
                    filteredEvents.add(event);
                }
            } else {
                Log.d("MyApp", "Event " + event.getEvent_name() + " has no participants list.");
            }
        }

        Log.d("MyApp", "Filtered events count: " + filteredEvents.size());
        return filteredEvents;
    }

    private void setupRecyclerView(List<Event> events) {
        Log.d("MyApp", "Setting up RecyclerView with " + events.size() + " events.");
        adapter = new EventAdapter(getApplicationContext(), events);
        rvEventList.setAdapter(adapter);
        rvEventList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        DividerItemDecoration divider = new DividerItemDecoration(rvEventList.getContext(), DividerItemDecoration.VERTICAL);
        rvEventList.addItemDecoration(divider);
    }

    public void clearSessionAndRedirect() {
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.event_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Event selectedEvent = adapter.getSelectedItem();
        Log.d("MyApp", "Selected event: " + selectedEvent.toString());

        if (item.getItemId() == R.id.menu_details) {
            doViewDetails(selectedEvent);
        }

        return super.onContextItemSelected(item);
    }

    private void doViewDetails(Event selectedEvent) {
        Log.d("MyApp", "Viewing details for event: " + selectedEvent.toString());
        Intent intent = new Intent(getApplicationContext(), EventDetailsActivity.class);
        intent.putExtra("event_id", selectedEvent.getEvent_id());
        startActivity(intent);
    }
}