package com.jeremyhaberman.playgrounds;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class PlaygroundItem extends OverlayItem {

	public PlaygroundItem(GeoPoint point, String title, String snippet) {
		super(point, title, snippet);
	}

}
