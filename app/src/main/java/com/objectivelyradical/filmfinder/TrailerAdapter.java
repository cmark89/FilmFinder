package com.objectivelyradical.filmfinder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by c.mark on 2015/12/07.
 */
public class TrailerAdapter extends ArrayAdapter<Trailer> {
    public TrailerAdapter(Context context, ArrayList<Trailer> trailers) {
        super(context, R.layout.trailer_list_item, trailers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Trailer trailer = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.trailer_list_item, parent, false);
        }

        // Populate data
        TextView view = (TextView)convertView.findViewById(R.id.trailer_list_title);
        view.setText(trailer.getTitle());

        //view.setOnClickListener();
        ImageView imageView = (ImageView)convertView.findViewById(R.id.trailer_list_play_icon);
        convertView.setOnClickListener(new View.OnClickListener() {
            String mUrl = "http://youtube.com/watch?v=" + trailer.getLink();
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(mUrl));
                getContext().startActivity(intent);
            }
        });

        return convertView;
    }
}
