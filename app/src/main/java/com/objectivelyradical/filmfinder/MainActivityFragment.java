package com.objectivelyradical.filmfinder;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import com.squareup.picasso.Picasso;

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
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    final String LOG_TAG = "MAIN_ACTIVITY_FRAGMENT";
    MovieAdapter adapter;
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);
       // ImageView imageView = (ImageView)rootView.findViewById(R.id.movies_image_view);
        //Picasso.with(getActivity()).load("http://i.imgur.com/DvpvklR.png").into(imageView);
        adapter = new MovieAdapter(getContext(), new ArrayList<Movie>());
        GridView gridView = (GridView)rootView.findViewById(R.id.movie_list_view);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = (Movie) parent.getItemAtPosition(position);

                Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                intent.putExtra(getString(R.string.intent_extra_id_key), "" + movie.getId());
                startActivity(intent);
            }
        });

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
                Log.d(LOG_TAG, movies[i].getTitle() + " ... " + movies[i].getId());
            }

            updateMovieGrid(movies);
        }

        // Don't update this in the UI thread, because it blocks input while the posters are being loaded
        private void updateMovieGrid(Movie[] movies) {
            adapter.clear();
            adapter.addAll(movies);
        }

        @Override
        protected Movie[] doInBackground(String... params) {
            Uri.Builder uriBuilder = Uri.parse(BASE_URL).buildUpon();
            uriBuilder.appendPath("discover");
            uriBuilder.appendPath("movie");
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

        private Movie[] parseJson(String jsonString) throws JSONException {

            JSONObject root = new JSONObject (jsonString);
            JSONArray results = root.getJSONArray("results");
            Movie[] movies = new Movie[results.length()];
            String title;
            String posterPath;
            int id;
            for(int i = 0; i < results.length(); i++) {
                JSONObject movie = results.getJSONObject(i);
                title = movie.getString("original_title");
                posterPath = movie.getString("poster_path");
                id = movie.getInt("id");

                movies[i] = new Movie(title, posterPath, id);
            }
            return movies;
        }
    }
}