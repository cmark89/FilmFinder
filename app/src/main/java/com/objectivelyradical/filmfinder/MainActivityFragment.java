package com.objectivelyradical.filmfinder;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);
        ImageView imageView = (ImageView)rootView.findViewById(R.id.movies_image_view);
        Picasso.with(getActivity()).load("http://i.imgur.com/DvpvklR.png").into(imageView);

        FetchMovieTask task = new FetchMovieTask();
        task.execute();

        return rootView;
    }



    public class FetchMovieTask extends AsyncTask<String, Void, Movie[]>
    {
        final String LOG_TAG = "FetchMovieTask";
        final String BASE_URL = "https://api.themoviedb.org/3/";

        @Override
        protected void onPostExecute(Movie[] movies) {
            // Do something with the returned movie data
            for(int i = 0; i < movies.length; i++) {
                Log.d(LOG_TAG, movies[i].getTitle() + " ... " + movies[i].getOverview());
            }
        }

        @Override
        protected Movie[] doInBackground(String... params) {
            Uri.Builder uriBuilder = Uri.parse(BASE_URL).buildUpon();
            uriBuilder.appendPath("discover");
            uriBuilder.appendPath("movie");
            uriBuilder.appendQueryParameter("api_key", ""); // <--- API KEY GO HERE

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonResponse = null;

            try {
                URL url = new URL(uriBuilder.toString());
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

            try {
                return parseJson(jsonResponse);
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
            String overview;
            String posterPath;
            String year;
            for(int i = 0; i < results.length(); i++) {
                JSONObject movie = results.getJSONObject(i);
                title = movie.getString("original_title");
                overview = movie.getString("overview");
                posterPath = movie.getString("poster_path");
                year = movie.getString("release_date");

                movies[i] = new Movie(title, posterPath, overview, year);
            }
            return movies;
        }
    }
}