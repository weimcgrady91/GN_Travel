package com.gionee.gntravel.widget;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.gionee.gntravel.H5Activity;
import com.gionee.gntravel.NewRouteActivity;
import com.gionee.gntravel.R;
import com.gionee.gntravel.SplashActvity;
import com.gionee.gntravel.TripFormActivity;
import com.gionee.gntravel.service.GetNewsInfoService;
import com.gionee.gntravel.service.WidgetNewsRemoteViewsService;
import com.gionee.gntravel.service.WidgetTravelRemoteViewsService;
import com.gionee.gntravel.utils.FinalString;

public class TravelWidget extends AppWidgetProvider {

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		if (intent != null) {
			RemoteViews updateView = new RemoteViews(context.getPackageName(),R.layout.widget);
			if(FinalString.UPDATE_WIDGET.equals(intent.getAction())
					|| FinalString.USER_LOGIN_ACTION.equals(intent.getAction())
					|| FinalString.USER_CHECKOUT_ACTION.equals(intent.getAction())) {
				AppWidgetManager awg = AppWidgetManager.getInstance(context);
				updateTravelList(context, awg);
				return;
			}
			
			if (FinalString.WIDGET_SHOW_TRAVEL.equals(intent.getAction())) {
				updateView.setViewVisibility(R.id.ll_travel_container, View.VISIBLE);
//				updateView.setImageViewResource(R.id.img_showTravel,R.drawable.widget_title_left);
				updateView.setViewVisibility(R.id.ll_news_container, View.INVISIBLE);
//				updateView.setImageViewResource(R.id.img_showNews, 0);
				updateView.setViewVisibility(R.id.ll_stock_container, View.INVISIBLE);
//				updateView.setImageViewResource(R.id.img_showStock, 0);
				AppWidgetManager awg = AppWidgetManager.getInstance(context);
				int[] appWidgetIds =  awg.getAppWidgetIds(new ComponentName(context, TravelWidget.class));
				awg.updateAppWidget(appWidgetIds,updateView);
				return;
			}
			if (FinalString.WIDGET_SHOW_NEWS.equals(intent.getAction())) {
				
				updateView.setViewVisibility(R.id.ll_news_container, View.VISIBLE);
//				updateView.setImageViewResource(R.id.img_showNews,R.drawable.widget_title_middle);
				updateView.setViewVisibility(R.id.ll_travel_container, View.INVISIBLE);
//				updateView.setImageViewResource(R.id.img_showTravel, 0);
				updateView.setViewVisibility(R.id.ll_stock_container, View.INVISIBLE);
//				updateView.setImageViewResource(R.id.img_showStock, 0);
				AppWidgetManager awg = AppWidgetManager.getInstance(context);
				int[] appWidgetIds =  awg.getAppWidgetIds(new ComponentName(context, TravelWidget.class));
				awg.updateAppWidget(appWidgetIds,updateView);
				return;
			}
			if(FinalString.UPDATE_WIDGET_NEWS.equals(intent.getAction())) {
				
				AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
				updateNewsList(context, appWidgetManager);
				return;
			}
			
			if (FinalString.WIDGET_SHOW_STOCK.equals(intent.getAction())) {
//				Intent stockIntent = new Intent();
//				stockIntent.setClass(context, WidgetStockRemoteViewsService.class);
//				updateView.setRemoteAdapter(R.id.stockListView, stockIntent);
//		        Intent stockClickIntent = new Intent(context, H5Activity.class); 
//		        PendingIntent stockPendingIntent = PendingIntent.getActivity(context, 0, stockClickIntent, 0);
//		        updateView.setPendingIntentTemplate(R.id.stockListView, stockPendingIntent);
//				showStock(context, updateView);
				
				updateView.setViewVisibility(R.id.ll_stock_container, View.VISIBLE);
//				updateView.setImageViewResource(R.id.img_showStock,R.drawable.widget_title_right);
				updateView.setViewVisibility(R.id.ll_travel_container, View.INVISIBLE);
//				updateView.setImageViewResource(R.id.img_showTravel, 0);
				updateView.setViewVisibility(R.id.ll_news_container, View.INVISIBLE);
//				updateView.setImageViewResource(R.id.img_showNews, 0);
				AppWidgetManager awg = AppWidgetManager.getInstance(context);
				awg.updateAppWidget(new ComponentName(context, TravelWidget.class),updateView);
				return;

			}
//			
//			if(FinalString.WIDGET_SHOW_NEWS_RELOAD.equals(intent.getAction())) {
//				int reloadpage = intent.getIntExtra("reloadpage", 0);
//				SharedPreferences sp = context.getSharedPreferences("config_sp", Context.MODE_APPEND);
//				int currentPage = sp.getInt("newPageNum", 2);
//				int newPage = currentPage + reloadpage;
//				if(newPage < 2){
//					newPage = 2;
//				} else if(newPage > 3) {
//					newPage = 3;
//				}
//				sp.edit().putInt("newPageNum", newPage).commit();
//				updateView.setTextViewText(R.id.tv_footer, newPage-1+"/2");
//
//				AppWidgetManager awg = AppWidgetManager.getInstance(context);
//				int[] appWidgetIds =  awg.getAppWidgetIds(new ComponentName(context, TravelWidget.class));
//				awg.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.newsListView);
//				awg.updateAppWidget(appWidgetIds,updateView);
//				return;
//			}



		}

		
	}

