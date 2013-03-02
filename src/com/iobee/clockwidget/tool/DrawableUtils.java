package com.iobee.clockwidget.tool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.goodev.cropimage.R;

import com.iobee.clockwidget.base.AnalogInformation;
import com.iobee.clockwidget.base.InfoDrawable;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class DrawableUtils {
	protected static final String TAG = DrawableUtils.class.getName();
	private Context mContext;
	private int hasAddedView = 0;
	
	public interface OnClickListener{
		public void onClick(View v, InfoDrawable d);
	}
	
	public DrawableUtils(Context mContext) {
		this.mContext = mContext;
	}
	
	public DrawableUtils(Context mContext, LinearLayout viewBox) {
		this.mContext = mContext;
	}
	/**
	 * add dial drawable form sdcard and assets to view box;
	 * @param viewBox the viewBox which will someviews add to;
	 * @param ocls listen to the view'click;
	 */
	public void addDialDrawable(LinearLayout viewBox, OnClickListener ocls){
		viewBox.removeAllViews();
		addSDCardDrawableToBox(viewBox, ocls);
		addDialFromAssets(viewBox, ocls);
	}
	
	private void addSDCardDrawableToBox(LinearLayout viewBox, OnClickListener ocls){
		List<InfoDrawable> drawables = getDrawablesFromSDCard();
		for (InfoDrawable dial : drawables) {
			viewBox.addView(createImageView(dial, ocls));
		}
	}
	
	private void addDialFromAssets(LinearLayout viewBox, OnClickListener ocls) {
		List<InfoDrawable> drawables = getDrawableFromAssets("dial");
		for (InfoDrawable dial : drawables) {
			viewBox.addView(createImageView(dial, ocls));
		}
	}
	
	public void addHandDrawable(LinearLayout viewBox, OnClickListener ocls){
		viewBox.removeAllViews();
		addHandDrawableFromAssets(viewBox, ocls);
		
	}
	
	private void addHandDrawableFromAssets(LinearLayout viewBox, OnClickListener ocls) {
		viewBox.removeAllViews();
		List<InfoDrawable> drawables = getDrawableFromAssets("hand");
		for (InfoDrawable dial : drawables) {
			hasAddedView++;
			viewBox.addView(createImageView(dial, ocls));
		}
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@SuppressWarnings("deprecation")
	public ImageView createImageView(final InfoDrawable drawable,final OnClickListener ocls) {		
		ImageView v = new ImageView(mContext);
		int sdk = android.os.Build.VERSION.SDK_INT;
		if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			v.setBackgroundDrawable(drawable.getDrawable());
		} else {
			v.setBackground(drawable.getDrawable());
		}

		v.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub				
				if(ocls != null){
					ocls.onClick(v, drawable);
				}
				
				Log.i(TAG, drawable.getUri().getSchemeSpecificPart());
			}
		});

		return v;
	}
	
	private List<InfoDrawable> getDrawableFromAssets(String path) {
		List<InfoDrawable> infoDrawables = new ArrayList<InfoDrawable>();
	
		try {
			AssetManager am = mContext.getResources().getAssets();
			String[] files = am.list(path);
			
			path = path + File.separator;
			for (String file : files) {
				StringBuilder sb = new StringBuilder(path);
				sb.append(file);
				BitmapDrawable bitmapDrawable = new BitmapDrawable(mContext.getResources(), am.open(sb.toString()));
				bitmapDrawable.setTargetDensity(mContext.getResources().getDisplayMetrics());
				InfoDrawable infoDrawable = new InfoDrawable(bitmapDrawable, 
						Uri.fromParts(InfoDrawable.SCHEME_ASSET, sb.toString(), null));
				
				infoDrawables.add(infoDrawable);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return infoDrawables;
	}
	
	private List<InfoDrawable> getDrawablesFromSDCard(){
		List<InfoDrawable> infoDrawables = new ArrayList<InfoDrawable>();
		File drawablesFiles = new File(Environment.getExternalStorageDirectory(), "clock widget");
		File[] files = drawablesFiles.listFiles();
		
		if(files == null){
			return null;
		}
		
		for(File file : files){
			BitmapDrawable bitmapDrawable = new BitmapDrawable(mContext.getResources(), file.getPath());
			bitmapDrawable.setTargetDensity(mContext.getResources().getDisplayMetrics());
			infoDrawables.add(new InfoDrawable(
					bitmapDrawable, 
					Uri.fromFile(file)));
			
			Log.d(TAG, Uri.fromFile(file).toString());
		}
		
		return infoDrawables;
	}
	
	public int getViewNum(){
		return hasAddedView;
	}
	
	public Bitmap createCircleAvatar(Bitmap bitmap){
		Bitmap bitmapTemp = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bitmapTemp);
		Rect dst = new Rect(0, 0, 300, 300);
		c.drawBitmap(bitmap, null, dst, null);
		
		c.save();
		Path p = new Path();
		p.addCircle(300 / 2F, 300 / 2F, 300 / 2F, Path.Direction.CW);
		c.clipPath(p, Region.Op.DIFFERENCE);
		c.drawColor(0x00000000, PorterDuff.Mode.CLEAR);
		c.restore();
		
		Bitmap clock_dial_base = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.clock_face_base);
		c.drawBitmap(clock_dial_base, null, dst, null);
		
		return bitmapTemp;
	}
	
	public static InfoDrawable getHourHandInfoDrawable(Context context, Uri mUri) throws IOException{
		String prefixUri = mUri.getSchemeSpecificPart().replace("hand/clock_hand", "hour hand/clock_hand_hour");
		Uri uri = Uri.fromParts("assets", prefixUri, null);
		AssetManager am = context.getResources().getAssets();
		Drawable drawable = Drawable.createFromStream(am.open(prefixUri), null);
		InfoDrawable infoDrawable = new InfoDrawable(drawable, uri);
		return infoDrawable;
	}
	
	public static InfoDrawable getMinuteHandInfoDrawable(Context context, Uri mUri) throws IOException{
		String prefixUri = mUri.getSchemeSpecificPart().replace("hand/clock_hand", "minute hand/clock_hand_minute");
		Uri uri = Uri.fromParts("assets", prefixUri, null);
		AssetManager am = context.getResources().getAssets();
		Drawable drawable = Drawable.createFromStream(am.open(prefixUri), null);
		InfoDrawable infoDrawable = new InfoDrawable(drawable, uri);
		return infoDrawable;
	}
}