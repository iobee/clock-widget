package com.iobee.clockwidget;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.iobee.clockwidget.view.AnalogClock;

public class ActivityMain extends Activity {
	private AnalogClock analogClock;
	private HorizontalScrollView vBoxHorinzon;
	private LinearLayout vBoxDial;
	
	private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        
        analogClock = (AnalogClock)findViewById(R.id.analogClock);
        vBoxHorinzon = (HorizontalScrollView)findViewById(R.id.viewBox_horinzon);
        vBoxDial = (LinearLayout)findViewById(R.id.viewBox_dial);
        
        addAssetsDrawableToBox();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	// TODO Auto-generated method stub
    	vBoxHorinzon.setVisibility(View.VISIBLE);
    	return true;
    }
    
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN) 
    @SuppressWarnings("deprecation")
	private ImageView createImageView(final Drawable drawable){
		ImageView v = new ImageView(mContext);
		int sdk = android.os.Build.VERSION.SDK_INT;
		if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN){
			v.setBackgroundDrawable(drawable);
		} else {
			v.setBackground(drawable);
		}
		
		v.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(analogClock != null){
					analogClock.setDial(drawable);
				}
			}
		});

		return v;
	}
    
    private List<Drawable> getDrawableFromAssets(String path){
    	List<Drawable> drawables = new ArrayList<Drawable>();
    	
    	try {
    		AssetManager am = mContext.getResources().getAssets();
			String[] files = am.list(path);
			for(String file : files){
				drawables.add(Drawable.createFromStream(am.open(path + "/" +file), null));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		return drawables;
    }
    
    private void addAssetsDrawableToBox(){
    	List<Drawable> drawables = getDrawableFromAssets("dial");
    	for(Drawable dial : drawables){
    		vBoxDial.addView(createImageView(dial));
    	}
    }
    
    @SuppressWarnings("unused")
	private List<Drawable> getDrawableFromSDCard(String path){
    	return null;
    }
    
}
