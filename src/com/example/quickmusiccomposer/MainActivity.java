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
	private static final int MAX_TRACKS = 3;
	private View selectedTrack;
	private String[] guitarsArray;
	private String[] bassesArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
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
				if (guitarsArray[trackNumber].equals("")) {
					onTrackClick("Guitar", 1);
					selectedTrack = v;
				} else { // Remove if double click
					guitarsArray[trackNumber] = "";
					v.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
				}
			}
		};
		OnClickListener bassTrackListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onTrackClick("Bass", 2);
				selectedTrack = v;
			}
		};
		guitarTrack1.setOnClickListener(guitarTrackListener);
		bassTrack1.setOnClickListener(bassTrackListener);
		guitarTrack2.setOnClickListener(guitarTrackListener);
		bassTrack2.setOnClickListener(bassTrackListener);
		guitarTrack3.setOnClickListener(guitarTrackListener);
		bassTrack3.setOnClickListener(bassTrackListener);
		
		// Play
		Button play = (Button) findViewById(R.id.playButton);
		play.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PlayThread playThread = new PlayThread(MainActivity.this, guitarsArray, bassesArray);
				playThread.start();
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
			case 1:
				guitarsArray[trackNumber] = filename;
				break;
			case 2:
				bassesArray[trackNumber] = filename;
				break;
			default:
				break;
			}
			selectedTrack.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * On track click event
	 * @param instrument
	 * @param requestCode (code for instrument)
	 */
	private void onTrackClick(String instrument, int requestCode) {
		Intent musicSelectionActivity = new Intent(MainActivity.this, MusicSelectionActivity.class);
		musicSelectionActivity.putExtra("Instrument", instrument);
		startActivityForResult(musicSelectionActivity, requestCode);
	}

}
