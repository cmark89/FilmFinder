package com.objectivelyradical.filmfinder;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback {
    boolean mTablet = false;
    // These are for restoring the instance state when in tablet-mode without pointless requerying:
    ArrayList<Review> mReviews;
    ArrayList<Trailer> mTrailers;
    Movie mMovie;

    int mSelectedIndex = 0;
    static final String DETAIL_TAG = "DETAIL_FRAGMENT";
    static final String SAVED_MOVIE_TAG = "savedmovie";
    static final String SELECTED_MOVIE_KEY = "SELECTED_MOVIE";
    static final String SAVED_TRAILERS_KEY = "SAVED_TRAILERS";
    static final String SAVED_REVIEWS_KEY = "SAVED_REVIEWS_KEY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View rightPane = findViewById(R.id.right_fragment_container);
        if(rightPane != null) {
            mTablet = true;
            Fragment welcomeFragment = new WelcomeFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.right_fragment_container, welcomeFragment)
                    .commit();
        } else {
            mTablet = false;
        }

        // Here we respawn the detail fragment if the saved movie is not null and we're in tablet
        // view. I really wanted a welcome fragment, you see.
        if(savedInstanceState != null) {
            if(mTablet) {
                mSelectedIndex = savedInstanceState.getInt(SELECTED_MOVIE_KEY);

                GridView movieList = (GridView)findViewById(R.id.movie_list_view);
                if(movieList != null) {
                    movieList.setSelection(mSelectedIndex);
                    movieList.smoothScrollToPosition(mSelectedIndex);
                }
                mMovie = savedInstanceState.getParcelable(SAVED_MOVIE_TAG);
                mTrailers = (ArrayList<Trailer>)savedInstanceState.get(SAVED_TRAILERS_KEY);
                mReviews = (ArrayList<Review>)savedInstanceState.get(SAVED_REVIEWS_KEY);
                if (mMovie != null) {
                    Fragment detailFragment = MovieDetailFragment.getMovieDetailFragment(mMovie, mReviews, mTrailers);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.right_fragment_container, detailFragment, DETAIL_TAG)
                            .commit();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onMovieSelected(Movie m, int position) {
        if(mTablet) {
            mSelectedIndex = position;
            Fragment fragment = MovieDetailFragment.getMovieDetailFragment(m, null, null);
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.right_fragment_container, fragment, DETAIL_TAG)
                    .commit();
            mMovie = m;
        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtra(getString(R.string.intent_parcelable_key), m);
            startActivity(intent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVED_MOVIE_TAG, mMovie);
        GridView movieList = (GridView)findViewById(R.id.movie_list_view);
        if(movieList != null) {
            outState.putInt(SELECTED_MOVIE_KEY, mSelectedIndex);
        }
        if(mTablet) {
            MovieDetailFragment detailFragment = (MovieDetailFragment)
                    getSupportFragmentManager().findFragmentByTag(DETAIL_TAG);
            if(detailFragment != null) {
                mReviews = detailFragment.getSavedReviews();
                mTrailers = detailFragment.getSavedTrailers();
            }
            if(mReviews == null) {
                mReviews = new ArrayList<Review>();
            }
            if(mTrailers == null) {
                mTrailers = new ArrayList<Trailer>();
            }
            outState.putParcelableArrayList(SAVED_REVIEWS_KEY, mReviews);
            outState.putParcelableArrayList(SAVED_TRAILERS_KEY, mTrailers);
        }
    }
}

