package com.example.cinemaapp;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.ImageView;

public class Movie {
    private int id; // Movie ID
    private String title;
    private String genre;
    private String summary;
    private ImageView poster;

    public Movie(int id, String title, String genre, String summary, ImageView poster) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.summary = summary;
        this.poster = poster;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public ImageView getPoster() {
        return poster;
    }

    public void setPoster(ImageView poster) {
        this.poster = poster;
    }

    public StringProperty titleProperty() {
        return new SimpleStringProperty(title);
    }

    public StringProperty genreProperty() {
        return new SimpleStringProperty(genre);
    }

    public StringProperty summaryProperty() {
        return new SimpleStringProperty(summary);
    }
}
