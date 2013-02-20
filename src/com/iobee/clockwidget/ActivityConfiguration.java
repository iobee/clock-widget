package com.iobee.clockwidget;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.iobee.clockwidget.tool.DrawableUtils;
import com.iobee.clockwidget.view.AnalogClock;

public class ActivityConfiguration extends Activity {
	private static final int PICK_FROM_CAMERA = 0;
	private static final int PICK_FROM_FILE = 1;
	private static final int CROP_FROM_CAMERA = 3;
	
	private static final String TAG = ActivityConfiguration.class.getName();
	
	private AnalogClock analogClock;
	private HorizontalScrollView vBoxHorinzon;
	private LinearLayout vBoxDial;
	private Uri mImageCaptureUri;

	private int viewNum = 0; // 表示当前已经添加了多少view了。

	private Context mContext;
	private DrawableUtils drawableUtils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;

		analogClock = (AnalogClock) findViewById(R.id.analogClock);
		vBoxHorinzon = (HorizontalScrollView) findViewById(R.id.viewBox_horinzon);
		vBoxDial = (LinearLayout) findViewById(R.id.viewBox_dial);
		
		drawableUtils = new DrawableUtils(this);

		drawableUtils.addAssetsDrawableToBox(vBoxDial, null);
		
		//TODO:move this function to DrawableUtils
		vBoxDial.addView(createAddButton());
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
				Bitmap photo = extras.getParcelable("data");
				
				File testFile = new File("/sdcard/test.png");		
				try {
					testFile.createNewFile();
					OutputStream os = new BufferedOutputStream(new FileOutputStream(testFile));
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
				
				vBoxDial.addView(drawableUtils.createImageView(new BitmapDrawable(photo)));
			}

			File f = new File(mImageCaptureUri.getPath());

			if (f.exists())
				f.delete();

			break;

		}
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
			intent.putExtra("outputX", convertDpToPixel(110));
			intent.putExtra("outputY", convertDpToPixel(110));
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("scale", true);
			intent.putExtra("return-data", true);
			//intent.putExtra("circleCrop", "true");
			
			Intent i = new Intent(intent);
			ResolveInfo res = list.get(1);

			i.setComponent(new ComponentName(res.activityInfo.packageName,
					res.activityInfo.name));

			startActivityForResult(i, CROP_FROM_CAMERA);
		}
	}
	
	private float convertDpToPixel(float dp){
	    Resources resources = mContext.getResources();
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    Log.i(TAG, metrics.densityDpi+ "");
	    float px = dp * (metrics.densityDpi/160f);
	    return px;
	}

}
