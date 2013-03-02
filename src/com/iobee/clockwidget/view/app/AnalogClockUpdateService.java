package com.iobee.clockwidget.view.app;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class AnalogClockUpdateService extends Service{
	private static final String TAG = AnalogClockUpdateService.class.getName();

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_TIME_TICK);
		registerReceiver(mTimePickerBroadcast, intentFilter);
		
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.i(TAG, "-->onDestroy");
		unregisterReceiver(mTimePickerBroadcast);
		Intent intentService = new Intent(getApplicationContext(), AnalogClockUpdateService.class);
		startService(intentService);
		
		super.onDestroy();
	}
	
	private BroadcastReceiver mTimePickerBroadcast = new BroadcastReceiver() {  
		
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			Log.d(TAG, "-->mTimerPickerBroadcast");
			//AnalogClockProvider.updateWidget(arg0, 0);
		}  
    };
}
