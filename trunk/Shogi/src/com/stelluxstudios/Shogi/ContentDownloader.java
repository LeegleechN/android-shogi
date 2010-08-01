package com.stelluxstudios.Shogi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class ContentDownloader
{
	final static String content_directory = "/Android/com.stelluxstudios.Shogi/";
	final static String fv = "fv.bin";
	final static String opening_book = "book.bin";
	final static String hash = "hash.bin";
	final static String content_URL = "http://android-shogi.googlecode.com/files/shogi_sdcard.zip";
	final static int content_size = 41213680; //in bytes
	
	static File path_to_storage,fv_file,opening_book_file,hash_file;
	//returns true if content is properly installed, false on failure
	public static boolean verifyContentPresence(final Activity context,final Handler handler)
	{
		File sdcard = Environment.getExternalStorageDirectory();
		
		if (!sdcard.canWrite())
		{
			AlertDialog.Builder b = new AlertDialog.Builder(context);
			b.setTitle("Error accessing SD Card");
			b.setMessage("Shogi requires access to the sd card to run.");
			b.setPositiveButton("OK", new OnClickListener()
			{
				
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					context.finish();
				}
			});
			b.show();
		}
		
		path_to_storage = new File(sdcard,content_directory);
		fv_file = new File(path_to_storage,fv);
		opening_book_file = new File(path_to_storage,opening_book);
		hash_file = new File(path_to_storage,hash);
		
		Log.d("pathtostorage", path_to_storage.getAbsolutePath());
		
		if (!path_to_storage.exists() || !fv_file.exists() || !opening_book_file.exists() || !hash_file.exists())
		{
			AlertDialog.Builder b = new AlertDialog.Builder(context);
			b.setTitle("Need to download files");
			b.setMessage("Shogi needs to download some files in order to run. They are about 40MB. Is it ok to download the files?");
			b.setPositiveButton("Download", new OnClickListener()
			{
				
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					//start the activity on the main thread
					handler.post(new Runnable()
					{
						
						@Override
						public void run()
						{
							Intent i = new Intent();
							i.setClassName(context, DownloadActivity.class.getName());
							
							context.startActivityForResult(i, 1);
						}
					});
					dialog.cancel();
				}
			});
			b.setNegativeButton("Later", new OnClickListener()
			{
				
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					context.finish();
				}
			});
			b.show();
			
		}
		
		return false;
	}
	
	//downloads the content into temporary storage, and returns the path
	//you must have called verifyContentPresence first
	public static File downloadContent(final Activity context, final TextView status_text,final Window progressUpdateWindow)
	{
		if (!path_to_storage.mkdirs())
		{
			AlertDialog.Builder b = new AlertDialog.Builder(context);
			b.setTitle("Unable to make directories on SD Card");
			b.setMessage("Please verify that the SD card is accessible and try again.");
			b.setPositiveButton("OK", new OnClickListener()
			{
				
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					context.finish();
				}
			});
		}
		
		 URL url = null;
		try
		{
			url = new URL(content_URL);
		} catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		
		try
		{
			File out_file = new File(path_to_storage,"shogi_sdcard.zip");
			out_file.delete();
			
			FileWriter out_stream = new FileWriter(out_file);
	        URLConnection urlConnection = url.openConnection();
	        urlConnection.setConnectTimeout(1000);
	        urlConnection.setReadTimeout(1000);
	        BufferedReader breader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
	        
	        int downloaded = 0;
	        String line;
	        while((line = breader.readLine()) != null) {
	        	downloaded += line.length();
	        	final int progress = (int)((downloaded/(float)content_size) * 10000);
	        	final int downloaded_copy = downloaded;
	        	status_text.post(new Runnable()
				{
					
					@Override
					public void run()
					{
						status_text.setText((downloaded_copy/1000) + "KB / " + (content_size/1000) + "KB");
						progressUpdateWindow.setFeatureInt(Window.FEATURE_PROGRESS, progress);
					}
				});
	        	
	            out_stream.write(line);
	        }

	        out_stream.close();
	        return out_file;
		}
		catch (IOException e) {
			e.printStackTrace();
			status_text.post(new Runnable()
			{
				
				@Override
				public void run()
				{AlertDialog.Builder b = new AlertDialog.Builder(context);
				b.setTitle("Download Error");
				b.setMessage("Is your internet connected? Please check and try again.");
				b.setPositiveButton("OK", new OnClickListener()
				{
					
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						context.finish();
					}
				});
				b.show();
				}
			});
			return null;
		}
	}
	
}
