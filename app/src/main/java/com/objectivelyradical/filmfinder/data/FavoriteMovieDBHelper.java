package com.objectivelyradical.filmfinder.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Debug;
import android.util.Log;

/**
 * Created by c.mark on 2015/12/03.
 */
public class FavoriteMovieDBHelper extends SQLiteOpenHelper {
    private static String LOG_TAG = "FavoriteMovieDBHelper";
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "favoritemovies.db";

    public FavoriteMovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*
        When creating the table, we do not use the movie's API ID as the primary key,
        because our app has no way to guarantee that the values will actually be unique
        */
        String createQuery = "CREATE TABLE " + MovieContract.TABLE_NAME + "(" +
                MovieContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieContract.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                MovieContract.COLUMN_TITLE + " TEXT NOT NULL," +
                MovieContract.COLUMN_POSTER_PATH + " TEXT NOT NULL," +
                MovieContract.COLUMN_RATING + " REAL NOT NULL," +
                MovieContract.COLUMN_RELEASE_DATE + " TEXT NOT NULL," +
                MovieContract.COLUMN_SUMMARY + " TEXT NOT NULL" +
        ")";
        db.execSQL(createQuery);
        Log.d(LOG_TAG, "Initialized " + DATABASE_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String purgeQuery = "DROP TABLE IF EXISTS " + MovieContract.TABLE_NAME;
        db.execSQL(purgeQuery);
        onCreate(db);
    }
}
