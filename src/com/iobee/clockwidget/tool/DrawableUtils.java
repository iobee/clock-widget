package com.iobee.clockwidget.tool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.iobee.clockwidget.base.AnalogInformation;
import com.iobee.clockwidget.base.InfoDrawable;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
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
	
	private SharedPreferences sp;
		
	private View.OnClickListener ocls;

	public DrawableUtils(Context mContext) {
		this.mContext = mContext;
		
		sp = mContext.getSharedPreferences(AnalogInformation.ANALOG_NAME, Context.MODE_PRIVATE);
	}
	
	public void addAssetsDrawableToBox(LinearLayout viewBox, View.OnClickListener ocls) {
		List<Drawable> drawables = getDrawableFromAssets("dial");
		for (Drawable dial : drawables) {
			hasAddedView++;
			viewBox.addView(createImageView(dial, ocls));
		}
	}
	
	public void addSDCardDrawableToBox(LinearLayout viewBox, View.OnClickListener ocls){
		List<InfoDrawable> drawables = getDrawablesFromSDCard();
		for (InfoDrawable dial : drawables) {
			hasAddedView++;
			viewBox.addView(createImageView(dial, ocls));
		}
	}
	
	public void test_addAssetsDrawableToBox(LinearLayout viewBox, View.OnClickListener ocls) {
		List<InfoDrawable> drawables = test_getDrawableFromAssets("dial");
		for (InfoDrawable dial : drawables) {
			hasAddedView++;
			viewBox.addView(createImageView(dial, ocls));
		}
	}
	
	/**
	 * 根据drawable生成响应ocls操作的ImageView;
	 * @param drawable
	 * @param ocls
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@SuppressWarnings("deprecation")
	public ImageView createImageView(final Drawable drawable, View.OnClickListener ocls) {
		this.ocls = ocls;
		
		ImageView v = new ImageView(mContext);
		int sdk = android.os.Build.VERSION.SDK_INT;
		if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			v.setBackgroundDrawable(drawable);
		} else {
			v.setBackground(drawable);
		}

		v.setOnClickListener(ocls);

		return v;
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@SuppressWarnings("deprecation")
	public ImageView createImageView(final InfoDrawable drawable,final View.OnClickListener ocls) {
		this.ocls = ocls;
		
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
				SharedPreferences.Editor et = sp.edit();
				et.putString(AnalogInformation.ANALOG_DRAWABLE_DIAL, drawable.getUri().toString());
				et.commit();
				
				if(ocls != null){
					ocls.onClick(v);
				}
				
				Log.i(TAG, drawable.getUri().getSchemeSpecificPart());
			}
		});

		return v;
	}
	
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN) 
	public ImageView createImageView(final Drawable drawable) {
		ImageView v = new ImageView(mContext);
		int sdk = android.os.Build.VERSION.SDK_INT;
		if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			v.setBackgroundDrawable(drawable);
		} else {
			v.setBackground(drawable);
		}

		v.setOnClickListener(ocls);

		return v;
	}
	
	private List<Drawable> getDrawableFromAssets(String path) {
		List<Drawable> drawables = new ArrayList<Drawable>();
	
		try {
			AssetManager am = mContext.getResources().getAssets();
			String[] files = am.list(path);
			for (String file : files) {
				drawables.add(Drawable.createFromStream(
						am.open(path + "/" + file), null));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return drawables;
	}
	
	private List<InfoDrawable> test_getDrawableFromAssets(String path) {
		List<InfoDrawable> infoDrawables = new ArrayList<InfoDrawable>();
	
		try {
			AssetManager am = mContext.getResources().getAssets();
			String[] files = am.list(path);
			
			path = path + File.separator;
			for (String file : files) {
				StringBuilder sb = new StringBuilder(path);
				sb.append(file);
				InfoDrawable infoDrawable = new InfoDrawable(
						Drawable.createFromStream(am.open(sb.toString()), null), 
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
			infoDrawables.add(new InfoDrawable(
					Drawable.createFromPath(file.getPath()), 
					Uri.fromFile(file)));
			
			Log.d(TAG, Uri.fromFile(file).toString());
		}
		
		return infoDrawables;
	}
	
	public int getViewNum(){
		return hasAddedView;
	}
}