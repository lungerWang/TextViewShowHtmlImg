package com.allen.textviewshowhtml;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;


public class ShowPicActivity extends Activity {

	int dwidth;
	int dheight;
	private int orientation;
	
	public ProgressBar progressBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_pic);
		
		final LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

		final ImageView view = (ImageView)findViewById(R.id.BigImage);
		
		final String source = getIntent().getExtras().getString("picUrl");
		
		progressBar = (ProgressBar)findViewById(R.id.leadProgressBar);
		view.setVisibility(ImageView.GONE);
        
        Display display = getWindowManager().getDefaultDisplay();
		dwidth = display.getWidth(); 
		dheight = display.getHeight();
		
		if (dwidth > dheight) { 
			 orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;  //横屏
		}else{
			orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;  //竖屏
		}
		
		new Thread(new Runnable(){
			public void run(){
				final Bitmap bitmap = reSizePicture(source);
				try{
					Thread.sleep(0);
				}catch(InterruptedException e){
					e.printStackTrace();
				}
				view.post(new Runnable(){
					public void run(){
						progressBar.setVisibility(View.GONE);
						view.setVisibility(ImageView.VISIBLE);
						view.setImageBitmap(bitmap);
						view.setLayoutParams(params);
						ViewGroup layout = (ViewGroup) findViewById(R.id.layout);
						layout.removeAllViews();
						layout.addView(view);
					}
				});
			}
		}).start();

	}
	
	public Bitmap reSizePicture(String path){
		Bitmap resizedBitmap = null;
		Bitmap bitmapOrg = null;
		try{
			InputStream a = getResources().getAssets().open(path);
			bitmapOrg = BitmapFactory.decodeStream(a);
			
			int width = bitmapOrg.getWidth();
			int height = bitmapOrg.getHeight();
			
			
			int newWidth = dwidth;
			int newHeight = dwidth*bitmapOrg.getHeight()/bitmapOrg.getWidth() ;

			if (newWidth > width && newHeight > height) {
				// calculate the scale - in this case = 0.4f
				float scaleWidth = ((float) newWidth) / width;
				float scaleHeight = ((float) newHeight) / height;

				// createa matrix for the manipulation
				Matrix matrix = new Matrix();
				// resize the bit map
				matrix.postScale(scaleWidth, scaleHeight);

				resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, width,
						height, matrix, true);
			} else {
				resizedBitmap = bitmapOrg;
			}
		}catch(MalformedURLException e1){
			e1.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		return resizedBitmap;
		
	}

	
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		this.setRequestedOrientation(orientation);
	}
	
	protected void onResume() {
		orientation = ActivityInfo.SCREEN_ORIENTATION_USER;
		this.setRequestedOrientation(orientation);
		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		if (width > height) {
			orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
		} else {
			orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
		}
		super.onResume();
	}

}
