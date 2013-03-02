package com.iobee.clockwidget;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.iobee.clockwidget.base.AnalogInformation;
import com.iobee.clockwidget.base.InfoDrawable;
import com.iobee.clockwidget.tool.DrawableUtils;
import com.iobee.clockwidget.view.AnalogClock;
import com.iobee.clockwidget.view.app.AnalogClockProvider;

public class ActivityConfiguration extends Activity {
	private static final String TAG = ActivityConfiguration.class.getName();

	private static final int PICK_FROM_CAMERA = 0;
	private static final int PICK_FROM_FILE = 1;
	private static final int CROP_FROM_CAMERA = 3;

	private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

	private AnalogClock analogClock;
	private HorizontalScrollView vBoxHorinzon;
	private LinearLayout vBoxDial;
	private Uri mImageCaptureUri;

	private int viewNum = 0; // 表示当前已经添加了多少view了。

	private Context mContext;
	private DrawableUtils drawableUtils;

	private SharedPreferences sp;
	private SharedPreferences.Editor et;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
		}

		mContext = this;

		analogClock = (AnalogClock) findViewById(R.id.analogClock);
		vBoxHorinzon = (HorizontalScrollView) findViewById(R.id.viewBox_horinzon);
		vBoxDial = (LinearLayout) findViewById(R.id.viewBox_dial);

		drawableUtils = new DrawableUtils(this);

		// TODO:move this function to DrawableUtils
		vBoxDial.addView(createAddButton());
		
		sp = getSharedPreferences(
				AnalogInformation.ANALOG_NAME, MODE_PRIVATE);
		et = sp.edit();
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
		switch (item.getItemId()) {
		case R.id.menu_settings_dial:
			vBoxHorinzon.setVisibility(View.VISIBLE);
			drawableUtils.addDialDrawable(vBoxDial,
					new DrawableUtils.OnClickListener() {

						@Override
						public void onClick(View v, InfoDrawable d) {
							// TODO Auto-generated method stub
							Log.i(TAG, "-->onClick");
							analogClock.setDial(d.getDrawable());
							et.putString(
									AnalogInformation.ANALOG_DRAWABLE_DIAL, d
											.getUri().toString());
							et.commit();
							
							AnalogClockProvider.updateWidget(mContext,
									appWidgetId);
						}
					});
			break;
		case R.id.menu_settings_hand:
			vBoxHorinzon.setVisibility(View.VISIBLE);
			drawableUtils.addHandDrawable(vBoxDial,
					new DrawableUtils.OnClickListener() {

						@Override
						public void onClick(View v, InfoDrawable d) {
							// TODO Auto-generated method stub
							InfoDrawable hourInfoDrawable = null;
							InfoDrawable minuteInfoDrawable = null;
							try {
								hourInfoDrawable = DrawableUtils
										.getHourHandInfoDrawable(mContext,
												d.getUri());
								minuteInfoDrawable = DrawableUtils
										.getMinuteHandInfoDrawable(mContext,
												d.getUri());
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							analogClock.setMinuteHand(minuteInfoDrawable
									.getDrawable());
							analogClock.setHourHand(hourInfoDrawable
									.getDrawable());
							
							et.putString(AnalogInformation.ANALOG_DRAWABLE_HOUR,
									hourInfoDrawable.getUri().toString());
							et.putString(AnalogInformation.ANALOG_DRAWABLE_MINUTE,
									minuteInfoDrawable.getUri().toString());
							et.commit();
							
							AnalogClockProvider.updateWidget(mContext,
									appWidgetId);
						}
					});
			break;
		default:
			break;
		}

		return true;
	}

	private Button createAddButton() {
		Button buttonAdd = new Button(mContext);
		buttonAdd.setBackgroundResource(R.drawable.clock_hand_hour);

		buttonAdd.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// addSDCardDrawableToBox("sdcard/test.jpg");
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

				mImageCaptureUri = Uri.fromFile(new File(Environment
						.getExternalStorageDirectory(), "tmp_avatar_"
						+ String.valueOf(System.currentTimeMillis()) + ".jpg"));

				intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
						mImageCaptureUri);

				try {
					intent.putExtra("return-data", true);

					startActivityForResult(intent, PICK_FROM_CAMERA);
				} catch (ActivityNotFoundException e) {
					e.printStackTrace();
				}
			}
		});

		return buttonAdd;
	}

	@SuppressWarnings("unused")
	private List<Drawable> getDrawableFromSDCard(String path) {
		return null;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return super.onTouchEvent(event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode != RESULT_OK)
			return;

		switch (requestCode) {
		case PICK_FROM_CAMERA:
			doCrop();

			break;

		case PICK_FROM_FILE:
			mImageCaptureUri = data.getData();

			doCrop();

			break;

		case CROP_FROM_CAMERA:
			Bundle extras = data.getExtras();

			if (extras != null) {
				Bitmap photo = drawableUtils.createCircleAvatar((Bitmap) extras
						.getParcelable("data"));

				InfoDrawable infoDrawable = saveCustiomDrawable(photo);

				vBoxDial.addView(drawableUtils.createImageView(infoDrawable,
						null));
			}

			File f = new File(mImageCaptureUri.getPath());

			if (f.exists())
				f.delete();

			break;

		}
	}

	/**
	 * @param photo
	 */
	private InfoDrawable saveCustiomDrawable(Bitmap photo) {
		File externFile = new File(Environment.getExternalStorageDirectory(),
				"clock widget");
		if (!externFile.exists()) {
			if (!externFile.mkdir()) {
				Log.d(TAG, "failed to create clock widget directory");
			}
		}
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile = new File(externFile.getPath() + File.separator + "PIC"
				+ timeStamp + ".bmp");

		try {
			mediaFile.createNewFile();
			OutputStream os = new BufferedOutputStream(new FileOutputStream(
					mediaFile));
			photo.compress(Bitmap.CompressFormat.PNG, 0, os);
			os.flush();
			os.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new InfoDrawable(new BitmapDrawable(getResources(), photo),
				Uri.fromFile(mediaFile));
	}

	private void doCrop() {
		// TODO Auto-generated method stub
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setType("image/*");

		List<ResolveInfo> list = getPackageManager().queryIntentActivities(
				intent, 0);

		int size = list.size();

		if (size == 0) {
			Toast.makeText(this, "Can not find image crop app",
					Toast.LENGTH_SHORT).show();

			return;
		} else {
			intent.setData(mImageCaptureUri);
			intent.putExtra("outputX", convertDpToPixel(150));
			intent.putExtra("outputY", convertDpToPixel(150));
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("scale", true);
			intent.putExtra("return-data", true);
			// intent.putExtra("circleCrop", "true");

			Intent i = new Intent(intent);
			ResolveInfo res = list.get(0);

			i.setComponent(new ComponentName(res.activityInfo.packageName,
					res.activityInfo.name));

			startActivityForResult(i, CROP_FROM_CAMERA);
		}
	}

	private float convertDpToPixel(float dp) {
		Resources resources = mContext.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		Log.i(TAG, metrics.densityDpi + "");
		float px = dp * (metrics.densityDpi / 160f);
		return px;
	}

}
