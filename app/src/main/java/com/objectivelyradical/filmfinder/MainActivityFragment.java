package com.objectivelyradical.filmfinder;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.objectivelyradical.filmfinder.data.MovieContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements MovieDataHandler.MovieLoadCallback {
    final String LOG_TAG = "MAIN_ACTIVITY_FRAGMENT";
    final String MOVIE_LIST_KEY = "MOVIE_LIST";
    final String SORT_METHOD_KEY = "SORT_METHOD";
    ProgressDialog mProgress;
    MovieAdapter adapter;
    ArrayList<Movie> mMovieList;
    String mLastSortMethod = "";
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);
        adapter = new MovieAdapter(getContext(), new ArrayList<Movie>());
        mProgress = new ProgressDialog(getContext());
        mProgress.setTitle(getString(R.string.loading_title));
        mProgress.setMessage(getString(R.string.loading_message));


        GridView gridView = (GridView)rootView.findViewById(R.id.movie_list_view);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = (Movie) parent.getItemAtPosition(position);
                ((Callback) getActivity()).onMovieSelected(movie, position);
            }
        });
        if(savedInstanceState != null) {
            mLastSortMethod = savedInstanceState.getString(SORT_METHOD_KEY);
            mMovieList = (ArrayList<Movie>)savedInstanceState.get(MOVIE_LIST_KEY);
            populateMovieList();
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Check if the preferred sort method has changed
        // if it hasn't changed, we don't need to requery the movies
        String sortMethod = PreferenceManager.getDefaultSharedPreferences(getContext())
                .getString(getContext().getString(R.string.pref_sort_type_key),
                        getContext().getString(R.string.pref_sort_type_default));
        if(!sortMethod.equals(mLastSortMethod)) {
            loadMovies();
        }
        mLastSortMethod = sortMethod;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mMovieList != null) {
            outState.putParcelableArrayList(MOVIE_LIST_KEY, mMovieList);
        }
        outState.putString(SORT_METHOD_KEY, mLastSortMethod);
    }

    @Override
    public void onStart() {
        super.onStart();

        // When the fragment starts, load the list of movies if we don't have any yet
        // Otherwise, restore the previous list
        if(mMovieList == null) {
            loadMovies();
        }

        // Run an empty query against the content provider to verify that it works
        Cursor c = getContext().getContentResolver().query(MovieContract.getMovieUri(), null, null, null, null);
        Log.d(LOG_TAG, "Content Provider query returned " + c.getCount() + " rows.");
        c.close();
    }

    public void loadMovies() {
        mProgress.show();
        MovieDataHandler handler = new MovieDataHandler();
        handler.getMovies(getContext(), this, networkAvailable());
    }

    private void populateMovieList() {
        mProgress.dismiss();
        adapter.clear();
        adapter.addAll(mMovieList);
    }

    public void onMovieLoadComplete(Movie[] movies) {
        if(movies.length > 0) {
            mMovieList = new ArrayList<Movie>(Arrays.asList(movies));
        } else {
            mMovieList = null;
        }

        populateMovieList();
    }

    public interface Callback {
        void onMovieSelected(Movie m, int i);
    }



    // based on stack overflow snippet from previous code review
    public boolean networkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}