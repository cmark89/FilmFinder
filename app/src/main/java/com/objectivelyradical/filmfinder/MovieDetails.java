package com.objectivelyradical.filmfinder;

import java.text.DecimalFormat;

/**
 * Created by c.mark on 2015/09/28.
 */
public class MovieDetails extends Movie {
    private String releaseDate;
    private String summary;
    private float rating;

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getSummary() {
        return summary;
    }

    public String getRating() {
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(rating);
    }

    public MovieDetails(String title, String posterPath, int id, String releaseDate,
                        String summary, float rating) {
        super(title, posterPath, id);

        this.releaseDate = releaseDate;
        this.summary = summary;
        this.rating = rating;
    }
}