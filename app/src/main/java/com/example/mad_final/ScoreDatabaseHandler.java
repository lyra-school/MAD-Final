package com.example.mad_final;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

// adapted from DB worksheets
public class ScoreDatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "leaderboard";
    private static final String TABLE_RECORDS = "records";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_SCORE = "score";

    public ScoreDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_RECORDS_TABLE = "CREATE TABLE " + TABLE_RECORDS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_SCORE + " INTEGER" + ")";
        db.execSQL(CREATE_RECORDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDS);

        onCreate(db);
    }

    public void addRecord(RecordEntry record) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, record.getName());
        values.put(KEY_SCORE, record.getScore());

        db.insert(TABLE_RECORDS, null, values);
        db.close();
    }

    public List<RecordEntry> getFiveHighestRecords()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        List<RecordEntry> fiveHighest = new ArrayList<>();
        // query adapted from:
        // https://github.com/JamieBredin/AndoridStudioDatabase/blob/master/app/src/main/java/com/example/gameprojecthighscoredatabase/DatabaseHandler.java
        String query = "SELECT id, name, score FROM " + TABLE_RECORDS + " ORDER BY score DESC LIMIT 5";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                fiveHighest.add(new RecordEntry(cursor.getInt((0)),
                        cursor.getString(1),
                        cursor.getInt(2)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return fiveHighest;
    }

    public boolean isDatabaseEmpty() {
        SQLiteDatabase db = this.getReadableDatabase();

        // https://stackoverflow.com/questions/71636656/check-if-table-is-empty-in-sqlite
        String query = "SELECT count(*) FROM (select 1 from records limit 1)";
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()) {
            if(cursor.getInt(0) != 0) {
                cursor.close();
                return true;
            } else {
                cursor.close();
                return false;
            }
        } else {
            cursor.close();
            return false;
        }

    }
}
