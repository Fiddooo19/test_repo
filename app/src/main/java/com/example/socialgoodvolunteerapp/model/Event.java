package com.example.socialgoodvolunteerapp.model;

public class Event {
    private int event_id;
    private String event_name;
    private User organizer;
    private String description;
    private String location;
    private String category;
    private String date;

    private int organizer_id;

    public Event(int event_id, String event_name, User organizer, String description, String location, String category, String date) {
        this.event_id = event_id;
        this.event_name = event_name;
        this.organizer = organizer;
        this.description = description;
        this.location = location;
        this.category = category;
        this.date = date;
    }

    public Event(){

    }
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

    public String getdescription() {

        return description;
    }

    public void setdescription(String description) {

        this.description = description;
    }
    public String getlocation() {

        return location;
    }

    public void setlocation(String location) {

        this.location = location;
    }

    public String getdate() {

        return date;
    }

    public void setdate(String date) {

        this.date = date;
    }
    public String getcategory() {

        return category;
    }

    public void setcategory(String category) {

        this.category = category;
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
                "event_id=" + event_id +
                ", event_name='" + event_name + '\'' +
                ", organizer=" + organizer +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", category='" + category + '\'' +
                ", date='" + date + '\'' +
                '}';
    }

}