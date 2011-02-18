package com.jeremyhaberman.playgrounds;

import android.location.Address;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

/**
 * AddByAddress is an Activity that allows adding a playground by providing an
 * address.
 * 
 * @author jeremy
 * 
 */
public class AddByAddress extends AddPlayground implements OnClickListener, Runnable {

	// Names of the input fields on the UI
	private EditText nameText;
	private EditText descriptionText;
	private EditText addressText;

	private View addButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addbyaddress);

		nameText = (EditText) findViewById(R.id.name);
		descriptionText = (EditText) findViewById(R.id.description);
		addressText = (EditText) findViewById(R.id.address);

		addButton = findViewById(R.id.add_button);
		addButton.setOnClickListener(this);
	}

	/**
	 * Background thread to add the address
	 */
	@Override
	public void run() {
		Looper.prepare();
		addAddress();
	}

	/**
	 * Adds the playground. This is a long-running task that should only be
	 * called in a background thread
	 */
	private void addAddress() {
		String name = nameText.getText().toString().trim();
		String description = descriptionText.getText().toString().trim();
		String address = addressText.getText().toString().trim();
		Address addr = GeoUtil.toAddress(getApplicationContext(), address);
		int latitude = (int) (addr.getLatitude() * 1E6);
		int longitude = (int) (addr.getLongitude() * 1E6);

		PlaygroundDAO playgroundDAO = new WebPlaygroundDAO(getParent());
		int result = playgroundDAO.createPlayground(name, description, latitude, longitude);
		handler.sendEmptyMessage(result);
	}

	

}
