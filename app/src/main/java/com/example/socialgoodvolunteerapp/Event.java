package com.example.socialgoodvolunteerapp;

import com.example.socialgoodvolunteerapp.model.User;

public class Event {
    private String event_id;
    private String event_name;
    private User organizer;
    private int organizer_id;

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public int getOrganizer_id() {
        return organizer_id;
    }

    public void setOrganizer_id(int organizer_id) {
        this.organizer_id = organizer_id;
    }

    @Override
    public String toString() {
        return "Event{" +
                "event_id='" + event_id + '\'' +
                ", event_name='" + event_name + '\'' +
                '}';
    }
}