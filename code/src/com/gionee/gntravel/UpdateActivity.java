package com.gionee.gntravel;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gionee.gntravel.db.dao.UpdateMode;
import com.gionee.gntravel.entity.UpdateEntity;
import com.gionee.gntravel.utils.DownLoadUtils;

public class UpdateActivity extends Activity implements OnClickListener {
	private TextView tv_title_name;
	private TextView tv_content;
	private UpdateEntity updateEntity;
	private ImageView img_title_cha;
	private Button btn_cancel;
	private Button btn_update;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_update);
		findViews();
		initData();
	}

	private void findViews() {
		tv_title_name=(TextView) findViewById(R.id.tv_dialog_title_name);
		tv_title_name.setText("新版本升级");
		img_title_cha=(ImageView) findViewById(R.id.img_dialog_title_cha);
		img_title_cha.setOnClickListener(this);
		tv_content = (TextView) findViewById(R.id.tv_content);
		 btn_cancel = (Button) findViewById(R.id.btn_cancel);
		btn_cancel.setOnClickListener(this);
		 btn_update = (Button) findViewById(R.id.btn_update);
		btn_update.setOnClickListener(this);
	}

	private void initData() {
		updateEntity = UpdateMode.getUpdateInfo(this);
		tv_content.setText(updateEntity.getUpDateMessage());
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
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_dialog_title_cha:
			finish();
			break;
		case R.id.btn_cancel:
			finish();
			break;
		case R.id.btn_update:
			doUpdate();
			Toast.makeText(this, "已在后台下载最新安装程序", Toast.LENGTH_SHORT).show();
			finish();
			break;
		}
	}

	private void doUpdate() {

		DownLoadUtils.download(this, updateEntity.getDownloadPath());
	}

}
