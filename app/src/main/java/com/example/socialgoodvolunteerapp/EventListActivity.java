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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventListActivity extends AppCompatActivity {

    private RecyclerView rvEventList;
    private EventAdapter adapter;

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

        // Back button click listener
        ImageView backButton = findViewById(R.id.btnBack);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(EventListActivity.this, MainActivityUser.class);
            startActivity(intent);
            finish();
        });

        // get reference to the RecyclerView bookList
        rvEventList = findViewById(R.id.rvEventList);

        //register for context menu
        registerForContextMenu(rvEventList);

        // get user info from SharedPreferences to get token value
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();

        // get event service instance
        EventService eventService = ApiUtils.getEventService();

        // execute the call. send the user token when sending the query
        eventService.getAllEvents(token).enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 200) {
                    // Get list of event object from response
                    List<Event> event = response.body();

                    // initialize adapter
                    adapter = new EventAdapter(getApplicationContext(), event);

                    // set adapter to the RecyclerView
                    rvEventList.setAdapter(adapter);

                    // set layout to recycler view
                    rvEventList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                    // add separator between item in the list
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvEventList.getContext(),
                            DividerItemDecoration.VERTICAL);
                    rvEventList.addItemDecoration(dividerItemDecoration);
                } else if (response.code() == 401) {
                    // invalid token, ask user to relogin
                    Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                } else {
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    // server return other error
                    Log.e("MyApp: ", response.toString());
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error connecting to the server", Toast.LENGTH_LONG).show();
                Log.e("MyApp:", t.toString());
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.event_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Event selectedEvent = adapter.getSelectedItem();
        Log.d("MyApp", "selected " + selectedEvent.toString());    // debug purpose

        if (item.getItemId() == R.id.menu_details) {    // user clicked details contextual menu
            doViewDetails(selectedEvent);
        }

        return super.onContextItemSelected(item);
    }

    private void doViewDetails(Event selectedEvent) {
        Log.d("MyApp:", "viewing details: " + selectedEvent.toString());
        // forward user to EventDetailsActivity, passing the selected event id
        Intent intent = new Intent(getApplicationContext(), EventDetailsActivity.class);
        intent.putExtra("event_id", selectedEvent.getEvent_id());
        startActivity(intent);
    }
}
