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
import java.util.Collection;
import java.util.Iterator;

import com.google.android.maps.GeoPoint;

/**
 * Utility class for converting <code>Playground</code>s to
 * <code>PlaygroundItem</code>s
 * 
 * @author jeremyhaberman
 * 
 */
public class PlaygroundItemCreator {

	/**
	 * Converts a <code>Playground</code> to a <code>PlaygroundItem</code>
	 * 
	 * @param playground
	 * @return
	 */
	public static PlaygroundItem createItem(Playground playground) {
		GeoPoint point = new GeoPoint(playground.getLatitude(), playground.getLongitude());
		return new PlaygroundItem(point, playground.getName(), playground.getDescription());
	}

	/**
	 * Converts a <code>Collection</code> of <code>Playground</code>s to a
	 * <code>Collection</code> of <code>PlaygroundItem</code>s
	 * 
	 * @param playground
	 * @return
	 */
	public static Collection<PlaygroundItem> createItems(Collection<Playground> playgrounds) {
		Collection<PlaygroundItem> playgroundItems = new ArrayList<PlaygroundItem>();

		Iterator<Playground> playgroundIter = playgrounds.iterator();
		while (playgroundIter.hasNext()) {
			playgroundItems.add(createItem(playgroundIter.next()));

		}

		return playgroundItems;
	}
}