//	public void showStock(Context context, RemoteViews updateView) {
//		updateView.setViewVisibility(R.id.ll_stock_container, View.VISIBLE);
//		updateView.setImageViewResource(R.id.img_showStock,
//				R.drawable.widget_title_right);
//
//		updateView.setViewVisibility(R.id.ll_travel_container, View.INVISIBLE);
//		updateView.setImageViewResource(R.id.img_showTravel, 0);
//		updateView.setViewVisibility(R.id.ll_news_container, View.INVISIBLE);
//		updateView.setImageViewResource(R.id.img_showNews, 0);
//		updateView.setViewVisibility(R.id.ll_weather_container, View.INVISIBLE);
////		updateView.setImageViewResource(R.id.img_showWeather, 0);
//		
//		AppWidgetManager awg = AppWidgetManager.getInstance(context);
//		awg.updateAppWidget(new ComponentName(context, TravelWidget.class),
//				updateView);
//	}
//
//	public void showNews(Context context, RemoteViews updateView) {
//		updateView.setViewVisibility(R.id.ll_news_container, View.VISIBLE);
//		updateView.setImageViewResource(R.id.img_showNews,
//				R.drawable.widget_title_middle);
//		updateView.setViewVisibility(R.id.ll_travel_container, View.INVISIBLE);
//		updateView.setImageViewResource(R.id.img_showTravel, 0);
//		updateView.setViewVisibility(R.id.ll_stock_container, View.INVISIBLE);
//		updateView.setImageViewResource(R.id.img_showStock, 0);
//		updateView.setViewVisibility(R.id.ll_weather_container, View.INVISIBLE);
////		updateView.setImageViewResource(R.id.img_showWeather, 0);
//		
//		AppWidgetManager awg = AppWidgetManager.getInstance(context);
//		awg.updateAppWidget(new ComponentName(context, TravelWidget.class),
//				updateView);
//	}

//	public void showTravel(Context context,RemoteViews updateView) {
//		updateView.setViewVisibility(R.id.ll_travel_container, View.VISIBLE);
//		updateView.setImageViewResource(R.id.img_showTravel,
//				R.drawable.widget_title_left);
//		updateView.setViewVisibility(R.id.ll_news_container, View.INVISIBLE);
//		updateView.setImageViewResource(R.id.img_showNews, 0);
//		updateView.setViewVisibility(R.id.ll_stock_container, View.INVISIBLE);
//		updateView.setImageViewResource(R.id.img_showStock, 0);
//		updateView.setViewVisibility(R.id.ll_weather_container, View.INVISIBLE);
////		updateView.setImageViewResource(R.id.img_showWeather, 0);
//		
//		AppWidgetManager awg = AppWidgetManager.getInstance(context);
//		awg.updateAppWidget(new ComponentName(context, TravelWidget.class),
//				updateView);
//	}

	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		
		
