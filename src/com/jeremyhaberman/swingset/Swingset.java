package com.jeremyhaberman.swingset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class Swingset extends MapActivity {

	protected static final int ERROR_LOADING_PLAYGROUNDS = 1;
	protected static final CharSequence ERROR_LOADING_PLAYGROUNDS_STRING = "Error loading playgrounds.";
	List<Overlay> mapOverlays;
	PlaygroundsLayer playgroundsLayer;
	public static Context context;
	private MapView map;
	private MapController controller;
	PlaygroundDAO playgroundDAO;
	MyLocationOverlay overlay;
	ProgressDialog progressDialog;
	protected static List<Playground> mPlaygrounds;
	public static Object initPlaygroundLock = new Object();
	static boolean initializing = true;
	protected static boolean mNewPlaygrounds = true;

	protected static void setNewPlaygrounds(boolean newPlaygrounds) {
		mNewPlaygrounds = newPlaygrounds;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		playgroundDAO = new WebPlaygroundDAO(this);
		setContentView(R.layout.main);
		initMapView();
		showMyLocation();
		showPlaygrounds();
	}

	protected void showPlaygrounds() {
		progressDialog = ProgressDialog.show(Swingset.this, "",
				"Loading playgrounds...", true);
		progressDialog.show();
		Thread getPlaygroundsFromWebThread = new Thread() {
			public void run() {
				synchronized(initPlaygroundLock) {
					setPlaygrounds(getPlaygrounds());
					if (getPlaygrounds().size() == 0) {
						mHandler.post(displayErrorTask);
					} else {
						mHandler.post(updatePlaygroundsOnMap);
				}
				}
			}
		};
		getPlaygroundsFromWebThread.start();
	}

	final Runnable updatePlaygroundsOnMap = new Runnable() {
		public void run() {
			addPlaygroundsToLayer();
			addPlaygroundsLayerToMap();
		}
	};

	final Runnable displayErrorTask = new Runnable() {
		public void run() {
			displayError();
		}
	};

	protected static void setPlaygrounds(List<Playground> playgrounds) {
		mPlaygrounds = playgrounds;
	}

	protected void displayError() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("ERROR");

		builder.setMessage("Error loading playgrounds");
		builder.setCancelable(false);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
		mHandler.sendEmptyMessage(0);
	}

	protected void refreshPlaygrounds() {
		removePlaygroundsOnMap();
		showPlaygrounds();
	}

	protected List<Playground> getPlaygrounds() {
		List<Playground> playgrounds = new ArrayList<Playground>(
				playgroundDAO.getAll(this));
		return playgrounds;
	}

	protected PlaygroundsLayer addPlaygroundsToLayer() {
		mapOverlays = map.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.pin);
		playgroundsLayer = new PlaygroundsLayer(drawable);

		Collection<PlaygroundItem> playgroundItems = PlaygroundItemCreator
				.createItems(mPlaygrounds);

		for (PlaygroundItem item : playgroundItems) {
			playgroundsLayer.addOverlayItem(item);
		}
		return playgroundsLayer;
	}

	protected void addPlaygroundsLayerToMap() {
		mapOverlays.add(playgroundsLayer);
		initializing = false;
		mNewPlaygrounds = false;
		mHandler.sendEmptyMessage(0);
	}

	// Handler for callbacks to the GUI thread
	final Handler mHandler = new Handler() {
		public void handleMessage(Message message) {
			switch (message.what) {
			case ERROR_LOADING_PLAYGROUNDS:
				progressDialog.dismiss();
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getApplicationContext());
				builder.setMessage(ERROR_LOADING_PLAYGROUNDS_STRING);

			}
			progressDialog.dismiss();
		}
	};

	private void removePlaygroundsOnMap() {
		mapOverlays = map.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.icon);
		if (playgroundsLayer != null) {
			mapOverlays.remove(playgroundsLayer);
		}
	}

	@Override
	protected void onResume() {
		synchronized (initPlaygroundLock) {
			super.onResume();
			initMapView();
			showMyLocation();

			Bundle errorBundle = getIntent().getExtras();

			if (errorBundle != null) {
				String error = errorBundle.getString("Exception");
			}
//			refreshPlaygrounds();
		}
	}

	@Override
	protected void onPause() {
		overlay.disableMyLocation();
		// removePlaygroundsOnMap();
		super.onPause();
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
		case R.id.add:
			startActivity(new Intent(this, AddPlayground.class));
			return true;
		}
		return false;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	private void initMapView() {
		map = (MapView) findViewById(R.id.map);
		controller = map.getController();
		map.setBuiltInZoomControls(true);
	}

	private void showMyLocation() {
		if (overlay == null) {
			overlay = new MyLocationOverlay(this, map);
		}
		overlay.enableMyLocation();
		overlay.runOnFirstFix(new Runnable() {
			@Override
			public void run() {
				controller.setZoom(13);
				controller.animateTo(overlay.getMyLocation());

			}
		});
		map.getOverlays().add(overlay);
	}
}