package com.example.socialgoodvolunteerapp;

import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialgoodvolunteerapp.adapter.EventAdapter;
import com.example.socialgoodvolunteerapp.model.DeleteResponse;
import com.example.socialgoodvolunteerapp.model.Event;
import com.example.socialgoodvolunteerapp.model.User;
import com.example.socialgoodvolunteerapp.remote.ApiUtils;
import com.example.socialgoodvolunteerapp.remote.EventService;
import com.example.socialgoodvolunteerapp.sharedpref.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventListActivityAdmin extends AppCompatActivity {

    private EventService eventService;
    private RecyclerView rvEventList;
    private EventAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_list_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Back button click listener
        ImageView backButton = findViewById(R.id.btnBack);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(EventListActivityAdmin.this, MainActivityAdmin.class);
            startActivity(intent);
            finish();
        });

        // get reference to the RecyclerView eventList
        rvEventList = findViewById(R.id.rvEventList);

        //register for context menu
        registerForContextMenu(rvEventList);

        // fetch and update event list
        updateRecyclerView();

        // get user info from SharedPreferences to get token value
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();

        // get event service instance
        eventService = ApiUtils.getEventService();

        // execute the call. send the user token when sending the query
        Call eventCall;
        if (user.getRole().equals("user"))
            eventCall = eventService.getAllEvents(token);
        else
            eventCall = eventService.getAllEventsForOrganizer(token, user.getId());

        eventCall.enqueue(new Callback<List<Event>>() {
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


    private void updateRecyclerView() {
        // get user info from SharedPreferences to get token value
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();
        int organizer_id = user.getId();

        // get event service instance
        eventService = ApiUtils.getEventService();

        // execute the call. send the user token when sending the query
        eventService.getAllEventsForOrganizer(token, organizer_id).enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 200) {
                    // Get list of event object from response
                    List<Event> events = response.body();

                    // initialize adapter
                    adapter = new EventAdapter(getApplicationContext(), events);

                    // set adapter to the RecyclerView
                    rvEventList.setAdapter(adapter);

                    // set layout to recycler view
                    rvEventList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                    // add separator between item in the list
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvEventList.getContext(),
                            DividerItemDecoration.VERTICAL);
                    rvEventList.addItemDecoration(dividerItemDecoration);
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
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error connecting to the server", Toast.LENGTH_LONG).show();
                Log.e("MyApp:", t.toString());
            }
        });
    }

    /**
     * Delete event record. Called by contextual menu "Delete"
     * @param selectedEvent - event selected by admin
     */
    private void doDeleteEvent(Event selectedEvent) {
        // get user info from SharedPreferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();

        // prepare REST API call
        EventService eventService = ApiUtils.getEventService();
        Call<DeleteResponse> call = eventService.deleteEvent(user.getToken(), selectedEvent.getEvent_id());

        // execute the call
        call.enqueue(new Callback<DeleteResponse>() {
            @Override
            public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
                if (response.code() == 200) {
                    // 200 means OK
                    displayAlert("Event successfully deleted");
                    // update data in list view
                    updateRecyclerView();
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
            public void onFailure(Call<DeleteResponse> call, Throwable t) {
                displayAlert("Error [" + t.getMessage() + "]");
                Log.e("MyApp:", t.getMessage());
            }
        });
    }

    /**
     * Displaying an alert dialog with a single button
     * @param message - message to be displayed
     */
    public void displayAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
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
        inflater.inflate(R.menu.event_context_menu_admin, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Event selectedEvent = adapter.getSelectedItem();
        Log.d("MyApp", "selected " + selectedEvent.toString());    // debug purpose

        if (item.getItemId() == R.id.menu_details) {    // user clicked details contextual menu
            doViewDetails(selectedEvent);
        }
        else if (item.getItemId() == R.id.menu_delete) {
            // user clicked the delete contextual menu
            doDeleteEvent(selectedEvent);
        }
        else if (item.getItemId() == R.id.menu_update) {
            // user clicked the update contextual menu
            doUpdateEvent(selectedEvent);
        }

        return super.onContextItemSelected(item);
    }

    private void doUpdateEvent(Event selectedEvent) {
        Log.d("MyApp:", "updating event: " + selectedEvent.toString());
        // forward user to UpdateEventActivity, passing the selected event id
        Intent intent = new Intent(getApplicationContext(), UpdateEventActivity.class);
        intent.putExtra("event_id", selectedEvent.getEvent_id());
        startActivity(intent);
    }

    private void doViewDetails(Event selectedEvent) {
        Log.d("MyApp:", "viewing details: " + selectedEvent.toString());
        // forward user to EventDetailsActivity, passing the selected event id
        Intent intent = new Intent(getApplicationContext(), EventDetailsActivityAdmin.class);
        intent.putExtra("event_id", selectedEvent.getEvent_id());
        startActivity(intent);
    }

    public void floatingAddEventClicked(View view) {

        // forward user to NewEventActivity
        Intent intent = new Intent(getApplicationContext(), NewEventActivity.class);
        startActivity(intent);
    }
}
