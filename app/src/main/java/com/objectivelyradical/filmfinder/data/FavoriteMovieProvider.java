package com.objectivelyradical.filmfinder.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by c.mark on 2015/12/03.
 */
public class FavoriteMovieProvider extends ContentProvider {
    private FavoriteMovieDBHelper mDbHelper;
    private UriMatcher mUriMatcher;

    /*
        Constants for URI type matching
    */
    private static final int URI_TYPE_MOVIE = 300;
    private static final int URI_TYPE_MOVIE_ID = 301;

    @Override
    public boolean onCreate() {
        mDbHelper = new FavoriteMovieDBHelper(getContext());
        mUriMatcher = buildUriMatcher();
        return true;
    }

    private UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = MovieContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, MovieContract.MOVIE_PATH, URI_TYPE_MOVIE);
        matcher.addURI(authority, MovieContract.MOVIE_PATH + "/#", URI_TYPE_MOVIE_ID);
        return matcher;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch(mUriMatcher.match(uri)) {
            case(URI_TYPE_MOVIE): {
                return MovieContract.CONTENT_ITEM_TYPE;
            }
            case(URI_TYPE_MOVIE_ID): {
                return MovieContract.CONTENT_TYPE;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch(mUriMatcher.match(uri)) {
            case(URI_TYPE_MOVIE): {
                return mDbHelper.getReadableDatabase().query(MovieContract.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
            }
            case(URI_TYPE_MOVIE_ID): {
                return mDbHelper.getReadableDatabase().query(MovieContract.TABLE_NAME, projection,
                        MovieContract.COLUMN_MOVIE_ID + " = ? ", new String[] { MovieContract.getIdFromUri(uri) },
                        null, null, sortOrder);
            }
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if(mUriMatcher.match(uri) == URI_TYPE_MOVIE) {
            mDbHelper.getWritableDatabase().insert(MovieContract.TABLE_NAME, null, values);

            // Because the movie ID is not the primary key, we have to fetch it from the values
            int id = values.getAsInteger(MovieContract.COLUMN_MOVIE_ID);
            return MovieContract.getMovieUri(id);
        }
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if(mUriMatcher.match(uri) == URI_TYPE_MOVIE) {
            return mDbHelper.getWritableDatabase().update(MovieContract.TABLE_NAME, values, selection, selectionArgs);
        }

        return 0;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch(mUriMatcher.match(uri)) {
            case(URI_TYPE_MOVIE): {
                return mDbHelper.getWritableDatabase().delete(MovieContract.TABLE_NAME, selection, selectionArgs);
            }
            case(URI_TYPE_MOVIE_ID): {
                return mDbHelper.getWritableDatabase().delete(MovieContract.TABLE_NAME,
                        MovieContract.COLUMN_MOVIE_ID + " = ?", new String[] { MovieContract.getIdFromUri(uri) });
            }
        }
        return 0;
    }
}