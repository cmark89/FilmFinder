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
import android.widget.TextView;

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
public class MovieDetailActivityFragment extends Fragment {
    private View detailView;

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        // Cache the root view so the async thread can manipulate it later
        detailView = rootView;


        // Start another API call to load the movie information
        FetchMovieDetailTask task = new FetchMovieDetailTask();
        task.execute(getActivity().getIntent().getStringExtra(getString(R.string.intent_extra_id_key)));
        return rootView;
    }

    public class FetchMovieDetailTask extends AsyncTask<String, Void, MovieDetails> {
        final String BASE_URL = "https://api.themoviedb.org/3/";
        final String LOG_TAG = "FetchMovieDetailTask";
        @Override
        protected MovieDetails doInBackground(String... params) {
            Uri.Builder uriBuilder = Uri.parse(BASE_URL).buildUpon();
            uriBuilder.appendPath("movie");
            uriBuilder.appendPath(params[0]);
            uriBuilder.appendQueryParameter("api_key", "ba60e393c00559c4e8ed4aae77dcd7cb"); // <--- API KEY GO HERE

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

        private MovieDetails parseJson(String jsonString) throws JSONException {

            JSONObject root = new JSONObject (jsonString);

            String title = root.getString("title");
            int id = root.getInt("id");
            String posterPath = root.getString("poster_path");
            String releaseDate = root.getString("release_date");
            String summary = root.getString("overview");
            float rating = (float)root.getDouble("vote_average");

            return new MovieDetails(title, posterPath, id, releaseDate, summary, rating);
        }

        @Override
        protected void onPostExecute(MovieDetails movie) {
            // Update the UI elements here
            ((TextView)detailView.findViewById(R.id.movie_detail_title)).setText(movie.getTitle());
            ((TextView)detailView.findViewById(R.id.movie_detail_synopsis)).setText(movie.getSummary());
            ((TextView)detailView.findViewById(R.id.movie_detail_release)).setText(movie.getReleaseDate());
            ((TextView)detailView.findViewById(R.id.movie_detail_rating)).setText(movie.getRating());

            ImageView poster = (ImageView)detailView.findViewById(R.id.movie_detail_poster);

            Picasso.with(getContext()).load(movie.getFullPosterPath()).into(poster);
        }
    }
}