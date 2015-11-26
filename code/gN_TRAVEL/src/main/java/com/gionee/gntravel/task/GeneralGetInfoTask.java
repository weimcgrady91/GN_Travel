package com.gionee.gntravel.task;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.gionee.gntravel.utils.ConnUtil;
import com.gionee.gntravel.utils.TaskCallBack;

public class GeneralGetInfoTask extends AsyncTask<Object, Void, Object>{
	private TaskCallBack callBack;
	private Context mContext;
	private ParseTool parseTool;
	
	public interface ParseTool {
		public Object parseResult(String result);
	}
	
	public GeneralGetInfoTask(Context context ,ParseTool parseTool, TaskCallBack callBack) {
		this.mContext = context;
		this.callBack = callBack;
		this.parseTool = parseTool;
	}
	
	@Override
	protected Object doInBackground(Object... params) {
		ConnUtil connUtil = new ConnUtil();
		String result = connUtil.doInBackground(params);
		if(TextUtils.isEmpty(result)) {
			return null;
		} else {
			return parseTool.parseResult(result);
		}
	}

	@Override
	protected void onPostExecute(Object result) {
		super.onPostExecute(result);
		callBack.execute(result);
	}
}
