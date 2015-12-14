package com.objectivelyradical.filmfinder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.objectivelyradical.filmfinder.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by c.mark on 2015/12/03.
 *
 * This is a helper class that acts as an intermediary between the user interface,
 * the API, and the favorite movies content provider. Its primary purpose is to return a list of
 * movies based on user settings, without the app having to care about where the data
 * comes from.
 */
public class MovieDataHandler {
    static String LOG_TAG = "MovieDataHandler";
    Context mContext;

    // Returns a list of movies based on the current user preference
    public void getMovies(Context context, MovieLoadCallback callback, boolean networkAvailable) {
        mContext = ((Fragment)callback).getContext();
        String sortType = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(mContext.getString(R.string.pref_sort_type_key),
                        mContext.getString(R.string.pref_sort_type_default));
        Log.d(LOG_TAG, "SELECTION: " + sortType);

        String[] allSortTypes = context.getResources().getStringArray(R.array.pref_sort_type_list_values);
        boolean showFavorites = sortType.equals(mContext.getString(R.string.pref_sort_type_favorites));

        if(!showFavorites && networkAvailable)
        {
            // Fetch the movies from the MovieDB API
            Log.d(LOG_TAG, "Fetch movie data from API.");
            FetchMovieTask task = new FetchMovieTask();
            task.execute(callback);
        } else{
            Log.d(LOG_TAG, "Load favorites.");
            // Fetch the favorites list from the provider
            Movie[] favoriteMovies = getFavorites();
            callback.onMovieLoadComplete(favoriteMovies);
        }
    }

    private Movie[] getFavorites() {
        Cursor cursor = mContext.getContentResolver().query
                (MovieContract.getMovieUri(), null, null, null, null, null);
        ArrayList<Movie> movies = new ArrayList<Movie>();
        Movie newMovie;
        int id;
        String title;
        String posterPath;
        String release;
        String summary;
        float rating;
        if(cursor == null) {
            return new Movie[0];
        }

        while(cursor.moveToNext()) {
            id = cursor.getInt(cursor.getColumnIndex(MovieContract.COLUMN_MOVIE_ID));
            title = cursor.getString(cursor.getColumnIndex(MovieContract.COLUMN_TITLE));
            posterPath = cursor.getString(cursor.getColumnIndex(MovieContract.COLUMN_POSTER_PATH));
            release = cursor.getString(cursor.getColumnIndex(MovieContract.COLUMN_RELEASE_DATE));
            summary = cursor.getString(cursor.getColumnIndex(MovieContract.COLUMN_SUMMARY));
            rating = cursor.getFloat(cursor.getColumnIndex(MovieContract.COLUMN_RATING));

            newMovie = new Movie(title, posterPath, id, release, summary, rating);
            movies.add(newMovie);
        }
        return movies.toArray(new Movie[movies.size()]);
    }

    public void addMovieToFavorites(Context c, Movie m) {
        mContext = c;
        ContentValues values = new ContentValues();
        values.put(MovieContract.COLUMN_TITLE, m.getTitle());
        values.put(MovieContract.COLUMN_POSTER_PATH, m.getPosterPath());
        values.put(MovieContract.COLUMN_MOVIE_ID, m.getId());
        values.put(MovieContract.COLUMN_RELEASE_DATE, m.getReleaseDate());
        values.put(MovieContract.COLUMN_SUMMARY, m.getSummary());
        values.put(MovieContract.COLUMN_RATING, m.getRating());

        Uri resultUri = mContext.getContentResolver().insert(MovieContract.getMovieUri(), values);
        Log.d(LOG_TAG, "Added " + m.getTitle() + " to URI: " + resultUri);
    }

    public void removeMovieFromFavorites(Context c, int id) {
        mContext = c;
        c.getContentResolver().delete(MovieContract.getMovieUri(id), null, null);
    }

    public void getReviews(int id, DetailLoadCallback c) {
        FetchReviewTask task = new FetchReviewTask();
        task.setId(Integer.toString(id));
        task.execute(c);
    }

    public void getTrailers(int id, DetailLoadCallback c) {
        FetchTrailerTask task = new FetchTrailerTask();
        task.setId(Integer.toString(id));
        task.execute(c);
    }

    public class FetchMovieTask extends AsyncTask<MovieLoadCallback, Void, Movie[]>
    {
        final String LOG_TAG = "FetchMovieTask";
        final String BASE_URL = "https://api.themoviedb.org/3/";
        final int MIN_VOTES = 35;
        MovieLoadCallback mMovieLoadCallback;

        @Override
        protected void onPostExecute(Movie[] movies) {
            if(movies.length > 0) {
                // Do something with the returned movie data
                for (int i = 0; i < movies.length; i++) {
                    Log.d(LOG_TAG, movies[i].getTitle() + " ... " + movies[i].getId());
                }
                if(mMovieLoadCallback != null) {
                    mMovieLoadCallback.onMovieLoadComplete(movies);
                }
            }
        }


        @Override
        protected Movie[] doInBackground(MovieLoadCallback... params) {
            mMovieLoadCallback = params[0];

            Uri.Builder uriBuilder = Uri.parse(BASE_URL).buildUpon();
            uriBuilder.appendPath("discover");
            uriBuilder.appendPath("movie");
            uriBuilder.appendQueryParameter("sort_by", PreferenceManager.getDefaultSharedPreferences(mContext).
                    getString(mContext.getString(R.string.pref_sort_type_key), mContext.getString(R.string.pref_sort_type_default)));
            uriBuilder.appendQueryParameter("vote_count.gte", ""+MIN_VOTES);
            uriBuilder.appendQueryParameter("api_key", Globals.API_KEY);
            String result = readFromUri(uriBuilder.build().toString());
            try {
                return parseJson(result);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.toString());
            }
            return null;
        }

