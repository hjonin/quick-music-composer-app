package com.example.quickmusiccomposer;

import java.util.EnumMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final int MAX_TRACKS = 3; // Maximum of tracks the user can add one after the other
	private PlayAsyncTask playAsyncTask;
	private View selectedTrack;
	private Map<Instrument, String[]> tracksMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Initialize tracks map
		// Map which key is an instrument
		// And value is a track
		tracksMap = new EnumMap<Instrument, String[]>(Instrument.class);
		for (Instrument i: Instrument.values()) {
			String[] tracks = new String[MAX_TRACKS];
			for (int j = 0; j < MAX_TRACKS; j++) {
				tracks[j] = "";
			}
			tracksMap.put(i, tracks);
		}

		// Initialize and set listeners on tracks
		Button guitarTrack1 = (Button) findViewById(R.id.guitarButton1);
		Button bassTrack1 = (Button) findViewById(R.id.bassButton1);
		Button guitarTrack2 = (Button) findViewById(R.id.guitarButton2);
		Button bassTrack2 = (Button) findViewById(R.id.bassButton2);
		Button guitarTrack3 = (Button) findViewById(R.id.guitarButton3);
		Button bassTrack3 = (Button) findViewById(R.id.bassButton3);
		OnClickListener guitarTrackListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				onTrackClick(v, Instrument.GUITAR);
			}
		};
		OnClickListener bassTrackListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				onTrackClick(v, Instrument.BASS);
			}
		};
		guitarTrack1.setOnClickListener(guitarTrackListener);
		bassTrack1.setOnClickListener(bassTrackListener);
		guitarTrack2.setOnClickListener(guitarTrackListener);
		bassTrack2.setOnClickListener(bassTrackListener);
		guitarTrack3.setOnClickListener(guitarTrackListener);
		bassTrack3.setOnClickListener(bassTrackListener);

		// Set listener to play music
		Button playButton = (Button) findViewById(R.id.playButton);
		playButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// If second click, stop playing
				if ((playAsyncTask != null) && (playAsyncTask.getStatus() == AsyncTask.Status.RUNNING)) {
					;
				} else {
					// If first click, play music
					playAsyncTask = new PlayAsyncTask();
					byte[] music = MusicComposer.composeMusic(
							MainActivity.this, 
							tracksMap.get(Instrument.GUITAR),
							tracksMap.get(Instrument.BASS), 
							MAX_TRACKS);
					if (music != null) {
						playAsyncTask.execute(music);
					}
				}
			}
		});

		// Set listener to save music
		Button saveButton = (Button) findViewById(R.id.saveButton);
		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (MusicComposer.save(MainActivity.this, tracksMap.get(Instrument.GUITAR), tracksMap.get(Instrument.BASS), MAX_TRACKS)) {
					Toast.makeText(MainActivity.this, "Music saved", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(MainActivity.this, "Failed to save music", Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			int trackNumber = Integer.parseInt((String) selectedTrack.getTag());
			String filename = data.getStringExtra("Filename");
			// Check which request we're responding to
			switch (requestCode) {
			case 0: // Guitar
				tracksMap.get(Instrument.GUITAR)[trackNumber] = filename;
				selectedTrack.setBackgroundResource(R.drawable.button_selected_guitar);
				break;
			case 1: // Bass
				tracksMap.get(Instrument.BASS)[trackNumber] = filename;
				selectedTrack.setBackgroundResource(R.drawable.button_selected_bass);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * On track click event
	 * 
	 * @param v The track that triggered the event
	 * @param instrument The corresponding instrument
	 */
	private void onTrackClick(View v, Instrument instrument) {
		int trackNumber = Integer.parseInt((String) v.getTag());
		// If second click, remove music
		if (!tracksMap.get(instrument)[trackNumber].equals("")) {
			tracksMap.get(instrument)[trackNumber] = "";
			v.setBackgroundResource(R.drawable.button_custom);
		} else {
			// If first click, go to music selection
			Intent musicSelectionActivity = new Intent(
					MainActivity.this,
					MusicSelectionActivity.class);
			musicSelectionActivity.putExtra("Instrument", instrument.toString());
			// Request code can't be an instrument (should be int)
			// Give enum ordinal instead
			startActivityForResult(musicSelectionActivity, instrument.ordinal());
			selectedTrack = v;
		}
	}

	private class PlayAsyncTask extends AsyncTask<byte[], Void, Void> {
		AudioTrack audioTrack;

		@Override
		protected Void doInBackground(byte[]... params) {
			audioTrack.play();
			audioTrack.write(params[0], 0, params[0].length);
			return null;
		}

		@Override
		protected void onPreExecute() {
			audioTrack = new AudioTrack(
					AudioManager.STREAM_MUSIC, 44100,
					AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
					AudioTrack.getMinBufferSize(
							44100,
							AudioFormat.CHANNEL_OUT_MONO,
							AudioFormat.ENCODING_PCM_16BIT), 
					AudioTrack.MODE_STREAM);
		}

	 }

}
