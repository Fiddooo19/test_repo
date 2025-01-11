package com.example.socialgoodvolunteerapp.remote;

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
    Call<List<Event>> getAllEvents(@Header("api-key") String token);

    // fetch single record
    @GET("event/{id}")
    Call<Event> getEvent(String token, @Path("id") int id);

    // create new record
    @POST("event")
    @FormUrlEncoded
    Call<Event> addEvent(@Field("event_name") String eventName, @Field("organizer_id") int organizer_id);

    // delete record
    @DELETE("event/{event_id}")
    Call<Status> deleteEvent(@Path("event_id") int id);

    // update event record
    @POST("event/{event_id}")
    @FormUrlEncoded
    Call<Event> updateEvent(@Path("event_id") int id, @Field("event_name") String event_name,
                            @Field("organizer_id") int organizer_id);

    // fetch list of participation's for a user
    @GET("participations")
    Call<List<Participation>> getAllParticipationsForUser(@Query("user_id[e]") int userId);
}