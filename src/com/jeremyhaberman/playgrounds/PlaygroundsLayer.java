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

import java.util.ArrayList;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import com.google.android.maps.ItemizedOverlay;

public class PlaygroundsLayer extends ItemizedOverlay<PlaygroundItem> {
	
	private ArrayList<PlaygroundItem> playgrounds = new ArrayList<PlaygroundItem>();

	public PlaygroundsLayer(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		populate();
	}
	
	public void addOverlayItem(PlaygroundItem playground) {
		playgrounds.add(playground);
		populate();
	}

	@Override
	protected PlaygroundItem createItem(int i) {
		return playgrounds.get(i);
	}

	@Override
	public int size() {
		return playgrounds.size();
	}

	@Override
	protected boolean onTap(int index) {
		AlertDialog.Builder dialog = 
			new AlertDialog.Builder(Playgrounds.context);
		dialog.setTitle(playgrounds.get(index).getTitle());
		dialog.setMessage(playgrounds.get(index).getSnippet());
		dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		dialog.show();
		return super.onTap(index);
	}


}
