package com.iobee.clockwidget.tool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class DrawableUtils {
	private Context mContext;
	private int hasAddedView = 0;
		
	private View.OnClickListener ocls;

	public DrawableUtils(Context mContext) {
		this.mContext = mContext;
	}
	
	public void addAssetsDrawableToBox(LinearLayout viewBox, View.OnClickListener ocls) {
		List<Drawable> drawables = getDrawableFromAssets("dial");
		for (Drawable dial : drawables) {
			hasAddedView++;
			viewBox.addView(createImageView(dial, ocls));
		}
	}
	
	public void addSDCardDrawableToBox() {
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
	
	public int getViewNum(){
		return hasAddedView;
	}
}