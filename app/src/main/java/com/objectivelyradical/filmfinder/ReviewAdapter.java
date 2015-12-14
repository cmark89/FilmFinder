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

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by c.mark on 2015/12/07.
 */
public class ReviewAdapter extends ArrayAdapter<Review> {
    public ReviewAdapter(Context context, ArrayList<Review> reviews) {
        super(context, R.layout.trailer_list_item, reviews);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Review review = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.review_list_item, parent, false);
        }

        TextView view = (TextView)convertView.findViewById(R.id.review_item_author);
        view.setText(review.getAuthor());
        view = (TextView)convertView.findViewById(R.id.review_item_text);
        view.setText(review.getText());

        // Set the callback for the button
        if(review.isLongText()) {
            ImageView imageView = (ImageView) convertView.findViewById(R.id.review_item_open);
            TextView readMore = (TextView) convertView.findViewById(R.id.review_item_read_more_text);
            View.OnClickListener listener = new View.OnClickListener() {
                String mUrl = review.getUrl();

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(mUrl));
                    getContext().startActivity(intent);
                }
            };
            convertView.setOnClickListener(listener);
        } else {
            ((View)convertView.findViewById(R.id.review_item_read_more)).setVisibility(View.INVISIBLE);
        }

        return convertView;
    }
}
