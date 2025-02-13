package com.example.socialgoodvolunteerapp.remote;

public class ApiUtils {

    // REST API server URL
    public static final String BASE_URL = "https://codelah.my/2023116119/api/";

    // return UserService instance
    public static UserService getUserService() {
        return RetrofitClient.getClient(BASE_URL).create(UserService.class);
    }
    public static EventService getEventService() {
        return RetrofitClient.getClient(BASE_URL).create(EventService.class);
    }

    // return ParticipationService instance
    public static ParticipationService getParticipationService() {
        return RetrofitClient.getClient(BASE_URL).create(ParticipationService.class);
    }
}