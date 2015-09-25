package com.sharad.days;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Sharad on 15-Jun-14.
 */
public class DataProvider {
    // For logging
    private static final String TAG = "DataProvider";

    public static final int DATABASE_VERSION = 1;

    // DB Item FD table fields
    public static final String KEY_EVENT_ROWID      = "_id";
    public static final String KEY_EVENT_TITLE      = "title";
    public static final String KEY_EVENT_DATE       = "date";
    public static final String KEY_EVENT_COLOR      = "color";
    public static final String KEY_EVENT_REPEAT     = "notify";
    public static final String KEY_EVENT_FAVORITE   = "favorite";


    public static final String[] ALL_KEYS_EVENT = new String[] {KEY_EVENT_ROWID, KEY_EVENT_TITLE,
			KEY_EVENT_DATE, KEY_EVENT_COLOR, KEY_EVENT_REPEAT, KEY_EVENT_FAVORITE };

    public static final String DATABASE_NAME = "savings";
    public static final String DATABASE_TABLE_EVENT = "event_table";

    private static final String DATABASE_CREATE_SQL_EVENT = "create table " + DATABASE_TABLE_EVENT
            + " ("
            + KEY_EVENT_ROWID     + " integer primary key autoincrement, "
            + KEY_EVENT_TITLE     + " text not null, "
            + KEY_EVENT_DATE      + " integer not null, "
            + KEY_EVENT_COLOR     + " integer not null, "
            + KEY_EVENT_REPEAT    + " integer not null, "
            + KEY_EVENT_FAVORITE  + " integer not null"
            + ");";

    private final Context context;

    private DatabaseHelper myDBHelper;
    private SQLiteDatabase db;

    public DataProvider(Context ctx) {
        this.context = ctx;
        myDBHelper = new DatabaseHelper(context);
    }

    // Open the database connection.
    public DataProvider open() {
        db = myDBHelper.getWritableDatabase();

        // Enable foreign key constraints
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys = ON;");
        }

        return this;
    }

    // Close the database connection.
    public void close() {
        myDBHelper.close();
    }

    /*
       ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
       +++++++++++++++++++++ FD RECORD METHODS ++++++++++++++++++++++
       ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    */
    public long insertEvent(Event event) {
        // Create row's data:
        ContentValues content = new ContentValues();
        content.put(KEY_EVENT_TITLE    , event.get_title());
        content.put(KEY_EVENT_DATE     , event.get_startDate().getTime());
        content.put(KEY_EVENT_COLOR    , event.get_colorId());
        content.put(KEY_EVENT_REPEAT   , event.get_repeat());
        content.put(KEY_EVENT_FAVORITE , event.get_favoriteIndex());

        // Insert it into the database.
        return db.insert(DATABASE_TABLE_EVENT, null, content);
    }
	
	public boolean updateEvent(Event event) {
        String where = KEY_EVENT_ROWID + "=" + event.get_id();

        // Create row's data:
        ContentValues content = new ContentValues();
        content.put(KEY_EVENT_TITLE    , event.get_title());
        content.put(KEY_EVENT_DATE     , event.get_startDate().getTime());
        content.put(KEY_EVENT_COLOR    , event.get_colorId());
        content.put(KEY_EVENT_REPEAT   , event.get_repeat());
        content.put(KEY_EVENT_FAVORITE , event.get_favoriteIndex());

        // Update it into the database.
        return db.update(DATABASE_TABLE_EVENT, content, where, null) != 0;
    }

    public boolean deleteEvent(long rowId) {
        String where = KEY_EVENT_ROWID + "=" + rowId;
        return db.delete(DATABASE_TABLE_EVENT, where, null) != 0;
    }
	
	public void deleteAllEvents() {
		db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_EVENT);
        db.execSQL(DATABASE_CREATE_SQL_EVENT);
    }
	
	private Event parseEvent(Cursor c) {
        long id 		 = c.getLong(c.getColumnIndex(KEY_EVENT_ROWID));
        String title 	 = c.getString(c.getColumnIndex(KEY_EVENT_TITLE));
        Date stDate		 = new Date(c.getLong(c.getColumnIndex(KEY_EVENT_DATE)));
        int colorId	     = (int)c.getLong(c.getColumnIndex(KEY_EVENT_COLOR));
        int notify       = (int)c.getLong(c.getColumnIndex(KEY_EVENT_REPEAT));
        int favorite     = (int)c.getLong(c.getColumnIndex(KEY_EVENT_FAVORITE));

        Event event = new Event(id, title, stDate, colorId, notify, favorite);
        return event;
	}
	
	public Event getEvent(long rowId) {
        Event event = null;
        String where = KEY_EVENT_ROWID + "=" + rowId;
        Cursor c = 	db.query(true, DATABASE_TABLE_EVENT, ALL_KEYS_EVENT,
                where, null, null, null, null, null);
        if(c.moveToFirst()) {
            event = parseEvent(c);
        }
        return event;
    }
	
	public void getEvents(ArrayList<Event> events) {
        getEvents(events, null);
    }
	
	public void getEvents(ArrayList<Event> events, String where) {
        events.clear();
        Cursor c = 	db.query(true, DATABASE_TABLE_EVENT, ALL_KEYS_EVENT,
                where, null, null, null, null, null);
        if (c != null) {
            if(c.moveToFirst()) {
				do {
                    events.add(parseEvent(c));
				} while (c.moveToNext());
			}
        }
    }

    /////////////////////////////////////////////////////////////////////
    //	Private Helper Classes:
    /////////////////////////////////////////////////////////////////////

    /**
     * Private class which handles database creation and upgrading.
     * Used to handle low-level database access.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase _db) {
            _db.execSQL(DATABASE_CREATE_SQL_EVENT);
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading application's database from version " + oldVersion
                    + " to " + newVersion + ", which will destroy all old data!");

            // Destroy old database:
            _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_EVENT);

           // Recreate new database:
            onCreate(_db);
        }
    }
}
