package com.jeremyhaberman.swingset;

import static android.provider.BaseColumns._ID;
import static com.jeremyhaberman.swingset.Constants.DESCRIPTION;
import static com.jeremyhaberman.swingset.Constants.LATITUDE;
import static com.jeremyhaberman.swingset.Constants.LONGITUDE;
import static com.jeremyhaberman.swingset.Constants.NAME;
import static com.jeremyhaberman.swingset.Constants.TABLE_NAME;

import java.util.ArrayList;
import java.util.Collection;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLitePlaygroundDAO extends SQLiteOpenHelper implements
		PlaygroundDAO {

	public SQLitePlaygroundDAO(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sqlCreateStatement = "CREATE TABLE " + Constants.TABLE_NAME
				+ " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME
				+ " TEXT NOT NULL, " + DESCRIPTION + " TEXT, " + LATITUDE
				+ " INTEGER NOT NULL, " + LONGITUDE + " INTEGER NOT NULL)";
		db.execSQL(sqlCreateStatement);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

	@Override
	public int createPlayground(String name, String description, int latitude, int longitude)
	{	
		int result = SUCCESS;

		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		
		values.put(NAME, name);
		values.put(DESCRIPTION, description);
		values.put(LATITUDE, latitude);
		values.put(LONGITUDE, longitude);
		
		try
		{
			db.insertOrThrow(TABLE_NAME, null, values);
			db.close();
		}
		catch (SQLException e)
		{
			result = FAILURE;
		}

		return result;
	}

	@Override
	public boolean deletePlayground(int id) {
		throw new RuntimeException("Not implemented");
	}
	
	private static String[] FROM = { _ID, NAME, DESCRIPTION, LATITUDE, LONGITUDE };
	private static String ORDER_BY = NAME;

	@Override
	public Collection<Playground> getAll()
	{
		Collection<Playground> allPlaygrounds = new ArrayList<Playground>();
		try
		{
			SQLiteDatabase db = getReadableDatabase();
			Cursor cursor = db.query(TABLE_NAME, FROM, null, null, null,
				null, ORDER_BY);
			
			String name;
			String description;
			int latitude;
			int longitude;
			Playground playground;
			
			while(cursor.moveToNext())
			{
				name = null;
				description = null;
				latitude = 0;
				longitude = 0;
				
				name = cursor.getString(NAME_INDEX);
				description = cursor.getString(DESCRIPTION_INDEX);
				latitude = cursor.getInt(LATITUDE_INDEX);
				longitude = cursor.getInt(LONGITUDE_INDEX);
				
				playground = new Playground(name, description, latitude, longitude);
				allPlaygrounds.add(playground);
			}
			
			cursor.close();
			db.close();
		}
		catch (SQLException e)
		{
			// TODO
		}
		
		return allPlaygrounds;
	}
	
	private static final String DATABASE_NAME = "playgrounds.db";
	private static final int DATABASE_VERSION = 1;
	private static final int SUCCESS = 0;
	private static final int FAILURE = 1;
	private static final int NAME_INDEX = 1;
	private static final int DESCRIPTION_INDEX = 2;
	private static final int LATITUDE_INDEX = 3;
	private static final int LONGITUDE_INDEX = 4;
}
