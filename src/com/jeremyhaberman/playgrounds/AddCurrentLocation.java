/*
 * Copyright 2011 Jeremy Haberman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.jeremyhaberman.playgrounds;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class AddCurrentLocation extends AddPlayground implements OnClickListener, Runnable {

	private EditText nameText;
	private EditText descriptionText;
	private View addButton;
	private Location myLocation;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addcurrentlocation);

		nameText = (EditText) findViewById(R.id.name);
		descriptionText = (EditText) findViewById(R.id.description);

		LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String provider = manager.getBestProvider(criteria, true);
		myLocation = manager.getLastKnownLocation(provider);

		addButton = findViewById(R.id.add_button);
		addButton.setOnClickListener(this);
	}

	/**
	 * Background thread to add the address
	 */
	@Override
	public void run() {
		Looper.prepare();
		addCurrentLocation();
	}

	/**
	 * Adds the playground. This is a long-running task that should only be
	 * called in a background thread
	 */
	private void addCurrentLocation() {
		String name = nameText.getText().toString().trim();
		String description = descriptionText.getText().toString().trim();
		int latitude = (int) (myLocation.getLatitude() * 1E6);
		int longitude = (int) (myLocation.getLongitude() * 1E6);

		PlaygroundDAO playgroundDAO = new WebPlaygroundDAO(getParent());
		int result = playgroundDAO.createPlayground(name, description,
				latitude, longitude);
		handler.sendEmptyMessage(result);
	}
}
