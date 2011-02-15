package com.jeremyhaberman.playgrounds;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.google.android.maps.GeoPoint;

public class PlaygroundItemCreator {

	public static PlaygroundItem createItem(Playground playground)
	{
		GeoPoint point = new GeoPoint(playground.getLatitude(), playground.getLongitude());
		return new PlaygroundItem(point, playground.getName(), playground.getDescription());
	}
	
	public static Collection<PlaygroundItem> createItems(Collection<Playground> playgrounds)
	{
		Collection<PlaygroundItem> playgroundItems = new ArrayList<PlaygroundItem>();
		
		Iterator<Playground> playgroundIter = playgrounds.iterator();
		while(playgroundIter.hasNext())
		{
			playgroundItems.add(createItem(playgroundIter.next()));
			
		}
		
		return playgroundItems;
	}
}
