package com.gionee.gntravel.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.util.TypedValue;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.gionee.gntravel.R;
import com.gionee.gntravel.db.dao.impl.NewsInfoDaoImpl;
import com.gionee.gntravel.entity.WidgetNewsEntity;
import com.gionee.gntravel.task.AsyncImageTask;
import com.gionee.gntravel.utils.DBUtils;
import com.gionee.gntravel.utils.DateUtils;
import com.gionee.gntravel.utils.MD5Util;

public class WidgetNewsRemoteViewsFactory implements RemoteViewsFactory {
	private Context mContext;
	private ArrayList<WidgetNewsEntity> list = new ArrayList<WidgetNewsEntity>();
	private HashMap<String,Bitmap> imgMap = new HashMap<String,Bitmap>();
	private Map<String, Bitmap>  map = Collections.synchronizedMap(imgMap);
	private int imageHeigh = 0; 
	public boolean newsflag = true;
	private SharedPreferences sp;
	private File cache;
	public WidgetNewsRemoteViewsFactory(Context context, Intent intent) {
		this.mContext = context;
		imageHeigh = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,
				context.getResources().getDimension(R.dimen.widget_list_item_img_heigh),
				context.getResources().getDisplayMetrics());
		sp = mContext.getSharedPreferences("config_sp", Context.MODE_APPEND);
		cache = new File(context.getExternalCacheDir(), "img");
	}
	
	@Override
	public void onCreate() {
		
	}

	@Override
	public void onDataSetChanged() {
		list.clear();
		DBUtils dbUitls = new DBUtils(mContext);
		SQLiteDatabase db = dbUitls.openReadOnlyDatabase();
		NewsInfoDaoImpl newsInfoDaoImpl = new NewsInfoDaoImpl();
		list = newsInfoDaoImpl.findNews(db);
		if(list.isEmpty()) {
			//启动service下载
			Intent downService = new Intent(mContext, GetNewsInfoService.class);
			mContext.startService(downService);
		} else {
		}
	}

	@Override
	public void onDestroy() {

	}

	@Override
	public int getCount() {
		if(list.size()>10) 
			return 10;
		return list.size();
	}

	@Override
	public RemoteViews getViewAt(int position) {
		RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_news_item);
		rv.setTextViewText(R.id.tv_long_title, list.get(position).getLong_title());
		rv.setTextViewText(R.id.tv_pubDate, DateUtils.dateFromPudDate(Long.parseLong(list.get(position).getPubDate())));
		rv.setTextViewText(R.id.tv_comment, "评论  " + list.get(position).getComment()+"");
		String path = list.get(position).getPic();
		if(!TextUtils.isEmpty(path)) {
			String name = MD5Util.MD5Encrypt(path)+ path.substring(path.lastIndexOf("."));
			File file = new File(cache, name);
			if (file.exists()) {
				rv.setImageViewUri(R.id.img_newsImg, Uri.fromFile(file));
			} else {
				asyncLoadImage(rv,path);
			}
		} else {
			rv.setImageViewUri(R.id.img_newsImg, null);
		}
		

//		
//		
//		int width = bitmap.getWidth();
//		int heigh = bitmap.getHeight();
//
//		int newWidth = imageHeigh;
//		int newHeight = imageHeigh;
//		// 计算缩放比例
//		float scaleWidth = ((float) newWidth) / width;
//		float scaleHeight = ((float) newHeight) / heigh;
//		// 取得想要缩放的matrix参数
//		Matrix matrix = new Matrix();
//		matrix.postScale(scaleWidth, scaleHeight);
//		// 得到新的图片
//		bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, heigh,matrix, true);;
//		
//		rv.setImageViewBitmap(R.id.img_newsImg, bitmap = Bitmap.createBitmap(bitmap));
		Intent intent = new Intent();
		intent.putExtra("url", list.get(position).getLink());
		intent.putExtra("title", mContext.getString(R.string.widget_news));
		intent.putExtra("returnHome", true);
        rv.setOnClickFillInIntent(R.id.ll_news_item, intent);
        
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
	private void asyncLoadImage(RemoteViews remoteView, String path) {
		AsyncImageTask task = new AsyncImageTask(mContext, remoteView);
		task.execute(path);
	}
	
}
