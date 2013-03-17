package com.example.quickmusiccomposer;

import android.media.MediaPlayer;

/**
 * Provide a single instance of MediaPlayer
 * 
 */
public class MyPlayer {
	private static MediaPlayer mediaPlayer = new MediaPlayer();

	private MyPlayer() {

	}

	/**
	 * Get instance of MediaPlayer
	 * 
	 * @return MediaPlayer
	 */
	public static MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}

}
