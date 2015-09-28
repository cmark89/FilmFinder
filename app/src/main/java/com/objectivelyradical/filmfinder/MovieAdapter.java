package com.objectivelyradical.filmfinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by c.mark on 2015/09/28.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    public MovieAdapter(Context context, ArrayList<Movie> movies) {
        super(context, R.layout.movie_list_item, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_list_item, parent, false);
        }

        // Populate data
        ImageView view = (ImageView)convertView.findViewById(R.id.movie_list_image_view);
        Picasso.with(getContext()).load(movie.getFullPosterPath()).into(view);
        return convertView;
    }
}
