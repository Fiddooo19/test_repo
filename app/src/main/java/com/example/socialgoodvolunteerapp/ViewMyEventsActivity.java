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
import com.example.socialgoodvolunteerapp.model.Participation;
import com.example.socialgoodvolunteerapp.model.User;
import com.example.socialgoodvolunteerapp.remote.ApiUtils;
import com.example.socialgoodvolunteerapp.remote.EventService;
import com.example.socialgoodvolunteerapp.sharedpref.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewMyEventsActivity extends AppCompatActivity {

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
            Intent intent = new Intent(ViewMyEventsActivity.this, MainActivityUser.class);
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
        participations(token);
    }

    private void participations(String token) {
        eventService.getAllParticipationsForUser(user.getId()).enqueue(new Callback<List<Participation>>() {
            @Override
            public void onResponse(Call<List<Participation>> call, Response<List<Participation>> response) {
                if (response.code() == 200 && response.body() != null) {
                    List<Participation> participations = response.body();
                    List<Integer> joinedEventIds = new ArrayList<>();

                    for (Participation participation : participations) {
                        joinedEventIds.add(participation.getParticipation_id());
                    }

                    fetchEventsByJoinedIds(token, joinedEventIds);
                } else {
                    Toast.makeText(getApplicationContext(), "No joined events found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Participation>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error fetching joined events", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchEventsByJoinedIds(String token, List<Integer> joinedEventIds) {
        eventService.getAllEvents(token).enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.code() == 200 && response.body() != null) {
                    List<Event> allEvents = response.body();
                    List<Event> joinedEvents = new ArrayList<>();

                    for (Event event : allEvents) {
                        if (joinedEventIds.contains(event.getEvent_id())) {
                            joinedEvents.add(event);
                        }
                    }

                    setupRecyclerView(joinedEvents);
                } else {
                    Toast.makeText(getApplicationContext(), "No events found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error fetching events", Toast.LENGTH_LONG).show();
            }
        });
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
