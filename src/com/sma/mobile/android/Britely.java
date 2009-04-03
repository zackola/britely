package com.sma.mobile.android;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

public class Britely extends Activity {

	private final static String DUMP_PATH = "/sdcard/britely";

	public static int activeColor = R.drawable.red;
	public int currentX = 0;
	public int currentY = 0;
	public static int tileSize = 13;
	public static int screenWidth;
	public static int screenHeight;

	public static final int MY_TABLE_LAYOUT_ID = 100;
	public static final int BOARD_ID = 101;

	private static MediaScannerConnection mediaScanner = null;

	BriteView briteView;

	@Override
	protected void onStart() {
		super.onStart();
		mediaScanner.connect();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Window window = getWindow();
		screenWidth = window.getWindowManager().getDefaultDisplay().getWidth();
		screenHeight = window.getWindowManager().getDefaultDisplay()
				.getHeight();
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		briteView = new BriteView(this);
		briteView.setFocusable(true);
		briteView.setFocusableInTouchMode(true);
		briteView.setWillNotDraw(false);
		setContentView(briteView);

		mediaScanner = new MediaScannerConnection(this,
				new MediaScannerConnection.MediaScannerConnectionClient() {
					public void onMediaScannerConnected() {
						Log.d("MEDIA", "MediaScannerConnected()");
					}

					public void onScanCompleted(String path, Uri uri) {
						Log.d("MEDIA", "onScanCompleted()");
					}
				});

	}

	static private final int MENU_ITEM_PALLETE = 0;
	static private final int MENU_ITEM_SAVE = 1;
	static private final int MENU_ITEM_HELP = 2;	

	static private final int MENU_ITEM_COLORS_RED = 100;
	static private final int MENU_ITEM_COLORS_GREEN = 101;
	static private final int MENU_ITEM_COLORS_BLUE = 102;
	static private final int MENU_ITEM_COLORS_PINK = 103;
	static private final int MENU_ITEM_COLORS_VIOLET = 104;
	static private final int MENU_ITEM_COLORS_ORANGE = 105;
	static private final int MENU_ITEM_COLORS_YELLOW = 106;
	static private final int MENU_ITEM_COLORS_WHITE = 107;
	static private final int MENU_ITEM_COLORS_EMPTY = 108;

