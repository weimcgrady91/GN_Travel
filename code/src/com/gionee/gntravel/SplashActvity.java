package com.gionee.gntravel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.gionee.gntravel.service.GetNewsInfoService;
import com.gionee.gntravel.utils.FinalString;
import com.gionee.gntravel.utils.UpdateThread;
import com.youju.statistics.YouJuAgent;

public class SplashActvity extends Activity implements OnClickListener {
	private TravelApplication app;
	private String status;
	private String username;
	private String userId;
	private Dialog dialogShow;
	private boolean flag;
	private LinearLayout llDialog;
	private Button btnGoOn;
	private Button btnCancel;
	private TextView tvTitle;
	private CheckBox checkBox;
	private Handler resultHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case FinalString.DATA_FINISH:
				Intent intent = new Intent(SplashActvity.this,
						MainTabActivity.class);
				startActivity(intent);
				new Thread(new UpdateThread(getApplicationContext())).start();
				finish();
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		initView();
		initData();
	}

	private void initView() {
		llDialog = (LinearLayout) findViewById(R.id.ll_dialog);
		btnGoOn = (Button) findViewById(R.id.btn_dialog_sure);
		btnCancel = (Button) findViewById(R.id.btn_dialog_cancel);
		tvTitle = (TextView) findViewById(R.id.tv_dialog_title);
		tvTitle.setText("欢迎使用新旅程。" + "\n"
				+ "在使用过程中可能产生数据流量，流量费用请咨询当地运营商。通过小区基站定位您的位置。是否继续使用？");
		checkBox = (CheckBox) findViewById(R.id.check_box);
		checkBox.setChecked(true);
		btnGoOn.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
	}

	private void initData() {
		app = (TravelApplication) getApplication();
		mUserName = app.getU();
		flag = app.getUserInfo_sp().getBoolean("isVisit");
		Log.e("lijinbao", "onCrete  flag=" + flag);
		if (!flag) {
			llDialog.setVisibility(View.VISIBLE);
		} else {
			llDialog.setVisibility(View.GONE);
			new Thread(new InitRunnable()).start();
		}
		

	}

	/**
	 * 通过ContentProvider方式获取帐号状态
	 */
	private void getStatusByProvider() {
		Cursor cursor = null;
		try {
			String uriStr = "content://com.gionee.account/accountStatus";
			Uri uri = Uri.parse(uriStr);
			cursor = getContentResolver().query(uri, null, null, null, null);
			cursor.moveToFirst();
			status = cursor.getString(cursor.getColumnIndex("status"));
			username = cursor.getString(cursor.getColumnIndex("username"));
			userId = cursor.getString(cursor.getColumnIndex("userid"));
			if ("login".equals(status) && !TextUtils.isEmpty(username)) {// 获取会员中心登录状态
				app.getUserInfo_sp().putBoolean("userState", true);
				app.getUserInfo_sp().putString("userName", username);
				// app.getUserInfo_sp().putString("userId", userId);
				app.getUserInfo_sp().putString("u", userId);
				app.getUserInfo_sp().putString("userId", "");
				app.setLoginState(true);
			}
			// 654168964@qq.com
			// a3670499

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != cursor) {
				cursor = null;
			}
		}

	}

	private String mUserName;

	private class InitRunnable implements Runnable {

		@Override
		public void run() {
			try {
				importDB();
				getUserStatus();
//				app.initYouju();
				sendFinishMsg();
			} catch (Exception e) {
				finish();
			}
		}
	}

	private void getUserStatus() {
		if (!app.getUserInfo_sp().getBoolean("userState")) {
			getStatusByProvider();
		}
	}

	private void sendFinishMsg() {
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		Message msg = Message.obtain();
		msg.what = FinalString.DATA_FINISH;
		resultHandler.sendMessage(msg);
	}

	private void createCacheFile(String fileName) {
		File files = getApplicationContext().getFilesDir();
		String filesPath = files.toString();
		File file = new File(filesPath, fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void importDB() throws Exception {
		if (!new File(getString(R.string.datapath), "gntravel.db").exists()) {
			FileOutputStream fout = new FileOutputStream(new File(
					getString(R.string.datapath), "gntravel.db"));
			InputStream in = getAssets().open("gntravel.db");
			int len = 0;
			byte[] buffer = new byte[1024];
			while ((len = in.read(buffer)) != -1) {
				fout.write(buffer, 0, len);
			}
			fout.flush();
			fout.close();
			in.close();
		}
	}



	// public void writeRid(Context mContext, String value) {
	// SharedPreferences mPreference =
	// mContext.getSharedPreferences("push_demo", Context.MODE_PRIVATE);
	// Editor mEditor = mPreference.edit();
	// mEditor.putString("rid", value);
	// mEditor.commit();
	// }
	@Override
	protected void onResume() {
		super.onResume();
//		YouJuAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
//		YouJuAgent.onPause(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_dialog_sure:
			llDialog.setVisibility(View.GONE);
			if (checkBox.isChecked()) {
				app.getUserInfo_sp().putBoolean("isVisit", true);
			}
			new Thread(new InitRunnable()).start();
			break;

		case R.id.btn_dialog_cancel:
			this.finish();
			break;
		}

	}

}