package com.objectivelyradical.filmfinder;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

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
        Picasso.with(getActivity()).load("http://i.imgur.com/DvpvklR.png").fit().into(imageView);
        return rootView;
    }
}