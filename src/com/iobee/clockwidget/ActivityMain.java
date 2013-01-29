package com.iobee.clockwidget;

import java.io.IOException;
import java.io.InputStream;

import com.iobee.clockwidget.view.AnalogClock;

import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ActivityMain extends Activity {
	private AnalogClock analogClock;
	
	private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        
        analogClock = (AnalogClock)findViewById(R.id.analogClock);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
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
    
}
