package com.iobee.clockwidget.view.app;

import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import com.iobee.clockwidget.ActivityConfiguration;
import com.iobee.clockwidget.R;
import com.iobee.clockwidget.base.AnalogInformation;
import com.iobee.clockwidget.base.InfoDrawable;
import com.iobee.clockwidget.view.AnalogClock;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View.MeasureSpec;
import android.widget.RemoteViews;

public class AnalogClockProvider extends AppWidgetProvider {
	private static final String TAG = "AnalogClockProvider";

	public static String ACTION_CLOCK_UPDATE = "com.iobee.clockwidget.ACTION_CLOCK_UPDATE";
	
	private Intent intentService;

	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);
		Log.i(TAG, "-->onEnabled");
		//startTicking(context);
		
		//intentService = new Intent(context, AnalogClockUpdateService.class);
		//context.startService(intentService);
		IntentFilter i = new IntentFilter(Intent.ACTION_TIME_TICK);
		context.getApplicationContext().registerReceiver(this, i);
	}

	@Override
	public void onDisabled(Context context) {
		// TODO Auto-generated method stub
		super.onDisabled(context);
		//context.stopService(intentService);
		//stopTicking(context);
	}

	/**
	 * @param context
	 */
	private void stopTicking(Context context) {
		final AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		alarmManager.cancel(createUpdate(context));
	}

	/**
	 * Schedules an alarm to update the clock every minute, at the top of the
	 * minute.
	 * 
	 * @param context
	 */
	private void startTicking(Context context) {
		final AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		// schedules updates so they occur on the top of the minute
		final Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		c.add(Calendar.MINUTE, 1);
		alarmManager.setRepeating(AlarmManager.RTC, c.getTimeInMillis(),
				1000 * 60, createUpdate(context));
	}

	/**
	 * to update the clock(s).
	 * 
	 * @param context
	 * @return
	 */
	private PendingIntent createUpdate(Context context) {
		return PendingIntent.getBroadcast(context, 0, new Intent(
				ACTION_CLOCK_UPDATE), PendingIntent.FLAG_UPDATE_CURRENT);
	}
	

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		Log.i(TAG, "onUpdate");

		RemoteViews rv = new RemoteViews(context.getPackageName(),
				com.iobee.clockwidget.R.layout.layout_analogclock);
		final Bitmap cached = getAnalogClockBitmapCached(context);
		if (cached != null) {
			rv.setImageViewBitmap(com.iobee.clockwidget.R.id.analogClock,
					cached);
		}

		configureDial(context, rv);
		appWidgetManager.updateAppWidget(appWidgetIds, rv);
	}

	/**
	 * @param context
	 */
	private Bitmap getAnalogClockBitmapCached(Context context) {
		Log.i(TAG, "analogClock");
		
		AnalogClock analogClock = new AnalogClock(context);
		analogClock.setDial(getDialCustomDrawable(context));
		analogClock.updateClock();
		
		//configure view, then you can get the view drawingcacheEnabled. 
		analogClock.setDrawingCacheEnabled(true);
		analogClock.measure(
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		analogClock.layout(0, 0, analogClock.getMeasuredWidth(),
				analogClock.getMeasuredHeight());
		
		analogClock.buildDrawingCache();
		
		return analogClock.getDrawingCache(true);
	}

	/**
	 * 根据Uri获取用户设置的drawable资源。
	 * @param context
	 * @return
	 */
	private Drawable getDialCustomDrawable(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				AnalogInformation.ANALOG_NAME, Context.MODE_PRIVATE);
		Uri uri = Uri.parse(sp.getString(
				AnalogInformation.ANALOG_DRAWABLE_DIAL, "test"));
		BitmapDrawable bitmapDrawable = null;
		if (uri.getScheme().equals(InfoDrawable.SCHEME_ASSET)) {
			AssetManager am = context.getResources().getAssets();
			try {
				bitmapDrawable = new BitmapDrawable(context.getResources(), am.open(uri.getSchemeSpecificPart()));
				bitmapDrawable.setTargetDensity(context.getResources().getDisplayMetrics());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (uri.getScheme().equals(InfoDrawable.SCHEME_FILE)) {
			bitmapDrawable = new BitmapDrawable(context.getResources(), uri.getPath());
		}
		return bitmapDrawable;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
		Log.d(TAG, "-->onReceive");

		final String action = intent.getAction();
		if (ACTION_CLOCK_UPDATE.equals(action)
				|| Intent.ACTION_TIMEZONE_CHANGED.equals(action)
				|| Intent.ACTION_TIME_CHANGED.equals(action) 
				|| Intent.ACTION_TIME_TICK.equals(action)) {
			final ComponentName appWidgets = new ComponentName(
					context.getPackageName(), getClass().getName());

			Log.i(TAG, context.getPackageName());

			final AppWidgetManager appWidgetManager = AppWidgetManager
					.getInstance(context);
			final int ids[] = appWidgetManager.getAppWidgetIds(appWidgets);
			if (ids.length > 0) {
				onUpdate(context, appWidgetManager, ids);
			}
		}
	}

	private void configureDial(Context context, RemoteViews rv) {
		Intent configIntent = new Intent(context, ActivityConfiguration.class);
		PendingIntent dialPendingIntent = PendingIntent.getActivity(context, 0,
				configIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		rv.setOnClickPendingIntent(R.id.analogClock, dialPendingIntent);
	}

	public static void updateWidget(Context context, int appWidgetId) {
		Intent intent = new Intent(context, AnalogClockProvider.class);
		intent.setAction(ACTION_CLOCK_UPDATE);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		context.sendBroadcast(intent);
	}
	
}
