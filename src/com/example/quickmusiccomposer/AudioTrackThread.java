package com.example.quickmusiccomposer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Environment;

public class AudioTrackThread extends Thread {
	private AudioTrack audioTrack;
	private Context context;
	private String filename1;
	private String filename2;

	/**
	 * Constructor
	 * @param context
	 * @param filename (must be in assets)
	 */
	public AudioTrackThread(Context context, String filename1, String filename2) {
		audioTrack = new AudioTrack(
				AudioManager.STREAM_MUSIC, 
				44100, 
				AudioFormat.CHANNEL_OUT_MONO, 
				AudioFormat.ENCODING_PCM_16BIT, 
				AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT), 
				AudioTrack.MODE_STREAM);
		this.context = context;
		this.filename1 = filename1;
		this.filename2 = filename2;
	}

	@Override
	public void run() {
		InputStream inputStream1 = null;
		InputStream inputStream2 = null;
		try {
			inputStream1 = context.getAssets().open(filename1);
			inputStream2 = context.getAssets().open(filename2);
			
			AssetFileDescriptor afd1 = context.getAssets().openFd(filename1);
		    byte[] music1 = new byte[(int) afd1.getLength()];
		    
		    AssetFileDescriptor afd2 = context.getAssets().openFd(filename1);
		    byte[] music2 = new byte[(int) afd2.getLength()];
			
			BufferedInputStream bis1 = new BufferedInputStream(inputStream1);
	        DataInputStream dis1 = new DataInputStream(bis1);
	        
	        BufferedInputStream bis2 = new BufferedInputStream(inputStream2);
	        DataInputStream dis2 = new DataInputStream(bis2);

	        // Read the file into the music array.
	        int i = 0;
	        while (dis1.available() > 0) { // tres long !!!!!!
	          music1[i] = dis1.readByte();
	          i++;
	        }
	        i = 0;
	        while (dis2.available() > 0) { // tres long !!!!!!!
	          music2[i] = dis2.readByte();
	          i++;
	        }
	        
			inputStream1.close();
			inputStream2.close();
			
			byte[] res = makeChimeraAll(45, music1, music2);
			audioTrack.play();
	        audioTrack.write(res, 0, music1.length);
	        
	        boolean mExternalStorageAvailable = false;
	        boolean mExternalStorageWriteable = false;
	        String state = Environment.getExternalStorageState();

	        if (Environment.MEDIA_MOUNTED.equals(state)) {
	        	System.out.println("test1");
	            // We can read and write the media
	            mExternalStorageAvailable = mExternalStorageWriteable = true;
	        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        	System.out.println("test2");
	            // We can only read the media
	            mExternalStorageAvailable = true;
	            mExternalStorageWriteable = false;
	        } else {
	        	System.out.println("test3");
	            // Something else is wrong. It may be one of many other states, but all we need
	            //  to know is we can neither read nor write
	            mExternalStorageAvailable = mExternalStorageWriteable = false;
	        }
	        
System.out.println(Environment.getExternalStorageDirectory());
	        File file = new File(Environment.getExternalStorageDirectory(), "fichiertest.wav");
	        FileOutputStream out = new FileOutputStream(file);
	        out.write(res);
	        out.close();
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public byte[] makeChimeraAll(int offset, byte[] bigData, byte[] littleData){
		//bigData and littleData are each short arrays, populated elsewhere
	    int intBucket = 0;
	    for(int i=offset;i<bigData.length;i++){
	        if(i < littleData.length){
	            intBucket = bigData[i] + littleData[i];
	            if(intBucket > Byte.MAX_VALUE){
	                intBucket = Byte.MAX_VALUE;
	            }
	            else if (intBucket < Byte.MIN_VALUE){
	                intBucket = Byte.MIN_VALUE;
	            }
	            bigData[i] = (byte) intBucket;
	        }
	        else{
	            //leave bigData alone
	        }
	    } 
	    return bigData;
	}

}
