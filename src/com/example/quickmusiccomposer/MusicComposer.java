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
import android.os.Environment;

/**
 * Contains utility methods to compose and save music
 *
 */
public class MusicComposer {
	private static final int SAMPLE_FILE_SIZE = 882044; // bytes

	private MusicComposer() { 
		// Private constructor because static class
	}

	/**
	 * Compose music (mix samples)
	 * 
	 * @param context The context of the application
	 * @param samplesArray1 An array of samples filenames
	 * @param samplesArray2 Another array of samples filenames
	 * @param maxTracks The number of tracks
	 * @return The music bytes array
	 */
	public static byte[] composeMusic(Context context, String[] samplesArray1,
			String[] samplesArray2, int maxTracks) {
		ByteArrayOutputStream musicArrayOutputStream = new ByteArrayOutputStream();
		int emptyCounter = 0; // Counter of empty arrays

		for (int i = 0; i < maxTracks; i++) {
			byte[] mixedArray;
			try {
				// Mix the two files together
				mixedArray = mixFiles(context, samplesArray1[i], samplesArray2[i]);
				// If there were no files (mixed array null), create an empty array (pause in the music)
				if (mixedArray == null) {
					musicArrayOutputStream.write(new byte[SAMPLE_FILE_SIZE]);
					emptyCounter++;
				}
				else {
					// Copy the result into the music array
					musicArrayOutputStream.write(mixedArray);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// If every array is empty, no music so return null
		if (emptyCounter == maxTracks) {
			return null;
		}

		return musicArrayOutputStream.toByteArray();
	}

	/**
	 * Write music into a file
	 * 
	 * @param context The context of the application
	 * @param samplesArray1 An array of samples filenames
	 * @param samplesArray2 Another array of samples filenames
	 * @param maxTracks The number of tracks
	 * @return True if the music has been saved, false otherwise
	 */
	public static boolean save(Context context, String[] samplesArray1,
			String[] samplesArray2, int maxTracks) {
		byte[] music = composeMusic(context, samplesArray1,
				samplesArray2, maxTracks);

		// If music is empty, no point saving
		if (music == null) {
			return false;
		}

		FileOutputStream fileOutputStream;
		try {
			fileOutputStream = new FileOutputStream(new File(Environment.getExternalStorageDirectory(),
					"MyMusic"));
			fileOutputStream.write(music);
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

	/**
	 * Mix two files together
	 * 
	 * @param context The context of the application
	 * @param filename1 The name of a file (must be in assets folder)
	 * @param filename2 The name of another file (must be in assets folder)
	 * @return The mixed bytes array
	 * @throws IOException
	 */
	private static byte[] mixFiles(Context context, String filename1,
			String filename2) throws IOException {
		return mixBytesArray(45, fileToBytesArray(context, filename1), fileToBytesArray(context, filename2)); // 45 = offset to skip header of wav file
	}

	/**
	 * Mix two array of bytes into one
	 * 
	 * @param offset An offset
	 * @param bytesArray1 A bytes array
	 * @param bytesArray2 Another bytes array
	 * @return The mixed bytes array
	 */
	private static byte[] mixBytesArray(int offset, byte[] bytesArray1,
			byte[] bytesArray2) {
		// If the two arrays are null, return null
		if (bytesArray1 == null && bytesArray2 == null) {
			return null;
		}

		// If one of the array is null, return the other
		if (bytesArray1 == null) {
			return bytesArray2;
		}
		if (bytesArray2 == null) {
			return bytesArray1;
		}

		int minLength = bytesArray1.length < bytesArray2.length ? bytesArray1.length
				: bytesArray2.length; // In fact, the two arrays should have the same length
		int intBucket = 0;
		byte[] mixedArray = new byte[minLength];
		for (int i = offset; i < minLength; i++) {
			intBucket = bytesArray1[i] + bytesArray2[i];
			if (intBucket > Byte.MAX_VALUE) {
				intBucket = Byte.MAX_VALUE;
			} else if (intBucket < Byte.MIN_VALUE) {
				intBucket = Byte.MIN_VALUE;
			}
			mixedArray[i] = (byte) intBucket;
		}
		return mixedArray;
	}

	/**
	 * Convert a file into an array of bytes
	 * 
	 * @param filename The name of a file (must be in assets folder)
	 * @return A bytes array
	 * @throws IOException
	 */
	private static byte[] fileToBytesArray(Context context, String filename)
			throws IOException {
		// If no file, return null
		if (filename.isEmpty()) {
			return null;
		}

		InputStream inputStream = context.getAssets().open(filename);
		byte[] bytesArray = new byte[inputStream.available()];
		DataInputStream dataInputStream = new DataInputStream(
				new BufferedInputStream(inputStream));
		dataInputStream.read(bytesArray);
		inputStream.close();
		return bytesArray;
	}

}
