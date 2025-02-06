package com.example.socialgoodvolunteerapp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;

import com.bumptech.glide.Glide;
import com.example.socialgoodvolunteerapp.model.Event;
import com.example.socialgoodvolunteerapp.model.FileInfo;
import com.example.socialgoodvolunteerapp.remote.ApiUtils;
import com.example.socialgoodvolunteerapp.remote.EventService;
import com.example.socialgoodvolunteerapp.sharedpref.SharedPrefManager;
import com.example.socialgoodvolunteerapp.model.User;
import com.example.socialgoodvolunteerapp.remote.UserService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUsername, tvEmail, tvRole;
    private Button btnLogout, btnChangePicture;
    private ImageView imgProfilePicture;
    private User user;
    private UserService userService;  // Declare the UserService

    private static final int PICK_IMAGE = 1;  // Request code for selecting an image

    private static final int PERMISSION_REQUEST_STORAGE = 2;

    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Get references to the UI elements
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvRole = findViewById(R.id.tvRole);
        btnLogout = findViewById(R.id.btnLogout);
        imgProfilePicture = findViewById(R.id.imgProfilePicture);
        btnChangePicture = findViewById(R.id.btnChangePicture);

        // Retrieve user data from SharedPreferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        user = spm.getUser();

        userService = ApiUtils.getUserService();

        userService.getUser(user.getToken(), user.getId()).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                // Display user details
                if (response.code() == 200) {

                    user = response.body();

                    tvUsername.setText(user.getUsername());
                    tvEmail.setText(user.getEmail());
                    tvRole.setText(user.getRole());

                    // Use Glide to load the image into the ImageView
                    Glide.with(getApplicationContext())
                            .load("https://codelah.my/2023116119/api/" + user.getImage()) // Make sure your Book object has a method to get the image URL
                            .placeholder(R.drawable.account) // Placeholder image if the URL is empty
                            .error(R.drawable.icon) // Error image if there is a problem loading the image
                            .into(imgProfilePicture);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Error [" + t.getMessage() + "]",
                        Toast.LENGTH_LONG).show();
                // for debug purpose
                Log.d("MyApp:", "Error: " + t.getCause().getMessage());
            }



        });





        // Back button click listener
        ImageView backButton = findViewById(R.id.btnBack);
        backButton.setOnClickListener(view -> {
            Intent intent;

            // Check if user role is admin or user and navigate accordingly
            if (user != null) {
                if (user.getRole().equalsIgnoreCase("admin")) {
                    // If role is admin, go to MainActivityAdmin
                    intent = new Intent(ProfileActivity.this, MainActivityAdmin.class);
                } else {
                    // If role is user, go to MainActivityUser
                    intent = new Intent(ProfileActivity.this, MainActivityUser.class);
                }
                startActivity(intent);
                finish();  // Close ProfileActivity after navigating
            }
        });
    }

    /**
     * Button browse action handler
     * @param view
     */
    public void changeProfilePicture(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+, require READ_MEDIA_IMAGES permission
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_MEDIA_IMAGES},
                        PERMISSION_REQUEST_STORAGE);
            } else {
                openGallery();
            }
        } else {
            // For Android 12 and below, require READ_EXTERNAL_STORAGE permission
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_STORAGE);
            } else {
                openGallery();
            }
        }
    }

    /**
     * User clicked deny or allow in permission request dialog
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Open Image Picker Activity
     */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    /**
     * Callback for Activity, in this case will handle the result of Image Picker Activity
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            // image picker activity return a value
            if(data != null) {
                // get file uri
                uri = data.getData();
                // set image to imageview
                imgProfilePicture.setImageURI(uri);
            }
        }
    }

    /**
     * Called when Add Book button is clicked
     * @param v
     */
    public void saveProfile(View v) {

        if (uri != null && !uri.getPath().isEmpty()) {
            // Upload file first
            uploadFile(uri);
        } else {
            // No file to upload, proceed with adding book record with default image
            saveProfileImage("3-default.png");
        }

    }

    /**
     * Upload selected image to server through REST API
     * @param fileUri   full path of the file
     */
    private void uploadFile(Uri fileUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            byte[] fileBytes = getBytesFromInputStream(inputStream);
            RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(fileUri)), fileBytes);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", getFileName(uri), requestFile);

            // get token user info from shared preference in order to get token value
            SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
            User user = spm.getUser();

            EventService eventService = ApiUtils.getEventService();
            Call<FileInfo> call = eventService.uploadFile(user.getToken(), body);

            call.enqueue(new Callback<FileInfo>() {
                @Override
                public void onResponse(Call<FileInfo> call, Response<FileInfo> response) {
                    if (response.isSuccessful()) {
                        // file uploaded successfully
                        // Now add the book record with the uploaded file name
                        FileInfo fi = response.body();
                        String fileName = fi.getFile();
                        saveProfileImage(fileName);
                    } else {
                        Toast.makeText(getApplicationContext(), "File upload failed", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<FileInfo> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Error uploading file", Toast.LENGTH_LONG).show();
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error preparing file for upload", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Get image file name from Uri
     * @param uri
     * @return
     */
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (columnIndex != -1) {
                        result = cursor.getString(columnIndex);
                    }
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    /**
     * Get image bytes from local disk. Used to upload the image bytes to Rest API
     * @param inputStream
     * @return
     * @throws IOException
     */
    private byte[] getBytesFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    // Logout function
    public void logoutClicked(View view) {
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();
        Toast.makeText(getApplicationContext(), "You have logged out successfully.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Get path from Uri object returned by gallery / image picker
     * @param contentUri
     * @return
     */
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
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

    private void saveProfileImage(String FileName) {

        // Get references to the UI elements
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        btnLogout = findViewById(R.id.btnLogout);
        tvRole = findViewById(R.id.tvRole);
        imgProfilePicture = findViewById(R.id.imgProfilePicture);
        btnChangePicture = findViewById(R.id.btnChangePicture);

        // get user info from SharedPreferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();

        // send request to add new book to the REST API
        UserService userService = ApiUtils.getUserService();
        Call<User> call = userService.updateUserProfilePicture(user.getToken(), user.getId(), FileName);

        // execute
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 201) {
                    // book added successfully
                    User addedUser = response.body();
                    // display message
                    Toast.makeText(getApplicationContext(),
                            addedUser.getId() + " added successfully.",
                            Toast.LENGTH_LONG).show();

                    // end this activity and forward user to BookListActivity
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if (response.code() == 401) {
                    // invalid token, ask user to relogin
                    Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Successfully change picture profile", Toast.LENGTH_LONG).show();
                    // server return other error
                    Log.e("MyApp: ", response.toString());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Successfull [" + t.getMessage() + "]",
                        Toast.LENGTH_LONG).show();
                // for debug purpose
                Log.d("MyApp:", "Successfull: " + t.getCause().getMessage());
            }
        });
    }

}
