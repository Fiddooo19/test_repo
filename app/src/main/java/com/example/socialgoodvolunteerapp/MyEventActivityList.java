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

import com.example.socialgoodvolunteerapp.adapter.ParticipationAdapter;
import com.example.socialgoodvolunteerapp.model.Participation;
import com.example.socialgoodvolunteerapp.model.DeleteResponse;
import com.example.socialgoodvolunteerapp.model.User;
import com.example.socialgoodvolunteerapp.remote.ApiUtils;
import com.example.socialgoodvolunteerapp.remote.EventService;
import com.example.socialgoodvolunteerapp.remote.ParticipationService;
import com.example.socialgoodvolunteerapp.sharedpref.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyEventActivityList extends AppCompatActivity {

    private ParticipationService participationService;
    private RecyclerView rvEventListJoin;
    private ParticipationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_list_join);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Back button click listener
        ImageView backButton = findViewById(R.id.btnBack);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(MyEventActivityList.this, MainActivityUser.class);
            startActivity(intent);
            finish();
        });

        // get reference to the RecyclerView rvBorrowedList
        rvEventListJoin = findViewById(R.id.rvEventListJoin);

        //register for context menu
        registerForContextMenu(rvEventListJoin);

        // fetch and update borrowed list
        updateRecyclerView();
    }

    private void updateRecyclerView() {
        // get user info from SharedPreferences to get token value
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();

        // get book service instance
        participationService = ApiUtils.getParticipationService();

        // execute the call. send the user token when sending the query
        participationService.getAllParticipationByUserId(token, user.getId()).enqueue(new Callback<List<Participation>>() {
            @Override
            public void onResponse(Call<List<Participation>> call, Response<List<Participation>> response) {
                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 200) {
                    // Get list of borrow objects from response
                    List<Participation> borrows = response.body();

                    // initialize adapter
                    adapter = new ParticipationAdapter(getApplicationContext(), borrows);

                    // set adapter to the RecyclerView
                    rvEventListJoin.setAdapter(adapter);

                    // set layout to recycler view
                    rvEventListJoin.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                    // add separator between item in the list
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvEventListJoin.getContext(),
                            DividerItemDecoration.VERTICAL);
                    rvEventListJoin.addItemDecoration(dividerItemDecoration);
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
            public void onFailure(Call<List<Participation>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error connecting to the server", Toast.LENGTH_LONG).show();
                Log.e("MyApp:", t.toString());
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
        inflater.inflate(R.menu.event_context_menu_join, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Participation selectedParticipation = adapter.getSelectedItem();
        Log.d("MyApp", "selected "+selectedParticipation.toString());    // debug purpose

        if (item.getItemId() == R.id.menu_details) {
            // user clicked details contextual menu
            doViewDetails(selectedParticipation);
        }
        else if (item.getItemId() == R.id.menu_cancel) {
            // user clicked the update contextual menu
            doCancelParticipation(selectedParticipation);
        }

        return super.onContextItemSelected(item);
    }

    private void doCancelParticipation(Participation selectedParticipations) {
        // get user info from SharedPreferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();

        // prepare REST API call
        EventService eventService = ApiUtils.getEventService();
        Call<DeleteResponse> call = eventService.deleteParticipation(user.getToken(), selectedParticipations.getParticipation_id());

        // execute the call
        call.enqueue(new Callback<DeleteResponse>() {
            @Override
            public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
                if (response.code() == 200) {
                    // 200 means OK
                    displayAlert("Participations successfully cancel");
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

    private void doViewDetails(Participation selectedParticipation) {
        Log.d("MyApp:", "viewing details: " + selectedParticipation.toString());
        // forward user to BorrowDetailsActivity, passing the selected borrow id
        Intent intent = new Intent(getApplicationContext(), EventDetailsActivityJoined.class);
        intent.putExtra("event_id", selectedParticipation.getEvent_id());
        startActivity(intent);
    }

}
