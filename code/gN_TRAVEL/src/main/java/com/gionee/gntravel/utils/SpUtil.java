package com.gionee.gntravel.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class SpUtil {
	private SharedPreferences sp = null;

	public SpUtil(Context context) {
		this(context, null);
	}

	public SpUtil(Context context, String name) {
		if (TextUtils.isEmpty(name)) {
			sp = context.getSharedPreferences("default_sp",
					Context.MODE_PRIVATE);
		} else {
			sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
		}
	}

	public void putString(String key, String value) {
		if (TextUtils.isEmpty(key)) {
			throw new RuntimeException("putString key 不能为空！");
		}
		sp.edit().putString(key, value).commit();
	}

	public String getString(String key) {
		if (TextUtils.isEmpty(key)) {
			throw new RuntimeException("getString key 不能为空！");
		}
		return sp.getString(key, null);
	}

	public void putBoolean(String key, boolean value) {
		if (TextUtils.isEmpty(key)) {
			throw new RuntimeException("putString key 不能为空！");
		}
		sp.edit().putBoolean(key, value).commit();
	}
	
	public boolean getBoolean(String key) {
		if (TextUtils.isEmpty(key)) {
			throw new RuntimeException("getString key 不能为空！");
		}
		return sp.getBoolean(key, false);
	}

	public void clearSp() {
		sp.edit().clear().commit();
	}
}
