package com.gionee.gntravel.utils;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.Message;
import android.sax.StartElementListener;
import android.text.TextUtils;
import android.util.Log;

import com.gionee.gntravel.R;
import com.gionee.gntravel.UpdateActivity;
import com.gionee.gntravel.db.dao.UpdateMode;
import com.gionee.gntravel.entity.UpdateEntity;
import com.gionee.gntravel.task.GeneralGetInfoTask;
import com.google.gson.JsonObject;

public class UpdateThread implements Runnable,TaskCallBack,GeneralGetInfoTask.ParseTool {

	private Context mContext;
	private Handler mHandler ;
	public UpdateThread(Context context) {
		mContext = context;
	}
	
	public UpdateThread(Context context, Handler handler) {
		mContext = context;
		mHandler = handler;
	}
	
	@Override
	public void run() {
		isNeedUpdate();
	}
	
	private void isNeedUpdate() {
		if (!NetWorkUtil.isNetworkConnected(mContext)) {
			if(mHandler != null) {
				mHandler.sendEmptyMessage(FinalString.NET_ERROR);
			}
			return;
		}
		String checkUpdateUrl = mContext.getString(R.string.gionee_host) + FinalString.UPDATE_ACTION;
		GeneralGetInfoTask task = new GeneralGetInfoTask(mContext,this,this);
		task.execute(checkUpdateUrl, null, HttpConnUtil4Gionee.HttpMethod.POST);
	}
	
	@Override
	public Object parseResult(String result) {
		if(TextUtils.isEmpty(result)) {
			return null;
		} 
		
		try {
			UpdateEntity updateEntity = new UpdateEntity();
			JSONObject jsonObject = new JSONObject(result);
			if(!jsonObject.isNull("errorCode")) {
				if(jsonObject.getInt("errorCode") == 0) {
					String content = jsonObject.getString("content");
					JSONObject contentObj = new JSONObject(content);
					
					if(!contentObj.isNull("verCode")) {
						updateEntity.setVersionCode(contentObj.getInt("verCode"));
					}

					if(!contentObj.isNull("content")) {
						updateEntity.setUpDateMessage(contentObj.getString("content"));
					}
					if(!contentObj.isNull("url")) {
						updateEntity.setDownloadPath(contentObj.getString("url"));
					}
					return updateEntity;
				} else {
					return null;
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		} 
		return null;
	}
	

	@Override
	public void execute(Object obj) {
		if(obj != null) {
			UpdateEntity updateEntity = (UpdateEntity)obj;
			int currVersionCode = getAppCurrentVersonCode();
			int serverCurrVersionCode = updateEntity.getVersionCode();
			if(serverCurrVersionCode > currVersionCode) {
				UpdateMode.saveUpdateInfo(mContext,updateEntity);
				Intent intent = new Intent();
				intent.setClass(mContext, UpdateActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(intent);
			} else {
				if(mHandler != null) {
					Message msg = mHandler.obtainMessage();
					msg.what = FinalString.UPDATE;
					mHandler.sendMessage(msg);
				}
			}
		} else {
			if(mHandler != null) {
				mHandler.sendEmptyMessage(FinalString.UPDATE);
			}
		}
		
	}

	private int getAppCurrentVersonCode() {
		int verCode = -1;
		try {
			verCode = mContext.getPackageManager().getPackageInfo("com.gionee.gntravel", 0).versionCode;
		} catch (NameNotFoundException e) {
			Log.e("weiqun12345", "packageInfo not found");
		}
		return verCode;
	}
}
