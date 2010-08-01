package com.stelluxstudios.Shogi;

import java.io.File;

import android.app.Activity;
import android.os.Environment;
import android.view.Window;
import android.widget.TextView;

public class DownloadActivity extends Activity
{
	TextView status;
	boolean finished_setup = false;
	protected void onCreate(android.os.Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.download);

		status = (TextView) findViewById(R.id.downloadStatus);
		
		status.setText("Downloading Files");
		
		final Thread downloadThread = new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				File zipFile = ContentDownloader.downloadContent(DownloadActivity.this,status,getWindow());
				//File zipFile = new File(new File(Environment.getExternalStorageDirectory(),ContentDownloader.content_directory),"shogi_sdcard.zip");
				if (zipFile != null)
				{
					finished_setup = ContentDownloader.extractContent(DownloadActivity.this,zipFile,status);
					status.setText("finished!");
					setResult(ContentDownloader.SUCCESSFUL_SETUP);
					finish();
				}
				
			}
		});

		downloadThread.start();
		
	}
	
	@Override
	protected void onDestroy()
	{
		if (!finished_setup)
		{
			ContentDownloader.fv_file.delete();
			ContentDownloader.hash_file.delete();
			ContentDownloader.opening_book_file.delete();
			setResult(ContentDownloader.FAILED_SETUP);
		}
		super.onDestroy();
	}
}
