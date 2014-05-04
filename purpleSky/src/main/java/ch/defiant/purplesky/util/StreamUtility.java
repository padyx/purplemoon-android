package ch.defiant.purplesky.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import android.util.Log;

public class StreamUtility {
	
	private static final String TAG = "StreamUtility";

	public static String inputStreamToString(InputStream is, String encoding){
		if(is == null){
			return "";
		}
			
		final char[] buffer = new char[0x10000];
		StringBuilder out = new StringBuilder();
		Reader in;
		try {
			in = new InputStreamReader(is, encoding);
		} catch (UnsupportedEncodingException e) {
			Log.w(TAG, "Unsupported encoding", e);
			return "";
		}
		
		int read;
		do {
		  try {
			read = in.read(buffer, 0, buffer.length);
		} catch (IOException e) {
			Log.w(TAG, "IO Exception when converting stream to string", e);
			return "";
		}
		  if (read>0) {
		    out.append(buffer, 0, read);
		  }
		} while (read>=0);
		
		return out.toString();
	}
	
	public static String inputStreamToString(InputStream is){
		return inputStreamToString(is, "UTF-8");
	}
	
}
