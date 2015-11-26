package com.gionee.gntravel.fragment;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ctrip.openapi.java.engine.DeleteOrEditTrip;
import com.gionee.gntravel.NewRouteActivity;
import com.gionee.gntravel.R;
import com.gionee.gntravel.TravelApplication;
import com.gionee.gntravel.TripFormActivity;
import com.gionee.gntravel.adapter.TravelSimpleChildAdapter;
import com.gionee.gntravel.entity.TripSimpleEntity;
import com.gionee.gntravel.entity.TripSimpleItem;
import com.gionee.gntravel.task.GeneralGetInfoTask;
import com.gionee.gntravel.utils.FinalString;
import com.gionee.gntravel.utils.HttpConnUtil4Gionee;
import com.gionee.gntravel.utils.NetWorkUtil;
import com.gionee.gntravel.utils.TaskCallBack;
import com.gionee.gntravel.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youju.statistics.YouJuAgent;

public class TravelFragment extends ListBaseFragment implements View.OnClickListener,
		TaskCallBack,GeneralGetInfoTask.ParseTool{
	private RefreshTripReceiver refreshReceiver = new RefreshTripReceiver();
	private ResultHandler handler = new ResultHandler();
	private Dialog dialog;
	private Dialog dialogDel;
	private Dialog dialogEdit;
	private ViewGroup groupContainer;
	private LayoutInflater mInflater;
	private HashMap<String, String> params;
	private GeneralGetInfoTask task;
	private HttpConnUtil4Gionee modifyTask;
	private ImageView img_loading;
	private View rl_loading;
	private TravelApplication app;
	private ArrayList<TripSimpleEntity> groups;
//	private String mUserName;
//	private View ll_travel_list;
	private View scrollview;
	private String simpleTripUrl;
	private Resources mResource;
	private DeleteOrEditTrip doetEngine;
	private String delTripUrl;
	private String renameTriprUrl;
	private boolean mIsFinish;

	private class ResultHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			mIsFinish = true;
			switch (msg.what) {
			case FinalString.DATA_FINISH:
				loadDataFinish();
				break;
			case FinalString.NET_ERROR:
				loadingNativeCache();
				break;
			case FinalString.WEB_ERROR:
				loadingNativeCache();
				break;
			case FinalString.NOT_FOUND:
				loadingNativeCache();
				break;
			case FinalString.WEBSERVICE_ERROR:
				stopProgressDialog();
				Toast.makeText(getActivity(), "网络不给力,操作失败", Toast.LENGTH_SHORT).show();
				break;
			case FinalString.PARSE_ERROR:
				stopProgressDialog();
				break;
			case FinalString.MODIFY_SUCC:
				loadingData();
				break;
			default:
				break;
			}
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initParams();
		registerReceiver();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_travel, null);
		scrollview = (View) view.findViewById(R.id.scrollview);
		groupContainer = (ViewGroup) view.findViewById(R.id.container);
		rl_loading = (View) view.findViewById(R.id.rl_loading);
		img_loading = (ImageView) view.findViewById(R.id.img_loading);
		view.findViewById(R.id.btn_newRoute).setOnClickListener(this);
		mInflater = inflater;
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		if(!mIsFinish) {
			loadingData();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		if(task!= null && !task.isCancelled()) {
			task.cancel(true);
		}
		if(modifyTask != null && !modifyTask.isCancelled()) {
			modifyTask.cancel(true);
		}
		if(!mIsFinish) {
			stopProgressDialog();
		}
	}

	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver();
	}

	private void initParams() {
		app = (TravelApplication) getActivity().getApplication();
		mResource = getActivity().getResources();
		simpleTripUrl = getActivity().getString(R.string.gionee_host) + FinalString.SIMPLE_TRIP_URL;
		delTripUrl = getActivity().getString(R.string.gionee_host) + FinalString.DELETE_TRIP_URL;
		renameTriprUrl = getActivity().getString(R.string.gionee_host) + FinalString.RENAME_TRIP_URL;
		params = new HashMap<String, String>();
		doetEngine = new DeleteOrEditTrip(handler,getActivity(), app);
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_newRoute:
			startNewRoute();
			break;
		case R.id.btn_refresh:
			loadingData();
			break;
		default:
			break;
		}
	}

	public void startNewRoute() {
		Intent intent = new Intent(getActivity(), NewRouteActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//		intent.putExtra("oldTrip", false);
		app.setTripState(true);
		getActivity().startActivity(intent);
		YouJuAgent.onEvent(getActivity(), getActivity().getString(R.string.youju_newtrip));
	}
	
	
	private void loadingNativeCache() {
		if(TextUtils.isEmpty(app.getU())) {
			loadTripListExample();
			return;
		}
		
		File file = getTravelCacheFile(app.getU());
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
	
	private void loadTripListExample() {
		InputStream is = null;
		try {
			is = mResource.getAssets().open("triplistexample.txt");
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
		ArrayList<TripSimpleEntity> list = new ArrayList<TripSimpleEntity>();
		try {
			JSONObject json = new JSONObject(content);
			JSONArray arr = new JSONArray(json.getString("content"));
			for (int i = 0; i < arr.length(); i++) {
				TripSimpleEntity tripSimpleEntity = new TripSimpleEntity();
				JSONObject object = arr.getJSONObject(i);
				String tripId = object.getString("tripId");
				String tripName = object.getString("name");
				String status = object.getString("status");
				tripSimpleEntity.setTripId(tripId);
				tripSimpleEntity.setTripName(tripName);
				tripSimpleEntity.setState(Boolean.parseBoolean(status));
				String tripOrderSimpleBeans = object.getString("tripOrderSimpleBeans");
				Gson gson = new Gson();
				ArrayList<TripSimpleItem> itemLists  = gson.fromJson(tripOrderSimpleBeans, new TypeToken<List<TripSimpleItem>>(){}.getType());
				tripSimpleEntity.setTripSimpleItems(itemLists);
				list.add(tripSimpleEntity);
			}
			groups = list;
			loadView();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		stopProgressDialog();
	}
	
	public void loadingData() {
		mIsFinish = false;
		startProgressDialog();
		if (!NetWorkUtil.isNetworkConnected(getActivity())) {
			Message msg = Message.obtain();
			msg.what = FinalString.NET_ERROR;
			handler.sendMessage(msg);
			return;
		}
		params.clear();
//		params.put("userKey", app.getUserId());
		
		params.put("u", app.getU());
		params.put("userId", app.getUserId());
		
		task = new GeneralGetInfoTask(getActivity(),this,this);
		task.execute(simpleTripUrl, params, HttpConnUtil4Gionee.HttpMethod.POST);
	}

	private void startProgressDialog() {
		rl_loading.setVisibility(View.VISIBLE);
		AnimationDrawable animationDrawable = (AnimationDrawable) img_loading
				.getBackground();
		animationDrawable.start();
	}

	private void stopProgressDialog() {
		AnimationDrawable animationDrawable = (AnimationDrawable) img_loading
				.getBackground();
		animationDrawable.stop();
		rl_loading.setVisibility(View.GONE);
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

	@Override
	public void execute(Object result) {
		Message msg = Message.obtain();
		groups = (ArrayList<TripSimpleEntity>)result;
		if(groups == null) {
			msg.what = FinalString.NET_ERROR;
		} else if(groups.isEmpty()) {
			msg.what = FinalString.NOT_FOUND;
		} else {
			msg.what = FinalString.DATA_FINISH;
		}
		handler.sendMessage(msg);
		Intent updateWidget = new Intent(FinalString.UPDATE_WIDGET);
		getActivity().sendBroadcast(updateWidget);
	}

	
	public void loadDataFinish() {
		stopProgressDialog();
//		ll_travel_list.setVisibility(View.VISIBLE);
		loadView();
		
	}
	private int getImageId(String month) {
		int m = Integer.parseInt(month);
		switch(m) {
		case 1:
			return R.drawable.slt_newtrip_date_1_calendar;
		case 2:
			return R.drawable.slt_newtrip_date_2_calendar;
		case 3:
			return R.drawable.slt_newtrip_date_3_calendar;
		case 4:
			return R.drawable.slt_newtrip_date_4_calendar;
		case 5:
			return R.drawable.slt_newtrip_date_5_calendar;
		case 6:
			return R.drawable.slt_newtrip_date_6_calendar;
		case 7:
			return R.drawable.slt_newtrip_date_7_calendar;
		case 8:
			return R.drawable.slt_newtrip_date_8_calendar;
		case 9:
			return R.drawable.slt_newtrip_date_9_calendar;
		case 10:
			return R.drawable.slt_newtrip_date_10_calendar;
		case 11:
			return R.drawable.slt_newtrip_date_11_calendar;
		case 12:
			return R.drawable.slt_newtrip_date_12_calendar;
		}
		return 0;
	}
	
	private void startArrowAnim(ImageView imageView) {
		Animation animation = (Animation) AnimationUtils.loadAnimation(getActivity(), R.anim.anim_arrow_rotate);
		imageView.setAnimation(animation);
		animation.start();
	}
	
	public void loadView() {
		groupContainer.removeAllViews();
		int heigh = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,
				getResources().getDimension(R.dimen.common_group_item_heigh),
				getResources().getDisplayMetrics());
		
		for (int index = 0; index < groups.size(); index++) {
			final boolean status = groups.get(index).isState();
			final String tripName = groups.get(index).getTripName();
			final View group = mInflater.inflate(R.layout.group, null);
			final LinearLayout ll_group_container = (LinearLayout) group.findViewById(R.id.ll_group_container);
			final ImageView img_driv = (ImageView) group.findViewById(R.id.img_driv);
			TextView tv_tripName = (TextView) group.findViewById(R.id.tv_tripName);
			
			String part1 = tripName.substring(0, tripName.indexOf("T"));
			String part2 = tripName.substring(tripName.indexOf("T")+1, tripName.length());
			String[] d = part1.split("-");
			
			TextView tv_date = (TextView) group.findViewById(R.id.tv_date);
			tv_date.setText(d[2]);
			tv_tripName.setText(part2);
			
			ImageView calenar_frame = (ImageView) group.findViewById(R.id.img_calendar_frame);
			int imgId = getImageId(d[1]);
			calenar_frame.setImageResource(imgId);
			
			final View ll_group = (View) group.findViewById(R.id.ll_group);
			ll_group.setFocusable(true);
			final ImageView img_arrow = (ImageView) group.findViewById(R.id.img_arrow);
			final ViewGroup ll_child = (ViewGroup) group.findViewById(R.id.ll_child);
			ll_group.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (ll_child.getVisibility() == View.GONE) {
						ll_group_container.setBackground(mResource.getDrawable(R.drawable.slt_common_card_base));
						img_driv.setVisibility(View.VISIBLE);
//						img_arrow.setBackground(mResource.getDrawable(R.drawable.slt_));
						startArrowAnim(img_arrow);
						img_arrow.setImageDrawable(mResource.getDrawable(R.drawable.slt_newtrip_arrow_down_default));
						ll_child.setVisibility(View.VISIBLE);
					} else {
						ll_child.setVisibility(View.GONE);
						img_driv.setVisibility(View.GONE);
						ll_group_container.setBackground(mResource.getDrawable(R.drawable.slt_common_card_base_putaway));
//						img_arrow.setBackground(mResource.getDrawable(R.drawable.slt_down_arrow));
						startArrowAnim(img_arrow);
						img_arrow.setImageDrawable(mResource.getDrawable(R.drawable.slt_newtrip_arrow_up_default));
					}
				}
			});			
			
			final String tripId = groups.get(index).getTripId();
			ListView childList = (ListView) group.findViewById(R.id.child_listView);
			TravelSimpleChildAdapter childAdapter = new TravelSimpleChildAdapter(
					getActivity(), groups.get(index).getTripSimpleItems());
			childList.setAdapter(childAdapter);
			childList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Intent intent = new Intent(getActivity(),TripFormActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.putExtra("tripName", tripName);
					intent.putExtra("position", position);
					intent.putExtra("tripId", tripId);
					startActivity(intent);
				}
			
			});
//			
//			 if(rows > 5) {
//			 rows = 5;
//			 }
			int rows = groups.get(index).getTripSimpleItems().size();
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, rows* heigh);
			ll_child.setLayoutParams(lp);
			LayoutParams lp2 = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
			lp2.setMargins(0, 0, 0, 0);
			group.setLayoutParams(lp2);
			groupContainer.addView(group);
			ll_group.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					if(!status)
						showDialog(tripName, tripId);
					return true;
				}
			});
			//auto show the first view
			if(index == 0) {
			  ll_group_container.setBackground(mResource.getDrawable(R.drawable.slt_common_card_base));
//			  img_arrow.setBackground(mResource.getDrawable(R.drawable.slt_newtrip_arrow_up_default));
			  img_arrow.setImageDrawable(mResource.getDrawable(R.drawable.slt_newtrip_arrow_up_default));
			  ll_child.setVisibility(View.VISIBLE);
			  img_driv.setVisibility(View.VISIBLE);
			}
			
			
		}
	}

	/**
	 * 删除某个旅程 访问服务器
	 */
	public void delTripName(String tripName, String tripId) {
		startProgressDialog();
		if (!NetWorkUtil.isNetworkConnected(getActivity())) {
			Toast.makeText(getActivity(), "网络异常,删除失败", Toast.LENGTH_SHORT).show();
			stopProgressDialog();
			return;
		}
		params.clear();
		params.put("tripId", tripId);
		modifyTask= new HttpConnUtil4Gionee(doetEngine);
		modifyTask.execute(delTripUrl, params,HttpConnUtil4Gionee.HttpMethod.POST);
	}


	
	/**
	 * 修改某个旅程 访问服务器
	 */
	public void editTripName(String oldTripName, String newTripName,String tripId) {
		startProgressDialog();
		if (!NetWorkUtil.isNetworkConnected(getActivity())) {
			Toast.makeText(getActivity(), "网络异常,修改失败", Toast.LENGTH_SHORT).show();
			stopProgressDialog();
			return;
		}
		params.clear();
		params.put("tripId", tripId);
		params.put("oldTripName", oldTripName);
		String part1 = oldTripName.substring(0,oldTripName.indexOf("T"));
		params.put("newTripName", part1+"T"+newTripName);
		modifyTask = new HttpConnUtil4Gionee(doetEngine);
		modifyTask.execute(renameTriprUrl, params,HttpConnUtil4Gionee.HttpMethod.POST);
	}

	/**
	 * //修改和删除界面的dialog
	 */

	private void showDialog(final String tripName, final String tripId) {

		dialog = new Dialog(getActivity(), R.style.CustomProgressDialog);
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.trip_name_dialog, null);
		TextView tvTitleName= (TextView) view.findViewById(R.id.tv_title_name);
		RelativeLayout re = (RelativeLayout) view.findViewById(R.id.re_dialog);
		TextView tvDel = (TextView) view.findViewById(R.id.tv_delete);
		TextView tvEdit = (TextView) view.findViewById(R.id.tv_edit);
		String part2 = tripName.substring(tripName.indexOf("T")+1, tripName.length());
		tvTitleName.setText(part2);
		re.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		tvDel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showDelDialog(tripName, tripId);
				dialog.dismiss();
			}
		});
		tvEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showEditDialog(tripName, tripId);
				dialog.dismiss();
			}
		});

		dialog.setContentView(view);
		dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		dialog.show();
	}

	/**
	 * 删除某个旅程
	 */
	private void showDelDialog(final String tripName, final String tripId) {
		dialogDel = new Dialog(getActivity(), R.style.CustomProgressDialog);
		View viewdia = LayoutInflater.from(getActivity()).inflate(R.layout.delete_dialog, null);
		
		TextView tvTitle=(TextView) viewdia.findViewById(R.id.tv_dialog_title_name);
		tvTitle.setText(getString(R.string.del_trip_name));
		Button btnDel = (Button) viewdia.findViewById(R.id.btn_dialog_sure);
		Button btnDelCancel = (Button) viewdia.findViewById(R.id.btn_dialog_cancel);
		RelativeLayout reDeLayout = (RelativeLayout) viewdia.findViewById(R.id.re_dialog);
		ImageView imgCancel = (ImageView) viewdia.findViewById(R.id.img_dialog_title_cha);
		TextView tvMiddleTitle=(TextView) viewdia.findViewById(R.id.tv_dialog_title);
		tvMiddleTitle.setText("您确定要删除"+"\""+tripName+"\"");
		btnDel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 服务器删除
				delTripName(tripName, tripId);
				dialogDel.dismiss();
			}
		});
		btnDelCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialogDel.dismiss();
			}
		});
		reDeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialogDel.dismiss();
			}
		});
		imgCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialogDel.dismiss();
			}
		});
		dialogDel.setContentView(viewdia);
		dialogDel.getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		dialogDel.show();

	}

	/**
	 * 修改某个旅程 
	 */
	private void showEditDialog(final String oldName, final String tripId) {
		dialogEdit = new Dialog(getActivity(), R.style.CustomProgressDialog);
		View viewdia = LayoutInflater.from(getActivity()).inflate(R.layout.trip_name_edit_dialog, null);
		final EditText etTripName = (EditText) viewdia.findViewById(R.id.et_edit_trip_name);
		Button btnEdit = (Button) viewdia.findViewById(R.id.btn_dialog_sure);
		Button btnDelCancel = (Button) viewdia.findViewById(R.id.btn_dialog_cancel);
		RelativeLayout reDeLayout = (RelativeLayout) viewdia.findViewById(R.id.re_dialog);
		ImageView imgCancel = (ImageView) viewdia.findViewById(R.id.img_cha);
		
		String part2 = oldName.substring(oldName.indexOf("T")+1, oldName.length());
		
		etTripName.setText(part2);
		etTripName.setSelection(etTripName.getText().length());
		btnEdit.setText(getString(R.string.edit_form));
		btnEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(TextUtils.isEmpty( etTripName.getText().toString())){
					Toast.makeText(getActivity(), "旅程名不能为空", Toast.LENGTH_LONG).show();
				}
//				else if(! Utils.isChinese( etTripName.getText().toString())){
//					Toast.makeText(getActivity(), "旅程名必须为中文", Toast.LENGTH_LONG).show();
//				}
//				
//				
				else{
					
					editTripName(oldName, etTripName.getText().toString(), tripId);
					dialogEdit.dismiss();
				}
			}
		});
		btnDelCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 修改
				dialogEdit.dismiss();
			}
		});
		reDeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialogEdit.dismiss();
			}
		});
		imgCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialogEdit.dismiss();
			}
		});
		dialogEdit.setContentView(viewdia);
		dialogEdit.getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		dialogEdit.show();

	}

	private void registerReceiver() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(FinalString.REFRESH_TRIP_ACTION);
		intentFilter.addAction(FinalString.USER_LOGIN_ACTION);
		intentFilter.addAction(FinalString.USER_CHECKOUT_ACTION);
		getActivity().registerReceiver(refreshReceiver,intentFilter);
	}
	
	private void unregisterReceiver() {
		getActivity().unregisterReceiver(refreshReceiver);
	}
	
	private class RefreshTripReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(FinalString.REFRESH_TRIP_ACTION.equals(action)
					|| FinalString.USER_LOGIN_ACTION.equals(action)
					|| FinalString.USER_CHECKOUT_ACTION.equals(action)){
				
				scrollview.setBackgroundResource(0);
				if(groupContainer != null) {
					groupContainer.removeAllViews();
				}
//				loadingData();
				mIsFinish = false;
			}
		}
	}
	

	private File getTravelCacheFile(String fileName) {
		File files = getActivity().getApplicationContext().getFilesDir();
		String filesPath = files.toString();
		File file = new File(filesPath, fileName);
		return file;
	}

	@Override
	public void loadDate() {
			
	}

	@Override
	public boolean onKeyBackDown() {
		return false;
	}
	
	/**
	 * 网络不好返回 null 服务器数据错误返回 长度为0的 list
	 * 
	 * @param result
	 * @return
	 */
	public ArrayList<TripSimpleEntity> parseResult(String result) {
		ArrayList<TripSimpleEntity> list = new ArrayList<TripSimpleEntity>();
		try {
			JSONObject json = new JSONObject(result);
			if (!FinalString.ERRORCODE.equals(json.getString("errorCode"))) {
				if("用户电话号码不能为空!".equals(json.getString("errorMsg"))) {
				} else {
					writeFile("");
				}
				Message msg = Message.obtain();
				msg.what = FinalString.NOT_FOUND;
				return list;
			}
			JSONArray arr = new JSONArray(json.getString("content"));
			for (int i = 0; i < arr.length(); i++) {
				TripSimpleEntity tripSimpleEntity = new TripSimpleEntity();
				JSONObject object = arr.getJSONObject(i);
				String tripName = object.getString("name");
				String status = object.getString("status");
				if(object.has("tripId")) {
					String tripId = object.getString("tripId");
					tripSimpleEntity.setTripId(tripId);
				}
				tripSimpleEntity.setTripName(tripName);
				tripSimpleEntity.setState(Boolean.parseBoolean(status));
				
				String tripOrderSimpleBeans = object.getString("tripOrderSimpleBeans");
				Gson gson = new Gson();
				ArrayList<TripSimpleItem> itemLists  = gson.fromJson(tripOrderSimpleBeans, new TypeToken<List<TripSimpleItem>>(){}.getType());
				tripSimpleEntity.setTripSimpleItems(itemLists);
				list.add(tripSimpleEntity);
			}
			writeFile(result);
			return list;
			
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private void writeFile(String result) {
		if(TextUtils.isEmpty(app.getU())) {
			return;
		}
		
		File file = getTravelCacheFile(app.getU());
		try {
			if(!file.exists()) {
				file.createNewFile();
			} 
			FileWriter writer = new FileWriter(file);
			writer.write(result);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Intent intent = getActivity().getIntent();
		if(intent != null && intent.getExtras() != null) {
			boolean reload = intent.getExtras().getBoolean("tripload",false);
			if(reload) {
				loadingData();
			}
		}

	}
	
	


}
