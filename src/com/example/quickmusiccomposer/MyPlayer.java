package com.example.quickmusiccomposer;

import android.media.MediaPlayer;

/**
 * Provide a single instance of MediaPlayer
 * 
 */
public class MyPlayer {
	private static MediaPlayer mediaPlayer = new MediaPlayer(); // Initialize static MediaPlayer

	private MyPlayer() {
		// Private constructor
		// Because unnecessary
	}

	/**
	 * Get instance of MediaPlayer
	 * 
	 * @return MediaPlayer The instance of MediaPlayer
	 */
	public static MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}

}
