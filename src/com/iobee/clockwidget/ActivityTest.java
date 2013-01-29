package com.iobee.clockwidget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ActivityTest extends Activity{
	private final static String TAG = ActivityTest.class.getName();
	
	private LinearLayout layoutBox;
	
	private Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.clock_option_dial);
		mContext = this;
		
		layoutBox = (LinearLayout)findViewById(R.id.layout_box);
		ImageView v = new ImageView(mContext);
		v.setBackgroundResource(R.drawable.clock_dial);
		ImageView v1 = new ImageView(mContext);
		v1.setBackgroundResource(R.drawable.clock_dial);
		ImageView v2 = new ImageView(mContext);
		v2.setBackgroundResource(R.drawable.clock_dial);
		ImageView v3 = new ImageView(mContext);
		v3.setBackgroundResource(R.drawable.clock_dial);
		layoutBox.addView(v);
		layoutBox.addView(v1);
		layoutBox.addView(v2);
		layoutBox.addView(v3);
	}
}
