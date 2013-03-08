package com.example.quickmusiccomposer;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class AudioTrackThread extends Thread {
	private AudioTrack audioTrack;
	private Context context;
	private String filename;

	/**
	 * Constructor
	 * @param context
	 * @param filename (must be in assets)
	 */
	public AudioTrackThread(Context context, String filename) {
		audioTrack = new AudioTrack(
				AudioManager.STREAM_MUSIC, 
				44100, 
				AudioFormat.CHANNEL_OUT_MONO, 
				AudioFormat.ENCODING_PCM_16BIT, 
				AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT), 
				AudioTrack.MODE_STREAM);
		this.context = context;
		this.filename = filename;
	}

	@Override
	public void run() {
		audioTrack.play();
		int i = 0;
		InputStream inputStream = null;
		byte[] buffer = new byte[AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT)];
		try {
			inputStream = context.getAssets().open(filename);
			while ((i = inputStream.read(buffer)) != -1) {
				audioTrack.write(buffer, 0, i);
			}
			inputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
