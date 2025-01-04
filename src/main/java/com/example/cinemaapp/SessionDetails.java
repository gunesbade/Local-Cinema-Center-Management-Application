package com.example.cinemaapp;

public class SessionDetails {
    private String day;
    private String time;
    private String hall;




public SessionDetails(String day, String time, String hall) {
        this.day = day;
        this.time = time;
        this.hall = hall;
    }

    public String getDay() {
        return day;
    }

    public String getTime() {
        return time;
    }

    public String getHall() {
        return hall;
    }

    @Override
    public String toString() {
        return day + " | " + time + " | Hall: " + hall;
    }
}
