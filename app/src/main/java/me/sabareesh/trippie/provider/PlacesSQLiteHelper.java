package me.sabareesh.trippie.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by VE288800 on 09-Jan-17.
 */

public class PlacesSQLiteHelper extends SQLiteOpenHelper {


    public static final String TAG = "PlacesSQLiteHelper";
    public static final String DB_NAME="Trippie";
    static final String TABLE_NAME="FavoritePlaces";
    static final int DB_VERSION=1;

    //DB columns
    public static final String ROW_ID="id";
    public static final String ID="placeid";
    public static final String TITLE="title";
    public static final String POSTERPATH_WIDE="posterPathWide";
    public static final String FILEPATH_WIDE_CACHE="filePathWide";
    public static final String RATING_AVG="rating";
    public static final String ADDRESS_PHONE="phone";
    public static final String ADDRESS_WEB="web";
    public static final String ADDRESS_URL="url";


    static final String CREATE_TABLE=" CREATE TABLE " + TABLE_NAME +
            " ( "+ROW_ID+
            " INTEGER PRIMARY KEY AUTOINCREMENT, " + ID+
            " TEXT NOT NULL, " + "" + TITLE+
            " TEXT NOT NULL, " + POSTERPATH_WIDE+
            //" TEXT NOT NULL, " + FILEPATH_SQUARE_CACHE+
            //" TEXT NOT NULL, " + FILEPATH_WIDE_CACHE+
            " TEXT NOT NULL, " + RATING_AVG+
            " TEXT NOT NULL, " + ADDRESS_PHONE+
            " TEXT NOT NULL, " + ADDRESS_WEB+
            " TEXT NOT NULL, " + ADDRESS_URL+
            " TEXT NOT NULL); ";



    public PlacesSQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(" Creating a table", "" + CREATE_TABLE);
        db.execSQL(CREATE_TABLE );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +  TABLE_NAME);
        onCreate(db);
    }
}
