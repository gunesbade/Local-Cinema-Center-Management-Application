package com.example.cinemaapp;

/**
 * Represents the details of a movie session, including the day, time, and hall.
 * This class is used to encapsulate session-specific information.
 */
public class SessionDetails {
    private String day;
    private String time;
    private String hall;

    /**
     * Constructs a new {@code SessionDetails} object with the specified day, time, and hall.
     *
     * @param day  the day of the session (e.g., "Monday").
     * @param time the time of the session (e.g., "18:00").
     * @param hall the hall where the session takes place (e.g., "Hall 1").
     */
    public SessionDetails(String day, String time, String hall) {
        this.day = day;
        this.time = time;
        this.hall = hall;
    }

    /**
     * Returns the day of the session.
     *
     * @return the session day.
     */
    public String getDay() {
        return day;
    }

    /**
     * Returns the time of the session.
     *
     * @return the session time.
     */
    public String getTime() {
        return time;
    }

    /**
     * Returns the hall where the session takes place.
     *
     * @return the session hall.
     */
    public String getHall() {
        return hall;
    }

    /**
     * Returns a string representation of the session details.
     * The format is: {@code day | time | Hall: hall}.
     *
     * @return a string representation of the session details.
     */
    @Override
    public String toString() {
        return day + " | " + time + " | Hall: " + hall;
    }
}
