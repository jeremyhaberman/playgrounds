package com.jeremyhaberman.swingset;

import java.util.Collection;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		// playgroundDAO = new SQLitePlaygroundDAO(this);
		playgroundDAO = new WebPlaygroundDAO(this);
		setContentView(R.layout.main);
		// initMapView();
		// initMyLocation();
		// showPlaygounds();
	}

	@Override
	protected void onResume() {
		paused = false;
		super.onResume();
		initMapView();
		initMyLocation();
		showPlaygounds();
		
	}

	@Override
	protected void onPause() {
		paused = true;
		hidePlaygrounds();
		overlay.disableMyLocation();
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

	private void showPlaygounds() {
		
		progressDialog = ProgressDialog.show(Swingset.this, "", "Loading playgrounds...", true);
		progressDialog.show();
		
		mapOverlays = map.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.pin);
		// if (playgroundsLayer == null) {
		playgroundsLayer = new PlaygroundsLayer(drawable);

		Thread getPlaygroundsThread = new Thread(new Runnable() {
			public void run() {
				addPlaygroundsToLayer();
				handler.sendEmptyMessage(0);
			}
		});
		getPlaygroundsThread.start();
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(Message message) {
			progressDialog.dismiss();
		}
	};
	
	boolean paused = false;

	private void addPlaygroundsToLayer() {

		if (!paused) {
			Collection<Playground> allPlaygrounds = playgroundDAO.getAll();

			if (allPlaygrounds != null) {
				Collection<PlaygroundItem> playgroundItems = PlaygroundItemCreator
						.createItems(allPlaygrounds);

				for (PlaygroundItem item : playgroundItems) {
					playgroundsLayer.addOverlayItem(item);
				}
				mapOverlays.add(playgroundsLayer);
			}
		}
	}

	private void hidePlaygrounds() {
		mapOverlays = map.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.icon);
		if (playgroundsLayer != null) {
			mapOverlays.remove(playgroundsLayer);
		}
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