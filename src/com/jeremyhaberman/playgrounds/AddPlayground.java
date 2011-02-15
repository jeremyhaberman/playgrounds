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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class AddPlayground extends Activity implements OnClickListener {
	private static final String TAG = "AddPlayground";
	private static final int PLAYGROUND_ADDED = 0;
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
			Intent intent = new Intent(this, AddCurrentLocation.class);
			startActivityForResult(intent, PLAYGROUND_ADDED);
			break;
//		case R.id.add_by_address_button:
//			Intent i = new Intent(this, AddByAddress.class);
//			startActivity(i);
//			break;
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
        if (requestCode == PLAYGROUND_ADDED) {
            startActivity(new Intent(this, Playgrounds.class));
        }
    }
}
