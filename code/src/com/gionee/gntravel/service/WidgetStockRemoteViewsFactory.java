package com.gionee.gntravel.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.gionee.gntravel.H5Activity;
import com.gionee.gntravel.R;
import com.gionee.gntravel.entity.WidgetStockEntity;
import com.gionee.gntravel.utils.FinalString;
import com.gionee.gntravel.utils.HttpConnCallback;
import com.gionee.gntravel.utils.HttpConnUtil4Gionee;

public class WidgetStockRemoteViewsFactory implements RemoteViewsFactory ,HttpConnCallback{
	private Context mContext;
	private ArrayList<WidgetStockEntity> stockList = new ArrayList<WidgetStockEntity>();
	private boolean stockflag = true;
	@SuppressWarnings("unchecked")
	public WidgetStockRemoteViewsFactory(Context context, Intent intent) {
		this.mContext = context;
	}
	
	@Override
	public void onCreate() {

	}

	@Override
	public void onDataSetChanged() {
		stockflag = true;
		stockList.clear();
		loadingData();
		while(stockflag) {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void onDestroy() {

	}

	@Override
	public int getCount() {
		return stockList.size();
	}

	@Override
	public RemoteViews getViewAt(int position) {
		RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_stock_item);
		rv.setTextViewText(R.id.tv_name, stockList.get(position).getName());
		rv.setTextViewText(R.id.tv_price, stockList.get(position).getRecent());
		rv.setTextViewText(R.id.tv_rose, stockList.get(position).getChangeratio());
		
		Intent intent = new Intent(mContext, H5Activity.class);
		intent.putExtra("url", stockList.get(position).getUrl());
        rv.setOnClickFillInIntent(R.id.ll_stock_item, intent);
		
		return rv;
	}

	@Override
	public RemoteViews getLoadingView() {
		return null;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	
	@Override
	public void execute(String result) {
		if (TextUtils.isEmpty(result)) {
			stockflag = false;
			return;
		}
		
		try {
			JSONObject responseJson = new JSONObject(result);
			if (FinalString.ERRORCODE.equals(responseJson.getString("status"))) {
				stockflag = false;
				return;
			}
			stockList = new ArrayList<WidgetStockEntity>();
			JSONArray arr = new JSONArray(responseJson.getString("data"));
			for (int index = 0; index < arr.length(); index++) {
				JSONObject obj = arr.getJSONObject(index);
				WidgetStockEntity stockEntity = new WidgetStockEntity();
				stockEntity.setName(obj.getString("name"));
				stockEntity.setRecent(obj.getString("recent"));
				stockEntity.setChangeratio(obj.getString("changeratio"));
				stockEntity.setChange(obj.getString("change"));
				stockEntity.setStatus(obj.getString("status"));
				stockEntity.setUrl(obj.getString("url"));
				stockList.add(stockEntity);
			}
			
		} catch (JSONException e) {
			Log.getStackTraceString(e);
			stockflag = false;
		} finally {
			stockflag = false;
		}
	}

	public void loadingData() {
//		String stockURL = "http://open.api.sina.cn/finance/shlist?limit=6&appkey=d9181a67&page=1&secret=b7542122";
		String stockURL = "http://open.api.sina.cn/finance/shlist";
		HashMap<String, String> params = new HashMap<String, String>();
//		appkey=d9181a67&secret=b7542122&page=1&limit=2
		params.put("appkey", "d9181a67");
		params.put("secret", "b7542122");
		params.put("page", "1");
		params.put("limit", "10");
		params.put("wm", "3164_0012");
		HttpConnUtil4Gionee task = new HttpConnUtil4Gionee(this);
		task.execute(stockURL, params, HttpConnUtil4Gionee.HttpMethod.GET);
	}
}
