package com.example.socialgoodvolunteerapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;
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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivityUser extends AppCompatActivity {

    private TextView tvHello;
    private TextView tvUsername;
    private EventService eventService;
    private RecyclerView rvEventList;
    private EventAdapter adapter;
    private User user;
    private boolean showMyEventsOnly = false; // Flag to filter events

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mainuser);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.user), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
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

        RecyclerView rvEventList = findViewById(R.id.rvEventList);

        // Ensure that the layout manager is set
        rvEventList.setLayoutManager(new LinearLayoutManager(this));

        // Optionally, add item decoration if needed (for example, dividers between items)
        rvEventList.addItemDecoration(new DividerItemDecoration(rvEventList.getContext(), DividerItemDecoration.VERTICAL));

        // Get reference to the TextViews
        tvUsername = findViewById(R.id.tvUsername);

        // Display user details
        if (user != null) {
            tvUsername.setText("Hi, " + user.getUsername());
        }

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

    private void setupRecyclerView(List<Event> events) {
        Log.d("MyApp", "Setting up RecyclerView with " + events.size() + " events.");
        adapter = new EventAdapter(getApplicationContext(), events);
        rvEventList.setAdapter(adapter);
        rvEventList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        DividerItemDecoration divider = new DividerItemDecoration(rvEventList.getContext(), DividerItemDecoration.VERTICAL);
        rvEventList.addItemDecoration(divider);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // get the menu inflater
        MenuInflater inflater = super.getMenuInflater();
        // inflate the menu using our XML menu file id, options_menu
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    public void logoutClicked(View view) {
        // clear the shared preferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();

        // display message
        Toast.makeText(getApplicationContext(),
                "You have successfully logged out.",
                Toast.LENGTH_LONG).show();

        // terminate this MainActivity
        finish();

        // forward to Login Page
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }
    public void viewEventClicked(View view) {

        // forward to View Event Page
        Intent intent = new Intent(this, EventListActivity.class);
        startActivity(intent);
    }



    public void clearSessionAndRedirect() {
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void viewMyEventClicked(View view) {
        // forward to View Event Page
        Intent intent = new Intent(this, MyEventActivityList.class);
        startActivity(intent);
    }
    public void viewProfileClicked(View view) {
        // Forward to ProfileActivity
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }


}
