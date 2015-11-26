package com.gionee.gntravel;

import java.net.URLEncoder;
import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gionee.gntravel.customview.CustomProgressDialog;
import com.gionee.gntravel.entity.ResetBySMSCode;
import com.gionee.gntravel.entity.ResetRefCode;
import com.gionee.gntravel.utils.FinalString;
import com.gionee.gntravel.utils.HttpConnCallback;
import com.gionee.gntravel.utils.HttpConnUtil4Gionee;
import com.gionee.gntravel.utils.JSONUtil;
import com.gionee.gntravel.utils.NetWorkUtil;
import com.gionee.gntravel.utils.SmsBroadcastReceiver;
import com.gionee.gntravel.utils.Utils;

/**
 * @author lijinbao 忘记密码第二个界面，获取服务器验证码
 * 
 */
public class ForgetPwActivitySecond extends Activity implements
		OnClickListener, HttpConnCallback {
	private SmsBroadcastReceiver smsReceiver;
	private IntentFilter filter;
	private CustomProgressDialog progressDialog;
	private TextView tvTitle;// 标题名称
	private TextView tvNote;// 提示信息
	private EditText etYanzhengCode;
	private String inputYanzhengCode = "";// 输入的验证码
	private Button btnSendYanzhengCode;// 刷新按钮
	private Button btnNext;// 下一步按钮
	private String id = "";
	private Boolean flag = false;
	private MyThread mythread = null;
	public static Boolean smsFlag = true;// 防止弹出短信对话框后清空自动填写的内容,在此界面就不清空,跳到下一个界面设置为false
	private HttpConnUtil4Gionee task;
	private String s;
	private Handler hd = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:// 更新验证码倒计时 时间
				btnSendYanzhengCode.setText(msg.arg1+ getString(R.string.daojishi));
				break;
			case 1:// 时间到0，变回原来的样子
				btnSendYanzhengCode.setBackgroundResource(R.drawable.white_btn_normal);
				btnSendYanzhengCode.setEnabled(true);
				btnSendYanzhengCode.setText(getString(R.string.refreshyanzhengcode));
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_second);
		setupView();
		// 2发送验证码，显示 60秒后可重新获取验证码
		mythread = new MyThread();
		Thread thread = new Thread(mythread);
		thread.start();
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

	private void setupView() {
		smsFlag = true;
		Intent intent = getIntent();
		id = intent.getStringExtra("IdForget");
		s = intent.getStringExtra("S");
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(R.string.forget_pw);
		tvTitle.setOnClickListener(this);
		etYanzhengCode = (EditText) findViewById(R.id.et_forget_yanzheng_second);
		btnSendYanzhengCode = (Button) findViewById(R.id.btn_forget_yanzheng_refrash);
		btnSendYanzhengCode.setOnClickListener(this);
		btnNext = (Button) findViewById(R.id.btn_forget_next);
		btnNext.setOnClickListener(this);
		tvNote = (TextView) findViewById(R.id.tv_forget_second_note);
		tvNote.setText(getString(R.string.note_one) + id
				+ getString(R.string.note_sencond));
		btnSendYanzhengCode.setEnabled(false);
		btnSendYanzhengCode.setBackgroundResource(R.drawable.white_btn_pressed);

	}

	private void ValidateSms() {// 验证短信是否合法,最后一步
		ResetBySMSCode bySMSCodeRequest = new ResetBySMSCode();
		bySMSCodeRequest.setS(s);//
		bySMSCodeRequest.setTn(id);//
		bySMSCodeRequest.setSc(inputYanzhengCode);
		task = new HttpConnUtil4Gionee(this);
		String host = getString(R.string.gionee_host)
				+ FinalString.RESET_VALIDATE_SMS_URL;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(
				"json",
				URLEncoder.encode(JSONUtil.getInstance().toJSON(
						bySMSCodeRequest)));
		task.execute(host, params, HttpConnUtil4Gionee.HttpMethod.GET);
	}

	/**
	 * 重新获取短信验证码
	 */
	private void resetSms() {

		ResetRefCode refCodeRequset = new ResetRefCode();
		refCodeRequset.setS(s);
		task = new HttpConnUtil4Gionee(this);
		String host = getString(R.string.gionee_host)
				+ FinalString.RESET_REFRUSH_SMS_URL;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("json", URLEncoder.encode(JSONUtil.getInstance().toJSON(
				refCodeRequset)));
		task.execute(host, params, HttpConnUtil4Gionee.HttpMethod.GET);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title:
			this.finish();
			break;
		case R.id.btn_forget_yanzheng_refrash:
			startProgressDialog();
			smsFlag=true;
			if (!NetWorkUtil.isNetworkConnected(this)) {
				// 无网络连接 提示
				Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG)
						.show();
				stopProgressDialog();
				return;
			}
			// 2发送验证码，显示 60秒后可重新获取验证码
			resetSms();

			break;

		case R.id.btn_forget_next:
			startProgressDialog();
			if (!NetWorkUtil.isNetworkConnected(this)) {
				// 无网络连接 提示
				Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG)
						.show();
				stopProgressDialog();
				return;
			}
			inputYanzhengCode = etYanzhengCode.getText().toString().trim();
			ValidateSms();

			break;
		}
	}

	@Override
	protected void onResume() {
		if (flag) {
			if (!smsFlag) {
				etYanzhengCode.setText("");// 弹出短信对话框时候的影响，所以加个判断
			}
			mythread.stopTime();
		}
		flag = true;
		// etYanzhengCode.addTextChangedListener(mTextWatcher);
		super.onResume();
	}

	@Override
	public void execute(String result) {
		getValidateCodeAndSMSResult(result);
	}

	/**
	 * 
	 * @param result
	 *            返回结果
	 */
	private void getValidateCodeAndSMSResult(String result) {
		stopProgressDialog();
		if (TextUtils.isEmpty(result)) {
			Toast.makeText(this, R.string.network_timeout, 1).show();
			return;
		}
		try {
			JSONObject all = new JSONObject(result);
			if (!FinalString.ERRORCODE.equals(all.getString("errorCode"))) {
				Toast.makeText(this, R.string.service_error, 1).show();
				return;
			}
			String resultStr = all.getString("r");
			if ("00005".equals(resultStr)) {
				// 成功
				Toast.makeText(this, R.string.yanzhengma_send, 1).show();
				mythread = new MyThread();
				Thread thread = new Thread(mythread);
				thread.start();
				btnSendYanzhengCode.setEnabled(false);
				btnSendYanzhengCode
						.setBackgroundResource(R.drawable.white_btn_pressed);
			} else if ("00006".equals(resultStr)) {
				// 跳转到 输入密码界面
				Intent intent = new Intent(this, ForgetPwActivityFinish.class);
				intent.putExtra("S", s);
				intent.putExtra("IdForget", id);
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivityForResult(intent, 0);
			} else {
				Utils.MyToast(this, resultStr);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == TravelApplication.FORGET_PW_THIRD) {
			setResult(TravelApplication.FORGET_PW_SECOND);
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(smsReceiver);
	}
}
