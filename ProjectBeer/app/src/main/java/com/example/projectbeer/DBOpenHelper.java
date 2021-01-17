package com.example.projectbeer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import androidx.annotation.Nullable;

public class DBOpenHelper extends SQLiteOpenHelper {

    public static class Constants implements BaseColumns
    {
        public static final String DATABASE_NAME = "PersonalCatalog.db";

        // The database version
        public static final int DATABASE_VERSION = 1;

        // The table Name
        public static final String MY_TABLE = "Catalog";

        //column names
        public static final String KEY_COL_ID = "_id";// Mandatory
        public static final String KEY_COL_LOCALISATION = "localisation";
        public static final String KEY_COL_COMMENT = "comment";
        public static final String KEY_COL_SCORE = "score";
        public static final String KEY_COL_PHOTO = "photo";
        public static final String KEY_COL_DATE = "date";
        public static final String KEY_COL_FK_USER = "fk_user";
        public static final String KEY_COL_FK_BEER = "fk_beer";

        // Indexes des colonnes
        public static final int ID_COLUMN = 0;
        public static final int LOCALISATION_COLUMN = 1;
        public static final int COMMENT_COLUMN = 2;
        public static final int SCORE_COLUMN = 3;
        public static final int PHOTO_COLUMN = 4;
        public static final int DATE_COLUMN = 5;
        public static final int FK_USER_COLUMN = 6;
        public static final int FK_BEER_COLUMN = 7;
    }

    private static final String CREATE_DATABASE = "create table"
            + Constants.MY_TABLE + "("
            + Constants.KEY_COL_ID + " integer primary key,"
            + Constants.KEY_COL_LOCALISATION + "TEXT,"
            + Constants.KEY_COL_COMMENT + "TEXT,"
            + Constants.KEY_COL_SCORE + "TEXT,"
            + Constants.KEY_COL_PHOTO + "TEXT,"
            + Constants.KEY_COL_DATE + "DATE)";

    private static final String SELECT_ALL_LINE = "Select * from \"" + Constants.MY_TABLE + "\"";

    public DBOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL((CREATE_DATABASE));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
