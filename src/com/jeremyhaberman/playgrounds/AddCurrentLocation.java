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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class AddCurrentLocation extends Activity implements OnClickListener, Runnable {

	private EditText nameText;
	private EditText descriptionText;
	private View addButton;
	private ProgressDialog progressDialog;

	private Location myLocation;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addcurrentlocation);

		nameText = (EditText) findViewById(R.id.name);
		descriptionText = (EditText) findViewById(R.id.description);

		LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String provider = manager.getBestProvider(criteria, true);
		myLocation = manager.getLastKnownLocation(provider);

		addButton = findViewById(R.id.add_button);
		addButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		progressDialog = ProgressDialog.show(AddCurrentLocation.this, "",
				getString(R.string.adding_playground), true);
		progressDialog.show();
		Thread thread = new Thread(AddCurrentLocation.this);
		thread.start();
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message message) {
			progressDialog.dismiss();
			
			int result;
			if(message.what == 0) {
				result = Constants.SUCCESS;
			} else {
				result = Constants.FAILURE;
			}
			showResult(result);
			
		}
	};
	
	final Runnable goHome = new Runnable() {
		public void run() {
			goHome();
		}
	};

	@Override
	public void run() {
		Looper.prepare();
		addCurrentLocation();
	}

	protected void goHome() {
		Intent goHomeIntent = new Intent(this, Playgrounds.class);
		goHomeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(goHomeIntent);
	}

	protected void showResult(int result) {	
		AlertDialog dialog = getAlertDialog(result);
		dialog.show();
	}
		
	private AlertDialog getAlertDialog(int result) {
		
		AlertDialog dialog = null;
		
		switch (result) {
		case Constants.SUCCESS:
			dialog = getSuccessDialog();
			break;
		case Constants.FAILURE:
			dialog = getFailureDialog();
			break;
		default:
			break;
		}
		
		return dialog;
	}
	
	private AlertDialog getFailureDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
				
		builder.setMessage(getString(R.string.playground_add_failed));
		builder.setCancelable(false);
		builder.setPositiveButton(getString(R.string.try_again), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// leave the user with the same Activity
			}
		});
		builder.setNegativeButton(R.string.back_to_map, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				AddCurrentLocation.this.finish();
			}
		});	
		
		return builder.create();
	}

	private AlertDialog getSuccessDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setMessage(getString(R.string.playground_added));
		builder.setCancelable(false);
		builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				AddCurrentLocation.this.finish();
			}
		});	
		
		return builder.create();
	}

	private void addCurrentLocation() {
		String name = nameText.getText().toString().trim();
		String description = descriptionText.getText().toString().trim();
		int latitude = (int) (myLocation.getLatitude() * 1E6);
		int longitude = (int) (myLocation.getLongitude() * 1E6);

		PlaygroundDAO playgroundDAO = new WebPlaygroundDAO(getParent());
		int result = playgroundDAO.createPlayground(name, description,
				latitude, longitude);
		handler.sendEmptyMessage(result);
	}
}