//		Intent showTravel = new Intent(FinalString.WIDGET_SHOW_TRAVEL);
//		PendingIntent travelPendingIntent = PendingIntent.getBroadcast(context,1, showTravel, 0);
//		updateView.setOnClickPendingIntent(R.id.rl_showTravel,travelPendingIntent);
//
//		
//		Intent showNews = new Intent(FinalString.WIDGET_SHOW_NEWS);
//		PendingIntent newsPendingIntent = PendingIntent.getBroadcast(context,2, showNews, 0);
//		updateView.setOnClickPendingIntent(R.id.rl_showNews, newsPendingIntent);

//		Intent showStock = new Intent(FinalString.WIDGET_SHOW_STOCK);
//		PendingIntent stockPendingIntent = PendingIntent.getBroadcast(context,3, showStock, 0);
//		updateView.setOnClickPendingIntent(R.id.rl_showStock,stockPendingIntent);
		
		

//		Intent newsPre = new Intent(FinalString.WIDGET_SHOW_NEWS_RELOAD);
//		newsPre.putExtra("reloadpage", -1); //pre = -1 next = 1
//		PendingIntent newsPreIntent = PendingIntent.getBroadcast(context,4, newsPre, 4);
//		updateView.setOnClickPendingIntent(R.id.btn_news_pre, newsPreIntent);
//		
//		Intent newsNext = new Intent(FinalString.WIDGET_SHOW_NEWS_RELOAD);
//		newsNext.putExtra("reloadpage", 1); //pre = -1 next = 1
//		PendingIntent newsNextIntent = PendingIntent.getBroadcast(context,5, newsNext, 5);
//		updateView.setOnClickPendingIntent(R.id.btn_news_next, newsNextIntent);
//	
		
		
		// 开始新旅程
		RemoteViews updateView = new RemoteViews(context.getPackageName(),R.layout.widget);
		Intent startRouteIntent = new Intent();
		startRouteIntent.setClass(context, NewRouteActivity.class);
		PendingIntent startPendingIntent = PendingIntent.getActivity(context, 0, startRouteIntent, 0); 
		updateView.setOnClickPendingIntent(R.id.btn_startRoute, startPendingIntent);
		
		// 新闻刷新
		Intent refreshNewsIntent = new Intent();
		refreshNewsIntent.setClass(context, GetNewsInfoService.class);
		PendingIntent refreshNewsPendingIntent = PendingIntent.getService(context, 0, refreshNewsIntent, PendingIntent.FLAG_CANCEL_CURRENT); 
		updateView.setOnClickPendingIntent(R.id.btn_news_refersh, refreshNewsPendingIntent);
		
		//切换 旅程单
		Intent tripListIntent = new Intent(FinalString.WIDGET_SHOW_TRAVEL);
		PendingIntent tripListPendingIntent = PendingIntent.getBroadcast(context, 0, tripListIntent, 0); 
		updateView.setOnClickPendingIntent(R.id.rl_showTravel, tripListPendingIntent);
		
		//切换 新闻页
		Intent newsIntent = new Intent(FinalString.WIDGET_SHOW_NEWS);
		PendingIntent newsPendingIntent = PendingIntent.getBroadcast(context, 0, newsIntent, 0); 
		updateView.setOnClickPendingIntent(R.id.rl_showNews, newsPendingIntent);
		
		//切换 股市页
		Intent stockIntent = new Intent(FinalString.WIDGET_SHOW_STOCK);
		PendingIntent stockPendingIntent = PendingIntent.getBroadcast(context, 0, stockIntent, 0); 
		updateView.setOnClickPendingIntent(R.id.rl_showStock, stockPendingIntent);
		
		appWidgetManager.updateAppWidget(appWidgetIds, updateView);
		
		initWidgetList(context, appWidgetManager);
