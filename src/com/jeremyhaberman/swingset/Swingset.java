package com.jeremyhaberman.swingset;

import java.io.IOException;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

public class Swingset extends MapActivity {
	
	List<Overlay> mapOverlays;
	PlaygroundsLayer playgoundsLayer;
	public static Context context;
	private MapView map;
	private MapController controller;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.main);
        initMapView();
        initMyLocation();
        showPlaygoundsLayer();
    }
    
    

	private void showPlaygoundsLayer() {
		mapOverlays = map.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.icon);
		playgoundsLayer = new PlaygroundsLayer(drawable);
		
		List<Address> addresses;
		String myAddress = "201 West 49th Street, Minneapolis, MN";
		
		int geolat = 0;
		int geolon = 0;
		
		Geocoder geocoder = new Geocoder(this);
		try {
			addresses = geocoder.getFromLocationName(myAddress, 1);
			if(addresses != null & addresses.size() > 0) {
				Address x = addresses.get(0);
				
				geolat = (int) (x.getLatitude()*1E6);
				geolon = (int) (x.getLongitude()*1E6);
			} else {
				String bug = "this is it";
			}
		} catch (IOException e) {
			// TODO handle error
		}
		
//		GeoPoint point = new GeoPoint(geolat, geolon);
		
		// my house
		int myHouseLat = 44903865;
		int myHouseLon = -93256703;
		GeoPoint myHouse = new GeoPoint(myHouseLat, myHouseLon);
		
		OverlayItem myHouseItem = new OverlayItem(myHouse, "My Home", "This is my house");
		playgoundsLayer.addOverlayItem(myHouseItem);
		
		// Washburn
		int washburnLat = 44914174;
		int washburnLon = -93281705;
		GeoPoint washburn = new GeoPoint(washburnLat, washburnLon);
		
		OverlayItem washburnItem = new OverlayItem(washburn, "Washburn", "This is Jenny's ghetto school");
		playgoundsLayer.addOverlayItem(washburnItem);
		
		
		mapOverlays.add(playgoundsLayer);
	}



	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initMapView();
		initMyLocation();
		showPlaygoundsLayer();
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
		final MyLocationOverlay overlay = new MyLocationOverlay(this, map);
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