package com.sma.mobile.android;

import android.widget.ImageView;

public class Peg {

	public ImageView tile;
	public int index;
	public int currentColor;
		
	public Peg(ImageView _tile, int _index) {
		tile = _tile;
		index = _index;
		put(R.drawable.empty);				
	}
	
	public void put(int id) {
		currentColor = id;
		tile.setImageResource(id);
	}
}
