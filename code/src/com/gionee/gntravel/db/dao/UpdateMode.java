package com.gionee.gntravel.db.dao;

import com.gionee.gntravel.entity.UpdateEntity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UpdateMode {

	public static void saveUpdateInfo(Context context, UpdateEntity entity) {
		SharedPreferences sPreferences = context.getSharedPreferences("config_sp", Context.MODE_APPEND);
		Editor edit = sPreferences.edit();
		edit.putString("apkName", entity.getApkName());
		edit.putString("downloadPath", entity.getDownloadPath());
		edit.putString("upDateMessage", entity.getUpDateMessage());
		edit.commit();
	}

	public static UpdateEntity getUpdateInfo(Context context) {
		UpdateEntity updateEntity = new UpdateEntity();
		SharedPreferences sPreferences = context.getSharedPreferences("config_sp", Context.MODE_APPEND);
		updateEntity.setApkName(sPreferences.getString("apkName", ""));
		updateEntity.setDownloadPath(sPreferences.getString("downloadPath", ""));
		updateEntity.setUpDateMessage(sPreferences.getString("upDateMessage", ""));
		return updateEntity;
	}
}
