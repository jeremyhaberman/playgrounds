package com.jeremyhaberman.swingset;

import android.app.Activity;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class AddPlayground extends Activity implements OnClickListener {
	private static final String TAG = "AddPlayground";
	private View addCurrentLocationButton;
	private View addByAddressButton;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addplayground);
        
        addCurrentLocationButton = findViewById(R.id.add_current_location_button);
        addCurrentLocationButton.setOnClickListener(this);
        
//        addByAddressButton = findViewById(R.id.add_by_address_button);
//        addByAddressButton.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_current_location_button:
			Intent i = new Intent(this, AddCurrentLocation.class);
			startActivity(i);
			break;
//		case R.id.add_by_address_button:
//			Intent i = new Intent(this, AddByAddress.class);
//			startActivity(i);
//			break;
		}
	}
}
