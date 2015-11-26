package com.gionee.gntravel.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

import com.gionee.gntravel.db.dao.impl.NewsInfoDaoImpl;
import com.gionee.gntravel.entity.WidgetNewsEntity;
import com.gionee.gntravel.task.GeneralGetInfoTask;
import com.gionee.gntravel.utils.DBUtils;
import com.gionee.gntravel.utils.FinalString;
import com.gionee.gntravel.utils.HttpConnUtil4Gionee;
import com.gionee.gntravel.utils.TaskCallBack;

public class GetNewsInfoService extends Service implements TaskCallBack,GeneralGetInfoTask.ParseTool{

	
	
	
	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		String newsURL = FinalString.NEWS_URL;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("channel", "news_news");
		params.put("p", "2");
		params.put("s", "10");
		
		GeneralGetInfoTask task = new GeneralGetInfoTask(this,this,this);
		task.execute(newsURL, params, HttpConnUtil4Gionee.HttpMethod.GET);
		
		return super.onStartCommand(intent, flags, startId);
	}

	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void execute(Object obj) {
		ArrayList<WidgetNewsEntity> list = (ArrayList<WidgetNewsEntity>)obj;
		if(list == null || list.isEmpty()) {
			stopSelf();
			return;
		}
		DBUtils dbUitls = new DBUtils(this);
		SQLiteDatabase db = dbUitls.openReadWriteDatabase();
		NewsInfoDaoImpl newsInfoDaoImpl = new NewsInfoDaoImpl();
		newsInfoDaoImpl.insertNews(db, list);
		Intent intent = new Intent(FinalString.UPDATE_WIDGET_NEWS);
		sendBroadcast(intent);
		stopSelf();
	}

	@Override
	public ArrayList<WidgetNewsEntity> parseResult(String result) {
		ArrayList<WidgetNewsEntity> list = new ArrayList<WidgetNewsEntity>();
		try {
			JSONObject json = new JSONObject(result);
			if (!FinalString.ERRORCODE.equals(json.getString("status"))) {
				return list;
			}
			JSONObject dataJson = new JSONObject(json.getString("data"));
			JSONArray arr = new JSONArray(dataJson.getString("list"));
			for (int index = 0; index < arr.length(); index++) {
				JSONObject obj = arr.getJSONObject(index);
				WidgetNewsEntity newsEntity = new WidgetNewsEntity();
//				if(obj.has("pics")) {
//					continue;
//				}
				if(obj.has("long_title")) {
					newsEntity.setLong_title(obj.getString("long_title"));
				}
				if(obj.has("pic")) {
					newsEntity.setPic(obj.getString("pic"));
				}
				if(obj.has("pubDate")) {
					newsEntity.setPubDate(obj.getString("pubDate"));
				}
				if(obj.has("comment")) {
					newsEntity.setComment(obj.getString("comment"));
				}
				if(obj.has("link")) {
					newsEntity.setLink(obj.getString("link"));
				}
				if(obj.has("id")) {
					newsEntity.setId(obj.getString("id"));
				}
				list.add(newsEntity);
			}
			return list;
		} catch (Exception e) {
			Log.getStackTraceString(e);
			return new ArrayList<WidgetNewsEntity>();
		}
		
	}

	
}
