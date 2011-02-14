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

import android.app.ProgressDialog;
import android.content.Context;
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

	List<Overlay> mapOverlays;
	PlaygroundsLayer playgroundsLayer;
	public static Context context;
	private MapView map;
	private MapController controller;
	PlaygroundDAO playgroundDAO;
	MyLocationOverlay overlay;
	ProgressDialog progressDialog;
	protected static List<Playground> mPlaygrounds;
	Object initPlaygroundLock = new Object();
	static boolean initializing = true;
	Thread initPlaygroundsThread;

	protected static boolean mNewPlaygrounds = true;

	protected static void setNewPlaygrounds(boolean newPlaygrounds) {
		mNewPlaygrounds = newPlaygrounds;
	}

	final Handler mHandler = new Handler() {
		public void handleMessage(Message message) {
			progressDialog.dismiss();
		}
	};

	final Runnable mUpdateResults = new Runnable() {
		public void run() {
			showPlaygroundsOnMap();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		playgroundDAO = new WebPlaygroundDAO(this);
		setContentView(R.layout.main);
		// initMapView();
		// initMyLocation();
		// initPlaygrounds();
	}

	protected void initPlaygrounds() {
		
		removePlaygroundsOnMap();

		progressDialog = ProgressDialog.show(Swingset.this, "",
				"Loading playgrounds...", true);
		progressDialog.show();

		initPlaygroundsThread = new Thread() {
			public void run() {
				setPlaygrounds(getPlaygrounds());
				mHandler.post(mUpdateResults);
				
			}
		};
		initPlaygroundsThread.start();
	}

	protected static void setPlaygrounds(List<Playground> playgrounds) {
		mPlaygrounds = playgrounds;
	}

	protected List<Playground> getPlaygrounds() {

		return new ArrayList<Playground>(playgroundDAO.getAll());

	}

	protected void showPlaygroundsOnMap() {

		if (mPlaygrounds != null) {
			mapOverlays = map.getOverlays();
			Drawable drawable = this.getResources().getDrawable(R.drawable.pin);
			playgroundsLayer = new PlaygroundsLayer(drawable);

			Collection<PlaygroundItem> playgroundItems = PlaygroundItemCreator
					.createItems(mPlaygrounds);

			for (PlaygroundItem item : playgroundItems) {
				playgroundsLayer.addOverlayItem(item);
			}
			mapOverlays.add(playgroundsLayer);
			initializing = false;
			mNewPlaygrounds = false;
			
			mHandler.sendEmptyMessage(0);
		}
	}

	private void removePlaygroundsOnMap() {
		mapOverlays = map.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.icon);
		if (playgroundsLayer != null) {
			mapOverlays.remove(playgroundsLayer);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		initMapView();
		initMyLocation();
		initPlaygrounds();
	}

	private void refreshPlaygrounds() {
		if (!initializing && mNewPlaygrounds) {
			removePlaygroundsOnMap();
			initPlaygrounds();
		}
	}

	@Override
	protected void onPause() {
		if (initPlaygroundsThread.isAlive()) {
			initPlaygroundsThread.interrupt();
		}
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

	private void initMyLocation() {
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