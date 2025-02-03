package com.example.socialgoodvolunteerapp.remote;

import com.example.socialgoodvolunteerapp.model.DeleteResponse;
import com.example.socialgoodvolunteerapp.model.Event;
import com.example.socialgoodvolunteerapp.model.Participation;
import com.example.socialgoodvolunteerapp.Status;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EventService {

    // fetch list of records
    @GET("event")
    Call<List<Event>> getAllEvents(@Header("api-key") String api_key);

    @GET("event")
    Call<List<Event>> getAllEventsForOrganizer (@Header("api-key") String api_key, @Query("organizer_id") int organizer_id);

    // fetch single record
    @GET("event/{id}")
    Call<Event> getEvent(@Header("api-key") String api_key, @Path("id") int id);

    // create new record
    @FormUrlEncoded
    @POST("event")
    Call<Event> addEvent(@Header ("api-key") String apiKey, @Field("event_name") String event_name,
                         @Field("description") String description, @Field("location") String location,
                         @Field("category") String category, @Field("date") String date, @Field("organizer_id") int organizer_id);

    // delete record
    //@DELETE("event/{event_id}")
    //Call<Status> deleteEvent(@Path("event_id") int id);

    //delete record
    @DELETE("event/{id}")
    Call<DeleteResponse>deleteEvent(@Header ("api-key") String apiKey, @Path("id") int id);

    // update event record
    @FormUrlEncoded
    @POST("event/{event_id}")
    Call<Event> updateEvent(@Header ("api-key") String apiKey, @Path("event_id") int event_id,
                            @Field("event_name") String event_name, @Field("description") String description,
                            @Field("location") String location, @Field("category") String category,
                            @Field("date") String date, @Field("organizer_id") int organizer_id);


    // fetch list of participation's for a user
    @GET("participations")
    Call<List<Participation>> getAllParticipationsForUser(@Query("user_id[e]") int userId);
}