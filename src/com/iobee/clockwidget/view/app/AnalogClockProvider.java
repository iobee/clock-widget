package com.iobee.clockwidget.view.app;

import java.util.Calendar;

import com.iobee.clockwidget.view.AnalogClock;

import android.R;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View.MeasureSpec;
import android.widget.RemoteViews;

public class AnalogClockProvider extends AppWidgetProvider {

	@SuppressWarnings("unused")
	private static final String TAG = "AnalogClockProvider";

	public static String ACTION_CLOCK_UPDATE = "com.iobee.clockwidget.ACTION_CLOCK_UPDATE";

	private AnalogClock analogClock;

	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);

		startTicking(context);
	}

	@Override
	public void onDisabled(Context context) {
		// TODO Auto-generated method stub
		super.onDisabled(context);
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
	 * Creates an intent to update the clock(s).
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

		if (analogClock == null) {
			analogClock = new AnalogClock(context);
			analogClock.updateClock();

			analogClock.setDrawingCacheEnabled(true);
			analogClock.measure(
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			analogClock.layout(0, 0, analogClock.getMeasuredWidth(),
					analogClock.getMeasuredHeight());

			analogClock.buildDrawingCache();
		} else {
			analogClock.updateClock();
		}

		final Bitmap cached = analogClock.getDrawingCache(true);
		if (cached != null) {
			rv.setImageViewBitmap(com.iobee.clockwidget.R.id.analogClock,
					cached);
		}

		appWidgetManager.updateAppWidget(appWidgetIds, rv);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);

		final String action = intent.getAction();
		if (ACTION_CLOCK_UPDATE.equals(action)
				|| Intent.ACTION_TIMEZONE_CHANGED.equals(action)
				|| Intent.ACTION_TIME_CHANGED.equals(action)) {
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
}
