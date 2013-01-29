package com.iobee.clockwidget;

import java.io.IOException;
import java.io.InputStream;

import com.iobee.clockwidget.view.AnalogClock;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class ActivityMain extends Activity {
	private Button refreshView;
	private AnalogClock analogClock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        refreshView = (Button)findViewById(R.id.button_RefreshView);
        analogClock = (AnalogClock)findViewById(R.id.analogClock);
        
        refreshView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				testSetDial();
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    private void testSetDial(){
    	try {
			InputStream is = getResources().getAssets().open("clock_dial.png");
			Drawable drawableDial = Drawable.createFromStream(is, null);
	    	analogClock.setDial(drawableDial);			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
}
