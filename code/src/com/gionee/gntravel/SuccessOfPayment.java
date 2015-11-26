package com.gionee.gntravel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SuccessOfPayment extends Activity implements OnClickListener{

	private Button btn_finish;
	
	// test start
	private TravelApplication app ;
	//end
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_successofpayment);
		btn_finish = (Button) findViewById(R.id.btn_finish);
		btn_finish.setOnClickListener(this);
		app = (TravelApplication) getApplication();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.btn_finish:
			app.setLoginState(true);
			Intent intent = new Intent();
			intent.setClass(SuccessOfPayment.this, MainTabActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		default:
			
			break;
		}
	}

}
