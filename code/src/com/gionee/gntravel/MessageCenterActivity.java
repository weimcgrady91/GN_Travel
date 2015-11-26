package com.gionee.gntravel;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gionee.gntravel.entity.PushMessage;
import com.gionee.gntravel.utils.FinalString;
import com.gionee.gntravel.utils.HttpConnCallback;
import com.gionee.gntravel.utils.HttpConnUtil4Gionee;
import com.gionee.gntravel.utils.NetWorkUtil;

/**
 * 
 * @author lijinbao 消息推送
 */
public class MessageCenterActivity extends Activity implements
		HttpConnCallback, OnClickListener {
	private HttpConnUtil4Gionee task;
	private Thread doResultThread;
	private ResultHandler handler = new ResultHandler();
	private ListView listView;
	private TextView tvTitle;
	private View rl_failed;
	private TravelApplication app;
	private String messageUrl;
	private ImageView img_loading;
	private RelativeLayout rl_loading;
	private ArrayList<PushMessage> list = new ArrayList<PushMessage>();
	private MessageAdapter adapter = null;

	private class ResultHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case FinalString.DATA_FINISH:
				loadDataFinish();
				stopProgressDialog();
				break;
			case FinalString.NET_ERROR:
				showNetErrorMsg();
				break;
			case FinalString.NOT_FOUND:
				selecttErrorMsg(msg);
				break;
			}
		}
	}

	/**
	 * 
	 * @param msg
	 */
	private void selecttErrorMsg(Message msg) {
		stopProgressDialog();
		String errorMsg = (String) msg.obj;
		if(errorMsg==null){
			Toast.makeText(this, "没有推送的消息", Toast.LENGTH_LONG).show();
		}else{
			
			Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
		}
	}

	private void loadDataFinish() {
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_message_center);
		setupView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		initParams();
		loadingData();
	}

	private void setupView() {
		app = (TravelApplication) getApplication();
		app.getUserInfo_sp().putBoolean("newFlag", false);
		img_loading = (ImageView) findViewById(R.id.img_loading);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setOnClickListener(this);
		tvTitle.setText(getString(R.string.information_center));
		listView = (ListView) findViewById(R.id.lv_message);
		adapter = new MessageAdapter();
		listView.setAdapter(adapter);
		rl_loading = (RelativeLayout) findViewById(R.id.rl_loading);
		rl_failed = (View) findViewById(R.id.rl_failed);
		rl_failed.setOnClickListener(this);
	}

	private void initParams() {
		messageUrl = getString(R.string.gionee_host)
				+ "/GioneeTrip/pushAction_findTriPushMessage.action";
	}

	private void loadingData() {
		list.clear();
		startProgressDialog();

		if (!NetWorkUtil.isNetworkConnected(this)) {
			Message msg = Message.obtain();
			msg.what = FinalString.NET_ERROR;
			handler.sendMessageDelayed(msg, 1000);
			return;
		}
		task = new HttpConnUtil4Gionee(this);
		task.execute(messageUrl, null, HttpConnUtil4Gionee.HttpMethod.POST);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title:
			this.finish();
			break;
		case R.id.rl_failed:
			loadingData();
			break;
		}
	}

	@Override
	public void execute(String result) {
		if (TextUtils.isEmpty(result)) {
			Message msg = Message.obtain();
			msg.what = FinalString.NET_ERROR;
			handler.sendMessage(msg);
			return;
		}
		doResultThread = new Thread(new DoResult(result));
		doResultThread.start();
	}

	private class DoResult implements Runnable {
		private String result;

		public DoResult(String result) {
			this.result = result;
		}

		@Override
		public void run() {
			try {
				JSONObject responseJson = new JSONObject(result);
				if (!FinalString.ERRORCODE.equals(responseJson
						.getString("errorCode"))) {
					Message msg = Message.obtain();
					msg.what = FinalString.NOT_FOUND;
					msg.obj = responseJson.getString("errorMsg");
					handler.sendMessage(msg);
					return;
				}
				String cotent = responseJson.getString("content");
				// 查询所有

				JSONArray jsonArray = new JSONArray(cotent);
				for (int i = 0; i < jsonArray.length(); i++) {
					PushMessage message = new PushMessage();
					JSONObject Item = jsonArray.getJSONObject(i);
					String time = Item.getString("date");
					String content = Item.getString("content");
					String title = Item.getString("title");
					message.setTitle(title);
					message.setContent(content);
					message.setTime(time);
					list.add(message);
				}
				Message msg = Message.obtain();
				msg.what = FinalString.DATA_FINISH;
				handler.sendMessage(msg);

			} catch (Exception e) {
				e.printStackTrace();
				Message msg = Message.obtain();
				msg.what = FinalString.NOT_FOUND;
				handler.sendMessage(msg);
			}
		}
	}

	private void startProgressDialog() {
		rl_failed.setVisibility(View.GONE);
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

	public void showNetErrorMsg() {
		rl_failed.setBackground(getResources().getDrawable(
				R.drawable.net_error_bg));
		rl_failed.setVisibility(View.VISIBLE);
		stopProgressDialog();
	}

	class MessageAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = getLayoutInflater().inflate(
						R.layout.item_message, null);
				viewHolder.title = (TextView) convertView
						.findViewById(R.id.tv_message_title);
				viewHolder.time = (TextView) convertView
						.findViewById(R.id.tv_message_time);
				viewHolder.content = (TextView) convertView
						.findViewById(R.id.tv_message_content);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.title.setText(list.get(position).getTitle());
			viewHolder.time.setText(list.get(position).getTime());
			viewHolder.content.setText(list.get(position).getContent());
			return convertView;
		}

	}

	public final class ViewHolder {
		public TextView title;
		public TextView time;
		public TextView content;
	}

}