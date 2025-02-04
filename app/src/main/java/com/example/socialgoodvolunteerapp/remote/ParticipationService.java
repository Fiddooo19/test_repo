package com.example.socialgoodvolunteerapp.remote;

import com.example.socialgoodvolunteerapp.model.DeleteResponse;
import com.example.socialgoodvolunteerapp.model.Participation;

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

public interface ParticipationService {

    // fetch list of participation's for a user
    @GET("participations")
    Call<List<Participation>> getAllParticipationByUserId(@Header("api-key") String apiKey, @Query("user_id[e]") int userId);

    @GET("participations/{participation_id}")
    Call<Participation> getParticipation(@Header("api-key") String apiKey, @Path("participation_id") int participationId);

    @DELETE("participations/{participation_id}")
    Call<DeleteResponse> deleteParticipation(@Header ("api-key") String apiKey, @Path("participation_id") int participationId);

    @FormUrlEncoded
    @POST("participations")
    Call<Participation> addParticipation(@Header("api-key") String apiKey, @Field("user_id") int userId,
                                         @Field("event_id") int eventId);


    @FormUrlEncoded
    @POST("participations/{participation_id}")
    Call<Participation> updateParticipation(@Header("api-key") String apiKey, @Path("participation_id") int participationId,
                                            @Field("event_id") int eventId, @Field("user_id") int userId);
}
