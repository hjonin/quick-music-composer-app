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
 * Contains methods to compose and export music
 *
 */
public class MusicComposer {
	private static final int FILE_SIZE = 882044; // File size in bytes ---> TO CHANGE

	private MusicComposer() {
	}

	/**
	 * Compose music (mix loops)
	 * 
	 * @param context The context of the application
	 * @param guitarsArray The array of guitar loops file names
	 * @param bassesArray The array of basses loops file names
	 * @param numberOfTracks The number of tracks
	 * @return The mixed bytes array
	 */
	public static byte[] composeMusic(Context context, String[] guitarsArray,
			String[] bassesArray, int numberOfTracks) {
		ByteArrayOutputStream finalArray = new ByteArrayOutputStream();

		for (int i = 0; i < numberOfTracks; i++) {
			byte[] mixedArray;
			try {
				// Mix the two files together
				mixedArray = mixFiles(context, guitarsArray[i], bassesArray[i]);

				// Copy the result into the final array
				finalArray.write(mixedArray);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return finalArray.toByteArray();
	}

	/**
	 * Write music into a file
	 * 
	 * @param context The context of the application
	 * @param guitarsArray The array of guitar loops file names
	 * @param bassesArray The array of basses loops file names
	 * @param numberOfTracks The number of tracks
	 * @return True if it worked
	 */
	public static boolean export(Context context, String[] guitarsArray,
			String[] bassesArray, int numberOfTracks) {
		File file = new File(Environment.getExternalStorageDirectory(),
				"MyMusic");
		FileOutputStream fileOutputStream;
		try {
			fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(composeMusic(context, guitarsArray,
					bassesArray, numberOfTracks));
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
		byte[] bytesArray1, bytesArray2;
		// If file added, convert into byte array
		if (!filename1.equals("")) {
			bytesArray1 = fileToBytesArray(context, filename1);
		} else {
			// Else initialize a byte array (big enough)
			bytesArray1 = new byte[FILE_SIZE];
		}
		// Same
		if (!filename2.equals("")) {
			bytesArray2 = fileToBytesArray(context, filename2);
		} else {
			bytesArray2 = new byte[FILE_SIZE];
		}

		// Mix the two arrays together
		byte[] mixedArray = mixBytesArray(45, bytesArray1, bytesArray2); // 45 = offset to skip header of wav file

		return mixedArray;
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
		int minLength = bytesArray1.length < bytesArray2.length ? bytesArray1.length
				: bytesArray2.length;
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

}
