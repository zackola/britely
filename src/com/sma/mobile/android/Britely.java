package com.sma.mobile.android;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Britely extends Activity {

	public final static String TAG = "Britely";
	private final static String DUMP_PATH = "/sdcard/britely";

	public static int activeColor = R.drawable.red;
	public int currentX = 0;
	public int currentY = 0;
	public static int tileSize = 13;
	public static int screenWidth;
	public static int screenHeight;

	public static int[] lastMove = new int[2];

	// View Ids
	public static final int BRITE_VIEW_ID = 1;
	public static final int MY_TABLE_LAYOUT_ID = 100;
	public static final int BOARD_ID = 101;

	private static MediaScannerConnection mediaScanner = null;
	private static Intent captureImageIntent = null;
	private static ContentResolver contentResolver;

	// Result Ids
	private final static int IMAGE_SELECT_CALL = 1;

	BriteView briteView;

	@Override
	protected void onStart() {
		super.onStart();
		mediaScanner.connect();
		Toast.makeText(this, R.string.help, Toast.LENGTH_LONG).show();
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
		briteView.setId(BRITE_VIEW_ID);
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
		contentResolver = getContentResolver();
	}

	// Options Menu Stuff
	static private final int MENU_ITEM_PALETTE = 0;
	static private final int MENU_ITEM_SAVE = 1;
	static private final int MENU_ITEM_HELP = 2;
	static private final int MENU_ITEM_CLEAR = 3;
	static private final int MENU_ITEM_UNDO = 4;
	static private final int MENU_ITEM_LOAD = 5;

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
				.addSubMenu(0, MENU_ITEM_PALETTE, 1, "Colors");
		colorsMenu.setHeaderIcon(android.R.drawable.ic_menu_slideshow);
		colorsMenu.setIcon(android.R.drawable.ic_menu_slideshow);

		colorsMenu.add(0, MENU_ITEM_COLORS_EMPTY, 0, "Empty");
		colorsMenu.add(0, MENU_ITEM_COLORS_RED, 1, "Red");
		colorsMenu.add(0, MENU_ITEM_COLORS_GREEN, 2, "Green");
		colorsMenu.add(0, MENU_ITEM_COLORS_BLUE, 3, "Blue");
		colorsMenu.add(0, MENU_ITEM_COLORS_PINK, 4, "Pink");
		colorsMenu.add(0, MENU_ITEM_COLORS_VIOLET, 5, "Violet");
		colorsMenu.add(0, MENU_ITEM_COLORS_ORANGE, 6, "Orange");
		colorsMenu.add(0, MENU_ITEM_COLORS_YELLOW, 7, "Yellow");
		colorsMenu.add(0, MENU_ITEM_COLORS_WHITE, 8, "White");

		MenuItem loadMenuItem = _menu.add(0, MENU_ITEM_LOAD, 0, "Load");
		loadMenuItem.setIcon(android.R.drawable.ic_input_get);

		MenuItem shareMenuItem = _menu.add(0, MENU_ITEM_SAVE, 1, "Save");
		shareMenuItem.setIcon(android.R.drawable.ic_menu_send);

		MenuItem undoMenuItem = _menu.add(0, MENU_ITEM_UNDO, 2, "Undo");
		undoMenuItem.setIcon(android.R.drawable.ic_menu_revert);

		MenuItem clearMenuItem = _menu.add(0, MENU_ITEM_CLEAR, 3, "Clear");
		clearMenuItem.setIcon(android.R.drawable.ic_menu_delete);

		MenuItem helpMenuItem = _menu.add(0, MENU_ITEM_HELP, 4, "Help");
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
			Toast.makeText(this, R.string.help, Toast.LENGTH_LONG).show();
			return true;
		}
		case MENU_ITEM_CLEAR: {
			for (Peg p : briteView.board) {
				p.currentColor = R.drawable.empty;
			}
			briteView.invalidate();
			return true;
		}
		case MENU_ITEM_UNDO: {
			if (lastMove[0] >= 0 && lastMove[1] >= 0) {
				Peg p = briteView.board.get(lastMove[0]);
				p.currentColor = lastMove[1];
				briteView.invalidate();
			} else {
				Toast.makeText(this, "Sorry, nothing to undo",
						Toast.LENGTH_SHORT).show();
			}
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
				return false;
			}
		}
		case MENU_ITEM_LOAD: {
			captureImageIntent = new Intent("android.intent.action.GET_CONTENT");
			captureImageIntent.setType("image/*");
			startActivityForResult(captureImageIntent, IMAGE_SELECT_CALL);
			return true;
		}
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		captureImageIntent = null;
		switch (requestCode) {
		case IMAGE_SELECT_CALL:
			switch (resultCode) {
			case RESULT_OK:
				try {
					Uri dataUri = data.getData();
					if (briteView.loadFile(dataUri)) {
						Toast.makeText(this, "... loaded ...",
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(this,
								"sorry but I have failed to load it",
								Toast.LENGTH_SHORT);
					}
				} catch (NullPointerException e) {
					Toast.makeText(this, "sorry but I have failed to load it",
							Toast.LENGTH_SHORT);
				}
			default:
				break;
			}
			break;
		}
	}

	private static class BriteView extends LinearLayout {

		List<Peg> board;
		List<Peg> previouslyTargeted;
		int recticleX = 1;
		int recticleY = 1;
		int rows;
		int cols;
		public Paint paint;
		public Matrix matrix;
		public Bitmap bitmapSave = null;
		Bitmap bitmap = null;
		public Drawable palette[];
		public Drawable recticle;

		public BriteView(Context context) {
			super(context);

			bitmap = Bitmap.createBitmap(screenWidth, screenHeight,
					Config.ARGB_8888);
			paint = new Paint();
			matrix = new Matrix();
			palette = new Drawable[9];
			recticle = getResources().getDrawable(R.drawable.recticle);

			palette[0] = getResources().getDrawable(R.drawable.empty);
			palette[1] = getResources().getDrawable(R.drawable.red);
			palette[2] = getResources().getDrawable(R.drawable.green);
			palette[3] = getResources().getDrawable(R.drawable.blue);
			palette[4] = getResources().getDrawable(R.drawable.pink);
			palette[5] = getResources().getDrawable(R.drawable.violet);
			palette[6] = getResources().getDrawable(R.drawable.orange);
			palette[7] = getResources().getDrawable(R.drawable.yellow);
			palette[8] = getResources().getDrawable(R.drawable.white);

			lastMove[0] = -1;
			lastMove[1] = -1;
			setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT));
			setOrientation(VERTICAL);
			rows = screenHeight / tileSize;
			cols = screenWidth / tileSize;
			board = new ArrayList<Peg>();
			previouslyTargeted = new ArrayList<Peg>();

			for (int y = 0; y < rows; y++) {
				for (int x = 0; x < cols; x++) {
					int index = getIndex(x, y);
					board.add(index, new Peg());
				}
			}
			setOnTouchListener(touchListener);
		}

		@Override
		public boolean onTrackballEvent(MotionEvent event) {
			super.onTrackballEvent(event);

			switch (event.getAction()) {
			case MotionEvent.ACTION_MOVE: {

				float x = event.getX();
				float y = event.getY();

				if (x < 0.0 && recticleX > 0) {
					recticleX -= 1;
				} else if (x > 0.0 && recticleX < screenWidth) {
					recticleX += 1;
				}

				if (y < 0.0 && recticleY > 0) {
					recticleY -= 1;
				} else if (y > 0.0 && recticleY < screenHeight) {
					recticleY += 1;
				}

				int index = getIndex(recticleX, recticleY);
				for (Peg p : previouslyTargeted) {
					p.targeted = false;
				}
				previouslyTargeted.clear();
				Peg p = board.get(index);
				p.targeted = true;
				previouslyTargeted.add(p);
				invalidate();
				break;
			}
			case MotionEvent.ACTION_DOWN: {
				int index = getIndex(recticleX, recticleY);
				Peg p = board.get(index);
				lastMove[0] = index;
				lastMove[1] = p.currentColor;
				p.currentColor = activeColor;
				break;
			}
			}
			return true;
		}

		@Override
		public boolean onKeyUp(int keyCode, KeyEvent event) {
			super.onKeyUp(keyCode, event);

			if (keyCode == KeyEvent.KEYCODE_SPACE
					|| keyCode == KeyEvent.KEYCODE_CAMERA) {
				int index = getIndex(recticleX, recticleY);
				Peg p = board.get(index);
				lastMove[0] = index;
				lastMove[1] = p.currentColor;
				p.currentColor = activeColor;
				invalidate();
				return true;
			}
			return false;
		}

		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			super.onKeyDown(keyCode, event);

			if (keyCode == KeyEvent.KEYCODE_MENU
					|| keyCode == KeyEvent.KEYCODE_BACK
					|| keyCode == KeyEvent.KEYCODE_HOME)
				return false;

			if (keyCode == KeyEvent.KEYCODE_1 && recticleY > 0) {
				recticleY -= 1;
			}
			if (keyCode == KeyEvent.KEYCODE_A && recticleY < rows - 1) {
				recticleY += 1;
			}
			if (keyCode == KeyEvent.KEYCODE_Q && recticleX > 0) {
				recticleX -= 1;
			}
			if (keyCode == KeyEvent.KEYCODE_W && recticleX < cols - 1) {
				recticleX += 1;
			}
			int index = getIndex(recticleX, recticleY);

			for (Peg p : previouslyTargeted) {
				p.targeted = false;
			}
			previouslyTargeted.clear();
			Peg p = board.get(index);
			p.targeted = true;
			previouslyTargeted.add(p);
			invalidate();
			return true;
		}

		public OnTouchListener touchListener = new OnTouchListener() {

			public boolean onTouch(View target, MotionEvent motionEvent) {
				if (motionEvent.getEdgeFlags() != 0) {
					return false;
				}

				int x = (int) (motionEvent.getX() / tileSize);
				int y = (int) (motionEvent.getY() / tileSize);

				int index = getIndex(x, y);
				if (index < 0 || index >= rows * cols) {
					return false;
				}
				Peg p = board.get(index);

				switch (motionEvent.getAction()) {
				case MotionEvent.ACTION_MOVE: {
					recticleX = x;
					recticleY = y;
					for (Peg _p : previouslyTargeted) {
						_p.targeted = false;
					}
					previouslyTargeted.clear();
					p.targeted = true;
					previouslyTargeted.add(p);
					invalidate();
					break;
				}
				case MotionEvent.ACTION_UP: {
					lastMove[0] = index;
					lastMove[1] = p.currentColor;
					p.targeted = false;
					p.currentColor = activeColor;
					invalidate();
					break;
				}
				}
				return true;
			}

		};

		public int getQuadrant(int x, int y) {
			int quadrant = 1;
			if (x < screenWidth / 2) {
				if (y < screenHeight / 2) {
					quadrant = 1;
				} else {
					quadrant = 4;
				}
			} else {
				if (y < screenHeight / 2) {
					quadrant = 2;
				} else {
					quadrant = 3;
				}
			}

			switch (quadrant) {
			case 1: {
				x -= 2;
				y -= 2;
				break;
			}
			case 2: {
				x += 2;
				y -= 2;
				break;
			}
			case 3: {
				x += 2;
				y += 2;
				break;
			}
			case 4: {
				x -= 2;
				y += 2;
				break;
			}
			}
			return quadrant;
		}

		public int getIndex(int x, int y) {
			return x + y * (screenWidth / tileSize);
		}

		public boolean loadFile(Uri dataUri) {
			// Log.d(TAG, "loadBackground(): " + dataUri.toString());
			try {
				if (bitmapSave != null) {
					bitmapSave.recycle();
					bitmapSave = null;
				}
				bitmapSave = Media.getBitmap(contentResolver, dataUri);
				int width = bitmapSave.getWidth();
				int height = bitmapSave.getHeight();
				int newHeight = 320;
				int newWidth = 430;
				if (width < height) {
					Matrix matrix = new Matrix();
					matrix.postRotate(90);
					matrix.postScale(newWidth / width, newHeight / height);
					bitmapSave = Bitmap.createBitmap(bitmapSave, 0, 0, width,
							height, matrix, true);

				}

				for (int y = 0; y < rows; y++) {

					for (int x = 0; x < cols; x++) {

						int offset = 6;
						if (y % 2 == 1)
							offset = 12;

						int pixel = bitmapSave.getPixel(x * tileSize + offset,
								y * tileSize + 3);
						int r, g, b = 0;
						r = Color.red(pixel);
						g = Color.green(pixel);
						b = Color.blue(pixel);
						int discoveredColor = R.drawable.empty;

						if (r == 41 && g == 40 && b == 33)
							discoveredColor = R.drawable.empty;
						if (r == 231 && g == 32 && b == 33)
							discoveredColor = R.drawable.red;
						if (r == 33 && g == 231 && b == 33)
							discoveredColor = R.drawable.green;
						if (r == 33 && g == 150 && b == 231)
							discoveredColor = R.drawable.blue;
						if (r == 231 && g == 32 && b == 214)
							discoveredColor = R.drawable.pink;
						if (r == 156 && g == 32 && b == 231)
							discoveredColor = R.drawable.violet;
						if (r == 231 && g == 162 && b == 33)
							discoveredColor = R.drawable.orange;
						if (r == 231 && g == 223 && b == 33)
							discoveredColor = R.drawable.yellow;
						if (r == 239 && g == 239 && b == 239)
							discoveredColor = R.drawable.white;

						Peg p = board.get(getIndex(x, y));
						p.currentColor = discoveredColor;
						invalidate();
//						 Log.i(TAG, String.format(
//						 "offset:%d x:%dy:%d index:%d pixelAt r:%dg:%db:%d:a:%d"
//						 , offset,
//						 x * tileSize + offset, y * tileSize + 3, getIndex(x,
//						 y),
//						 Color.red(pixel), Color.green(pixel), Color
//						 .blue(pixel), Color.alpha(pixel)));
					}
				}
				return true;
			} catch (IOException e) {
				return false;
			}

		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			Canvas forDrawing = new Canvas(bitmap);
			forDrawing.drawColor(Color.BLACK);
			for (int y = 0; y < rows; y++) {
				for (int x = 0; x < cols; x++) {
					int index = getIndex(x, y);
					int offset = 0;
					if (y % 2 == 1) {
						offset = tileSize / 2;
					}
					Peg peg = board.get(index);
					Drawable pegDrawable = null;
					int drawableIndex = 0;
					switch (peg.currentColor) {
					case R.drawable.empty: {
						drawableIndex = 0;
						break;
					}
					case R.drawable.red: {
						drawableIndex = 1;
						break;
					}
					case R.drawable.green: {
						drawableIndex = 2;
						break;
					}
					case R.drawable.blue: {
						drawableIndex = 3;
						break;
					}
					case R.drawable.pink: {
						drawableIndex = 4;
						break;
					}
					case R.drawable.violet: {
						drawableIndex = 5;
						break;
					}
					case R.drawable.orange: {
						drawableIndex = 6;
						break;
					}
					case R.drawable.yellow: {
						drawableIndex = 7;
						break;
					}
					case R.drawable.white: {
						drawableIndex = 8;
						break;
					}
					}								
					if (!peg.targeted) {
						pegDrawable = palette[drawableIndex];
					} else {
						pegDrawable = recticle;
					}

					int realX = x * tileSize + offset;
					int realY = y * tileSize;
					pegDrawable.setBounds(realX, realY, realX + tileSize, realY
							+ tileSize);
					pegDrawable.draw(forDrawing);
				}
			}
			canvas.drawBitmap(bitmap, matrix, paint);
		}

		void clearTargets() {

		}

	}

}