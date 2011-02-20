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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;

import android.content.Context;
import android.util.Log;

public class WebPlaygroundDAO implements PlaygroundDAO {

	private static final String TAG = "WebPlaygroundDAO";

	private Collection<Playground> playgrounds;
	private Context context;
	private int port;
	private String servletPath;
	private String host;
	private String protocol;

	WebPlaygroundDAO(Context context) {
		this.context = context;
		this.protocol = context.getString(R.string.protocol);
		this.host = context.getString(R.string.host);
		this.port = context.getResources().getInteger(R.integer.port);
		this.servletPath = context.getString(R.string.playground_servlet_path);
	}

	@Override
	public int createPlayground(String name, String description, int latitude, int longitude) {

		int result = 0;
		
		HttpClient httpclient = new DefaultHttpClient();

		try {
			URI uri = URIUtils.createURI(protocol, host, port, servletPath, null, null);
			HttpPost httppost = new HttpPost(uri);
			
			// Add data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("name", name));
			nameValuePairs.add(new BasicNameValuePair("description", description));
			nameValuePairs.add(new BasicNameValuePair("latitude", Integer.toString(latitude)));
			nameValuePairs.add(new BasicNameValuePair("longitude", Integer.toString(longitude)));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			StatusLine status = response.getStatusLine();
			int statusCode = status.getStatusCode();

			if (statusCode != 200) {
				result = statusCode;
			}

		} catch (Exception e) {
			result = -1;
		}

		return result;
	}

	/**
	 * NOT IMPLEMENTED
	 * 
	 * Get all playgrounds from the web
	 */
	@Override
	public Collection<Playground> getAll(Context context) {
		return null;
	}

	/**
	 * Get playgrounds within a given range
	 * 
	 * @param location current location
	 * @param range range in miles
	 */
	@Override
	public Collection<Playground> getNearby(GeoPoint location, int range) {
		playgrounds = new ArrayList<Playground>();

		URI nearbyUrl = getNearbyURL(location.getLatitudeE6() / 1E6, location.getLongitudeE6() / 1E6, range);
		String response = getResponse(nearbyUrl);
		convertJsonToPlaygrounds(response, playgrounds);

		return playgrounds;
	}

	/**
	 * Execute a URI and return the response body as a string
	 * 
	 * @param uri
	 * @return body of response
	 */
	private String getResponse(URI uri) {
		StringBuffer responseBuffer = new StringBuffer();
		
		try {
			HttpClient httpClient = new DefaultHttpClient();

			HttpGet httpget = new HttpGet(uri);
			HttpResponse response = httpClient.execute(httpget);
			HttpEntity entity = response.getEntity();

			responseBuffer = new StringBuffer();
			BufferedReader input = new BufferedReader(new InputStreamReader(entity.getContent()));
			String strLine = null;
			while ((strLine = input.readLine()) != null) {
				responseBuffer.append(strLine);
			}
		} catch (Exception e) {
			Log.e(TAG, context.getString(R.string.http_error), e);
		}
		
		return responseBuffer.toString();
	}

	protected void setPlaygrounds(Collection<Playground> playgrounds) {
		this.playgrounds = playgrounds;
	}

	/**
	 * Converts a playground in JSON to a Playground 
	 * 
	 * @param jsonPlayground
	 * @return
	 * @throws JSONException
	 */
	private Playground toPlayground(JSONObject jsonPlayground) throws JSONException {
		String name = jsonPlayground.getString("name");
		String description = jsonPlayground.getString("description");
		int latitude = jsonPlayground.getInt("latitude");
		int longitude = jsonPlayground.getInt("longitude");

		return new Playground(name, description, latitude, longitude);
	}

	/**
	 * Converts JSON representing multiple playgrounds to 
	 * @param jsonPlaygrounds
	 * @param playgrounds
	 */
	private void convertJsonToPlaygrounds(String jsonPlaygrounds, Collection<Playground> playgrounds) {

		try {
			JSONArray jsonPlaygroundsArray = new JSONArray(jsonPlaygrounds);
			int numOfPlaygrounds = jsonPlaygroundsArray.length();

			JSONObject jsonPlayground = null;

			for (int i = 0; i < numOfPlaygrounds; i++) {
				jsonPlayground = jsonPlaygroundsArray.getJSONObject(i);
				playgrounds.add(toPlayground(jsonPlayground));
			}
		} catch (JSONException e) {
			Log.e(TAG, context.getString(R.string.exception_converting_json), e);
		}
	}

	private URI getNearbyURL(double latitude, double longitude, int range) {
		
		URI uri = null;
		
		try {
			List<NameValuePair> qparams = new ArrayList<NameValuePair>();
			
			qparams.add(new BasicNameValuePair(context.getString(R.string.latitude_param_name),
					Double.toString(latitude)));
			qparams.add(new BasicNameValuePair(context.getString(R.string.longitude_param_name),
					Double.toString(longitude)));
			qparams.add(new BasicNameValuePair(context.getString(R.string.range_param_name), Integer
					.toString(range)));
			
			uri = URIUtils.createURI(protocol, host, port, servletPath, URLEncodedUtils.format(qparams, "UTF-8"), null);
			

		} catch (Exception e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		}
		
		return uri;
	}
	
	@Override
	public boolean deletePlayground(Context context, int id) {
		return false;
	}
}
