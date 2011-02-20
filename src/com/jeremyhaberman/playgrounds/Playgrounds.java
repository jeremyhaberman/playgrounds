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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.ListPreference;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Playgrounds is the entry and primary Activity. It displays the map, the
 * user's current location and all playgrounds
 * 
 * @author jeremyhaberman
 * 
 */
public class Playgrounds extends MapActivity {

	private static Context context;
	private MapView map;
	private MapController controller;
	private MyLocationOverlay overlay;
	protected GeoPoint myLocation = null;
	private List<Overlay> mapOverlays;
	private PlaygroundsLayer playgroundsLayer;
	private ProgressDialog progressDialog;
	private AlertDialog alertDialog;
	private static final String LOADING_NEARBY_PLAYGROUNDS = "Loading nearby playgrounds";
	protected static final int ERROR_LOADING_PLAYGROUNDS = 1;
	protected static final CharSequence ERROR_LOADING_PLAYGROUNDS_STRING = "Error loading playgrounds.";
	protected static final int ERROR_FINDING_LOCATION = 0;
	private DownloadPlaygroundsTask downloadPlaygroundsTask;

	/**
	 * Instantiate the playgroundDAO for retrieving playground data and display
	 * the map. The current location and playground data is loaded by
	 * onResume().
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		initMapView();
	}

	/**
	 * Display the map, allowing zoom controls
	 */
	private void initMapView() {
		map = (MapView) findViewById(R.id.map);
		controller = map.getController();
		map.setBuiltInZoomControls(true);
	}

	/**
	 * During onResume(), show the current location and nearby playgrounds
	 */
	@Override
	protected void onResume() {
		super.onResume();
		showMyLocationAndPlaygrounds();
	}
	
	

	/**
	 * Show the current location on the map, zoom in to level 13 and then
	 * display nearby playgrounds
	 */
	private void showMyLocationAndPlaygrounds() {
		if (overlay == null) {
			overlay = new MyLocationOverlay(this, map);
		}

		overlay.enableMyLocation();

		// Once the location is known, zoom in to it
		overlay.runOnFirstFix(new Runnable() {
			@Override
			public void run() {
				controller.setZoom(getZoom());
				myLocation = overlay.getMyLocation();
				controller.animateTo(myLocation);
				mHandler.post(showPlaygrounds);
			}
		});

		map.getOverlays().add(overlay);
	}
	
	/**
	 * Gets the zoom level for the map based on the range preference
	 * 
	 * @return
	 */
	protected int getZoom() {
		int range = getRange();
		switch (range) {
		case 1:	return 14;
		case 2: return 13;
		case 5: return 12;
		case 10: return 11;
		default: return 13;
		}
	}

	/**
	 * Gets the range preference
	 * @return
	 */
	private int getRange() {
		return Integer.parseInt(Settings.getRange(getApplicationContext()));
	}

	final Runnable showPlaygrounds = new Runnable() {
		public void run() {
			showNearbyPlaygrounds();
		}
	};

