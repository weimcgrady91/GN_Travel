package com.gionee.gntravel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gionee.gntravel.utils.Utils;

/**
 * 
 * @author lijinbao 修改昵称
 */
public class EditNickNameActivity extends Activity implements OnClickListener {
	private TextView tvTitle;// 标题名称
	private Button btnSure;
	private EditText edNickName;
	private String name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_nick_name);
		setupView();
	}

	private void setupView() {
		Intent intent = getIntent();
		name = intent.getStringExtra("editNickName");
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(R.string.edit_nickname);
		tvTitle.setOnClickListener(this);
		btnSure = (Button) findViewById(R.id.btn_edit_nick_name);
		btnSure.setOnClickListener(this);
		edNickName = (EditText) findViewById(R.id.et_nick_name);
		edNickName.setText(name);
		edNickName.requestFocus();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title:
			Utils.closeSoftware(this);
			Utils.showDialog(this, new EditText[]{edNickName});
			break;

		case R.id.btn_edit_nick_name:
			editNiakName();
			break;
		}
	}

	private void editNiakName() {
		name = edNickName.getText().toString().trim();
		if (TextUtils.isEmpty(name)) {
			Toast.makeText(this, R.string.nick_name_null, Toast.LENGTH_SHORT)
					.show();
		} else if (name.length() > 15) {
			Toast.makeText(this, R.string.nick_name_limit, Toast.LENGTH_SHORT)
					.show();
		} else {

			Intent intent = new Intent();
			intent.putExtra("editNickNameBack", name);
			setResult(InfomationActivity.EDIT_NAME, intent);
			Utils.closeSoftware(this);
			this.finish();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (4 == event.getKeyCode()) {
			Utils.showDialog(this, new EditText[]{edNickName});
		}
		return super.onKeyDown(keyCode, event);
	}
}
