package com.example.quickmusiccomposer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.content.Context;

public class PlayThread extends Thread {
	private static final int MAX_TRACKS = 3;
	private Context context;
	private String[] guitarsArray;
	private String[] bassesArray;

	public PlayThread(Context context, String[] guitarsArray, String[] bassesArray) {
		this.context = context;
		this.guitarsArray = guitarsArray;
		this.bassesArray = bassesArray;
	}

	@Override
	public void run() {
		AudioTrackThread guitarThread;
		AudioTrackThread bassThread;
		List<Thread> threadsList = new CopyOnWriteArrayList<Thread>();
		boolean retry;
		
		for (int i = 0; i < MAX_TRACKS; i++) {
			if (!guitarsArray[i].equals("")) {
				guitarThread = new AudioTrackThread(context, guitarsArray[i]);
				guitarThread.start();
				threadsList.add(guitarThread);
			}
			if (!bassesArray[i].equals("")) {
				bassThread = new AudioTrackThread(context, bassesArray[i]);
				bassThread.start();
				threadsList.add(bassThread);
			}
			
			// Join threads
			for (Thread thread: threadsList) {
				retry = true;
				while (retry) {
					try {
						thread.join();
						retry = false;
						threadsList.remove(thread);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

}
