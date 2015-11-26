package com.gionee.gntravel;

import java.io.File;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gionee.gntravel.customview.CustomProgressDialog;
import com.gionee.gntravel.db.dao.ContactsDao;
import com.gionee.gntravel.db.dao.PassengerDao;
import com.gionee.gntravel.db.dao.impl.ContactsDaoImpl;
import com.gionee.gntravel.db.dao.impl.PassengerDaoImpl;
import com.gionee.gntravel.utils.DBUtils;
import com.gionee.gntravel.utils.FinalString;
import com.gionee.gntravel.utils.UpdateThread;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 
 * @author lijinbao 退出登录
 */
public class SettingActivity extends Activity implements OnClickListener {
	private TextView tvTitle;// 标题名称
	private ImageView imgWifiStatus;// 是否允许打开wifi
	private ImageView imgLocationStatus;// 是否允许打开wifi
	private RelativeLayout reUpdate;// 检查更新
	private RelativeLayout reAboutUs;// 检查更新
	private TextView tvCleanCache;// 清除缓存
	private TextView tvVersion;// 当前版本
	

	private Dialog cleanDialog;
	private TravelApplication app;
	private CustomProgressDialog progressDialog;
	private ResultHandler handler = new ResultHandler();
	private class ResultHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case FinalString.CLEAR_CACHE_FINISH:
				if (cleanDialog != null) {
					cleanDialog.dismiss();
				}
				stopProgressDialog();
				break;
			case FinalString.UPDATE:
				Toast.makeText(SettingActivity.this, R.string.update_vername,
						Toast.LENGTH_SHORT).show();
				break;
			case  FinalString.NET_ERROR:
				Toast.makeText(SettingActivity.this, "网络连接失败",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		setupView();
		app = (TravelApplication) getApplication();
		initShowImageWithWifiSwitchState();
		initVisitLocationSwitchState();
	}

	private void setupView() {
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(R.string.setting);
		tvTitle.setOnClickListener(this);
		imgWifiStatus = (ImageView) findViewById(R.id.img_wifi_status);
		imgLocationStatus = (ImageView) findViewById(R.id.img_location_status);
		imgWifiStatus.setOnClickListener(this);
		imgLocationStatus.setOnClickListener(this);
		reUpdate = (RelativeLayout) findViewById(R.id.re_update);
		reUpdate.setOnClickListener(this);
		reAboutUs = (RelativeLayout) findViewById(R.id.re_about_us);
		reAboutUs.setOnClickListener(this);
		tvVersion = (TextView) findViewById(R.id.tv_version);
		tvVersion.setText(getAppVersioInfo());
		tvCleanCache = (TextView) findViewById(R.id.tv_clean_cache);
		tvCleanCache.setOnClickListener(this);
	}

	private String getAppVersioInfo() {// 获取版本号和版本名字
		try {
			String pkName = this.getPackageName();
			String versionName = this.getPackageManager().getPackageInfo(
			pkName, 0).versionName;
			return versionName;
		} catch (Exception e) {
		}
		return "";

	}

