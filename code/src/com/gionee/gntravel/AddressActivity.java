package com.gionee.gntravel;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.gionee.gntravel.db.dao.MaiingAddressDao;
import com.gionee.gntravel.db.dao.impl.MailAddressDaoImpl;
import com.gionee.gntravel.entity.AddressEntity;
import com.gionee.gntravel.utils.DBUtils;
import com.gionee.gntravel.utils.Utils;

/**
 * 
 * @author lijinbao 飞机票报销凭证地址填写界面
 */
public class AddressActivity extends Activity implements OnClickListener {
	private Button btnSure;// 返回按钮
	private TextView tvTitle;// 标题
	private EditText etName; // 收件人姓名
	private EditText etMobile;// 收件人手机号
	private EditText etMailCode;// 收件人邮政编码
	private EditText etAddress;// 收件人地址
	private ScrollView scrollView;
	/*
	 * 以下4个是//传回去的值
	 */
	private String name;
	private String mobile;
	private String address;
	private String mailCode;
	private Dialog dialog;
	public static final int ADDRESS = 0;
	private TravelApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address);
		setupView();
		findMailAddress();
	}

	/*
	 * 实例化控件
	 */
	private void setupView() {
		app = (TravelApplication) getApplication();
		Intent intent = getIntent();
		name = intent.getStringExtra("intoAddressName");
		mobile = intent.getStringExtra("intoAddressMobile");
		mailCode = intent.getStringExtra("intoAddressMailCode");
		address = intent.getStringExtra("intoAddress");
		scrollView = (ScrollView) findViewById(R.id.sv);
		findViewById(R.id.tv_title).setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(getString(R.string.address_message));
		btnSure = (Button) findViewById(R.id.btn_function);
		btnSure.setText(getString(R.string.finish));
		btnSure.setOnClickListener(this);
		etName = (EditText) findViewById(R.id.et_address_name);
		etMobile = (EditText) findViewById(R.id.et_address_mobile);
		etMailCode = (EditText) findViewById(R.id.et_maincode);
		etMailCode.setOnClickListener(this);
		etAddress = (EditText) findViewById(R.id.et_address);
		etAddress.setOnClickListener(this);
		Utils.mobileAddSpace(13, etMobile);
		etAddress.setText(address);
		etMailCode.setText(mailCode);
		etName.setText(name);
		etMobile.setText(mobile);
	}

	/*
	 * 传回去的所有值的信息
	 */
	private void getMessage() {
		Intent intent = new Intent();

		name = etName.getText().toString();
		mobile = etMobile.getText().toString();
		mailCode = etMailCode.getText().toString();
		address = etAddress.getText().toString();
		String cancelKongGe = mobile.replaceAll(" ", "");
		if (Utils.limitName(this, name)) {
			if (Utils.limitMobile(this, cancelKongGe)) {

				if (mailCode == null || "".equals(mailCode)) {
					Toast.makeText(this, R.string.maincode_null,
							Toast.LENGTH_SHORT).show();
				} else if (mailCode.length() != 6) {
					Toast.makeText(this, R.string.maincode_error,
							Toast.LENGTH_SHORT).show();
				}

				else if (address == null || "".equals(address)) {
					Toast.makeText(this, R.string.address_null,
							Toast.LENGTH_SHORT).show();
				}

				else {
					saveMailAddress();
					intent.putExtra("addressName", name);
					intent.putExtra("addressMobile", mobile);
					intent.putExtra("addressMailCode", mailCode);
					intent.putExtra("address", address);
					setResult(ADDRESS, intent);
					Utils.closeSoftware(this);
					this.finish();
				}
			}
		}
	}
	private void findMailAddress(){
		try {
			DBUtils dbUitls = new DBUtils(this);
			SQLiteDatabase db = dbUitls.openReadOnlyDatabase();
			MaiingAddressDao dao = new MailAddressDaoImpl();
			AddressEntity addressEntity=dao.findMailAddressByUserId(db, app.getU());
			if(addressEntity!=null){
				etName.setText(addressEntity.getName());
				etMailCode.setText(addressEntity.getMailCode());
				etMobile.setText(addressEntity.getMobile());
				etAddress.setText(addressEntity.getAddress());
			}
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 保存邮寄地址等信息
	 */
	private void saveMailAddress() {
		try {
			DBUtils dbUitls = new DBUtils(this);
			SQLiteDatabase db = dbUitls.openReadWriteDatabase();
			MaiingAddressDao dao = new MailAddressDaoImpl();
			dao.delectMailAddressByUserId(db, app.getU());
			dao.insertMailAddressByUserId(db, app.getU(), name, mobile, "0",
					address, mailCode);
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title:
			Utils.closeSoftware(this);
//			Utils.showDialog(this, new EditText[] { etName, etMobile,
//					etMailCode, etAddress });
			this.finish();
			break;
		case R.id.btn_function:
			getMessage();

			break;
		case R.id.et_address:
			scrollView.post(new Runnable() {
				public void run() {
					scrollView.fullScroll(ScrollView.FOCUS_DOWN);
				}
			});
			break;
		case R.id.et_maincode:
			scrollView.post(new Runnable() {
				public void run() {
					scrollView.fullScroll(ScrollView.FOCUS_DOWN);
				}
			});
		default:
			break;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (4 == event.getKeyCode()) {
//			Utils.showDialog(this, new EditText[] { etName, etMobile,
//					etMailCode, etAddress });
			this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