	/**
	 * Handler for callbacks to the UI thread
	 */
	final Handler mHandler = new Handler() {
		public void handleMessage(Message message) {

			AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());

			switch (message.what) {
			case ERROR_FINDING_LOCATION:
				progressDialog.dismiss();
				builder.setMessage(getString(R.string.unable_to_get_current_location));
				break;
			case ERROR_LOADING_PLAYGROUNDS:
				progressDialog.dismiss();
				builder.setMessage(ERROR_LOADING_PLAYGROUNDS_STRING);
				break;
			}
			progressDialog.dismiss();
		}
	};
	

	/**
	 * Get playground data via background task and add it to the map
	 */
	private void showNearbyPlaygrounds() {
		downloadPlaygroundsTask = new DownloadPlaygroundsTask();
		downloadPlaygroundsTask.execute(myLocation);
	}

	/**
	 * Add playground items to the map. If <code>playgrounds</code> is
	 * <code>null</code>, send a message to the UI handler to display the error
	 * and allow the user to try again.
	 * 
	 * @param playgrounds
	 */
	public void addPlaygroundsToMap(Collection<Playground> playgrounds) {

		if (playgrounds == null) {
			mHandler.sendEmptyMessage(ERROR_LOADING_PLAYGROUNDS);
			return;
		}

		mapOverlays = map.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.pin2);
		playgroundsLayer = new PlaygroundsLayer(this, drawable);

		Collection<PlaygroundItem> playgroundItems = PlaygroundItemCreator.createItems(playgrounds);

		for (PlaygroundItem item : playgroundItems) {
			playgroundsLayer.addOverlayItem(item);
		}

		mapOverlays.add(playgroundsLayer);
		map.postInvalidate();
	}

	@Override
	protected void onPause() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.cancel();
		}
		if (alertDialog != null && alertDialog.isShowing()) {
			alertDialog.cancel();
		}
		removePlaygroundsOnMap();
		overlay.disableMyLocation();
		overlay = null;
		downloadPlaygroundsTask.cancel(true);
		super.onPause();
	}

	/**
	 * Clear the playgrounds from the map
	 */
	private void removePlaygroundsOnMap() {
		mapOverlays = map.getOverlays();
		if (playgroundsLayer != null) {
			mapOverlays.remove(playgroundsLayer);
		}
	}

	/**
	 * Display a "Loading nearby playgrounds..." dialog while the background
	 * task is retrieving playground data
	 */
	private void displayLoadingPlaygroundsProgressDialog() {
		progressDialog = ProgressDialog
				.show(Playgrounds.this, "", LOADING_NEARBY_PLAYGROUNDS, true);
		progressDialog.show();
	}

	/**
	 * Display an error about failing to load playground data. Called by
	 * displayErrorTask
	 */
	protected void displayLocationError() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// builder.setTitle("ERROR");

		builder.setMessage(getString(R.string.unable_to_get_current_location));
		builder.setCancelable(false);
		builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				alertDialog.dismiss();
				mHandler.post(showPlaygrounds);
			}
		});
		builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				alertDialog.dismiss();
			}
		});
		alertDialog = builder.create();
		alertDialog.show();
		mHandler.sendEmptyMessage(0);
	}

	/**
	 * Displays an error if the playground data was not successfully loaded by
	 * the thread in showPlaygrounds().
	 */
	final Runnable displayPlaygroundLoadErrorTask = new Runnable() {
		public void run() {
			displayPlaygroundLoadError();
		}
	};

	/**
	 * Display an error about failing to load playground data. Called by
	 * displayErrorTask
	 */
	protected void displayPlaygroundLoadError() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// builder.setTitle("ERROR");

		builder.setMessage("Error loading playgrounds");
		builder.setCancelable(false);
		builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				alertDialog.dismiss();
				mHandler.post(showPlaygrounds);
			}
		});
		builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				alertDialog.dismiss();
			}
		});
		alertDialog = builder.create();
		alertDialog.show();
		mHandler.sendEmptyMessage(0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.add_current_location_button:
			startActivity(new Intent(this, AddCurrentLocation.class));
			return true;
		case R.id.add_by_address_button:
			startActivity(new Intent(this, AddByAddress.class));
			return true;
		case R.id.settings:
			startActivity(new Intent(this, Settings.class));
			return true;
		case R.id.about:
			startActivity(new Intent(this, About.class));
		}
		return false;
	}

	protected MapView getMap() {
		return map;
	}

	protected Overlay getPlaygroundOverlay() {
		return playgroundsLayer;
	}

	/**
	 * Background task for retrieving playground data from the web
	 * 
	 * @author jeremyhaberman
	 *
	 */
	private class DownloadPlaygroundsTask extends AsyncTask<GeoPoint, Void, Collection<Playground>> {

		private static final String TAG = "DownloadPlaygroundsTask";

		/**
		 * Execute this in the UI thread before starting background task
		 */
		protected void onPreExecute() {
			displayLoadingPlaygroundsProgressDialog();
		}

		/**
		 * Background work to to get playground data from the web
		 */
		@Override
		protected Collection<Playground> doInBackground(GeoPoint... params) {
			GeoPoint location = params[0];
			PlaygroundDAO playgroundDAO = new WebPlaygroundDAO(getApplicationContext());
			return playgroundDAO.getNearby(location, getRange());
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		/**
		 * Execute this back on the UI thread once this task is complete
		 */
		@Override
		protected void onPostExecute(Collection<Playground> playgrounds) {
			if (playgrounds.size() == 0) {
				mHandler.post(displayPlaygroundLoadErrorTask);
			} else {
				addPlaygroundsToMap(playgrounds);
				mHandler.sendEmptyMessage(0);
			}
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	protected static Context getContext() {
		return context;
	}
}