package com.jeremyhaberman.swingset;

import java.util.Collection;

public interface PlaygroundDAO {
	
	public int createPlayground(String name, String description, int latitude, int longitude);
	
	public boolean deletePlayground(int id);
	
	public Collection<Playground> getAll();

}
