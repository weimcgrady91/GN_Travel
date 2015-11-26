package com.gionee.gntravel.service;

import java.util.HashMap;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.text.TextUtils;

import com.gionee.gntravel.R;
import com.gionee.gntravel.utils.FinalString;
import com.gionee.gntravel.utils.HttpConnCallback;
import com.gionee.gntravel.utils.HttpConnUtil4Gionee;

public class SendRidToAPS extends Service implements HttpConnCallback{

	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Thread sendRidThread = new Thread(new SendRid()) ;
		sendRidThread.start();
		
	}
	
    private String readRid(Context mContext) {
        String rid = null;
        SharedPreferences mPreference = mContext.getSharedPreferences("config_sp", Context.MODE_PRIVATE);
        rid = mPreference.getString("rid", "-1");
        return rid;
    }
    
	public class SendRid implements Runnable {

		@Override
		public void run() {
			HashMap<String, String> params = new HashMap<String, String>();
			params.clear();
			HttpConnUtil4Gionee task = new HttpConnUtil4Gionee(SendRidToAPS.this);
			String rid = readRid(SendRidToAPS.this);
			if(!TextUtils.isEmpty(rid)) {
				params.put("rid", rid );
				String url = getString(R.string.gionee_host)+FinalString.SEND_RID_TO_APS;
				task.execute(url, params, HttpConnUtil4Gionee.HttpMethod.POST);
			}
			stopSelf();
		}
		
	}

	@Override
	public void execute(String result) {
		
	}
	
	
}

