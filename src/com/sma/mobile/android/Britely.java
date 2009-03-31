package com.sma.mobile.android;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.ImageView.ScaleType;
import android.widget.TableRow.LayoutParams;

public class Britely extends Activity {

	public int activeColor = R.drawable.red;
	public int currentX = 0;
	public int currentY = 0;
	public int tileSize = 18; 
	public int screenWidth;
    public int screenHeight;
    public List<Peg> board;
	
	public OnTouchListener imageViewToucher = new OnTouchListener() {

		public boolean onTouch(View target, MotionEvent motionEvent) {
			if (motionEvent.getEdgeFlags() != 0) {
				return false;
			}
			
			int x = (int)(motionEvent.getX() / tileSize);				
			int y = (int)(motionEvent.getY() / tileSize);		
			int index = x + y * (screenWidth / tileSize);

			Peg p = board.get(index);			
			
			switch(motionEvent.getAction()) {
			case MotionEvent.ACTION_MOVE: {
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        final Window window = getWindow();
        screenWidth = window.getWindowManager().getDefaultDisplay().getWidth();
        screenHeight = window.getWindowManager().getDefaultDisplay().getHeight();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);         
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.main);
                
        int rows = (screenHeight / tileSize);
        int cols = screenWidth / tileSize;
        
        board = new ArrayList<Peg>();
        
        TableLayout tl = (TableLayout)findViewById(R.id.myTableLayout);
        tl.setOnTouchListener(imageViewToucher);
        
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
				board.add(index, p);				
        		
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
      
}