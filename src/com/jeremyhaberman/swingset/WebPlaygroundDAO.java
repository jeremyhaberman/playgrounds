package com.jeremyhaberman.swingset;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class WebPlaygroundDAO implements PlaygroundDAO {

	private static final String TAG = "WebPlaygroundDAO";
	private Collection<Playground> playgrounds;
	private Swingset swingset;
	private Context context;

	WebPlaygroundDAO(Swingset swingset) {
		this.swingset = swingset;
	}
	
	WebPlaygroundDAO(Context context) {
		this.context = context;
	}

	@Override
	public int createPlayground(String name, String description, int latitude,
			int longitude) {
		
		int result = 0;
		
		// Create a new HttpClient and Post Header  
	    HttpClient httpclient = new DefaultHttpClient();  
	    HttpPost httppost = new HttpPost("http://swingsetweb.appspot.com/playground");  
	  
	    try {  
	        // Add your data  
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  
	        nameValuePairs.add(new BasicNameValuePair("name", name));  
	        nameValuePairs.add(new BasicNameValuePair("description", description));
	        nameValuePairs.add(new BasicNameValuePair("latitude", Integer.toString(latitude)));
	        nameValuePairs.add(new BasicNameValuePair("longitude", Integer.toString(longitude)));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));  
	  
	        // Execute HTTP Post Request  
	        HttpResponse response = httpclient.execute(httppost); 
	        
	        StatusLine status = response.getStatusLine();
	        int statusCode = status.getStatusCode();
	        
	        if(statusCode != 200) {
	        	result = statusCode;
	        }
	          
	    } catch (ClientProtocolException e) {  
	        result = -1;
	    } catch (IOException e) {  
	        result = -1;  
	    }
	    
	    return result;
	}

	@Override
	public boolean deletePlayground(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<Playground> getAll() {
		playgrounds = new ArrayList<Playground>();
		String result = swingset.getResources().getString(R.string.error);
		HttpURLConnection con = null;
		Log.d(TAG, "getPlaygrounds()");
		
		try {
			// Check if task has been interrupted
			if (Thread.interrupted()) {
				throw new InterruptedException();
			}
			
			// Build query
			URL url = new URL("http://swingsetweb.appspot.com/playground");
			con = (HttpURLConnection) url.openConnection();
			con.setReadTimeout(10000);
			con.setConnectTimeout(15000);
			con.setRequestMethod("GET");
			con.setDoInput(true);
			
			// Start the query
			con.connect();
			
			// Check if task has been interrupted
			if (Thread.interrupted()) {
				throw new InterruptedException();
			}
			
			// Read results from the query
			BufferedReader reader = new BufferedReader(
				new InputStreamReader(con.getInputStream(), "UTF-8"));
			String payload = reader.readLine();
			reader.close();
			
			// Parse to get translated text
			JSONArray jsonPlaygrounds = new JSONArray(payload);
			int numOfPlaygrounds = jsonPlaygrounds.length();
			
			JSONObject jsonPlayground = null;
			
			for (int i = 0; i < numOfPlaygrounds; i++) {
				jsonPlayground = jsonPlaygrounds.getJSONObject(i);
				playgrounds.add(toPlayground(jsonPlayground));
			}
			
		} catch (IOException e) {
			Log.e(TAG, "IOException", e);
		} catch (InterruptedException e) {
			Log.e(TAG, "InterruptedException", e);
			result = swingset.getResources().getString(R.string.interrupted_error);
		} catch (JSONException e) {
			Log.e(TAG, "JSONException", e);
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
		
		// all done
		Log.d(TAG, "   -> returned " + result);
		return playgrounds;
	}

	protected void setPlaygrounds(Collection<Playground> playgrounds) {
		this.playgrounds = playgrounds;
	}
	
	private Playground toPlayground(JSONObject jsonPlayground) throws JSONException {
		String name = jsonPlayground.getString("name");
		String description = jsonPlayground.getString("description");
		int latitude = jsonPlayground.getInt("latitude");
		int longitude = jsonPlayground.getInt("longitude");
		
		return new Playground(name, description, latitude, longitude);
	}
}
