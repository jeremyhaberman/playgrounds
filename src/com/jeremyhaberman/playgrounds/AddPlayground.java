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
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * AddPlayground is an abstract class that contains methods common to the
 * AddByAddress and AddCurrentLocation Activities
 * 
 * @author jeremyhaberman
 * 
 */
public abstract class AddPlayground extends Activity implements OnClickListener, Runnable {

	private ProgressDialog progressDialog;

	/**
	 * Displays the progress dialog and starts the background thread to add the
	 * playground
	 */
	@Override
	public void onClick(View v) {
		progressDialog = ProgressDialog.show(this, "",
				getString(R.string.adding_playground), true);
		progressDialog.show();
		Thread thread = new Thread(this);
		thread.start();
	}

	/**
	 * Handler to show a dialog with the result of the request to add the
	 * playground from the background thread
	 */
	protected Handler handler = new Handler() {
		public void handleMessage(Message message) {
			progressDialog.dismiss();

			int result;
			if (message.what == 0) {
				result = Constants.SUCCESS;
			} else {
				result = Constants.FAILURE;
			}
			showResult(result);
		}
	};

	/**
	 * If the result of adding the playground was successful, go back to the
	 * map. This is called from a background task.
	 */
	final Runnable goHome = new Runnable() {
		public void run() {
			goBackToMap();
		}
	};

	/**
	 * Go back to the map
	 */
	protected void goBackToMap() {
		Intent goHomeIntent = new Intent(this, Playgrounds.class);
		goHomeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(goHomeIntent);
	}

	/**
	 * Shows a dialog with the result of the add playground request
	 * 
	 * @param result
	 */
	protected void showResult(int result) {
		AlertDialog dialog = getAlertDialog(result);
		dialog.show();
	}

	/**
	 * Creates an AlertDialog specific to the result
	 * 
	 * @param result
	 * @return
	 */
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

	/**
	 * Dialog after a failed request to add a playground
	 * 
	 * @return
	 */
	protected AlertDialog getFailureDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage(getString(R.string.playground_add_failed));
		builder.setCancelable(false);
		builder.setPositiveButton(getString(R.string.try_again),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// leave the user with the same Activity
					}
				});
		builder.setNegativeButton(R.string.back_to_map, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});

		return builder.create();
	}

	/**
	 * Dialog after a successful request to add a playground
	 * 
	 * @return
	 */
	protected AlertDialog getSuccessDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage(getString(R.string.playground_added));
		builder.setCancelable(false);
		builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});

		return builder.create();
	}
}
