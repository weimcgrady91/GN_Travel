package com.gionee.gntravel;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ImageView;

import com.gionee.gntravel.utils.GNConnUtil;
import com.gionee.gntravel.utils.GNConnUtil.GNConnListener;

public class BaseActivity extends FragmentActivity implements GNConnListener {
	private boolean log_switch = true;
	protected GNConnUtil post;
	protected ProgressDialog pd;
	
	public void printLog(String tag, String msg) {
		if(log_switch) {
			Log.e(tag, msg);
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		post = new GNConnUtil();
		post.setGNConnListener(this);
	}
	protected void dismissDialog() {
		if (pd != null) {
			pd.dismiss();
		}
	}
	protected void showPgDialog(Context context){
		try {
			if (pd!=null && pd.isShowing()) {
			return;	
			}
			pd = new ProgressDialog(context);
			pd.setCancelable(true);
			pd.show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	protected void dismissProgress() {
		if (lastShowDialog != null && lastShowDialog.isShowing()) {
			try {
				lastShowDialog.dismiss();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private AlertDialog lastShowDialog;
	protected void showProgress() {
		try {
			if (lastShowDialog!=null&&lastShowDialog.isShowing()) {
				return;
			}
			AlertDialog.Builder ab = new AlertDialog.Builder(this);
			AlertDialog ad = ab.create();
			ad.show();

			ImageView iv = new ImageView(this);
			iv.setBackgroundResource(R.anim.progress_round);

			AnimationDrawable an = (AnimationDrawable) iv.getBackground();
			an.start();

			ad.setContentView(iv);

			lastShowDialog = ad;
			lastShowDialog.setCancelable(false);
			ad = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public SimpleDateFormat getFormater() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日", Locale.getDefault());
		return sdf;
	}
	public SimpleDateFormat getStrDateFormater() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		return sdf;
	}

	@Override
	public void onReqSuc(String requestUrl, String json) {
		
	}
	@Override
	public void onReqFail(String requestUrl, Exception ex, int errorCode) {
		
	}
}
