package com.example.quickmusiccomposer;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MusicSelectionActivity extends Activity implements MediaPlayer.OnPreparedListener {
	private int lastPlayedPosition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_selection);
		
		final String[] guitarsArray = new String[] {
				"guitar_1.wav",
				"guitar_2.wav",
				"guitar_3.wav"
		};
		final String[] bassesArray = new String[] {
				"bass_1.wav",
				"bass_2.wav",
				"bass_3.wav"
		};
		
		String[] musicsArray = null;
		String instrument = getIntent().getExtras().getString("Instrument");
		// Switch instrument
		if (instrument.equals("Guitar")) {
			musicsArray = guitarsArray;
		} else if (instrument.equals("Bass")) {
			musicsArray = bassesArray;
		}
		
		ListView musicsList = (ListView) findViewById(R.id.musicsList);
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				this, 
				android.R.layout.simple_list_item_1,
				android.R.id.text1,
				musicsArray);
		musicsList.setAdapter(adapter);
		
		lastPlayedPosition = -1;
		MyPlayer.getMediaPlayer().setAudioStreamType(AudioManager.STREAM_MUSIC);
		MyPlayer.getMediaPlayer().setOnPreparedListener(MusicSelectionActivity.this);
		musicsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
				// If double click, stop player
				if (MyPlayer.getMediaPlayer().isPlaying() && (position == lastPlayedPosition)) {
					stop();
				} else {
					if (MyPlayer.getMediaPlayer().isPlaying()) {
						stop();
					}
					String filename = adapter.getItem(position);
					play(filename);
				}
				lastPlayedPosition = position;
			}
			
		});
		
		musicsList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position,long id) {
				String filename = adapter.getItem(position);
				Intent mainActivity = new Intent(MusicSelectionActivity.this, MainActivity.class);
				mainActivity.putExtra("Filename", filename);
				setResult(RESULT_OK, mainActivity);
				MusicSelectionActivity.this.finish();
				return true;
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.music_selection, menu);
		return true;
	}

	@Override
	/** Called when MediaPlayer is ready */
	public void onPrepared(MediaPlayer player) {
		player.start();
	}

	@Override
	/** Called when Activity is destroyed */
	protected void onDestroy() {
		super.onDestroy();
		if (MyPlayer.getMediaPlayer().isPlaying()) {
			stop();
		}
	}

	/**
	 * Stop and reset player
	 */
	private void stop() {
		MyPlayer.getMediaPlayer().stop();
		MyPlayer.getMediaPlayer().reset();
	}

	/**
	 * Play file
	 * @param filename (must be in assets)
	 */
	private void play(String filename) {
		AssetFileDescriptor afd = null;
		try {
			afd = getAssets().openFd(filename);
			MyPlayer.getMediaPlayer().setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
			MyPlayer.getMediaPlayer().prepareAsync();
			afd.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
