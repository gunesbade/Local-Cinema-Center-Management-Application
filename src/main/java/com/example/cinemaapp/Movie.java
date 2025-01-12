package com.example.cinemaapp;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.ImageView;

/**
 * Represents a movie in the cinema application.
 * Contains details such as movie ID, title, genre, summary, and poster image.
 */
public class Movie {
    private int id; // Movie ID
    private String title;
    private String genre;
    private String summary;
    private ImageView poster;

    /**
     * Constructs a new {@code Movie} object with the specified details.
     *
     * @param id      the unique identifier for the movie
     * @param title   the title of the movie
     * @param genre   the genre of the movie
     * @param summary the summary or description of the movie
     * @param poster  the poster image of the movie as an {@link ImageView}
     */
    public Movie(int id, String title, String genre, String summary, ImageView poster) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.summary = summary;
        this.poster = poster;
    }

    /**
     * Returns the unique identifier of the movie.
     *
     * @return the movie ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the movie.
     *
     * @param id the movie ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the title of the movie.
     *
     * @return the movie title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the movie.
     *
     * @param title the movie title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the genre of the movie.
     *
     * @return the movie genre
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Sets the genre of the movie.
     *
     * @param genre the movie genre to set
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * Returns the summary or description of the movie.
     *
     * @return the movie summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     * Sets the summary or description of the movie.
     *
     * @param summary the movie summary to set
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * Returns the poster image of the movie.
     *
     * @return the poster as an {@link ImageView}
     */
    public ImageView getPoster() {
        return poster;
    }

    /**
     * Sets the poster image of the movie.
     *
     * @param poster the poster image to set as an {@link ImageView}
     */
    public void setPoster(ImageView poster) {
        this.poster = poster;
    }

    /**
     * Returns the {@link StringProperty} for the movie title.
     * This is used for JavaFX bindings.
     *
     * @return the title property
     */
    public StringProperty titleProperty() {
        return new SimpleStringProperty(title);
    }

    /**
     * Returns the {@link StringProperty} for the movie genre.
     * This is used for JavaFX bindings.
     *
     * @return the genre property
     */
    public StringProperty genreProperty() {
        return new SimpleStringProperty(genre);
    }

    /**
     * Returns the {@link StringProperty} for the movie summary.
     * This is used for JavaFX bindings.
     *
     * @return the summary property
     */
    public StringProperty summaryProperty() {
        return new SimpleStringProperty(summary);
    }
}
