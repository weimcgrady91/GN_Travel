package com.gionee.gntravel.service;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.gionee.gntravel.R;
import com.gionee.gntravel.entity.TripWidgetEntity;

public class WidgetTravelRemoteViewsFactory implements RemoteViewsFactory {
	private Context mContext;
	private ArrayList<TripWidgetEntity> entitys = new ArrayList<TripWidgetEntity>();
	private String mUserName;
	public WidgetTravelRemoteViewsFactory(Context context, Intent intent) {
		this.mContext = context;
	}
	public boolean flag = true;
	
	@Override
	public void onCreate() {
	}

	@Override
	public void onDataSetChanged() {
		mUserName = getUserName();
		entitys.clear();
		loadingNativeCache();
	}

	@Override
	public void onDestroy() {

	}

	@Override
	public int getCount() {
		if(entitys == null || entitys.size() == 0) {
			return 0;
		}
		return entitys.size();
	}

	@Override
	public RemoteViews getViewAt(int position) {
		
		RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_triplist_item);
		String tripName = entitys.get(position).getTripName();
		String tirpId = entitys.get(position).getTripId();
		String showName = tripName.substring(tripName.indexOf("T")+1,tripName.length());
		rv.setTextViewText(R.id.tv_tripName, showName);
//		if("1990/01/01 地球到火星(示例)".equals(tripNames.get(position))) {
//			Intent fillInIntent = new Intent();
//			fillInIntent.putExtra("tripName", tripNames.get(position));
//			fillInIntent.putExtra("userName", "123456789");
//	        rv.setOnClickFillInIntent(R.id.ll_widget_item, fillInIntent);
//		} else {
			Intent fillInIntent = new Intent();
			fillInIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			fillInIntent.putExtra("tripName", tripName);
			fillInIntent.putExtra("position", 0);
			fillInIntent.putExtra("tripId", tirpId);
			fillInIntent.putExtra("tripName", tripName);
			fillInIntent.putExtra("userName", getUserName());
	        rv.setOnClickFillInIntent(R.id.ll_widget_item, fillInIntent);
//		}

		return rv;
	}

	@Override
	public RemoteViews getLoadingView() {
		return null;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}
	
	private void loadingNativeCache() {
		if(TextUtils.isEmpty(mUserName)) {
			loadTripListExample();
			return;
		}
		
		File file = getTravelCacheFile(mUserName);
		if(!file.exists()) {
			loadTripListExample();
		} else {
			String content = readString(file);
			if(!TextUtils.isEmpty(content)) {
				parse(content);
			} else {
				loadTripListExample();
			}
		}
	}
	
	private  String readString(File file){ 
		StringBuffer sb = new StringBuffer();
		try {
			FileReader fr=new FileReader(file);
			BufferedReader bf = new BufferedReader(fr);
			String context = null;
			while( (context = bf.readLine()) != null) {
				 sb.append(context);
			}
			bf.close();
			return sb.toString();
			
		} catch (IOException e) {
			e.printStackTrace();
			
		}
		return null;
	}
	
	private File getTravelCacheFile(String fileName) {
		File files = mContext.getApplicationContext().getFilesDir();
		String filesPath = files.toString();
		File file = new File(filesPath, fileName);
		return file;
	}
	
	private void loadTripListExample() {
		InputStream is = null;
		try {
			is = mContext.getResources().getAssets().open("triplistexample.txt");
			String content = inputStream2String(is);
			parse(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public String inputStream2String(InputStream is) throws IOException{ 
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
        int i=-1; 
        while((i=is.read())!=-1){ 
        	baos.write(i); 
        } 
       return baos.toString(); 
	}
	
	public void parse(String content) {
		ArrayList<TripWidgetEntity> list = new ArrayList<TripWidgetEntity>();
		try {
			JSONObject json = new JSONObject(content);
			JSONArray arr = new JSONArray(json.getString("content"));
			for (int i = 0; i < arr.length(); i++) {
				JSONObject object = arr.getJSONObject(i);
//				list.add(object.getString("name"));
				TripWidgetEntity entity = new TripWidgetEntity();
				entity.setTripName(object.getString("name"));
				entity.setTripId(object.getString("tripId"));
				list.add(entity);
			}
			entitys = list;
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
	private boolean isLoginState(Context context) {
		SharedPreferences sp = context.getSharedPreferences("userInfo_sp",
				Context.MODE_PRIVATE);
		return sp.getBoolean("userState", false);
	}
	
	public String getUserName() {
		if(isLoginState(mContext)) {
			SharedPreferences sp = mContext.getSharedPreferences("userInfo_sp",Context.MODE_PRIVATE);
			return sp.getString("userId","");
		} else {
			TelephonyManager tm = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
			return tm.getSimSerialNumber();
		}
	}
	
}
