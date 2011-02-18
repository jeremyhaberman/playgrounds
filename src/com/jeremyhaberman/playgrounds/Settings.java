package com.jeremyhaberman.playgrounds;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Settings extends PreferenceActivity {

	// Option name and default value
	public static final String RANGE = "range";
	public static final String RANGE_DEFAULT = "5";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}

	/** Get the current value of the range option */
	public static String getRange(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getString(RANGE, RANGE_DEFAULT);
	}
}
