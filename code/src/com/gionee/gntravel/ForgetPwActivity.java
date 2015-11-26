package com.gionee.gntravel;

import java.net.URLEncoder;
import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gionee.gntravel.customview.CustomProgressDialog;
import com.gionee.gntravel.entity.ForgetPassword;
import com.gionee.gntravel.utils.FinalString;
import com.gionee.gntravel.utils.HttpConnCallback;
import com.gionee.gntravel.utils.HttpConnUtil4Gionee;
import com.gionee.gntravel.utils.JSONUtil;
import com.gionee.gntravel.utils.NetWorkUtil;
import com.gionee.gntravel.utils.Utils;

/**
 * @author lijinbao 忘记密码第一个界面，产生4随机数
 * 
 */
public class ForgetPwActivity extends Activity implements OnClickListener,
		HttpConnCallback {

	private CustomProgressDialog progressDialog;
	private TextView tvTitle;// 标题名称
	private EditText etId;// 帐号
	private ImageView imaRefrush;// 刷新验证码的图标
	private EditText etYanzhengCode;// 输入的验证码
	private ImageView imgYanzhengCode;// 产生的4位随机数
	private String inputYanzhengCode;// 输入4位随机数
	private String id;
	private Button btnGetCode;// 点击获取服务器上的验证吗
	private ImageView imgDialog;
	private HttpConnUtil4Gionee task;
	private String vid;
	public Handler handler;
	private TravelApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_pw);
		app = (TravelApplication) getApplication();
		setupView();
		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case FinalString.SMS_CODE_FINISH:
					etYanzhengCode.setText((String) msg.obj);
					break;
				}

			};
		};

	}

	private void setupView() {
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(R.string.forget_pw);
		tvTitle.setOnClickListener(this);
		etId = (EditText) findViewById(R.id.et_forget_id);
		etId.setSelection(etId.getText().length());
		imgDialog = (ImageView) findViewById(R.id.img_dialog);
		etYanzhengCode = (EditText) findViewById(R.id.et_forget_yanzheng);
		imgYanzhengCode = (ImageView) findViewById(R.id.tv_forget_bendi_yanzheng);
		imaRefrush = (ImageView) findViewById(R.id.img_refresh_yanzheng_code);
		imaRefrush.setOnClickListener(this);
		btnGetCode = (Button) findViewById(R.id.btn_forget_get_yanzheng_code);
		btnGetCode.setOnClickListener(this);
		Utils.mobileAddSpace(13, etId);

	}

	private void getValidateImage() {// 获取图形验证码
		showPb();
		if (!NetWorkUtil.isNetworkConnected(this)) {
			// 无网络连接 提示
			Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show();
			dissmissPb();
			imgYanzhengCode.setBackgroundResource(R.drawable.loading_fail);
			imgYanzhengCode.setImageBitmap(null);
			return;
		}
		task = new HttpConnUtil4Gionee(this);
		String host = getString(R.string.gionee_host)
				+ FinalString.RESET_REFRESH_IMAGE_URL;// 刷图形验证码、
		task.execute(host, null, HttpConnUtil4Gionee.HttpMethod.GET);

	}

	/**
	 * 获取短信验证码
	 */
	private void getCode() {
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
		forgetPasswordReguest.setVtx(inputYanzhengCode);

		String host2 = getString(R.string.gionee_host)
				+ FinalString.PASS_RESET_READY;// 如果图形验证码正确，则给发送短信验证码
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("json",URLEncoder.encode(JSONUtil.getInstance().toJSON(forgetPasswordReguest)));
		task = new HttpConnUtil4Gionee(this);
		task.execute(host2, params, HttpConnUtil4Gionee.HttpMethod.GET);
	}

	/**
	 * 
	 * @param vda
	 *            转换成图片
	 */
	public void testBase64Encoder(String vda) {
		try {
			byte[] gvCodeData = Base64.decode(vda, Base64.DEFAULT);
			Bitmap bitmap = BitmapFactory.decodeByteArray(gvCodeData, 0,gvCodeData.length);
			imgYanzhengCode.setImageBitmap(bitmap);
			imgYanzhengCode.setBackgroundResource(android.R.color.transparent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title:
			this.finish();
			break;
		case R.id.img_refresh_yanzheng_code:
			getValidateImage();
			break;
		case R.id.btn_forget_get_yanzheng_code:
			inputYanzhengCode = etYanzhengCode.getText().toString().trim();
			id = etId.getText().toString().trim();
			id = id.replaceAll(" ", "");
			if (TextUtils.isEmpty(id)) {
				Toast.makeText(this, R.string.id_not_null, Toast.LENGTH_SHORT).show();
			} else if (!Utils.isMobile(id)) {
				Toast.makeText(this, R.string.mobile_not_guize,Toast.LENGTH_SHORT).show();
			} else if (TextUtils.isEmpty(inputYanzhengCode)) {Toast.makeText(this, R.string.validate_not_null,	Toast.LENGTH_SHORT).show();
			} else if (inputYanzhengCode.length() != 4) {
				Toast.makeText(this, R.string.validate_error,Toast.LENGTH_SHORT).show();
			}else {
				getCode();
			}
			//etYanzhengCode.setText("");
			break;
		}
	}

	@Override
	protected void onResume() {
		etYanzhengCode.setText("");
		getValidateImage();
		super.onResume();
	}

	/**
	 * 
	 * @param result
	 *            返回结果
	 */
	private void getsmsValidate(String result) {
		try {
			JSONObject all = new JSONObject(result);
			if (!FinalString.ERRORCODE.equals(all.getString("errorCode"))) {
				Toast.makeText(this, R.string.service_error, 1).show();
				return;
			}
			String resultStr;
			if (all.has("content")) {// 得到图形验证码
				resultStr = all.getString("content");
				JSONObject content = new JSONObject(resultStr);
				vid = content.getString("vid");
				String vda = content.getString("vda");
				testBase64Encoder(vda);
				dissmissPb();
			}

			else if (all.has("s")) {// 获取短信验证码正确的话
				resultStr = all.getString("s");
				if (resultStr != null && !"".equals(resultStr)) {
					// 2 如果注册，请求服务器获取短信验证码
					Intent intent = new Intent(this,ForgetPwActivitySecond.class);
					intent.putExtra("IdForget", id);
					intent.putExtra("S", resultStr);
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					startActivityForResult(intent, 0);
				}
			} else if (all.has("r")) {// 获取短信验证码错误的话
				resultStr = all.getString("r");
				Utils.MyToast(this, resultStr);
			} else {
				Toast.makeText(this, R.string.service_error, 1).show();
			}

		} catch (Exception e) {
			Toast.makeText(this, R.string.service_error, 1).show();
		}
	}

	@Override
	public void execute(String result) {
		stopProgressDialog();
		dissmissPb();
		if (TextUtils.isEmpty(result)) {
			Toast.makeText(this, R.string.network_timeout, Toast.LENGTH_SHORT).show();
			dissmissPb();
			imgYanzhengCode.setBackgroundResource(R.drawable.loading_fail);
			imgYanzhengCode.setImageBitmap(null);
			return;
		}
		getsmsValidate(result);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == TravelApplication.FORGET_PW_SECOND) {
			setResult(TravelApplication.FORGET_PW_ONE);
			this.finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void startProgressDialog() {
		if (progressDialog == null) {
			progressDialog = CustomProgressDialog.createDialog(this,R.layout.customprogressdialog);
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

	private void showPb() {
		imgDialog.setVisibility(View.VISIBLE);
		AnimationDrawable animationDrawable = (AnimationDrawable) imgDialog.getBackground();
		animationDrawable.start();
		imgYanzhengCode.setVisibility(View.GONE);
	}

	private void dissmissPb() {
		AnimationDrawable animationDrawable = (AnimationDrawable) imgDialog.getBackground();
		animationDrawable.stop();
		imgDialog.setVisibility(View.GONE);
		imgYanzhengCode.setVisibility(View.VISIBLE);
	}

}
