package com.objectivelyradical.filmfinder;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by c.mark on 2015/12/07.
 */
public class Trailer implements Parcelable {
    String title;
    String link;
    public String getTitle() {
        return title;
    }
    public String getLink() {
        return link;
    }

    public Trailer (String title, String link) {
        this.title = title;
        this.link = link;
    }

    public Trailer (Parcel p) {
        title = p.readString();
        link = p.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(link);
    }

    public static final Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel source) {
            return new Trailer(source);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };
}
