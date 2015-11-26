package com.gionee.gntravel;

import java.net.URLEncoder;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gionee.gntravel.customview.CustomProgressDialog;
import com.gionee.gntravel.entity.EditPassWord;
import com.gionee.gntravel.utils.FinalString;
import com.gionee.gntravel.utils.HttpConnCallback;
import com.gionee.gntravel.utils.HttpConnUtil4Gionee;
import com.gionee.gntravel.utils.JSONUtil;
import com.gionee.gntravel.utils.Utils;

/**
 * 
 * @author lijinbao 修改密码
 */
public class EditPwActivity extends Activity implements OnClickListener,
		HttpConnCallback {
	private TextView tvTitle;// 标题名称
	private EditText etNowPw;// 当前密码
	private EditText etNewPw;// 新密码
	private Button btnFinish;// 完成按钮
	private ImageView imgNew;// 新密码 明文暗纹
	private String id;
	private String nowPw;// 当前密码
	private String newPw;// 新密码
	private int statusImgNew = 1;// 新密码1 为暗文 2为明文
	private HttpConnUtil4Gionee task;
	private TravelApplication app;
	private CustomProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_pw);
		app = (TravelApplication) getApplication();
		setupView();
	}

	private void setupView() {
		id = app.getUserInfo_sp().getString("userName");
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(R.string.edit_pw);
		tvTitle.setOnClickListener(this);
		etNowPw = (EditText) findViewById(R.id.et_edit_now_pw);
		etNewPw = (EditText) findViewById(R.id.et_edit_new_pw);
		imgNew = (ImageView) findViewById(R.id.img_new_ming_or_an);
		imgNew.setOnClickListener(this);
		btnFinish = (Button) findViewById(R.id.btn_edit_pw);
		btnFinish.setOnClickListener(this);
	}

	/**
	 * 输入的密码明文暗纹切换
	 */
	private void passwordstatus() {
		int lengthNew;
		int lengthNow;
		if (statusImgNew == 1) {
			// 输入密码 是暗文
			imgNew.setBackgroundResource(R.drawable.ming_code);
			statusImgNew = 2;
			etNowPw.setInputType(InputType.TYPE_CLASS_TEXT);
			lengthNow = etNowPw.getText().toString().length();
			etNowPw.setSelection(lengthNow);
			etNewPw.setInputType(InputType.TYPE_CLASS_TEXT);
			lengthNew = etNewPw.getText().toString().length();
			etNewPw.setSelection(lengthNew);
		} else {
			imgNew.setBackgroundResource(R.drawable.an_code);
			statusImgNew = 1;
			etNewPw.setInputType(InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_VARIATION_PASSWORD);
			lengthNew = etNewPw.getText().toString().length();
			etNewPw.setSelection(lengthNew);
			etNowPw.setInputType(InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_VARIATION_PASSWORD);
			lengthNow = etNowPw.getText().toString().length();
			etNowPw.setSelection(lengthNow);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.img_new_ming_or_an:
			passwordstatus();

			break;
		case R.id.tv_title:
			Utils.closeSoftware(this);
			this.finish();
			break;

		case R.id.btn_edit_pw:
			nowPw = etNowPw.getText().toString().trim();
			newPw = etNewPw.getText().toString().trim();
			if (TextUtils.isEmpty(nowPw)) {
				Toast.makeText(this, R.string.now_pw_null, Toast.LENGTH_LONG).show();
			} else if (TextUtils.isEmpty(newPw)) {
				Toast.makeText(this, R.string.new_pw_null, Toast.LENGTH_LONG).show();
			} else if ((newPw.length() < 4 || newPw.length() > 16)|| (nowPw.length() < 4 || nowPw.length() > 16)) {
				Toast.makeText(this, getString(R.string.passwordguize),Toast.LENGTH_LONG).show();
			} else if (newPw.equals(nowPw)) {
				Toast.makeText(this, getString(R.string.passwordguize2),Toast.LENGTH_LONG).show();
			} else if(!Utils.isPassword(newPw)){
				Toast.makeText(this, R.string.passwordguize, Toast.LENGTH_SHORT).show();
			}else {
				editPw();
			}
			break;
		}

	}
	/**
	 * 修改密码
	 */
	private void editPw() {
		startProgressDialog();
		String host = getString(R.string.gionee_host)
				+ FinalString.EDIT_REFRUSH_SMS_URL;
		EditPassWord editPassWordRequest = new EditPassWord();
		editPassWordRequest.setTn(id);
		editPassWordRequest.setP(nowPw);
		editPassWordRequest.setNp(newPw);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(
				"json",
				URLEncoder.encode(JSONUtil.getInstance().toJSON(
						editPassWordRequest)));
		task = new HttpConnUtil4Gionee(this);
		task.execute(host, params, HttpConnUtil4Gionee.HttpMethod.GET);

	}

	@Override
	public void execute(String result) {
		stopProgressDialog();
		if (TextUtils.isEmpty(result)) {
			Toast.makeText(this, R.string.network_timeout, 1).show();

			return;
		}
		try {
			JSONObject all = new JSONObject(result);
			if (!FinalString.ERRORCODE.equals(all
					.getString("errorCode"))) {
				Toast.makeText(this, R.string.service_error, 1).show();
				return;
			}
			String resultStr = all.getString("r");
			if ("00003".equals(resultStr)) {
				Toast.makeText(this, R.string.edit_pw_suc, Toast.LENGTH_LONG)
						.show();
				this.finish();
			} else {// 该帐号没有注册，可以给用户发送验证码
				Utils.MyToast(this, resultStr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

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
