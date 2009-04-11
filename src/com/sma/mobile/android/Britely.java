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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

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

	// Strings
	public static final String HELP_MESSAGE = "Menu for options. Trackball to move and put. Keyboard too: 1 (up) A (down) Q (left) W (right) and Spacebar to put.";

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
		Log.i(TAG, "onStart");
		mediaScanner.connect();
		Toast.makeText(this, HELP_MESSAGE, Toast.LENGTH_LONG).show();
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
		briteView.setId(BRITE_VIEW_ID);
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
	static private final int MENU_ITEM_PALLETE = 0;
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
				.addSubMenu(0, MENU_ITEM_PALLETE, 1, "Colors");
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
			Toast.makeText(this, HELP_MESSAGE, Toast.LENGTH_LONG).show();
			return true;
		}
		case MENU_ITEM_CLEAR: {
			for (PegView p : briteView.board) {
				p.currentColor = R.drawable.empty;
				p.invalidate();
			}
			return true;
		}
		case MENU_ITEM_UNDO: {
			if (lastMove[0] >= 0 && lastMove[1] >= 0) {
				PegView pv = briteView.board.get(lastMove[0]);
				pv.currentColor = lastMove[1];
				pv.invalidate();
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

		List<PegView> board;
		int recticleX = 1;
		int recticleY = 1;
		int rows;
		int cols;
		boolean power = true;
		List<Integer> previouslyTargeted;
		Drawable glow;
		public Bitmap bitmap = null;
		private Canvas canvas = null;

		public BriteView(Context context) {
			super(context);

			board = new ArrayList<PegView>();
			previouslyTargeted = new ArrayList<Integer>();
			lastMove[0] = -1;
			lastMove[1] = -1;
			setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT));
			setOrientation(VERTICAL);
			setId(BOARD_ID);
			TableLayout tableLayout = new TableLayout(this.getContext());
			LayoutParams lp = new LayoutParams(
					TableLayout.LayoutParams.FILL_PARENT,
					TableLayout.LayoutParams.FILL_PARENT);
			tableLayout.setId(MY_TABLE_LAYOUT_ID);
			tableLayout.setLayoutParams(lp);
			tableLayout.setBackgroundColor(Color.BLACK);
			addView(tableLayout);

			rows = screenHeight / tileSize;
			cols = screenWidth / tileSize;

			for (int y = 0; y < rows; y++) {

				TableRow tr = new TableRow(this.getContext());
				tr.setId(1000 + y);

				for (int x = 0; x < cols; x++) {

					int index = x + y * cols;
					PegView p = new PegView(this.getContext());
					p.setImageResource(R.drawable.empty);
					p.setId(index);
					p.setAdjustViewBounds(true);
					p.setMaxHeight(tileSize);
					p.setMaxWidth(tileSize);
					p.setScaleType(ScaleType.FIT_CENTER);
					TableRow.LayoutParams lp1 = new TableRow.LayoutParams();
					lp1.width = LayoutParams.WRAP_CONTENT;
					lp1.height = LayoutParams.WRAP_CONTENT;
					if (y % 2 == 1) {
						lp1.setMargins(0, 0, -tileSize, 0);
					}
					p.setLayoutParams(lp1);
					tr.addView(p, lp1);
					board.add(index, p);
				}
				tableLayout.addView(tr, new TableLayout.LayoutParams());
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
				for (Integer i : previouslyTargeted) {
					if (i != index) {
						PegView pv = board.get(i);
						pv.targeted = false;
						pv.invalidate();
					}
				}
				PegView p = board.get(index);
				p.targeted = true;
				p.invalidate();
				previouslyTargeted.add(index);
				break;
			}
			case MotionEvent.ACTION_DOWN: {
				int index = getIndex(recticleX, recticleY);
				PegView p = board.get(index);
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

			if (keyCode == KeyEvent.KEYCODE_SPACE) {
				int index = getIndex(recticleX, recticleY);
				PegView p = board.get(index);
				lastMove[0] = index;
				lastMove[1] = p.currentColor;
				p.currentColor = activeColor;
				p.invalidate();
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
			for (Integer i : previouslyTargeted) {
				if (i != index) {
					PegView pv = board.get(i);
					pv.targeted = false;
					pv.invalidate();
				}
			}
			PegView p = board.get(index);
			p.targeted = true;
			p.invalidate();
			previouslyTargeted.add(index);
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
				PegView p = board.get(index);

				switch (motionEvent.getAction()) {
				case MotionEvent.ACTION_MOVE: {
					for (Integer i : previouslyTargeted) {
						if (i != index) {
							PegView pv = board.get(i);
							pv.targeted = false;
							pv.invalidate();
						}
					}
					recticleX = x;
					recticleY = y;
					p.targeted = true;
					p.invalidate();
					previouslyTargeted.add(index);
					break;
				}
				case MotionEvent.ACTION_UP: {
					lastMove[0] = index;
					lastMove[1] = p.currentColor;
					p.targeted = false;
					p.currentColor = activeColor;
					p.invalidate();
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
			int index = x + y * (screenWidth / tileSize);
			return index;
		}

		public boolean loadFile(Uri dataUri) {
			Log.d(TAG, "loadBackground(): " + dataUri.toString());
			try {
				if (bitmap != null) {
					bitmap.recycle();
					bitmap = null;
				}
				bitmap = Media.getBitmap(contentResolver, dataUri);
				int width = bitmap.getWidth();
				int height = bitmap.getHeight();
				int newHeight = 320;
				int newWidth = 430;
				if (width < height) {
					Matrix matrix = new Matrix();
					matrix.postRotate(90);
					matrix.postScale(newWidth / width, newHeight / height);
					bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
							matrix, true);

				}

				for (int y = 0; y < rows; y++) {

					for (int x = 0; x < cols; x++) {

						int offset = 6;
						if (y % 2 == 1)
							offset = 12;

						int pixel = bitmap.getPixel(x * tileSize + offset, y
								* tileSize + 3);
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

						PegView p = board.get(getIndex(x, y));
						p.currentColor = discoveredColor;
						p.invalidate();

						// Log.i(TAG, String.format(
						// "offset:%d x:%dy:%d index:%d pixelAt r:%dg:%db:%d:a:%d"
						// , offset,
						// x * tileSize + offset, y * tileSize + 3, getIndex(x,
						// y),
						// Color.red(pixel), Color.green(pixel), Color
						// .blue(pixel), Color.alpha(pixel)));
					}
				}
				return true;
			} catch (IOException e) {
				return false;
			}

		}

	}

}