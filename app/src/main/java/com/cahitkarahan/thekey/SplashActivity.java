package com.cahitkarahan.thekey;

import android.animation.*;
import android.app.*;
import android.content.*;
import android.content.Intent;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.net.Uri;
import android.os.*;
import android.os.Bundle;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import java.io.*;
import java.io.InputStream;
import java.text.*;
import java.util.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.*;
import org.json.*;

public class SplashActivity extends AppCompatActivity {
	
	private Timer _timer = new Timer();
	
	private LinearLayout splash_main_lay;
	private LinearLayout splash_top_spacer;
	private ImageView splash_logo;
	private LinearLayout splash_btm_spacer;
	private TextView splash_subtx;
	
	private TimerTask t1;
	private Intent i1 = new Intent();
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.splash);
		initialize(_savedInstanceState);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		splash_main_lay = findViewById(R.id.splash_main_lay);
		splash_top_spacer = findViewById(R.id.splash_top_spacer);
		splash_logo = findViewById(R.id.splash_logo);
		splash_btm_spacer = findViewById(R.id.splash_btm_spacer);
		splash_subtx = findViewById(R.id.splash_subtx);
	}
	
	private void initializeLogic() {
		_ViewFadeIn(splash_main_lay, 1500);
		t1 = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						i1.setClass(getApplicationContext(), MainActivity.class);
						startActivity(i1);
						finish();
					}
				});
			}
		};
		_timer.schedule(t1, (int)(2000));
	}
	
	public void _ViewFadeIn(final View _viewFadeIn, final double _viewFadeInSetTime) {
		long x = (long)_viewFadeInSetTime;
		
		Animation fadeIn = new AlphaAnimation(0, 1); 
		fadeIn.setDuration(x);
		AnimationSet animation = new AnimationSet(true); animation.addAnimation(fadeIn);
		_viewFadeIn.startAnimation(animation);
	}
	
}