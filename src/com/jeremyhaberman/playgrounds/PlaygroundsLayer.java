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

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;

/**
 * Layer over a map to show playgrounds
 * 
 * @author jeremyhaberman
 *
 */
public class PlaygroundsLayer extends ItemizedOverlay<PlaygroundItem> {

	private ArrayList<PlaygroundItem> playgrounds = new ArrayList<PlaygroundItem>();
	private Context mContext;
	private int mPlaygroundIndex;

	public PlaygroundsLayer(Context context, Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
		populate();
	}

	public void addOverlayItem(PlaygroundItem playground) {
		playgrounds.add(playground);
		populate();
	}

	@Override
	protected PlaygroundItem createItem(int i) {
		return playgrounds.get(i);
	}

	@Override
	public int size() {
		return playgrounds.size();
	}

	@Override
	protected boolean onTap(int index) {
		
		setPlaygroundIndex(index);

		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setTitle(playgrounds.get(index).getTitle());
		dialog.setMessage(playgrounds.get(index).getSnippet());
		dialog.setPositiveButton("Directions", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				LocationManager manager = (LocationManager) mContext.getApplicationContext().getSystemService(Activity.LOCATION_SERVICE);
				Criteria criteria = new Criteria();
				criteria.setAccuracy(Criteria.ACCURACY_FINE);
				String provider = manager.getBestProvider(criteria, true);
				Location lastKnownLocation = manager.getLastKnownLocation(provider);

				GeoPoint currentLocation = GeoUtil.toGeoPoint(lastKnownLocation);
				GeoPoint destination = playgrounds.get(getPlaygroundIndex()).getPoint();

				String mapDirectionsUri = getMapDirectionsUri(currentLocation, destination);

				Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri
						.parse(mapDirectionsUri));
				mContext.startActivity(intent);
			}
		});
		dialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		dialog.show();
		return super.onTap(index);
	}

	private void setPlaygroundIndex(int index) {
		mPlaygroundIndex = index;
	}
	
	private int getPlaygroundIndex() {
		return mPlaygroundIndex;
	}

	protected String getMapDirectionsUri(GeoPoint start, GeoPoint end) {

		StringBuffer uri = new StringBuffer("http://maps.google.com/maps?");

		double startLat = start.getLatitudeE6() / 1E6;
		double startLon = start.getLongitudeE6() / 1E6;

		double endLat = end.getLatitudeE6() / 1e6;
		double endLon = end.getLongitudeE6() / 1e6;

		uri.append("saddr=").append(startLat).append(",").append(startLon);
		uri.append("&daddr=").append(endLat).append(",").append(endLon);

		return uri.toString();
	}
}
