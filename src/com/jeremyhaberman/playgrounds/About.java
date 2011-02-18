package com.jeremyhaberman.playgrounds;

import android.app.Activity;
import android.os.Bundle;

/**
 * About page for Playgrounds app
 * 
 * @author jeremyhaberman
 *
 */
public class About extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
	}

}
