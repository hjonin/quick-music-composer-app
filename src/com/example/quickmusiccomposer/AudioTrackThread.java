package com.example.quickmusiccomposer;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class AudioTrackThread extends Thread {
	private AudioTrack audioTrack;
	private Context context;
	private String[] guitarsArray;
	private String[] bassesArray;
	private int numberOfTracks;

	/**
	 * Constructor
	 * 
	 * @param context
	 * @param filename The name of a file (must be in assets folder)
	 */
	public AudioTrackThread(Context context, String[] guitarsArray,
			String[] bassesArray, int numberOfTracks) {
		audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
				AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
				AudioTrack.getMinBufferSize(44100,
						AudioFormat.CHANNEL_OUT_MONO,
						AudioFormat.ENCODING_PCM_16BIT), AudioTrack.MODE_STREAM);
		this.context = context;
		this.guitarsArray = guitarsArray;
		this.bassesArray = bassesArray;
		this.numberOfTracks = numberOfTracks;
	}

	@Override
	public void run() {
		audioTrack.play();
		byte[] music = MusicComposer.composeMusic(context, guitarsArray,
				bassesArray, numberOfTracks);
		audioTrack.write(music, 0, music.length);
	}

}
