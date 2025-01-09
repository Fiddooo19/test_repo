package com.example.socialgoodvolunteerapp;

import com.example.socialgoodvolunteerapp.model.User;

public class Participation {
    private int participation_id;
    private User user;
    private int user_id;
    private Event event;
    private int event_id;

    public int getParticipation_id() {
        return participation_id;
    }

    public void setParticipation_id(int participation_id) {
        this.participation_id = participation_id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public int getEvent_id() {
        return event_id;
    }

    public void setEvent_id(int event_id) {
        this.event_id = event_id;
    }
}