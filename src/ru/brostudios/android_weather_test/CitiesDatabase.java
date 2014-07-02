package ru.brostudios.android_weather_test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CitiesDatabase {
	
	public static final String DB_NAME = "weather_test.db";
	public static final String DB_TABLE = "cities";
	public static final String DB_ROW_NAME = "city";
	public static final int DB_VERSION = 1;
	
	private SQLiteDatabase database;
	private DatabaseHelper helper;
	
	public CitiesDatabase(Context context) {
		helper = new DatabaseHelper(context);
	}
	
	public CitiesDatabase open() {
		database = helper.getWritableDatabase();
		return this;
	}
	
	public boolean isEmpty() {
		Cursor cursor = database.rawQuery("select * from "+DB_TABLE, null);
		if(cursor.getCount()>0) return false;
		else return true;
		
	}
	
	public void close() {
		database.close();
	}
	
	public boolean insert(String row_name) {
		// if success returns true, false otherwise
		ContentValues values = new ContentValues();
		//values.put(DB_ROW_ID, row_id);
		values.put(DB_ROW_NAME, row_name);
		// next field is used for log about inserting row
		String log = DB_ROW_NAME+": "+row_name;
		try {
			// don't put an "insert" method, because it hasn't a throw
			database.insertOrThrow(DB_TABLE, null, values);
		} catch(SQLiteConstraintException e) {
			// if we have some errors with database
			Log.d("DatabaseHandler", "Insert failed, "+log+". Member is not unique!");
			return false;
		}
		Log.d("DatabaseHandler", "Insert success: "+log);
		return true;
	}
	
	public Cursor selectAll() {
		return database.query(DB_TABLE, new String[] { DB_ROW_NAME, DB_ROW_NAME },
				null, null, null, null, null);
	}
	
	public Cursor selectRowById(String id) {
		String selectQuery = "select * from "+DB_TABLE+" where "+DB_ROW_NAME+"=?";
		return database.rawQuery(selectQuery, new String[] { id });
	}
	
	public boolean delete(String row_id) {
		// if success returns true, false otherwise
		boolean result = database.delete(DB_TABLE, DB_ROW_NAME+"="+row_id, null)>0;
		if(result) Log.d("DatabaseHandler", "Delete success, id: "+row_id);
		else Log.d("DatabaseHandler", "Delete failed, id: "+row_id+". Maybe field is not found");
		return result;
	}
	
	
	public static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String query = "create table "+DB_TABLE+"("+
					DB_ROW_NAME+" text primary key)";
			Log.d("DatabaseHandler", query);
			db.execSQL(query);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w("DatabaseHandler", "Updating database from version "+oldVersion+" to version "+newVersion+
					"it will destroy all saved data");
			db.execSQL("drop table if exists "+DB_TABLE);
			onCreate(db);
		}
	}
}
