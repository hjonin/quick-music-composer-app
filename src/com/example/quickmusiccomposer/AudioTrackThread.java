package com.example.quickmusiccomposer;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Environment;

public class AudioTrackThread extends Thread {
	private static final int FILE_SIZE = 882044; // File size in bytes
	private AudioTrack audioTrack;
	private Context context;
	private String[] guitarsArray;
	private String[] bassesArray;
	private int numberOfTracks;

	/**
	 * Constructor
	 * 
	 * @param context
	 * @param filename
	 *            (must be in assets folder)
	 */
	public AudioTrackThread(Context context, String[] guitarsArray,
			String[] bassesArray, int numberOfTracks) {
		audioTrack = new AudioTrack(
				AudioManager.STREAM_MUSIC, 44100,
				AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
				AudioTrack.getMinBufferSize(
						44100,
						AudioFormat.CHANNEL_OUT_MONO,
						AudioFormat.ENCODING_PCM_16BIT), AudioTrack.MODE_STREAM);
		this.context = context;
		this.guitarsArray = guitarsArray;
		this.bassesArray = bassesArray;
		this.numberOfTracks = numberOfTracks;
	}

	@Override
	public void run() {
		ByteArrayOutputStream byteArrayOutputSream = new ByteArrayOutputStream(); // Final array

		for (int i = 0; i < numberOfTracks; i++) {
			byte[] bytesArray1, bytesArray2;
			try {
				// If music added, convert into byte array
				if (!guitarsArray[i].equals("")) {
					bytesArray1 = fileToBytesArray(guitarsArray[i]);
				} else {
					// Else initialise a byte array (big enough)
					bytesArray1 = new byte[FILE_SIZE];
				}
				// Same
				if (!bassesArray[i].equals("")) {
					bytesArray2 = fileToBytesArray(bassesArray[i]);
				} else {
					bytesArray2 = new byte[882044];
				}

				// Mix the two arrays together
				byte[] mixedArray = mixBytesArray(45, bytesArray1, bytesArray2); // 45 = offset to skip header of wav file
				// And play the result
				audioTrack.play();
				audioTrack.write(mixedArray, 0, mixedArray.length);

				// Copy the result into the final array
				byteArrayOutputSream.write(mixedArray);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Write the final array into a file (raw file) ---> need a header to be
		// wav
		File file = new File(Environment.getExternalStorageDirectory(),
				"MyMusic");
		FileOutputStream fileOutputStream;
		try {
			fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(byteArrayOutputSream.toByteArray());
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Convert a file into an array of bytes
	 * 
	 * @param filename
	 *            The name of a file (must be in assets folder)
	 * @return A bytes array
	 * @throws IOException
	 */
	private byte[] fileToBytesArray(String filename) throws IOException {
		InputStream inputStream = context.getAssets().open(filename);
		byte[] bytesArray = new byte[inputStream.available()];
		BufferedInputStream bufferedInputStream = new BufferedInputStream(
				inputStream);
		DataInputStream dataInputStream = new DataInputStream(
				bufferedInputStream);
		dataInputStream.read(bytesArray);
		inputStream.close();
		return bytesArray;
	}

	/**
	 * Mix two array of bytes into one
	 * 
	 * @param offset
	 *            An offset
	 * @param bytesArray1
	 *            A bytes array
	 * @param bytesArray2
	 *            Another bytes array
	 * @return The mixed bytes array
	 */
	public byte[] mixBytesArray(int offset, byte[] bytesArray1,
			byte[] bytesArray2) {
		int minLength = bytesArray1.length < bytesArray2.length ? bytesArray1.length
				: bytesArray2.length;
		int intBucket = 0;
		for (int i = offset; i < minLength; i++) {
			intBucket = bytesArray1[i] + bytesArray2[i];
			if (intBucket > Byte.MAX_VALUE) {
				intBucket = Byte.MAX_VALUE;
			} else if (intBucket < Byte.MIN_VALUE) {
				intBucket = Byte.MIN_VALUE;
			}
			bytesArray1[i] = (byte) intBucket;
		}
		return bytesArray1;
	}

}
