package com.gionee.gntravel.fragment;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.gionee.gntravel.R;
import com.gionee.gntravel.utils.GNConnUtil;
import com.gionee.gntravel.utils.GNConnUtil.GNConnListener;

public class BaseFragment extends ListBaseFragment implements GNConnListener{
	
	protected GNConnUtil post;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		post = new GNConnUtil();
		post.setGNConnListener(this);
	}
	protected void dismissProgress() {
		if (lastShowDialog != null && lastShowDialog.isShowing()) {
			lastShowDialog.dismiss();
		}

	}

	private AlertDialog lastShowDialog;
	protected AlertDialog showProgress() {
		AlertDialog.Builder ab = new AlertDialog.Builder(this.getActivity());
		AlertDialog ad = ab.create();
		ad.show();

		ImageView iv = new ImageView(this.getActivity());
		iv.setBackgroundResource(R.anim.progress_round);

		AnimationDrawable an = (AnimationDrawable) iv.getBackground();
		an.start();

		ad.setContentView(iv);

		lastShowDialog = ad;
		ad = null;
		return lastShowDialog;
	}
	protected ProgressDialog createProgressDialog(String title,String msg){
		ProgressDialog pd = new ProgressDialog(getActivity());
		pd.setTitle(title);
		pd.setMessage(msg);
		return pd;
	}
	protected void showDialog(String title,String msg){
		AlertDialog.Builder builder = new Builder(getActivity());
		builder.setMessage(msg);
		builder.setTitle(title);
		builder.setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(android.os.Build.VERSION.SDK_INT > 10 ){
				     //3.0以上打开设置界面，也可以直接用ACTION_WIRELESS_SETTINGS打开到wifi界面
				    startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
				} else {
				    startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
				}
			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}

	@Override
	public void onReqSuc(String requestUrl, String json) {
		
	}

	@Override
	public void onReqFail(String requestUrl, Exception ex, int errorCode) {
		
	}

	@Override
	public void loadDate() {
		
	}

	@Override
	public boolean onKeyBackDown() {
		return false;
	}
}