	private void showDialog() {
		cleanDialog = new Dialog(this, R.style.CustomProgressDialog);
		View viewdia = LayoutInflater.from(this).inflate(R.layout.delete_dialog, null);
		RelativeLayout reDeLayout = (RelativeLayout) viewdia.findViewById(R.id.re_dialog);
		Button btnSure = (Button) viewdia.findViewById(R.id.btn_dialog_sure);
		btnSure.setText(getString(R.string.ok));
		TextView tvTitle = (TextView) viewdia.findViewById(R.id.tv_dialog_title_name);
		tvTitle.setText(getString(R.string.clean_cache));
		Button btnCancel = (Button) viewdia.findViewById(R.id.btn_dialog_cancel);
		TextView tvDialogTitle = (TextView) viewdia.findViewById(R.id.tv_dialog_title);
		tvDialogTitle.setText(getString(R.string.clean_dialog_title));
		reDeLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				cleanDialog.dismiss();
			}
		});
		btnSure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startProgressDialog();
				new Thread() {
					public void run() {
						if (ImageLoader.getInstance().isInited()) {
							ImageLoader.getInstance().clearDiscCache();// 清除酒店图片																
						}
						app.getDefault_sp().clearData();//
						app.getConfig_sp().clearData();//
						deleteDb() ;
						handler.sendEmptyMessage(FinalString.CLEAR_CACHE_FINISH);
					};
				}.start();

			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cleanDialog.dismiss();
			}
		});

		cleanDialog.setContentView(viewdia);
		cleanDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		cleanDialog.show();

	}

	
	 private void cleanPhoto() {// 删除目录下的文件
	 File fileDir = new File(TravelApplication.PATH);
	 for (String fileName : fileDir.list()) {
	 File file = new File(TravelApplication.PATH + "/" + fileName);
	 file.delete();
	 }
	 }
	/**
	 * 删除本地数据库中的联系人，登机人，入住人自动填写的
	 */
	private void deleteDb() {
		DBUtils dbUitls = new DBUtils(SettingActivity.this);
		SQLiteDatabase db = dbUitls.openReadWriteDatabase();
		PassengerDao daopassenger = new PassengerDaoImpl();
		daopassenger.delectPassengersByUserId(db, app.getU());
		ContactsDao daoContact = new ContactsDaoImpl();
		daoContact.delectContactsByType(db, app.getU(), null);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title:
			this.finish();
			break;
		case R.id.img_wifi_status:// 是否wifi状态下才显示酒店图片
			updateImageWithWifiSwitchState();
			break;
		case R.id.img_location_status:// 是否允许访问我的位置
			VisitLocationSwitchState();
			break;
		case R.id.re_update:// 检查更新
			update();
			break;
		case R.id.re_about_us:// 关于我们
			Intent intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
			break;
		case R.id.tv_clean_cache:// 清除缓存
			showDialog();
			break;
		default:
			break;
		}
	}

	/**
	 * 检查版本更新
	 */
	private void update() {
//		startProgressDialog();
//		new Thread() {
//			public void run() {
//				try {
//					Thread.sleep(1500);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				handler.sendEmptyMessage(UPDATE);
//			};
//		}.start();
		new Thread(new UpdateThread(getApplicationContext(),handler)).start();
	}

	public void initShowImageWithWifiSwitchState() {
		if (app.getConfig_sp().getBoolean("showImgWithWifiState")) {
			imgWifiStatus.setBackgroundResource(R.drawable.slt_common_switch_on);
		} else {
			imgWifiStatus.setBackgroundResource(R.drawable.slt_common_switch_off);
		}
	}

	public void initVisitLocationSwitchState() {
		if (!app.getConfig_sp().getBoolean("visitLocationState")) {
			imgLocationStatus.setBackgroundResource(R.drawable.slt_common_switch_on);
		} else {
			imgLocationStatus.setBackgroundResource(R.drawable.slt_common_switch_off);
		}
	}

	public void updateImageWithWifiSwitchState() {
		if (app.isShowImgWithWifiState()) {
			imgWifiStatus.setBackgroundResource(R.drawable.slt_common_switch_off);
			app.getConfig_sp().putBoolean("showImgWithWifiState", false);
			app.setShowImgWithWifiState(false);
		} else {
			imgWifiStatus.setBackgroundResource(R.drawable.slt_common_switch_on);
			app.getConfig_sp().putBoolean("showImgWithWifiState", true);
			app.setShowImgWithWifiState(true);
		}
	}

	public void VisitLocationSwitchState() {
		if (app.isVisitLocationState()) {
			imgLocationStatus.setBackgroundResource(R.drawable.slt_common_switch_off);
			app.getConfig_sp().putBoolean("visitLocationState", true);
			app.setVisitLocationState(false);
		} else {
			imgLocationStatus.setBackgroundResource(R.drawable.slt_common_switch_on);
			app.getConfig_sp().putBoolean("visitLocationState", false);
			app.setVisitLocationState(true);
		}
	}

	public void sendUserCheckoutBroadcast() {
		Intent intent = new Intent(FinalString.USER_CHECKOUT_ACTION);
		sendBroadcast(intent);
	}

	private void startProgressDialog() {
		if (progressDialog == null) {
			progressDialog = CustomProgressDialog.createDialog(this,
					R.layout.customprogressdialog);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setCancelable(false);
		}
		progressDialog.show();
	}

	private void stopProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}
}
