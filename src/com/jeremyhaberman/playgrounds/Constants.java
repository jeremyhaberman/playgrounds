package com.jeremyhaberman.playgrounds;

import android.provider.BaseColumns;

public class Constants implements BaseColumns{

	public static final String TABLE_NAME = "playgrounds";
	
	// playgrounds table columns
	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
}
