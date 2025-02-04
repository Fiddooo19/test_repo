package com.example.socialgoodvolunteerapp.model;

import java.util.List;

public class Event {
    private int event_id;
    private String event_name;
    private User organizer;
    private String description;
    private String location;
    private String category;
    private String date;
    private List<String> participants; // List of user IDs
    private int organizer_id;
    private String image; // New field for event image URL

    public Event(int event_id, String event_name, User organizer, String description, String location, String category, String date, String image) {
        this.event_id = event_id;
        this.event_name = event_name;
        this.organizer = organizer;
        this.description = description;
        this.location = location;
        this.category = category;
        this.date = date;
        this.image = image;
    }

    public Event() {}

    public int getEvent_id() {
        return event_id;
    }

    public void setEvent_id(int event_id) {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public int getOrganizer_id() {
        return organizer_id;
    }

    public void setOrganizer_id(int organizer_id) {
        this.organizer_id = organizer_id;
    }

    public String getImage() {  // Getter for image
        return image;
    }

    public void setImage(String image) {  // Setter for image
        this.image = image;
    }

    @Override
    public String toString() {
        return "Event{" +
                "event_id=" + event_id +
                ", event_name='" + event_name + '\'' +
                ", organizer=" + organizer +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", category='" + category + '\'' +
                ", date='" + date + '\'' +
                ", image='" + image + '\'' +  // Include image in the string representation
                '}';
    }
}
