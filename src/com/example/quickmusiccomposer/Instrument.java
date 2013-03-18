package com.example.quickmusiccomposer;

public enum Instrument {
	GUITAR("Guitar"), 
	BASS("Bass");

	private String name;

	Instrument(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

}
