package com.example.reminder;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RemindersDbAdapter {

    //these are the column names
    public static final String COL_ID = "_id";
    public static final String COL_CONTENT = "content";
    public static final String COL_IMPORTANT = "important";
    //these are the corresponding indices
    public static final int INDEX_ID = 0;
    public static final int INDEX_CONTENT = INDEX_ID + 1;
    public static final int INDEX_IMPORTANT = INDEX_ID + 2;
    //used for logging
    private static final String TAG = "RemindersDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private static final String DATABASE_NAME = "dba_remdrs";
    private static final String TABLE_NAME = "tbl_remdrs";
    private static final int DATABASE_VERSION = 1;
    private final Context mCtx;
    //SQL statement used to create the database
    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + TABLE_NAME + " ( " +
                    COL_ID + " INTEGER PRIMARY KEY autoincrement, " +
                    COL_CONTENT + " TEXT, " +
                    COL_IMPORTANT + " INTEGER );";

    private static final String GET_REMINDERS =
            "SELECT * FROM " + TABLE_NAME + ";";


    public RemindersDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }
    //open
    public void open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
    }
    //close
    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }

    //TODO implement the function createReminder() which take the name as the content of the reminder and boolean important...note that the id will be created for you automatically
    public void createReminder(String name, boolean important) {
        mDb=mDbHelper.getWritableDatabase();
        int i=0;
      if(important==true)
          i=1;
        mDb.execSQL("INSERT INTO " + TABLE_NAME + " ( " + COL_CONTENT+ ", "+ COL_IMPORTANT + " )" + " Values('" + name + "', " + i + ");");
}
    //TODO overloaded to take a reminder
    public long createReminder(Reminder reminder) {
        createReminder(reminder.getContent(),reminder.getImportant()==1);
        return 0;
    }

    //TODO implement the function fetchReminderById() to get a certain reminder given its id
    public Reminder fetchReminderById(int id) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_ID + " = " + id, null);
        if(cursor.getCount()!= 0)
        {
            cursor.moveToNext();
            Reminder reminder = new Reminder(cursor.getInt(cursor.getColumnIndex(RemindersDbAdapter.COL_ID))
                    , cursor.getString(cursor.getColumnIndex(RemindersDbAdapter.COL_CONTENT))
                    , cursor.getInt(cursor.getColumnIndex(RemindersDbAdapter.COL_IMPORTANT)));
            return reminder;
        }
        return null;
    }


    //TODO implement the function fetchAllReminders() which get all reminders
    public Cursor fetchAllReminders() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        return db.rawQuery(GET_REMINDERS, null);
    }

    //TODO implement the function updateReminder() to update a certain reminder
    public void updateReminder(Reminder reminder) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_NAME + " SET " + RemindersDbAdapter.COL_IMPORTANT + " = " + reminder.getImportant() + " , " + RemindersDbAdapter.COL_CONTENT + " = '" + reminder.getContent() + "' WHERE " + COL_ID + " = " + reminder.getId() + ";");    }
    //TODO implement the function deleteReminderById() to delete a certain reminder given its id
    public void deleteReminderById(int nId) {
        mDb.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + RemindersDbAdapter.COL_ID + " = " + nId );
    }

    //TODO implement the function deleteAllReminders() to delete all reminders
    public void deleteAllReminders() {
        mDb.execSQL("DELETE FROM " + TABLE_NAME + ";");
    }


    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG, DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }


}
