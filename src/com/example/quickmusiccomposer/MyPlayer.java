package com.example.quickmusiccomposer;

import android.media.MediaPlayer;

public class MyPlayer {
	private static MediaPlayer mediaPlayer = new MediaPlayer();
	
	private MyPlayer() {
		
	}

	public static MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}

}
