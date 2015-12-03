package com.objectivelyradical.filmfinder.data;

import android.content.Context;
import android.preference.PreferenceManager;

import com.objectivelyradical.filmfinder.Movie;
import com.objectivelyradical.filmfinder.R;

/**
 * Created by c.mark on 2015/12/03.
 *
 * This is a helper class that acts as an intermediary between the user interface,
 * the API, and the favorite movies content provider. Its primary purpose is to return a list of
 * movies based on user settings, without the app having to care about where the data
 * comes from.
 */
public class MovieDataHandler {
    // Returns a list of movies based on the current user preference
    public static Movie[] getMovies(Context c) {

        /*PreferenceManager.getDefaultSharedPreferences(c).getString(
                c.getString(R.string.pr);*/
        return null;
    }
}
