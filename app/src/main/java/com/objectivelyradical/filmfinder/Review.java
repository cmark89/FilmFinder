package com.objectivelyradical.filmfinder;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Console;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by c.mark on 2015/12/07.
 */
public class Review implements Parcelable {
    private final static int IS_LONG_TRUE = 1;
    private final static int IS_LONG_FALSE = 0;

    String author;
    String text;
    boolean longText = false;
    String url;
    public String getAuthor() {
        return author;
    }
    public String getText() {
        return text;
    }
    public String getUrl() {
        return url;
    }

    public Review (String author, String text, String url) {
        this.author = author;
        this.text = trimText(text);
        this.url = url;
    }

    public Review (Parcel p) {
        author = p.readString();
        text = p.readString();
        url = p.readString();

        int isLong = p.readInt();
        longText = (isLong == IS_LONG_TRUE);
    }


    public boolean isLongText() {
        return longText;
    }

    private String trimText(String full) {
        Pattern p = Pattern.compile("\n");
        Matcher m = p.matcher(full);
        if(m.find()) {
            longText = true;
            String[] parts = full.split("\n");
            return parts[0];
        } else {
            longText = false;
            return full;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(text);
        dest.writeString(url);

        int isLong = IS_LONG_FALSE;
        if(longText) {
            isLong = IS_LONG_TRUE;
        }
        dest.writeInt(isLong);
    }

    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel source) {
            return new Review(source);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}
