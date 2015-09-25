package com.objectivelyradical.filmfinder;

/**
 * Created by c.mark on 2015/09/25.
 */
public class Movie {
    private String title;
    private String posterPath;
    private String overview;
    private String year;

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public String getYear() {
        return year;
    }

    public Movie(String title, String posterPath, String overview, String year) {
        this.title = title;
        this.posterPath = posterPath;
        this.overview = overview;
        this.year = year;
    }
}
