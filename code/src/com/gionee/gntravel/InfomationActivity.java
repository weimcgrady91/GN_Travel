package com.gionee.gntravel;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gionee.gntravel.customview.CustomProgressDialog;
import com.gionee.gntravel.customview.RoundImageView;
import com.gionee.gntravel.utils.FinalString;
import com.gionee.gntravel.utils.Utils;

/**
 * 
 * @author lijinbao 帐号信息 （头像 手机号 修改密码）
 */
public class InfomationActivity extends Activity implements OnClickListener {
	private File tempFile = new File(TravelApplication.PATH, getPhotoFileName());
	private TextView tvTitle;// 标题名称
	private TextView tvId;// 帐号
	private String id;
	private RoundImageView imgPhoto;// 头像
	private RelativeLayout reEditPw;// 修改密码
	private RelativeLayout reEditName;// 修改昵称
	private Button btnExit;
	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;
	private static final int PHOTO_REQUEST_GALLERY = 2;
	private static final int PHOTO_REQUEST_CUT = 3;
	public static final int EDIT_NAME = 4;
	private Dialog dialogPhoto;
	private TravelApplication app;
	private static final int EXIT_FINISH = 0x1000;
	private static final int NO_EXIT = 0x1001;
	private String userId;
	private ResultHandler handler = new ResultHandler();

	private class ResultHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case EXIT_FINISH:
				stopProgressDialog();
				InfomationActivity.this.finish();
				break;

			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		setupView();
	}

	private void setupView() {
		app = (TravelApplication) getApplication();
		userId = app.getUserInfo_sp().getString("userId");
		Intent intent = getIntent();
		id = intent.getStringExtra("infoId");
		tvId = (TextView) findViewById(R.id.tv_info_id);
		tvId.setText(id);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(getString(R.string.id_info));
		tvTitle.setOnClickListener(this);
		tvId = (TextView) findViewById(R.id.tv_info_id);
		id = intent.getStringExtra("infoId");
		tvId.setText(id);
		imgPhoto = (RoundImageView) findViewById(R.id.img_info_photo);
		imgPhoto.setOnClickListener(this);
		reEditName = (RelativeLayout) findViewById(R.id.re_nickname);
		reEditName.setOnClickListener(this);
		reEditPw = (RelativeLayout) findViewById(R.id.re_edit_info_pw);
		reEditPw.setOnClickListener(this);
		btnExit = (Button) findViewById(R.id.btn_setting_exit);
		btnExit.setOnClickListener(this);

	}

	@Override
	protected void onResume() {
		// 文件转换成bitmap
		Bitmap bitmap = Utils.loadImageFromSDCard(TravelApplication.PATH + "/"
				+ userId + ".jpg");
		if (bitmap != null) {
			imgPhoto.setImageBitmap(bitmap);
		}
		super.onResume();
	}

	private void showDialog() {
		dialogPhoto = new Dialog(this, R.style.CustomProgressDialog);
		View view = LayoutInflater.from(this).inflate(R.layout.photo_dialog,
				null);
		RelativeLayout re = (RelativeLayout) view
				.findViewById(R.id.re_photo_dialog);
		LinearLayout btnTakePhoto = (LinearLayout) view
				.findViewById(R.id.ll_takephoto);
		LinearLayout btnLocalPhotos = (LinearLayout) view
				.findViewById(R.id.ll_local_photos);
		TextView tvTitle = (TextView) view
				.findViewById(R.id.tv_dialog_title_name);
		tvTitle.setText(getString(R.string.setting_photo));
		re.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialogPhoto.dismiss();
			}
		});
		btnLocalPhotos.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialogPhoto.dismiss();
				Intent intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
			}
		});
		btnTakePhoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialogPhoto.dismiss();
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
				startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
			}
		});

		dialogPhoto.setContentView(view);
		dialogPhoto.getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		dialogPhoto.show();
	}

	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch (requestCode) {
		case PHOTO_REQUEST_TAKEPHOTO:
			if (intent == null) {
				startPhotoZoom(Uri.fromFile(tempFile), 480);
			}
			break;
		case PHOTO_REQUEST_GALLERY:
			if (intent != null)
				startPhotoZoom(intent.getData(), 480);
			break;
		case PHOTO_REQUEST_CUT:
			if (intent != null)
				setPicToView(intent);
			break;
		case EDIT_NAME:
			if (intent != null) {
				id = intent.getExtras().getString("editNickNameBack");
				tvId.setText(id);
				// 存入sharePrefence userNickName
				// 根据手机号 存入昵称 （键为手机号，值为昵称）
				app.getUserInfo_sp().putString(
						app.getUserInfo_sp().getString("userName"), id);
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, intent);
	}

	private void startPhotoZoom(Uri uri, int size) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", size);
		intent.putExtra("outputY", size);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

	private void setPicToView(Intent picdata) {
		Bundle bundle = picdata.getExtras();
		if (bundle != null) {
			Bitmap photo = bundle.getParcelable("data");
			imgPhoto.setImageBitmap(photo);
			// 保存到sd卡
			File file = new File(TravelApplication.PATH, userId + ".jpg");
			try {
				FileOutputStream out = new FileOutputStream(file);
				photo.compress(CompressFormat.PNG, 100, out);
				out.flush();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 文件转换成bitmap
			Bitmap bitmap = Utils.loadImageFromSDCard(TravelApplication.PATH
					+ "/" + userId + ".jpg");
			if (bitmap != null) {
				imgPhoto.setImageBitmap(bitmap);
			}

		}
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.tv_title:
			this.finish();
			break;
		case R.id.re_edit_info_pw:
			intent = new Intent(this, EditPwActivity.class);
			startActivity(intent);
			break;

		case R.id.img_info_photo:
			showDialog();
			break;
		case R.id.re_nickname:
			intent = new Intent(this, EditNickNameActivity.class);
			intent.putExtra("editNickName", id);
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivityForResult(intent, EDIT_NAME);
			break;
		case R.id.btn_setting_exit:// 退出登录
			exitLogin();
			break;
		}

	}

	/**
	 * 
	 * 退出登录
	 */
	private void exitLogin() {
		startProgressDialog();
		new Thread() {
			public void run() {
				app.getUserInfo_sp().putBoolean("userState", false);
				app.getUserInfo_sp().putString("userName", "");
				app.setLoginState(false);
				sendUserCheckoutBroadcast();
				handler.sendEmptyMessage(EXIT_FINISH);
			};
		}.start();
	}

	public void sendUserCheckoutBroadcast() {
		Intent intent = new Intent(FinalString.USER_CHECKOUT_ACTION);
		sendBroadcast(intent);
	}

	private CustomProgressDialog progressDialog;

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