package com.stelluxstudios.Shogi;

import android.app.Activity;
import android.view.Window;
import android.widget.TextView;

public class DownloadActivity extends Activity
{
	TextView status;
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
				ContentDownloader.downloadContent(DownloadActivity.this,status,getWindow());
			}
		});

		downloadThread.start();
		
	}
}
