package com.example.asd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.List;
import android.content.ContentValues;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.Date;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "meeting_scheduler.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_MEETINGS = "meetings";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DATETIME = "datetime";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_MEETINGS = "CREATE TABLE " + TABLE_MEETINGS + "("
                + COLUMN_ID + " TEXT PRIMARY KEY,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_DATETIME + " INTEGER"
                + ")";
        db.execSQL(CREATE_TABLE_MEETINGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEETINGS);
        onCreate(db);
    }

    public void addMeeting(Meeting meeting) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, meeting.getId());
        values.put(COLUMN_TITLE, meeting.getTitle());
        values.put(COLUMN_DATETIME, meeting.getDateTime().getTime());
        db.insert(TABLE_MEETINGS, null, values);
        db.close();
    }

    public void deleteMeeting(String meetingId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MEETINGS, COLUMN_ID + " = ?", new String[]{meetingId});
        db.close();
    }

    public List<Meeting> getAllMeetings() {
        List<Meeting> meetings = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_MEETINGS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
            int dateTimeIndex = cursor.getColumnIndex(COLUMN_DATETIME);

            do {
                if (idIndex != -1 && titleIndex != -1 && dateTimeIndex != -1) {
                    String id = cursor.getString(idIndex);
                    String title = cursor.getString(titleIndex);
                    long dateTimeInMillis = cursor.getLong(dateTimeIndex);
                    Date dateTime = new Date(dateTimeInMillis);
                    meetings.add(new Meeting(id, title, dateTime));
                } else {
                    // Handle column index -1 error
                    // Log an error message or perform appropriate error handling
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return meetings;
    }

}
