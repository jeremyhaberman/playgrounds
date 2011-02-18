package com.jeremyhaberman.playgrounds;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import com.google.android.maps.GeoPoint;

/**
 * Utility class for location-related tasks
 * 
 * @author jeremyhaberman
 * 
 */
public class GeoUtil {

	/**
	 * the maximum number of results to expect from the Maps API search by
	 * address
	 */
	private static final int MAX_ADDRESS_RESULTS = 1;

	/**
	 * Used for logging
	 */
	private static final String TAG = "GeoUtil";

	/**
	 * Converts a <code>Location</code> to a <code>GeoPoint</code>. Returns
	 * <code>null</code> of a <code>null</code> location was supplied.
	 * 
	 * @param location
	 * @return
	 */
	public static GeoPoint toGeoPoint(Location location) {
		GeoPoint point = null;

		if (location != null) {
			int latitude = (int) (location.getLatitude() * 1E6);
			int longitude = (int) (location.getLongitude() * 1E6);
			point = new GeoPoint(latitude, longitude);
		}

		return point;
	}

	/**
	 * Convert the string representation of an address to an
	 * <code>Address</code>. May return null if null values are provided.
	 * 
	 * @param context
	 * @param addr
	 *            the address
	 * @return
	 */
	public static Address toAddress(Context context, String addr) {

		Geocoder geocoder = null;
		Address address = null;
		try {
			geocoder = new Geocoder(context);
			List<Address> addresses = geocoder.getFromLocationName(addr, MAX_ADDRESS_RESULTS);
			if (addresses.size() > 0) {
				address = addresses.get(0);
			}
		} catch (IOException e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		}

		return address;
	}
}
