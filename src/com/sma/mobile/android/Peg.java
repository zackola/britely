package com.sma.mobile.android;

import android.widget.ImageView;

public class Peg {

	public ImageView tile;
	public int index;
	public int currentColor;
		
	public Peg(ImageView _tile, int _index) {
		tile = _tile;
		index = _index;
		currentColor = R.drawable.empty;
		tile.setImageResource(currentColor);		
	}
}