	@Override
	public boolean onCreateOptionsMenu(Menu _menu) {
		super.onCreateOptionsMenu(_menu);

		SubMenu colorsMenu = _menu
				.addSubMenu(0, MENU_ITEM_PALLETE, 1, "Colors");
		colorsMenu.setHeaderIcon(android.R.drawable.ic_menu_slideshow);
		colorsMenu.setIcon(android.R.drawable.ic_menu_slideshow);
		colorsMenu.add(0, MENU_ITEM_COLORS_RED, 1, "Red");
		colorsMenu.add(0, MENU_ITEM_COLORS_GREEN, 2, "Green");
		colorsMenu.add(0, MENU_ITEM_COLORS_BLUE, 3, "Blue");
		colorsMenu.add(0, MENU_ITEM_COLORS_PINK, 4, "Pink");
		colorsMenu.add(0, MENU_ITEM_COLORS_VIOLET, 5, "Violet");
		colorsMenu.add(0, MENU_ITEM_COLORS_ORANGE, 6, "Orange");
		colorsMenu.add(0, MENU_ITEM_COLORS_YELLOW, 7, "Yellow");
		colorsMenu.add(0, MENU_ITEM_COLORS_WHITE, 8, "White");
		colorsMenu.add(0, MENU_ITEM_COLORS_EMPTY, 9, "Empty");

		MenuItem shareMenuItem = _menu.add(0, MENU_ITEM_SAVE, 2, "Save");
		shareMenuItem.setIcon(android.R.drawable.ic_menu_send);
		
		MenuItem helpMenuItem = _menu.add(0, MENU_ITEM_HELP, 3, "Help");
		helpMenuItem.setIcon(android.R.drawable.ic_menu_help);		
		return true;

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// ignore orientation change
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem _menuItem) {
		switch (_menuItem.getItemId()) {
		case MENU_ITEM_COLORS_RED: {
			activeColor = R.drawable.red;
			return true;
		}
		case MENU_ITEM_COLORS_GREEN: {
			activeColor = R.drawable.green;
			return true;
		}
		case MENU_ITEM_COLORS_BLUE: {
			activeColor = R.drawable.blue;
			return true;
		}
		case MENU_ITEM_COLORS_PINK: {
			activeColor = R.drawable.pink;
			return true;
		}
		case MENU_ITEM_COLORS_VIOLET: {
			activeColor = R.drawable.violet;
			return true;
		}
		case MENU_ITEM_COLORS_ORANGE: {
			activeColor = R.drawable.orange;
			return true;
		}
		case MENU_ITEM_COLORS_YELLOW: {
			activeColor = R.drawable.yellow;
			return true;
		}
		case MENU_ITEM_COLORS_WHITE: {
			activeColor = R.drawable.white;
			return true;
		}
		case MENU_ITEM_COLORS_EMPTY: {
			activeColor = R.drawable.empty;
			return true;
		}
		case MENU_ITEM_HELP: {
			Toast.makeText(this, "Trackball to move. Alternately  W(up) Z(down) A(left) S(right) Enter to put.", Toast.LENGTH_LONG)
			.show();			
			return true;
		}
		case MENU_ITEM_SAVE: {
			Bitmap b = Bitmap.createBitmap(screenWidth, screenHeight,
					Bitmap.Config.RGB_565);
			Canvas c = new Canvas(b);
			this.briteView.draw(c);
			try {
				Long now = Long.valueOf(System.currentTimeMillis() / 1000);
				File dir = new File(DUMP_PATH);
				dir.mkdirs();
				String savefile = String.format("%s/%s", DUMP_PATH, String
						.format("britely%d.png", now));

				File newFile = new File(savefile);
				newFile.createNewFile();
				OutputStream os = new FileOutputStream(newFile);
				b.compress(Bitmap.CompressFormat.PNG, 100, os);
				os.flush();
				os.close();

				mediaScanner.connect();
				mediaScanner.scanFile(savefile, "image/png");

				Toast.makeText(this, "Saved your rad pic!", Toast.LENGTH_SHORT)
						.show();

				return true;
			} catch (Exception e) {
				Toast.makeText(this, "Saving failed. Bummer man.",
						Toast.LENGTH_SHORT).show();
				Log.i("ERR", e.toString());
				return false;
			}
		}
		}
		return true;
	}

	private static class BriteView extends LinearLayout {

		public List<Peg> board;
		int recticleX = 1;
		int recticleY = 1;
		int rows;
		int cols;
		boolean power = true;
		Drawable glow;		

		public BriteView(Context context) {
			super(context);
			
			this.glow = getResources().getDrawable(R.drawable.glow);
			
			this.board = new ArrayList<Peg>();
			this.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT));
			this.setOrientation(VERTICAL);
			this.setId(BOARD_ID);
			TableLayout tableLayout = new TableLayout(this.getContext());
			LayoutParams lp = new LayoutParams(
					TableLayout.LayoutParams.FILL_PARENT,
					TableLayout.LayoutParams.FILL_PARENT);
			tableLayout.setId(MY_TABLE_LAYOUT_ID);
			tableLayout.setLayoutParams(lp);
			tableLayout.setBackgroundColor(Color.BLACK);
			this.addView(tableLayout);

			rows = screenHeight / tileSize;
			cols = screenWidth / tileSize;

