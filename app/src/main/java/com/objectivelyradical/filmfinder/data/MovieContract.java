package com.objectivelyradical.filmfinder.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by c.mark on 2015/12/03.
 */

//import

public class MovieContract implements BaseColumns {
    public static final String CONTENT_AUTHORITY = "com.objectivelyradical.filmfinder";
    public static final String BASE_CONTENT_URI = "content://" + CONTENT_AUTHORITY;
    public static final String MOVIE_PATH = "movie";
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
            "/" + CONTENT_AUTHORITY + "/" + MOVIE_PATH;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
            "/" + CONTENT_AUTHORITY + "/" + MOVIE_PATH;

    public static Uri getMovieUri() {
        return Uri.parse(BASE_CONTENT_URI + "/" + MOVIE_PATH);
    }

    public static Uri getMovieUri(int id) {
        return Uri.parse(BASE_CONTENT_URI + "/" + MOVIE_PATH + "/" + id);
    }

    // This can only be called by a URI that matches the pattern shown in getMovieUri
    public static String getIdFromUri(Uri uri) {
        return uri.getLastPathSegment();
    }

    // --- COLUMNS ---
    public static final String TABLE_NAME = "FavoriteMovies";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_POSTER_PATH = "posterPath";
    public static final String COLUMN_MOVIE_ID = "movieId";
    public static final String COLUMN_RELEASE_DATE = "releaseDate";
    public static final String COLUMN_SUMMARY = "summary";
    public static final String COLUMN_RATING = "rating";
}
