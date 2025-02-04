package com.example.socialgoodvolunteerapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.socialgoodvolunteerapp.model.FailLogin;
import com.example.socialgoodvolunteerapp.model.User;
import com.example.socialgoodvolunteerapp.remote.ApiUtils;
import com.example.socialgoodvolunteerapp.remote.UserService;
import com.example.socialgoodvolunteerapp.sharedpref.SharedPrefManager;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername;
    private EditText edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize the EditText fields
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);

        // Ensure that the main view is not null before calling the ViewCompat method
        View mainView = findViewById(R.id.login);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        } else {
            Log.e("LoginActivity", "Main view is null. Please check the layout file.");
        }
    }


    /**
     * Login button action handler
     */
    public void loginClicked(View view) {
        String username = edtUsername.getText().toString();
        String password = edtPassword.getText().toString();

        // Validate form before making the API call
        if (validateLogin(username, password)) {
            doLogin(username, password);
        }
    }

    /**
     * Validate value of username and password entered. Client side validation.
     * @param username
     * @param password
     * @return
     */
    private boolean validateLogin(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            displayToast("Username is required");
            return false;
        }
        if (password == null || password.trim().isEmpty()) {
            displayToast("Password is required");
            return false;
        }
        return true;
    }

    /**
     * Call REST API to login
     *
     * @param username username
     * @param password password
     */
    private void doLogin(String username, String password) {
        UserService userService = ApiUtils.getUserService();

        // Prepare the REST API call using the service interface
        Call<User> call = username.contains("@") ? userService.loginEmail(username, password) : userService.login(username, password);

        // Execute the REST API call
        call.enqueue(new Callback<User>() {

            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {  // code 200
                    User user = response.body();
                    if (user != null && user.getToken() != null) {
                        // Successful login. Store the user in SharedPreferences
                        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
                        spm.storeUser(user);

                        // Log successful login
                        Log.d("LoginActivity", "Login successful. User token: " + user.getToken());

                        // Forward user to MainActivity
                        finish();  // Close the LoginActivity

                        if (user.getRole().equals("admin")) {
                            startActivity(new Intent(getApplicationContext(), MainActivityAdmin.class));
                        } else if (user.getRole().equals("user")) {
                            startActivity(new Intent(getApplicationContext(), MainActivityUser.class));
                        } else {
                            displayToast("Unknown user role.");
                        }
                    } else {
                        displayToast("Login failed: No user info returned.");
                    }
                } else {
                    // Handle unsuccessful response
                    try {
                        String errorResp = response.errorBody().string();
                        FailLogin failLogin = new Gson().fromJson(errorResp, FailLogin.class);
                        displayToast(failLogin.getError().getMessage());
                    } catch (Exception e) {
                        Log.e("LoginActivity", "Error parsing response: ", e);
                        displayToast("Error occurred during login.");
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("LoginActivity", "API call failed: " + t.toString());
                displayToast("Error connecting to the server.");
            }
        });
    }

    /**
     * Display a Toast message
     * @param message message to be displayed inside toast
     */
    private void displayToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void openRegisterPage(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

}
