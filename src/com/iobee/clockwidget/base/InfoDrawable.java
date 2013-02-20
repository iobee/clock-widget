package com.iobee.clockwidget.base;

import java.io.File;
import java.io.FileOutputStream;

import android.graphics.drawable.Drawable;
import android.net.Uri;

public class InfoDrawable {
	private Drawable mDrawable;
	private Uri mUri;
	
	public InfoDrawable(Drawable drawable, Uri uri){
		this.mDrawable = drawable;
		this.mUri = uri;
	}
	
	public Drawable getDrawable(){
		return mDrawable;
	}

	public Uri getUri() {
		return mUri;
	}

	public void setUri(Uri mUri) {
		this.mUri = mUri;
	}
	
	public void setUri(File filePath){
		mUri = Uri.fromFile(filePath);
	}
}
