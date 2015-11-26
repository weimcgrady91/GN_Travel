package com.gionee.gntravel;

import com.youju.statistics.YouJuAgent;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

public abstract class DetailsActivity extends Activity implements
		View.OnClickListener {
	private int layoutid = 0;
	private Dialog dialog;

	public DetailsActivity(){}
	
	public DetailsActivity(int layoutid) {
		this.layoutid = layoutid;
	}

	protected abstract void payment();

	protected abstract String curstomTitle();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		TextView title = (TextView) findViewById(R.id.tv_title);
		title.setText(curstomTitle());
		View phoned = (View) findViewById(R.id.phoned);
		phoned.setOnClickListener(this);
		title.setOnClickListener(this);
		ViewGroup container = (ViewGroup) findViewById(R.id.container);
		LayoutInflater inflater = LayoutInflater.from(this);
		container.addView(inflater.inflate(layoutid, null));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.phoned:
			showCustomServiceDialog();
			YouJuAgent.onEvent(this, getString(R.string.youju_kefu));
			break;
		case R.id.tv_title:
			finish();
			break;
		default:
			break;
		}
	}

	private void showCustomServiceDialog() {
		dialog = new Dialog(this, R.style.CustomProgressDialog);
		View viewDailog = LayoutInflater.from(this).inflate(
				R.layout.delete_dialog, null);
		RelativeLayout reDeLayout = (RelativeLayout) viewDailog
				.findViewById(R.id.re_dialog);
		TextView title = (TextView) viewDailog
				.findViewById(R.id.tv_dialog_title_name);
		title.setText(this.getString(R.string.custom_dialog_title));
		TextView tcContent = (TextView) viewDailog
				.findViewById(R.id.tv_dialog_title);
		tcContent.setText(this.getString(R.string.custom_dialog_content));
		Button btnCalcel = (Button) viewDailog
				.findViewById(R.id.btn_dialog_cancel);
		btnCalcel.setText(this.getString(R.string.cancel));
		Button btnOk = (Button) viewDailog.findViewById(R.id.btn_dialog_sure);
		btnOk.setText(this.getString(R.string.call));
		btnCalcel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				callCustomService();
				dialog.dismiss();
			}
		});
		reDeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.setContentView(viewDailog);
		dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		dialog.show();
	}

	private void callCustomService() {
		Intent phoneIntent = new Intent("android.intent.action.CALL",
				Uri.parse("tel:"
						+ getString(R.string.custom_service_phoneNumber)));
		phoneIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(phoneIntent);
	}

}
