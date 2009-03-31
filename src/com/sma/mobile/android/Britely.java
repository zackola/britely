package com.sma.mobile.android;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
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
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.ImageView.ScaleType;
import android.widget.TableRow.LayoutParams;

public class Britely extends Activity {

	public static int activeColor = R.drawable.red;
	public int currentX = 0;
	public int currentY = 0;
	public static int tileSize = 18; 
	public static int screenWidth;
    public static int screenHeight;
    
    public static final int MY_TABLE_LAYOUT_ID = 100;
    public static final int BOARD_ID = 101;
    
    BriteView briteView;
	
	public OnTouchListener imageViewToucher = new OnTouchListener() {

		public boolean onTouch(View target, MotionEvent motionEvent) {
			if (motionEvent.getEdgeFlags() != 0) {
				return false;
			}
			
			int x = (int)(motionEvent.getX() / tileSize);				
			int y = (int)(motionEvent.getY() / tileSize);		
			int index = x + y * (screenWidth / tileSize);

			briteView.recticleX = x;
			briteView.recticleY = y;
			Peg p = briteView.board.get(index);			
			
			switch(motionEvent.getAction()) {
			case MotionEvent.ACTION_MOVE: {
				for (Peg _p : briteView.board) {
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        final Window window = getWindow();
        screenWidth = window.getWindowManager().getDefaultDisplay().getWidth();
        screenHeight = window.getWindowManager().getDefaultDisplay().getHeight();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);         
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        briteView = new BriteView(this);
        briteView.setFocusable(true);
        briteView.setFocusableInTouchMode(true);
        setContentView(briteView);
                
        int rows = screenHeight / tileSize;
        int cols = screenWidth / tileSize;
        
        
        TableLayout tl = (TableLayout)findViewById(MY_TABLE_LAYOUT_ID);
        briteView.setOnTouchListener(imageViewToucher);
        
        for(int y = 0; y < rows; y++) {
        	
        	TableRow tr = new TableRow(this);

        	for(int x = 0; x < cols; x++) {
				
				int index = x + y*cols;
				ImageView tile = new ImageView(this);
				tile.setImageResource(R.drawable.empty);
				tile.setId(index);
				tile.setAdjustViewBounds(true);
				tile.setMaxHeight(tileSize);
				tile.setMaxWidth(tileSize);
				tile.setScaleType(ScaleType.FIT_CENTER);
        		LayoutParams lp = new LayoutParams();
        		lp.width = LayoutParams.WRAP_CONTENT;
        		lp.height = LayoutParams.WRAP_CONTENT;
        		
				if (y % 2 == 1) {
	        		lp.setMargins(0, 0, -tileSize, 0);
				}
        		tile.setLayoutParams(lp);
        		tr.addView(tile, lp);
        		
				Peg p = new Peg(tile, index);
				briteView.board.add(index, p);				
        		
			}
        	tl.addView(tr,new TableLayout.LayoutParams());        
        }
    }
   
    
   static private final int MENU_ITEM_PALLETE = 0;
   static private final int MENU_ITEM_SHARE = 1;

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
	   
	   SubMenu colorsMenu = _menu.addSubMenu(0, MENU_ITEM_PALLETE, 1, "Colors");
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
	   
	   MenuItem shareMenuItem = _menu.add(0, MENU_ITEM_SHARE, 2, "Share");
	   shareMenuItem.setIcon(android.R.drawable.ic_menu_send);
	   return true;
   
   }
   
   @Override
   public void onConfigurationChanged(Configuration newConfig) {
     //ignore orientation change
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
	   case MENU_ITEM_SHARE: {
		   return true;
	   	}
	   }
	   return false;
   }
   
   private static class BriteView extends LinearLayout {

	    public List<Peg> board;
	   	int recticleX = 1;
	   	int recticleY = 1;
	   
		public BriteView(Context context) {
			super(context);			
	        this.board = new ArrayList<Peg>();			
			this.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			this.setOrientation(VERTICAL);
			this.setId(BOARD_ID);
			TableLayout tableLayout = new TableLayout(this.getContext());
    		LayoutParams lp = new LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT);
    		tableLayout.setId(MY_TABLE_LAYOUT_ID);
			tableLayout.setLayoutParams(lp);
			tableLayout.setBackgroundColor(Color.BLACK);
			this.addView(tableLayout);
		}
		
		@Override
		public boolean onTrackballEvent(MotionEvent event) {
			super.onTrackballEvent(event);
			Log.i("onTrackballEvent", event.toString());
			
			switch (event.getAction()){
			case MotionEvent.ACTION_MOVE: {
				
				int x = (int)(event.getX());				
				int y = (int)(event.getY());
				if (x < 0 && recticleX > 0) {
					Log.i("TRACK", "Decrementing X");
					recticleX -= 1;
				} else if (x > 0 && recticleX < screenWidth) {
					Log.i("TRACK", "Incrementing X");
					recticleX += 1;
				} 
				if (y < 0 && recticleY > 0) {
					Log.i("TRACK", "Decrementing Y");					
					recticleY -= 1;
				} else if (y > 0 && recticleY < screenHeight) {
					Log.i("TRACK", "Incrementing Y");					
					recticleY += 1;
				}
					
				int index = recticleX + recticleY * (screenWidth / tileSize);

				Peg p = board.get(index);
				
				for (Peg _p : board) {
					_p.tile.setImageResource(_p.currentColor);
				}				
				p.tile.setImageResource(R.drawable.recticle);
				Log.i("RECT", String.valueOf(recticleX) + "," + String.valueOf(recticleY));
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
			Log.i("onKeyDown", event.toString());
			return true;
		}
   }
      
}