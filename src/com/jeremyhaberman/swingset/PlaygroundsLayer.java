package com.jeremyhaberman.swingset;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class PlaygroundsLayer extends ItemizedOverlay {
	
	private ArrayList<OverlayItem> overlays = new ArrayList<OverlayItem>();

	public PlaygroundsLayer(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		populate();
	}
	
	public void addOverlayItem(OverlayItem overlay) {
		overlays.add(overlay);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return overlays.get(i);
	}

	@Override
	public int size() {
		return overlays.size();
	}

	@Override
	protected boolean onTap(int index) {
		AlertDialog.Builder dialog = 
			new AlertDialog.Builder(Swingset.context);
		dialog.setTitle(overlays.get(index).getTitle());
		dialog.setMessage(overlays.get(index).getSnippet());
		dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
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
