package com.jeremyhaberman.swingset;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class AddPlayground extends Activity implements OnClickListener {
	
	private EditText name;
	private EditText description;
	private EditText latitude;
	private EditText longitude;
	private View addButton;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addplayground);
        
        name = (EditText) findViewById(R.id.name);
        description = (EditText) findViewById(R.id.description);
        latitude = (EditText) findViewById(R.id.latitude);
        longitude = (EditText) findViewById(R.id.longitude);
        
        addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		
		String nameStr = name.getText().toString().trim();
		String descriptionStr = description.getText().toString().trim();
		int latitudeInt = Integer.parseInt(latitude.getText().toString().trim());
		int longitudeInt = Integer.parseInt(longitude.getText().toString().trim());
		
		PlaygroundDAO playgroundDao = new SQLitePlaygroundDAO(this);
		playgroundDao.createPlayground(nameStr, descriptionStr, latitudeInt, longitudeInt);
		
		finish();
	}
}
