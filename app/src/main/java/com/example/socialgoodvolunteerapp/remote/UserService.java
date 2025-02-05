package com.example.socialgoodvolunteerapp.remote;

import com.example.socialgoodvolunteerapp.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserService {

    @GET("users")
    Call<List<User>> getAllUsers(@Header("api-key") String api_key);

    @FormUrlEncoded
    @POST("users/login")
    Call<User> login(@Field("username") String username, @Field("password") String password);

    @FormUrlEncoded
    @POST("users/login")
    Call<User> loginEmail(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("register")
    Call<User> registerUser(
            @Field("username") String username,
            @Field("email") String email,
            @Field("password") String password,
            @Field("role") String role // Added field for role
    );

    // Add the method for updating profile picture
    @FormUrlEncoded
    @POST("users/{id}")  // Replace with your actual API endpoint
    Call<User> updateUserProfilePicture(@Header("api-key") String api_key, @Path("id") int id , @Field("image") String image);

    @GET("users/{id}")
    Call<User> getUser (@Header("api-key") String api_key, @Path("id") int id);
}