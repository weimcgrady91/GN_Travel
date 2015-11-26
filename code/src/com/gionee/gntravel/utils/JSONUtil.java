package com.gionee.gntravel.utils;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSONUtil {
	private static JSONUtil jsonUtil;
	private Gson gson;

	private JSONUtil() {
		GsonBuilder gb = new GsonBuilder();
		gb.serializeNulls();
		gson = gb.create();
	}

	public String toJSON(Object obj) {
		if (obj == null) {
			return "";
		}
		return gson.toJson(obj);
	}

	public <T> T fromJSON(Class<T> clazz, String json) {
		if (json == null) {
			return null;
		}
		return gson.fromJson(json, clazz);
	}

	public <T> T fromJSON(Type type, String json) {
		if (json == null) {
			return null;
		}
		return gson.fromJson(json, type);
	}

	public synchronized static JSONUtil getInstance() {
		if (jsonUtil == null) {
			jsonUtil = new JSONUtil();
		}
		return jsonUtil;
	}
}
