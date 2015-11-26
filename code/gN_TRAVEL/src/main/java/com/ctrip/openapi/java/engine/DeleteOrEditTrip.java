package com.ctrip.openapi.java.engine;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.gionee.gntravel.TravelApplication;
import com.gionee.gntravel.utils.FinalString;
import com.gionee.gntravel.utils.HttpConnCallback;

public class DeleteOrEditTrip implements HttpConnCallback {
	
	public Handler handler;
	public Context context;
	public TravelApplication app;
	public DeleteOrEditTrip(Handler handler, Context context,TravelApplication app) {
		this.handler = handler;
		this.context = context;
		this.app = app;
	}
	@Override
	public void execute(String result) {
		if (TextUtils.isEmpty(result)) {
			Message msg = Message.obtain();
			msg.what = FinalString.WEBSERVICE_ERROR;
			handler.sendMessage(msg);
			return;
		}
		handler.post(new DoResult(result));
	}
	
	private class DoResult implements Runnable {
		private String result;

		public DoResult(String result) {
			this.result = result;
		}

		@Override
		public void run() {
			try {	
				JSONObject responseJson = new JSONObject(result);
				if(!FinalString.ERRORCODE.equals(responseJson.getString("errorCode"))) {
					Message msg = Message.obtain();
					msg.what = FinalString.WEBSERVICE_ERROR;
					handler.sendMessage(msg);
					return;
				} else {
					Message msg = Message.obtain();
					msg.what = FinalString.MODIFY_SUCC;
					handler.sendMessage(msg);
				}
			} catch (JSONException e) {
				Message msg = Message.obtain();
				msg.what = FinalString.PARSE_ERROR;
				handler.sendMessageDelayed(msg, 1000);
			}
		}
	}
	
}