//		initNewsList(context, appWidgetManager);
		
		
	}

	
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		try {
			importDB(context);
		} catch (Exception e) {
			Log.getStackTraceString(e);
		}
		
	}

	private void importDB(Context context) throws Exception {
		if (!new File(context.getString(R.string.datapath)).exists()) {
			File dataDir = new File(context.getString(R.string.datapath));
			dataDir.mkdir();
			FileOutputStream fout = new FileOutputStream(new File(
					context.getString(R.string.datapath), "gntravel.db"));
			InputStream in = context.getAssets().open("gntravel.db");
			int len = 0;
			byte[] buffer = new byte[1024];
			while ((len = in.read(buffer)) != -1) {
				fout.write(buffer, 0, len);
			}
			fout.flush();
			fout.close();
			in.close();
		}
	}
	
	public void initWidgetList(Context context,AppWidgetManager appWidgetManager) {
		
		RemoteViews updateView = new RemoteViews(context.getPackageName(),R.layout.widget);
		updateView.setViewVisibility(R.id.ll_travel_container, View.VISIBLE);
//		updateView.setImageViewResource(R.id.img_showTravel,R.drawable.widget_title_left);
		updateView.setViewVisibility(R.id.ll_news_container, View.INVISIBLE);
//		updateView.setImageViewResource(R.id.img_showNews, 0);
		updateView.setViewVisibility(R.id.ll_stock_container, View.INVISIBLE);
//		updateView.setImageViewResource(R.id.img_showStock, 0);
		
		
		Intent travelIntent = new Intent(context, WidgetTravelRemoteViewsService.class);
		updateView.setRemoteAdapter(R.id.travel_listView, travelIntent);
        Intent travelClickIntent = new Intent(context, TripFormActivity.class); 
        PendingIntent travelPendingIntent = PendingIntent.getActivity(context, 0, travelClickIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        updateView.setPendingIntentTemplate(R.id.travel_listView, travelPendingIntent);
        
        
		Intent newsIntent = new Intent(context, WidgetNewsRemoteViewsService.class);
		updateView.setRemoteAdapter(R.id.newsListView, newsIntent);
        Intent newsClickIntent = new Intent(context, H5Activity.class); 
        PendingIntent newsPendingIntent = PendingIntent.getActivity(context, 0, newsClickIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        updateView.setPendingIntentTemplate(R.id.newsListView, newsPendingIntent);

        
        
        
        
        
		AppWidgetManager awg = AppWidgetManager.getInstance(context);
		awg.updateAppWidget(new ComponentName(context, TravelWidget.class),updateView);
	}

	public void updateTravelList(Context context, AppWidgetManager appWidgetManager) {
		int[] appWidgetIds =  appWidgetManager.getAppWidgetIds(new ComponentName(context, TravelWidget.class));
		appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.travel_listView);
	}
	
	
	public void initNewsList(Context context, AppWidgetManager appWidgetManager) {
		RemoteViews updateView = new RemoteViews(context.getPackageName(),R.layout.widget);
		Intent newsIntent = new Intent(context, WidgetNewsRemoteViewsService.class);
		updateView.setRemoteAdapter(R.id.newsListView, newsIntent);
        Intent newsClickIntent = new Intent(context, H5Activity.class); 
        PendingIntent newsPendingIntent = PendingIntent.getActivity(context, 0, newsClickIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        updateView.setPendingIntentTemplate(R.id.newsListView, newsPendingIntent);
		AppWidgetManager awg = AppWidgetManager.getInstance(context);
		awg.updateAppWidget(new ComponentName(context, TravelWidget.class),updateView);
	}
	
	public void updateNewsList(Context context, AppWidgetManager appWidgetManager) {
		int[] appWidgetIds =  appWidgetManager.getAppWidgetIds(new ComponentName(context, TravelWidget.class));
		appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.newsListView);
	}
	
	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		
		SharedPreferences sp = context.getSharedPreferences("config_sp", Context.MODE_APPEND);
		sp.edit().putInt("newPageNum", 2).commit();
	}

	private boolean getUserState(Context context) {
		SharedPreferences sp = context.getSharedPreferences("userInfo_sp",Context.MODE_PRIVATE);
		return sp.getBoolean("userState", false);
	}

	public void loadTravelDate(Context context , RemoteViews updateView) {
		Intent intent = new Intent();
		intent.setClass(context, WidgetTravelRemoteViewsService.class);
		
		updateView.setRemoteAdapter(R.id.listView, intent);
        Intent clickIntent = new Intent(context, TripFormActivity.class); 
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, clickIntent, 0);
        updateView.setPendingIntentTemplate(R.id.listView, pendingIntent);
		
		Intent startRouteIntent = new Intent();
		startRouteIntent.setClass(context, NewRouteActivity.class);
		PendingIntent startPendingIntent = PendingIntent.getActivity(context, 0, startRouteIntent, 0); 
		updateView.setOnClickPendingIntent(R.id.btn_startRoute, startPendingIntent);
		
//		showTravel(context, updateView);
	}

}
