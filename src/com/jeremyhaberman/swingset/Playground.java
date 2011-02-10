package com.jeremyhaberman.swingset;

public class Playground {

	Playground(String name, String description, int latitude, int longitude) {
		setName(name);
		setDescription(description);
		setLatitude(latitude);
		setLongitude(longitude);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getLatitude() {
		return latitude;
	}

	public void setLatitude(int latitude) {
		this.latitude = latitude;
	}

	public int getLongitude() {
		return longitude;
	}

	public void setLongitude(int longitude) {
		this.longitude = longitude;
	}

	private String name;
	private String description;
	private int latitude;
	private int longitude;
}
