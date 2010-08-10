package com.stelluxstudios.Shogi;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class GalleryImageAdapter extends BaseAdapter
{
	
	private Context context;
	private int[] images;
	
	private int sizeX = 100,sizeY = 65;
	
	public GalleryImageAdapter(Context c)
	{
		context = c;
	}

	public int getCount()
	{
		return images.length;
	}

	public Object getItem(int position)
	{
		return position;
	}

	public long getItemId(int position)
	{
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		ImageView i = new ImageView(context);

		i.setImageResource(images[position]);
		i.setScaleType(ImageView.ScaleType.CENTER);
		i.setLayoutParams(new Gallery.LayoutParams(sizeX, sizeY));
		i.setPadding(10, 10, 10, 10);
		return i;
	}
	

//	@Override
//	public float getAlpha(boolean focused, int offset)
//	{
//		return Math.max(0, 1.0f - (0.2f * Math.abs(offset)));
//	}
//
//	@Override
//	public float getScale(boolean focused, int offset)
//	{
//		return Math.max(0, 1.0f - (0.2f * Math.abs(offset)));
//	}

	//the input is an array of drawable ids
	public void setImages(int[] images)
	{
		this.images = images;
	}
	
	public void setImageSize(int x, int y)
	{
		sizeX = x;
		sizeY = y;
	}
}