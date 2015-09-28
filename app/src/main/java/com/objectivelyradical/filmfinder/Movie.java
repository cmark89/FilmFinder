package com.objectivelyradical.filmfinder;

/**
 * Created by c.mark on 2015/09/25.
 */
public class Movie {
    public final static String POSTER_URL_BASE = "http://image.tmdb.org/t/p/";
    public static String posterSize = "w185";

    private String title;
    private String posterPath;
    private int id;

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getFullPosterPath() {
        return POSTER_URL_BASE + posterSize + posterPath;
    }

    public int getId() {
        return id;
    }

    public Movie(String title, String posterPath, int id) {
        this.title = title;
        this.posterPath = posterPath;
        this.id = id;
    }
}
