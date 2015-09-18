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
    public static final String KEY_FD_ROWID      = "_id";
    public static final String KEY_FD_TITLE      = "title";
    public static final String KEY_FD_DATE       = "date";
    public static final String KEY_FD_END_DATE   = "end_date";


    public static final String[] ALL_KEYS_FD = new String[] {KEY_FD_ROWID, KEY_FD_TITLE,
			KEY_FD_DATE, KEY_FD_END_DATE };

    public static final String DATABASE_NAME = "savings";
    public static final String DATABASE_TABLE_FD = "fd_table";

    private static final String DATABASE_CREATE_SQL_FD = "create table " + DATABASE_TABLE_FD
            + " ("
            + KEY_FD_ROWID     + " integer primary key autoincrement, "
            + KEY_FD_TITLE     + " text not null, "
            + KEY_FD_DATE      + " integer not null, "
            + KEY_FD_END_DATE  + " integer not null"
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
    public long insertDeposit(Event event) {
        // Create row's data:
        ContentValues content = new ContentValues();
        content.put(KEY_FD_TITLE    , event.get_title());
        content.put(KEY_FD_DATE     , event.get_startDate().getTime());
        content.put(KEY_FD_END_DATE , event.get_endDate().getTime());

        // Insert it into the database.
        return db.insert(DATABASE_TABLE_FD, null, content);
    }
	
	public boolean updateDeposit(long rowId, Event event) {
        String where = KEY_FD_ROWID + "=" + rowId;

        // Create row's data:
        ContentValues content = new ContentValues();
        content.put(KEY_FD_TITLE    , event.get_title());
        content.put(KEY_FD_DATE     , event.get_startDate().getTime());
        content.put(KEY_FD_END_DATE , event.get_endDate().getTime());

        // Update it into the database.
        return db.update(DATABASE_TABLE_FD, content, where, null) != 0;
    }

    public boolean deleteDeposit(long rowId) {
        String where = KEY_FD_ROWID + "=" + rowId;
        return db.delete(DATABASE_TABLE_FD, where, null) != 0;
    }
	
	public void deleteAllDeposits() {
		db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_FD);
        db.execSQL(DATABASE_CREATE_SQL_FD);
    }
	
	private Event parseDeposit(Cursor c) {
        long id 		= c.getLong(c.getColumnIndex(KEY_FD_ROWID));
        String title 	= c.getString(c.getColumnIndex(KEY_FD_TITLE));
        Date stDate		= new Date(c.getLong(c.getColumnIndex(KEY_FD_DATE)));
        Date endDate	= new Date(c.getLong(c.getColumnIndex(KEY_FD_END_DATE)));

        Event event = new Event(id, title, stDate, endDate);
        return event;
	}
	
	public Event getDeposit(long rowId) {
        Event deposit = null;
        String where = KEY_FD_ROWID + "=" + rowId;
        Cursor c = 	db.query(true, DATABASE_TABLE_FD, ALL_KEYS_FD,
                where, null, null, null, null, null);
        if(c.moveToFirst()) {
            deposit = parseDeposit(c);
        }
        return deposit;
    }
	
	public void getDeposits(ArrayList<Event> deposits) {
		getDeposits(deposits, null);
	}
	
	public void getDeposits(ArrayList<Event> deposits, String where) {
        deposits.clear();
        Cursor c = 	db.query(true, DATABASE_TABLE_FD, ALL_KEYS_FD,
                where, null, null, null, null, null);
        if (c != null) {
            if(c.moveToFirst()) {
				do {
					deposits.add(parseDeposit(c));
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
            _db.execSQL(DATABASE_CREATE_SQL_FD);
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading application's database from version " + oldVersion
                    + " to " + newVersion + ", which will destroy all old data!");

            // Destroy old database:
            _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_FD);

           // Recreate new database:
            onCreate(_db);
        }
    }
}
