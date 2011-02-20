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

import java.util.Collection;

import com.google.android.maps.GeoPoint;

import android.content.Context;

/**
 * Data Access Object for <code>Playground</code>s
 * 
 * @author jeremy
 * 
 */
public interface PlaygroundDAO {

	/**
	 * Create a playground
	 * 
	 * @param name
	 *            name of the playground
	 * @param description
	 *            playground details
	 * @param latitude
	 *            latitude of the playground as an <code>int</code>
	 * @param longitude
	 *            longitude of the playground as an <code>int</code>
	 * @return
	 */
	public int createPlayground(String name, String description, int latitude, int longitude);

	/**
	 * Delete a playground
	 * 
	 * @param context
	 * @param id
	 * @return
	 */
	public boolean deletePlayground(Context context, int id);

	/**
	 * Get all playgrounds
	 * 
	 * @param context
	 * @return
	 */
	public Collection<Playground> getAll(Context context);

	/**
	 * Get playgrounds within a range of miles
	 * 
	 * @param myLocation
	 * @param range range in miles
	 * @return
	 */
	public Collection<Playground> getNearby(GeoPoint myLocation, int range);
}