        private Movie[] parseJson(String jsonString) throws JSONException {

            JSONObject root = new JSONObject (jsonString);
            JSONArray results = root.getJSONArray("results");
            Movie[] movies = new Movie[results.length()];
            String title;
            String posterPath;
            int id;
            String releaseDate;
            String summary;
            float rating;
            for(int i = 0; i < results.length(); i++) {
                JSONObject movie = results.getJSONObject(i);
                title = movie.getString("original_title");
                posterPath = movie.getString("poster_path");
                id = movie.getInt("id");
                releaseDate = movie.getString("release_date");
                summary = movie.getString("overview");
                rating = (float)movie.getDouble("vote_average");

                movies[i] = new Movie(title, posterPath, id, releaseDate, summary, rating);
            }
            return movies;
        }
    }

    public class FetchTrailerTask extends AsyncTask<DetailLoadCallback, Void, Trailer[]>
    {
        final String LOG_TAG = "FetchTrailerTask";
        final String BASE_URL = "https://api.themoviedb.org/3/";
        DetailLoadCallback mDetailLoadCallback;
        String id = "";

        public void setId(String s) {
            id = s;
        }

        @Override
        protected void onPostExecute(Trailer[] trailers) {
            if(trailers.length > 0) {
                // Do something with the returned movie data
                if(mDetailLoadCallback != null) {
                    mDetailLoadCallback.onTrailerLoadComplete(trailers);
                }
            }
        }


        @Override
        protected Trailer[] doInBackground(DetailLoadCallback... params) {
            mDetailLoadCallback = params[0];
            Uri.Builder uriBuilder = Uri.parse(BASE_URL).buildUpon();
            uriBuilder.appendPath("movie");
            uriBuilder.appendPath(id);
            uriBuilder.appendPath("videos");
            uriBuilder.appendQueryParameter("api_key", Globals.API_KEY);
            String result = readFromUri(uriBuilder.toString());

            try {
                Log.d(LOG_TAG, "TRAILER JSON: " + result);
                return parseJson(result);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.toString());
            }
            return null;
        }

        private Trailer[] parseJson(String jsonString) throws JSONException {
            Log.d(LOG_TAG, "PARSE JSON");
            JSONObject root = new JSONObject (jsonString);
            JSONArray results = root.getJSONArray("results");
            String name;
            String site;
            String type;
            String link;
            ArrayList<Trailer> trailers = new ArrayList<Trailer>();
            for(int i = 0; i < results.length(); i++) {
                JSONObject movie = results.getJSONObject(i);
                site = movie.getString("site");
                type = movie.getString("type");

                if(!site.equals("YouTube") || !type.equals("Trailer")) {
                    Log.d(LOG_TAG, "SKIPPING TRAILER");
                    Log.d(LOG_TAG, "Site: " + site);
                    Log.d(LOG_TAG, "Type: " + type);
                    continue;
                } else {
                    name = movie.getString("name");
                    link = movie.getString("key");
                    trailers.add(new Trailer(name, link));
                }
            }

            return trailers.toArray(new Trailer[trailers.size()]);
        }
    }

    public class FetchReviewTask extends AsyncTask<DetailLoadCallback, Void, Review[]>
    {
        final String LOG_TAG = "FetchReviewTask";
        final String BASE_URL = "https://api.themoviedb.org/3/";
        DetailLoadCallback mDetailLoadCallback;

        String id = "";
        public void setId(String s) {
            id = s;
        }

        @Override
        protected void onPostExecute(Review[] reviews) {
            if(reviews.length > 0) {
                // Do something with the returned movie data
                if(mDetailLoadCallback != null) {
                    mDetailLoadCallback.onReviewLoadComplete(reviews);
                }
            }
        }


        @Override
        protected Review[] doInBackground(DetailLoadCallback... params) {
            mDetailLoadCallback = params[0];
            Uri.Builder uriBuilder = Uri.parse(BASE_URL).buildUpon();
            uriBuilder.appendPath("movie");
            uriBuilder.appendPath(id);
            uriBuilder.appendPath("reviews");
            uriBuilder.appendQueryParameter("api_key", Globals.API_KEY);

            String result = readFromUri(uriBuilder.toString());

            try {
                return parseJson(result);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.toString());
            }
            return null;
        }

        private Review[] parseJson(String jsonString) throws JSONException {
            JSONObject root = new JSONObject (jsonString);
            JSONArray results = root.getJSONArray("results");
            Review[] reviews = new Review[results.length()];
            String author;
            String link;
            String text;
            for(int i = 0; i < results.length(); i++) {
                JSONObject movie = results.getJSONObject(i);
                author = movie.getString("author");
                link = movie.getString("url");
                text = movie.getString("content");

                reviews[i] = new Review(author, text, link);
            }
            return reviews;
        }
    }

    private String readFromUri(String uri) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonResponse = null;
        try {
            URL url = new URL(uri);
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if(inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if(buffer.length() == 0) {
                return null;
            }

            jsonResponse = buffer.toString();
        } catch (Exception exception) {
            Log.e(LOG_TAG, exception.toString());
        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
            if(reader != null) {
                try {
                    reader.close();
                } catch(final Exception e) {
                    Log.e(LOG_TAG, e.toString());
                }
            }
        }

        return jsonResponse;
    }

    public interface MovieLoadCallback {
        void onMovieLoadComplete(Movie[] movies);
    }
    public interface DetailLoadCallback {
        void onReviewLoadComplete(Review[] reviews);
        void onTrailerLoadComplete(Trailer[] trailers);
    }
}
