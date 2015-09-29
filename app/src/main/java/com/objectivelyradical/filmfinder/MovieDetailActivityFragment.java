package com.objectivelyradical.filmfinder;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        // Grab the passed Movie and use it to populate the fields on the screen
        Movie movie = getActivity().getIntent().getParcelableExtra(getString(R.string.intent_parcelable_key));

        ((TextView)rootView.findViewById(R.id.movie_detail_title)).setText(movie.getTitle());
        ((TextView)rootView.findViewById(R.id.movie_detail_synopsis)).setText(movie.getSummary());
        ((TextView)rootView.findViewById(R.id.movie_detail_release)).setText(getString(R.string.details_release_date) + movie.getReleaseDate());
        ((TextView)rootView.findViewById(R.id.movie_detail_rating)).setText(getString(R.string.details_rating) + movie.getRating());
        Picasso.with(getContext()).load(movie.getFullPosterPath()).into((ImageView) rootView
                .findViewById(R.id.movie_detail_poster));

        return rootView;
    }
}