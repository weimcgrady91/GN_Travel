package com.gionee.gntravel;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gionee.gntravel.entity.Food;
import com.gionee.gntravel.utils.SyncImageLoader;

public class FoodAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	private Context mContext;
	private ArrayList<Food> mFoodList;
	private ListView mListView;
	private SyncImageLoader syncImageLoader;
	
	public FoodAdapter(Context context,ListView listView, ArrayList<Food> foodList){
		mInflater = LayoutInflater.from(context);
		syncImageLoader = new SyncImageLoader();
		mContext = context;
		mListView = listView;
		mFoodList = foodList;
		mListView.setOnScrollListener(onScrollListener);
	}
	
	public void clean(){
		mFoodList.clear();
	}
	
	@Override
	public int getCount() {
		return mFoodList.size();
	}

	@Override
	public Object getItem(int position) {
		if(position >= getCount()){
			return null;
		}
		return mFoodList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.food_item, null);
		}
		Food food = mFoodList.get(position);
		convertView.setTag(position);
		ImageView img = (ImageView) convertView.findViewById(R.id.img);
		TextView name =  (TextView) convertView.findViewById(R.id.name);
		name.setText(food.getName());
		img.setBackgroundResource(R.drawable.ic_launcher);
		syncImageLoader.loadImage(position,food.getS_photo_url(),imageLoadListener);
		return  convertView;
	}

	SyncImageLoader.OnImageLoadListener imageLoadListener = new SyncImageLoader.OnImageLoadListener(){

		@Override
		public void onImageLoad(Integer t, Drawable drawable) {
			//BookModel model = (BookModel) getItem(t);
			View view = mListView.findViewWithTag(t);
			if(view != null){
				ImageView iv = (ImageView) view.findViewById(R.id.img);
				iv.setBackgroundDrawable(drawable);
			}
		}
		@Override
		public void onError(Integer t) {
//			Food food = (Food) getItem(t);
			View view = mListView.findViewWithTag(t);
			if(view != null){
				ImageView iv = (ImageView) view.findViewById(R.id.img);
				iv.setBackgroundResource(R.drawable.activity_room_detail_area);
			}
		}
		
	};
	
	public void loadImage(){
		int start = mListView.getFirstVisiblePosition();
		int end =mListView.getLastVisiblePosition();
		if(end >= getCount()){
			end = getCount() -1;
		}
		syncImageLoader.setLoadLimit(start, end);
		syncImageLoader.unlock();
	}
	
	AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch (scrollState) {
				case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
					syncImageLoader.lock();
					break;
				case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
					loadImage();
					break;
				case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
					syncImageLoader.lock();
					break;
	
				default:
					break;
			}
			
		}
		
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// TODO Auto-generated method stub
			
		}
	};
}
