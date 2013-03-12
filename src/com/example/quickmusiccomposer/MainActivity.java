package com.example.quickmusiccomposer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	private static final int GUITAR_REQ_CODE = 1;
	private static final int BASS_REQ_CODE = 2;
	private static final int MAX_TRACKS = 3; // Maximum of tracks the user can add one after the other
	private View selectedTrack;
	private String[] guitarsArray;
	private String[] bassesArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Initialise musics arrays
		guitarsArray = new String[MAX_TRACKS];
		bassesArray = new String[MAX_TRACKS];
		for (int i = 0; i < MAX_TRACKS; i++) {
			guitarsArray[i] = "";
			bassesArray[i] = "";
		}

		// Create and set listeners on tracks
		Button guitarTrack1 = (Button) findViewById(R.id.guitarButton1);
		Button bassTrack1 = (Button) findViewById(R.id.bassButton1);
		Button guitarTrack2 = (Button) findViewById(R.id.guitarButton2);
		Button bassTrack2 = (Button) findViewById(R.id.bassButton2);
		Button guitarTrack3 = (Button) findViewById(R.id.guitarButton3);
		Button bassTrack3 = (Button) findViewById(R.id.bassButton3);
		OnClickListener guitarTrackListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				int trackNumber = Integer.parseInt((String) v.getTag());
				// If second click, remove music
				if (!guitarsArray[trackNumber].equals("")) {
					guitarsArray[trackNumber] = "";
					v.getBackground().setColorFilter(Color.GRAY,
							PorterDuff.Mode.MULTIPLY);
				} else {
					// Else go to music selection
					onTrackClick("Guitar", GUITAR_REQ_CODE);
					selectedTrack = v;
				}
			}
		};
		OnClickListener bassTrackListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				int trackNumber = Integer.parseInt((String) v.getTag());
				if (!bassesArray[trackNumber].equals("")) {
					bassesArray[trackNumber] = "";
					v.getBackground().setColorFilter(Color.GRAY,
							PorterDuff.Mode.MULTIPLY);
				} else {
					onTrackClick("Bass", BASS_REQ_CODE);
					selectedTrack = v;
				}
			}
		};
		guitarTrack1.setOnClickListener(guitarTrackListener);
		bassTrack1.setOnClickListener(bassTrackListener);
		guitarTrack2.setOnClickListener(guitarTrackListener);
		bassTrack2.setOnClickListener(bassTrackListener);
		guitarTrack3.setOnClickListener(guitarTrackListener);
		bassTrack3.setOnClickListener(bassTrackListener);

		// Set listener for playing music
		Button play = (Button) findViewById(R.id.playButton);
		play.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Thread audioTrackThread = new AudioTrackThread(
						MainActivity.this, guitarsArray, bassesArray,
						MAX_TRACKS);
				audioTrackThread.start();
			}
		});
		
		// Set listener for exporting music
		Button export = (Button) findViewById(R.id.exportButton);
		export.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MusicComposer.export(MainActivity.this, guitarsArray, bassesArray, MAX_TRACKS);
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
			case GUITAR_REQ_CODE:
				guitarsArray[trackNumber] = filename;
				break;
			case BASS_REQ_CODE:
				bassesArray[trackNumber] = filename;
				break;
			default:
				break;
			}
			selectedTrack.getBackground().setColorFilter(Color.RED,
					PorterDuff.Mode.MULTIPLY);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * On track click event
	 * 
	 * @param instrument The instrument that triggers the event
	 * @param requestCode The corresponding request code
	 */
	private void onTrackClick(String instrument, int requestCode) {
		Intent musicSelectionActivity = new Intent(MainActivity.this,
				MusicSelectionActivity.class);
		musicSelectionActivity.putExtra("Instrument", instrument);
		startActivityForResult(musicSelectionActivity, requestCode);
	}

}
