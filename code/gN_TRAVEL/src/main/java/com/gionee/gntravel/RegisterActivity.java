package com.gionee.gntravel;

import java.net.URLEncoder;
import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.gionee.gntravel.customview.CustomProgressDialog;
import com.gionee.gntravel.entity.CodeEntity;
import com.gionee.gntravel.entity.ForgetPassword;
import com.gionee.gntravel.entity.ValidateCode;
import com.gionee.gntravel.utils.FinalString;
import com.gionee.gntravel.utils.HttpConnCallback;
import com.gionee.gntravel.utils.HttpConnUtil4Gionee;
import com.gionee.gntravel.utils.JSONUtil;
import com.gionee.gntravel.utils.NetWorkUtil;
import com.gionee.gntravel.utils.SmsBroadcastReceiver;
import com.gionee.gntravel.utils.Utils;

/**
 * 
 * @author lijinbao 注册第一个界面
 */
public class RegisterActivity extends Activity implements OnClickListener,
		HttpConnCallback {
	private SmsBroadcastReceiver smsReceiver;
	private IntentFilter filter;
	private TextView tvTitle;// 标题名称
	private CustomProgressDialog progressDialog;
	private EditText etId;// 帐号
	private EditText etYanzhengCode;// 验证码
	private Button btnSendYanzhengCode;// 按钮
	private Button btnRegister;
	private String id;// 手机号（开始是带有空格的）
	private String inputYanzhengCode = "";// 输入的验证码
	private CodeEntity codeEntityRequest;
	private MyThread mythread = null;
	private HttpConnUtil4Gionee task;
	public static Boolean smsRegFlag = true;// 防止弹出短信对话框后清空自动填写的内容,在此界面就不清空,跳到下一个界面设置为false
	private Dialog dialog;
	private ImageView imgPicYanzhengCode;// 产生的4位随机数
	private String inputPicYanzhengCode;// 输入4位随机数
	private EditText etPicYanzhengCode;// 验证码
	private ImageView imgDialog;
	private String vid;
	private String SSms=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		setupView();
		registerSmsReceiver();
	}

	/**
	 * 注册获取短信验证码的广播
	 */
	private void registerSmsReceiver() {
		smsReceiver = new SmsBroadcastReceiver(hd);
		filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(Integer.MAX_VALUE);
		registerReceiver(smsReceiver, filter);
	}

	private void setId() {
		id = Utils.getSIMCardInfo(this);
		id = Utils.getId(id);
		etId.setText(id);
		etId.setSelection(etId.length());
	}

	private void setupView() {
		smsRegFlag = true;
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(R.string.regcode);
		tvTitle.setOnClickListener(this);
		etId = (EditText) findViewById(R.id.et_reg_id);
		// setId();
		etYanzhengCode = (EditText) findViewById(R.id.et_reg_yanzheng);
		btnSendYanzhengCode = (Button) findViewById(R.id.btn_reg_yanzheng);
		btnSendYanzhengCode.setOnClickListener(this);
		btnRegister = (Button) findViewById(R.id.btn_register);
		btnRegister.setOnClickListener(this);
		Utils.mobileAddSpace(13, etId);

	}

	/**
	 * 点击发送验证码
	 */
	private void sendvalidateCode() {
		// 发送验证码.向服务器请求验证码，同时倒计时 按钮变灰
		smsRegFlag = true;
		id = etId.getText().toString().trim();
		id = id.replaceAll(" ", "");// 去掉空格
		if (TextUtils.isEmpty(id)) {
			Toast.makeText(this, R.string.mobile_null, Toast.LENGTH_SHORT)
					.show();
		} else if (!Utils.isMobile(id)) {
			Toast.makeText(this, R.string.mobile_not_guize, Toast.LENGTH_SHORT)
					.show();
		} else {
			getCode();
		}
	}

	private void getCode() {
		startProgressDialog();
		if (!NetWorkUtil.isNetworkConnected(this)) {
			// 无网络连接 提示
			Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show();
			stopProgressDialog();
			return;
		}
		btnSendYanzhengCode.setBackgroundResource(R.drawable.white_btn_pressed);// 变灰
		btnSendYanzhengCode.setEnabled(false);
		// 向服务器请求验证码
		codeEntityRequest = new CodeEntity();
		codeEntityRequest.setTn(id);
		codeEntityRequest.setS(SSms);
		HashMap<String, String> params = new HashMap<String, String>();
		String host = getString(R.string.gionee_host)
				+ FinalString.REGISTER_GET_SMS_URL;
		params.put(
				"json",
				URLEncoder.encode(JSONUtil.getInstance().toJSON(
						codeEntityRequest)));
		task = new HttpConnUtil4Gionee(this);
		task.execute(host, params, HttpConnUtil4Gionee.HttpMethod.GET);

	}

	/**
	 * 注册按钮验证验证码
	 */
	private void onClickReg() {

		// 发送验证码.向服务器请求验证码，同时倒计时 按钮变灰
		id = etId.getText().toString().trim();
		id = id.replaceAll(" ", "");// 去掉空格
		inputYanzhengCode = etYanzhengCode.getText().toString().trim();
		if (Utils.limitMobile(this, id)) {
			if (TextUtils.isEmpty(inputYanzhengCode)) {
				Toast.makeText(this, R.string.yanzhengma_null,
						Toast.LENGTH_SHORT).show();
			} else if (inputYanzhengCode.length() != 6) {
				Toast.makeText(this, R.string.yanzhengma_error2,
						Toast.LENGTH_SHORT).show();
			} else {
				if (mythread != null) {
					mythread.stopTime();
				}
				validateCode();
			}
		}
	}

	/**
	 * 注册按钮验证验证码
	 */
	private void validateCode() {
		startProgressDialog();
		if (!NetWorkUtil.isNetworkConnected(this)) {
			Toast.makeText(this, R.string.no_network, 1).show();
			return;
		}
		ValidateCode validateCodeReuqset = new ValidateCode();
		validateCodeReuqset.setTn(id);
		validateCodeReuqset.setSc(inputYanzhengCode);
		validateCodeReuqset.setS(SSms);
		HashMap<String, String> params = new HashMap<String, String>();
		String host = getString(R.string.gionee_host)
				+ FinalString.REGISTER_VALIDATE_SMS_URL;
		params.put(
				"json",
				URLEncoder.encode(JSONUtil.getInstance().toJSON(
						validateCodeReuqset)));
		Log.e("lijinbao",
				"json  = " + JSONUtil.getInstance().toJSON(validateCodeReuqset));
		task = new HttpConnUtil4Gionee(this);
		task.execute(host, params, HttpConnUtil4Gionee.HttpMethod.GET);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_reg_yanzheng://
			sendvalidateCode();
			break;
		case R.id.btn_register:// 注册按钮
			onClickReg();
			break;
		case R.id.tv_title:
			this.finish();
			break;
		}
	}

	Handler hd = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:// 更新验证码倒计时 时间
				btnSendYanzhengCode.setText(msg.arg1
						+ getString(R.string.daojishi));
				break;
			case 1:// 时间到0，变回原来的样子
				btnSendYanzhengCode
						.setBackgroundResource(R.drawable.white_btn_normal);
				btnSendYanzhengCode.setEnabled(true);
				btnSendYanzhengCode
						.setText(getString(R.string.refreshyanzhengcode));
				break;
			case 2:
				showDialog();
				getValidateImage();
				break;
			case FinalString.SMS_CODE_FINISH:
				etYanzhengCode.setText((String) msg.obj);
				break;
			}
		}
	};

	class MyThread implements Runnable {
		volatile int time = 60;

		public void run() {
			while (time > 0) {
				try {
					Thread.sleep(1000);
					time--;
					Message message = new Message();
					message.what = 0;
					message.arg1 = time;
					hd.sendMessage(message);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			Message message = new Message();
			message.what = 1;
			hd.sendMessage(message);
		}

		public void stopTime() {
			time = 0;
		}
	}

	@Override
	protected void onResume() {
		if (!smsRegFlag) {
			etYanzhengCode.setText("");
		}
		Log.e("lijinbao", "onResume");
		super.onResume();
	}

	TextWatcher mTextWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (s.length() == 13) {
				if ((etYanzhengCode.getText().toString().trim().length() == 6)) {

					btnRegister.setEnabled(true);
					btnRegister
							.setBackgroundResource(R.drawable.red_btn_selector);
				}
			} else {
				btnRegister.setEnabled(false);
				btnRegister.setBackgroundResource(R.drawable.no_click);
			}
		}
	};
	/**
	 * 只有输入的位数合适 才能把登录按钮变成高亮。验证码按钮监测
	 */
	TextWatcher mTextWatcher2 = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (s.length() == 6) {
				if ((etId.getText().toString().trim().length() == 13)) {

					btnRegister.setEnabled(true);
					btnRegister
							.setBackgroundResource(R.drawable.red_btn_selector);
				}
			} else {
				btnRegister.setEnabled(false);
				btnRegister.setBackgroundResource(R.drawable.no_click);
			}
		}
	};

	@Override
	public void execute(String result) {
		Log.e("lijinbao", "result = " + result);
		stopProgressDialog();
		if (TextUtils.isEmpty(result)) {
			Toast.makeText(this, R.string.network_timeout, 1).show();
			btnSendYanzhengCode
					.setBackgroundResource(R.drawable.white_btn_normal);
			btnSendYanzhengCode.setEnabled(true);
			return;
		}
		try {
			JSONObject all = new JSONObject(result);
			if (!FinalString.ERRORCODE.equals(all.getString("errorCode"))) {
				Toast.makeText(this, R.string.service_error, 1).show();
				return;
			}
			String resultStr;
			if (all.has("content")) {// 第二步得到图形验证码
				Log.e("lijinbao", "  第二步得到图形验证码");
				resultStr = all.getString("content");
				JSONObject content = new JSONObject(resultStr);
				vid = content.getString("vid");

				String vda = content.getString("vda");
				testBase64Encoder(vda);
				dissmissPb();
			} else if (all.has("r")) {
				resultStr = all.getString("r");
				if ("00001".equals(resultStr)) {// 第三步 该帐号没有注册，可以给用户发送短信验证码
					Log.e("lijinbao", "   第四步 短信验证码获取成功 正在等待短信验证码");
					Toast.makeText(
							this,
							getString(R.string.note_one) + id
									+ getString(R.string.note_sencond),
							Toast.LENGTH_LONG).show();
					// 2发送验证码，显示 60秒后可重新获取验证码
					mythread = new MyThread();
					Thread thread = new Thread(mythread);
					thread.start();

				} else if ("00002".equals(resultStr)) {
					Log.e("lijinbao", "   第五步 请求跳到输入密码界面 获取短信验证码正确的话");
					String session = all.getString("s");
					Intent intent = new Intent(this, RegInputPwActivity.class);
					intent.putExtra("Id", id);
					intent.putExtra("S", session);
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					startActivityForResult(intent, 100);
				} else if ("1118".equals(resultStr)) {
					Log.e("lijinbao", "1118  第一步 获取图形验证码");
					hd.sendEmptyMessage(2);// 第一步发图形验证码
				}

				else {
					Utils.MyToast(this, resultStr);
					hd.sendEmptyMessage(1);
				}
			} else if (all.has("s")) {//
				resultStr = all.getString("s");
				SSms = resultStr;
				Log.e("lijinbao", "   第三步 请求获取短信验证码");
				getCode();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 20) {
			try {
				setResult(TravelApplication.FORGET_PW_ONE);
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
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

	private void showDialog() {

		dialog = new Dialog(this, R.style.CustomProgressDialog);
		View viewdia = LayoutInflater.from(this).inflate(
				R.layout.validate_code_dialog, null);
		Button btnSure = (Button) viewdia.findViewById(R.id.btn_dialog_sure);
		ImageView reFresh = (ImageView) viewdia
				.findViewById(R.id.img_refresh_yanzheng_code);
		ImageView imgCha = (ImageView) viewdia
				.findViewById(R.id.img_dialog_title_cha);
		TextView tvTitle = (TextView) viewdia
				.findViewById(R.id.tv_dialog_title_name);
		tvTitle.setText("请输入图片中的文字");
		imgDialog = (ImageView) viewdia.findViewById(R.id.img_dialog);
		etPicYanzhengCode = (EditText) viewdia
				.findViewById(R.id.et_forget_yanzheng);
		imgPicYanzhengCode = (ImageView) viewdia
				.findViewById(R.id.tv_forget_bendi_yanzheng);
		reFresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getValidateImage();

			}
		});
		btnSure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				inputPicYanzhengCode = etPicYanzhengCode.getText().toString()
						.trim();
				getCodePassPic();

			}
		});
		imgCha.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				btnSendYanzhengCode
						.setBackgroundResource(R.drawable.white_btn_normal);
				btnSendYanzhengCode.setEnabled(true);
				btnSendYanzhengCode
						.setText(getString(R.string.refreshyanzhengcode));
			}
		});
		dialog.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					dialog.dismiss();
					btnSendYanzhengCode
							.setBackgroundResource(R.drawable.white_btn_normal);
					btnSendYanzhengCode.setEnabled(true);
					return true;
				} else {
					return false;
				}
			}
		});
		dialog.setContentView(viewdia);
		dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		dialog.show();

	}

	private void getValidateImage() {// 获取图形验证码
		showPb();
		if (!NetWorkUtil.isNetworkConnected(this)) {
			// 无网络连接 提示
			Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show();
			dissmissPb();
			imgPicYanzhengCode.setBackgroundResource(R.drawable.loading_fail);
			imgPicYanzhengCode.setImageBitmap(null);
			return;
		}
		task = new HttpConnUtil4Gionee(this);
		String host = getString(R.string.gionee_host)
				+ FinalString.RESET_REFRESH_IMAGE_URL;// 刷图形验证码、
		task.execute(host, null, HttpConnUtil4Gionee.HttpMethod.GET);

	}

	private void showPb() {
		imgDialog.setVisibility(View.VISIBLE);
		AnimationDrawable animationDrawable = (AnimationDrawable) imgDialog
				.getBackground();
		animationDrawable.start();
		imgPicYanzhengCode.setVisibility(View.GONE);
	}

	private void dissmissPb() {
		AnimationDrawable animationDrawable = (AnimationDrawable) imgDialog
				.getBackground();
		animationDrawable.stop();
		imgDialog.setVisibility(View.GONE);
		imgPicYanzhengCode.setVisibility(View.VISIBLE);
	}

	/**
	 * 
	 * @param vda
	 *            转换成图片
	 */
	public void testBase64Encoder(String vda) {
		try {
			byte[] gvCodeData = Base64.decode(vda, Base64.DEFAULT);
			Bitmap bitmap = BitmapFactory.decodeByteArray(gvCodeData, 0,
					gvCodeData.length);
			imgPicYanzhengCode.setImageBitmap(bitmap);
			imgPicYanzhengCode
					.setBackgroundResource(android.R.color.transparent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据图形验证码获取短信验证码
	 */
	private void getCodePassPic() {
		startProgressDialog();
		if (!NetWorkUtil.isNetworkConnected(this)) {
			// 无网络连接 提示
			Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show();
			stopProgressDialog();

			return;
		}
		ForgetPassword forgetPasswordReguest = new ForgetPassword();
		forgetPasswordReguest.setTn(id);
		forgetPasswordReguest.setVid(vid);
		forgetPasswordReguest.setVtx(inputPicYanzhengCode);

		String host2 = getString(R.string.gionee_host)
				+ FinalString.GET_SMS_BY_GVC_URL;// 如果图形验证码正确，则给发送短信验证码
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(
				"json",
				URLEncoder.encode(JSONUtil.getInstance().toJSON(
						forgetPasswordReguest)));
		task = new HttpConnUtil4Gionee(this);
		task.execute(host2, params, HttpConnUtil4Gionee.HttpMethod.GET);
		Log.e("lijinbao", "params " + params.toString());
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(smsReceiver);
		super.onDestroy();
	}
}
