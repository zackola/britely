package com.sma.mobile.android;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.ImageView;

public class PegView extends ImageView {

	public int currentColor;
	public boolean targeted;

	public PegView(Context context) {
		super(context);
		currentColor = R.drawable.empty;
		targeted = false;
		invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (targeted) 
			setImageResource(R.drawable.recticle); 
		else
			setImageResource(currentColor);
	}
}
