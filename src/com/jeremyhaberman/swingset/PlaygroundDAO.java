package com.jeremyhaberman.swingset;

import java.util.Collection;

import android.content.Context;

public interface PlaygroundDAO {
	
	public int createPlayground(String name, String description, int latitude, int longitude);
	
	public boolean deletePlayground(Context context, int id);
	
	public Collection<Playground> getAll(Context context);

}
