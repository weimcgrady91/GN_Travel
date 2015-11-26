package com.gionee.gntravel.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferenceUtils {
	private SharedPreferences sp;
	private Editor editor;
	
	@SuppressLint("CommitPrefEdits")
	public SharedPreferenceUtils(Context context, String name) {
		sp = context.getSharedPreferences(name, Context.MODE_APPEND);
		editor = sp.edit();
	}
	
	public void putString(String name, String value) {
		editor.putString(name, value).commit();
	}

	public String getString(String name) {
		return sp.getString(name, "");
	}
	
	public void putBoolean(String name, boolean value) {
		editor.putBoolean(name, value).commit();
	}
	
	public boolean getBoolean(String name) {
		return sp.getBoolean(name, false);
	}
	
	public void clearData() {
		editor.clear().commit();
	}
	
	public void putLong(String name, long value) {
		editor.putLong(name, value).commit();
	}
	
	public long getLong(String name) {
		return sp.getLong(name, -1);
	}
}
