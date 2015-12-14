package com.objectivelyradical.filmfinder;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.objectivelyradical.filmfinder.data.MovieContract;

import java.text.DecimalFormat;

/**
 * Created by c.mark on 2015/09/25.
 */
public class Movie implements Parcelable {
    public final static String POSTER_URL_BASE = "http://image.tmdb.org/t/p/";
    public static String posterSize = "w185";

    private String title;
    private String posterPath;
    private int id;
    private String releaseDate;
    private String summary;
    private float rating;

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

    public boolean isFavorite(Context context) {
        Cursor c = context.getContentResolver().query(MovieContract.getMovieUri(id), null, null, null, null);
        return c.moveToFirst();
    }

    public Movie(String title, String posterPath, int id, String releaseDate,
                 String summary, float rating) {
        this.title = title;
        this.posterPath = posterPath;
        this.id = id;
        this.releaseDate = releaseDate;
        this.summary = summary;
        this.rating = rating;
    }

    private Movie(Parcel p) {
        title = p.readString();
        posterPath = p.readString();
        id = p.readInt();
        releaseDate = p.readString();
        summary = p.readString();
        rating = p.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(posterPath);
        dest.writeInt(id);
        dest.writeString(releaseDate);
        dest.writeString(summary);
        dest.writeFloat(rating);
    }



    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
