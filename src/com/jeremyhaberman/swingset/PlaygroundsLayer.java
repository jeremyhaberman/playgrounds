package com.jeremyhaberman.swingset;

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
			new AlertDialog.Builder(Swingset.context);
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
