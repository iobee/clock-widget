package com.iobee.clockwidget;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ActivityTest extends Activity{
	private final static String TAG = ActivityTest.class.getName();
	
	private ViewPager pager;
	
	private Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.clock_option_dial);
		mContext = this;
		
		pager = (ViewPager)findViewById(R.id.viewPager_Box);
		
		pager.setAdapter(new PagerAdapter() {
			
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				// TODO Auto-generated method stub
				return arg0 == arg1;
			}
			
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return 5;
			}

			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				// TODO Auto-generated method stub
				super.destroyItem(container, position, object);
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				// TODO Auto-generated method stub
				Log.i(TAG, "-->instantiateItem");
				
				ImageView v = new ImageView(mContext);
				v.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_launcher));
				container.addView(v);
				return v;
			}

			
			
		});
	}
}
