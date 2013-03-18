package com.example.quickmusiccomposer;

import android.media.MediaPlayer;

/**
 * Provide a single instance of MediaPlayer
 * 
 */
public class MyPlayer {
	private static MediaPlayer mediaPlayer = new MediaPlayer(); // Initialize MediaPlayer

	private MyPlayer() {
		// Private constructor because static class
	}

	/**
	 * Get instance of MediaPlayer
	 * 
	 * @return The instance of MediaPlayer
	 */
	public static MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}

}
