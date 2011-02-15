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
import android.location.Location;

public interface PlaygroundDAO {
	
	public int createPlayground(String name, String description, int latitude, int longitude);
	
	public boolean deletePlayground(Context context, int id);
	
	public Collection<Playground> getAll(Context context);

	public Collection<Playground> getNearby(Context context,
			GeoPoint myLocation, int maxQuantity);

	public Collection<? extends Playground> getWithin(Context context,
			GeoPoint topLeft, GeoPoint bottomRight, int maxQuantity);
	

}
