package com.objectivelyradical.filmfinder;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment implements MovieDataHandler.DetailLoadCallback{
    Movie mMovie;
    ArrayList<Trailer> mTrailers;
    ArrayList<Review> mReviews;

    View mRootView;
    boolean mIsFavorite;
    static final String SAVED_MOVIE_KEY = "savedmovie";
    static final String SAVED_REVIEWS_KEY = "savedreviews";
    static final String SAVED_TRAILERS_KEY = "savedtrailers";

    public MovieDetailFragment() {
    }

    public static MovieDetailFragment getMovieDetailFragment(Movie movie, ArrayList<Review> reviews, ArrayList<Trailer> trailers) {
        MovieDetailFragment newFragment = new MovieDetailFragment();
        newFragment.mMovie = movie;
        newFragment.mReviews = reviews;
        newFragment.mTrailers = trailers;
        return newFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        // Grab the passed Movie and use it to populate the fields on the screen
        Movie passedMovie = getActivity().getIntent().getParcelableExtra(getString(R.string.intent_parcelable_key));
        if(passedMovie != null) {
            mMovie = passedMovie;
        }
        if(savedInstanceState != null) {
            Log.d("MovieDetailFragment", "LOAD FROM INSTANCE STATE!");
            mMovie = savedInstanceState.getParcelable(SAVED_MOVIE_KEY);
            mTrailers = (ArrayList<Trailer>)savedInstanceState.get(SAVED_TRAILERS_KEY);
            mReviews = (ArrayList<Review>)savedInstanceState.get(SAVED_REVIEWS_KEY);
            if(mTrailers == null) {
                Log.d("MovieDetailFragment", "NO TRAILERS SAVED");
            } else {
                for(Trailer t : mTrailers) {
                    Log.d("MovieDetailFragment", "Found saved trailer: " + t.getTitle());
                }
            }
            if(mReviews == null) {
                Log.d("MovieDetailFragment", "NO REVIEWS SAVED");
            } else {
                for(Review r : mReviews) {
                    Log.d("MovieDetailFragment", "Found review: " + r.getAuthor());
                }
            }
        }

        //mReviewAdapter = new ReviewAdapter(getContext(), new ArrayList<Review>());
        //mTrailerAdapter = new TrailerAdapter(getContext(), new ArrayList<Trailer>());

        return mRootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateDetails();
    }

    // In the event of a tablet layout, this is used by the Activity to cache our trailers and
    // reviews before the fragment is stripped down and recreated from scratch
    // ...I'm certain there's a better way to do this whole thing.
    public ArrayList<Trailer> getSavedTrailers() {
        return mTrailers;
    }
    public ArrayList<Review> getSavedReviews() {
        return mReviews;
    }

    private void updateDetails() {
        Log.d("MovieDetailFragment", "updateDetails()");
        final int id = mMovie.getId();

        ((TextView)mRootView.findViewById(R.id.movie_detail_title)).setText(mMovie.getTitle());
        ((TextView)mRootView.findViewById(R.id.movie_detail_synopsis)).setText(mMovie.getSummary());
        ((TextView)mRootView.findViewById(R.id.movie_detail_release)).setText(getString(R.string.details_release_date) + mMovie.getReleaseDate());
        ((TextView)mRootView.findViewById(R.id.movie_detail_rating)).setText(getString(R.string.details_rating) + mMovie.getRating());

        String path = mMovie.getFullPosterPath();
        // Don't pass the poster URL if it's empty or NULL
        if(path != null && path.length() > 0) {
            Picasso.with(getContext()).load(mMovie.getFullPosterPath()).into((ImageView) mRootView
                    .findViewById(R.id.movie_detail_poster));
        }

        // Setup the favorite / unfavorite action here
        mIsFavorite = mMovie.isFavorite(getContext());
        final ImageView starButton = (ImageView)mRootView.findViewById(R.id.movie_detail_favorite);

        if(mIsFavorite) {
            starButton.setColorFilter(getResources().getColor(R.color.favorite_active));
        } else {
            starButton.setColorFilter(getResources().getColor(R.color.favorite_inactive));
        }

        mRootView.findViewById(R.id.movie_detail_favorite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MovieDataHandler handler = new MovieDataHandler();
                if (!mIsFavorite) {
                    // Add it to favorites and change the button
                    starButton.setColorFilter(getResources().getColor(R.color.favorite_active));
                    handler.addMovieToFavorites(getContext(), mMovie);
                } else {
                    // Delete it from favorites and change the button
                    starButton.setColorFilter(getResources().getColor(R.color.favorite_inactive));
                    handler.removeMovieFromFavorites(getContext(), id);
                }
                mIsFavorite = !mIsFavorite;
            }
        });

        MovieDataHandler handler = new MovieDataHandler();
        if(mTrailers == null) {
            Log.d("MovieDetailFragment", "NO TRAILERS SAVED");
            handler.getTrailers(id, this);
        } else {
            Log.d("MovieDetailFragment", "Restore trailers");
            updateTrailerList();
        }
        if(mReviews == null) {
            Log.d("MovieDetailFragment", "NO REVIEWS SAVED");
            handler.getReviews(id, this);
        } else {
            Log.d("MovieDetailFragment", "Restore reviews");
            updateReviewList();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVED_MOVIE_KEY, mMovie);

        // If there are no reviews or trailers, we want to save an empty list
        // so we don't keep querying for them when the fragment is recreated
        if(mReviews == null) {
            mReviews = new ArrayList<Review>();
        }
        if(mTrailers == null) {
            mTrailers = new ArrayList<Trailer>();
        }
        outState.putParcelableArrayList(SAVED_REVIEWS_KEY, mReviews);
        outState.putParcelableArrayList(SAVED_TRAILERS_KEY, mTrailers);
    }

    @Override
    public void onReviewLoadComplete(Review[] reviews) {
        mReviews = new ArrayList<Review>( Arrays.asList(reviews));
        updateReviewList();
    }

    @Override
    public void onTrailerLoadComplete(Trailer[] trailers) {
        mTrailers = new ArrayList<Trailer>(Arrays.asList(trailers));
        updateTrailerList();
    }

    private void updateTrailerList() {
        LinearLayout layout = (LinearLayout) mRootView.findViewById(R.id.detail_trailer_list);
        if(mTrailers.size() > 0) {
            View newView;
            for(final Trailer t : mTrailers) {
                newView =  LayoutInflater.from(getContext()).inflate(R.layout.trailer_list_item, layout, false);

                // Populate data
                TextView view = (TextView)newView.findViewById(R.id.trailer_list_title);
                view.setText(t.getTitle());

                newView.setOnClickListener(new View.OnClickListener() {
                    String mUrl = "http://youtube.com/watch?v=" + t.getLink();
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(mUrl));
                        getContext().startActivity(intent);
                    }
                });
                layout.addView(newView);
            }
        } else {
            layout.setVisibility(View.INVISIBLE);
            mRootView.findViewById(R.id.detail_trailer_text).setVisibility(View.INVISIBLE);
        }
    }

    private void updateReviewList() {
        LinearLayout layout = (LinearLayout) mRootView.findViewById(R.id.detail_review_list);
        if(mReviews.size() > 0) {
            // Inflate the reviews
            View newView;
            for(final Review r : mReviews) {
                newView = LayoutInflater.from(getContext()).inflate(R.layout.review_list_item, layout, false);

                TextView view = (TextView)newView.findViewById(R.id.review_item_author);
                view.setText(r.getAuthor());
                view = (TextView)newView.findViewById(R.id.review_item_text);
                view.setText(r.getText());

                // Set the callback for the button
                if(r.isLongText()) {
                    View.OnClickListener listener = new View.OnClickListener() {
                        String mUrl = r.getUrl();

                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(mUrl));
                            getContext().startActivity(intent);
                        }
                    };
                    newView.setOnClickListener(listener);
                } else {
                    newView.findViewById(R.id.review_item_read_more).setVisibility(View.INVISIBLE);
                }
                layout.addView(newView);
            }
        } else {
            layout.setVisibility(View.INVISIBLE);
            mRootView.findViewById(R.id.detail_review_text).setVisibility(View.INVISIBLE);
        }
    }
}