			for (int y = 0; y < rows; y++) {

				TableRow tr = new TableRow(this.getContext()
						.getApplicationContext());

				for (int x = 0; x < cols; x++) {

					int index = x + y * cols;
					ImageView tile = new ImageView(this.getContext()
							.getApplicationContext());
					tile.setImageResource(R.drawable.empty);
					tile.setId(index);
					tile.setAdjustViewBounds(true);
					tile.setMaxHeight(tileSize);
					tile.setMaxWidth(tileSize);
					tile.setScaleType(ScaleType.FIT_CENTER);

					TableRow.LayoutParams lp1 = new TableRow.LayoutParams();
					lp1.width = LayoutParams.WRAP_CONTENT;
					lp1.height = LayoutParams.WRAP_CONTENT;

					if (y % 2 == 1) {
						lp1.setMargins(0, 0, -tileSize, 0);
					}
					tile.setLayoutParams(lp1);
					tr.addView(tile, lp1);

					Peg p = new Peg(tile, index);
					board.add(index, p);

				}
				tableLayout.addView(tr, new TableLayout.LayoutParams());
			}
			setOnTouchListener(touchListener);
		}

		@Override
		public boolean onTrackballEvent(MotionEvent event) {
			super.onTrackballEvent(event);
			Log.i("onTrackballEvent", event.toString());

			switch (event.getAction()) {
			case MotionEvent.ACTION_MOVE: {

				float x = event.getX();
				float y = event.getY();
				if (Math.abs(x) > 0.15) {
					if (x < 0.0 && recticleX > 0) {
						Log.i("TRACK", "Decrementing X");
						recticleX -= 1;
					} else if (x > 0.0 && recticleX < screenWidth) {
						Log.i("TRACK", "Incrementing X");
						recticleX += 1;
					}
				}
				if (y < 0.0 && recticleY > 0) {
					Log.i("TRACK", "Decrementing Y");
					recticleY -= 1;
				} else if (y > 0.0 && recticleY < screenHeight) {
					Log.i("TRACK", "Incrementing Y");
					recticleY += 1;
				}

				int index = recticleX + recticleY * (screenWidth / tileSize);

				Peg p = board.get(index);

				for (Peg _p : board) {
					_p.tile.setImageResource(_p.currentColor);
				}
				p.tile.setImageResource(R.drawable.recticle);
				Log.i("RECT", String.valueOf(recticleX) + ","
						+ String.valueOf(recticleY));
				break;
			}
			case MotionEvent.ACTION_DOWN: {
				int index = recticleX + recticleY * (screenWidth / tileSize);
				Peg p = board.get(index);
				p.tile.setImageResource(activeColor);
				p.currentColor = activeColor;
				break;
			}
			}
			return true;
		}

		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			super.onKeyDown(keyCode, event);

			if (keyCode == 82)
				return false;			
			Log.i("onKeyDown", event.toString());
			
			if (keyCode == KeyEvent.KEYCODE_W && recticleY > 0) {
				recticleY -= 1;
			}
			if (keyCode == KeyEvent.KEYCODE_Z && recticleY < rows-1) {
				recticleY += 1;				
			}
			if (keyCode == KeyEvent.KEYCODE_A && recticleX > 0) {
				recticleX -= 1;				
			}
			if (keyCode == KeyEvent.KEYCODE_S && recticleX < cols-1) {
				recticleX += 1;				
			}			
			int index = recticleX + recticleY * (screenWidth / tileSize);

			Peg p = board.get(index);

			for (Peg _p : board) {
				_p.tile.setImageResource(_p.currentColor);
			}
			p.tile.setImageResource(R.drawable.recticle);				
			
			return true;
		}

		public OnTouchListener touchListener = new OnTouchListener() {

			public boolean onTouch(View target, MotionEvent motionEvent) {
				if (motionEvent.getEdgeFlags() != 0) {
					return false;
				}

				int x = (int) (motionEvent.getX() / tileSize);
				int y = (int) (motionEvent.getY() / tileSize);
				int index = x + y * (screenWidth / tileSize);
				Peg p = board.get(index);

				switch (motionEvent.getAction()) {
				case MotionEvent.ACTION_MOVE: {
					recticleX = x;
					recticleY = y;
					for (Peg _p : board) {
						_p.tile.setImageResource(_p.currentColor);
					}
					p.tile.setImageResource(R.drawable.recticle);
					break;
				}
				case MotionEvent.ACTION_UP: {
					p.tile.setImageResource(activeColor);
					p.currentColor = activeColor;
					break;
				}
				}
				return true;
			}

		};
		
		@Override
		protected void onDraw(Canvas canvas) {
			 super.onDraw(canvas);
			
			glow.setBounds(10, 10, 100, 100);
			glow.draw(canvas);
			for (int y = 0; y < rows; y++) {
				for (int x = 0; x < cols; x++) {
					int index = x + y * cols;
					Peg p = board.get(index);
					if (p.currentColor != R.drawable.empty) {
						glow.setBounds(x* tileSize, y*tileSize, x* tileSize + 20, y*tileSize + 20);
						glow.draw(canvas);
					}
				}
			}
			super.onDraw(canvas);
		}
		
	}

}