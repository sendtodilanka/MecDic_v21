package com.sldroid.mecdic_v21.dbms;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;

public class TestAdapter
{
    protected static final String TAG = "DataAdapter";

    private final Context mContext;
    private SQLiteDatabase mDb;
    private DataBaseHelper mDbHelper;

    public TestAdapter(Context context)
    {
        this.mContext = context;
        mDbHelper = new DataBaseHelper(mContext);
    }

    public TestAdapter createDatabase() throws SQLException
    {
        try
        {
            mDbHelper.createDataBase();
        }
        catch (IOException mIOException)
        {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public TestAdapter open() throws SQLException
    {
        try
        {
            try {
                mDbHelper.openDataBase();
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "open >>"+ mSQLException.toString());
            throw mSQLException;
        }
        return this;
    }

    public void close()
    {
        mDbHelper.close();
    }

    public Cursor getAllWord(String table)
    {
        Cursor mCursor = mDb.query(table, new String[] {"_id","word","definition","favourite"},
                null, null, null,null, " word COLLATE NOCASE");

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor getWordByName(String table, String inputText) throws SQLException {
        Cursor mCursor = null;
        if (inputText == null  ||  inputText.length () == 0)  {
            mCursor = mDb.query(table, new String[] {"_id","word","definition","favourite"},
                    null, null, null,null, " word COLLATE NOCASE");

        }
        else {
            mCursor = mDb.query(true, table, new String[] {"_id","word","definition","favourite"},
                    "word" + " like '" + inputText + "%'", null,
                    null, null, " word COLLATE NOCASE",null);
        }
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public void favUpdate(String table, String id, int num){

        ContentValues cv = new ContentValues();
        cv.put("favourite", num);

        mDb.update(table, cv, "_id="+id, null);
    }

    public boolean isFavourite(String table,String id){
        boolean state = false;
        Cursor mCursor = mDb.query(
                table, new String[] {"favourite"}, "_id=?", new String[]{id}, null,null,null);

        if (mCursor.moveToFirst()) {
            do {
                if(mCursor.getInt(mCursor.getColumnIndex("favourite"))==1)
                    state = true;
                else
                    state = false;
            } while (mCursor.moveToNext());
        }
        return state;
    }
}
