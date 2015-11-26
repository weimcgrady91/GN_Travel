package com.gionee.gntravel.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.gionee.gntravel.entity.BaseBean;

public class GNConnUtil {

	private static final int CONN_TIME_OUT = 20000;
	private static final int READ_TIME_OUT = 20000;
	public boolean postIsExecuting;
	public boolean getIsExecuting;
	private GNConnListener l;
	private Handler handler;

	public GNConnUtil() {
		handler = new Handler();
	}

	public void doPost(String url, Object obj) {
		doPost(url, JSONUtil.getInstance().toJSON(obj));
	}

	public void doPost(String url,HashMap<String, String> params) {
		doPost(url, CONN_TIME_OUT, READ_TIME_OUT, params);
	}
	public void doPost(String url, String json) {
		if (TextUtils.isEmpty(json)) {
			throw new IllegalArgumentException(
					"post params json can't be null!");
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("json", json);
		doPost(url, CONN_TIME_OUT, READ_TIME_OUT, params);
	}

	public void doPost(final String url, final int connTimeOut,
			final int readTimeOut, final HashMap<String, String> params) {
		if (postIsExecuting) {
			return;
		}
		postIsExecuting = true;

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					String json = doPostSyn(url, connTimeOut, readTimeOut,
							params);
					reqJson(url, json);

				} catch (final Exception ex) {

					handler.post(new Runnable() {

						@Override
						public void run() {
							if (l != null) {
								l.onReqFail(url, ex, BaseBean.CODE_LOCAL_ERROR);
							}
						}
					});

				} finally {
					// log
					postIsExecuting = false;
				}
			}

		}).start();
	}

	private String doPostSyn(String url, int connTimeOut, int readTimeOut,
			HashMap<String, String> params) throws Exception {
		if (TextUtils.isEmpty(url) || connTimeOut < 1 || readTimeOut < 1) {
			throw new IllegalArgumentException("params invalid");
		}
		// post
		HttpPost post = new HttpPost(url);
		List<BasicNameValuePair> entityParams = new ArrayList<BasicNameValuePair>();
		// set params
		if (params != null) {
			Iterator<String> keys = params.keySet().iterator();
			while (keys.hasNext()) {
				String key = keys.next();
				String value = params.get(key);
				BasicNameValuePair nameValuePair = new BasicNameValuePair(key,
						value);
				entityParams.add(nameValuePair);
			}
			UrlEncodedFormEntity encodedEntity = new UrlEncodedFormEntity(
					entityParams);
			post.setEntity(encodedEntity);
		}
		// client
		HttpParams httpParams = new BasicHttpParams();
		HttpProtocolParams.setContentCharset(httpParams, "UTF-8");
		ConnManagerParams.setTimeout(httpParams, connTimeOut);
		HttpConnectionParams.setSoTimeout(httpParams, readTimeOut);
		HttpClient client = new DefaultHttpClient(httpParams);
		HttpResponse response = client.execute(post);

		int code = response.getStatusLine().getStatusCode();
		if (code == 200) {
			return EntityUtils.toString(response.getEntity(), "UTF-8");
		} else {
			throw new RuntimeException("Bad request code:" + code);
		}

	}

	public void reqJson(final String url, final String json) {
		if (TextUtils.isEmpty(json)) {

			handler.post(new Runnable() {

				@Override
				public void run() {
					if (l != null) {
						l.onReqFail(url, new RuntimeException(
								"return json is empty"),
								BaseBean.CODE_LOCAL_ERROR);
					}
				}
			});
			return;
		}
		final BaseBean bean = JSONUtil.getInstance().fromJSON(BaseBean.class,
				json);
		if (bean == null) {
			if (l != null) {

				handler.post(new Runnable() {

					@Override
					public void run() {
						l.onReqFail(url, new RuntimeException(
								"BaseBean abnormal,json:" + json),
								BaseBean.CODE_SERVER_ERROR);
					}
				});

			}
			return;
		}
		// 服务器返回
		if (bean.getErrorCode() == BaseBean.CODE_NORMAL) {
			final String jsonContent = bean.getContent();
			// 正常情况下 内容为空
			if (TextUtils.isEmpty(jsonContent)) {
				if (l != null) {

					handler.post(new Runnable() {

						@Override
						public void run() {
							l.onReqFail(url, new RuntimeException(
									"return cotent of BaseBean is empty"),
									BaseBean.CODE_SERVER_ERROR);
						}
					});

				}
			} else {

				handler.post(new Runnable() {

					@Override
					public void run() {
						if (l != null) {
							l.onReqSuc(url, jsonContent);
						}
					}
				});

			}
		} else {

			handler.post(new Runnable() {

				@Override
				public void run() {
					if (l != null) {
						l.onReqFail(url,
								new RuntimeException(bean.getErrorMsg()),
								bean.getErrorCode());
					}
				}
			});

		}
	}

	public void setGNConnListener(GNConnListener l) {
		this.l = l;
	}

	public static interface GNConnListener {
		public void onReqSuc(String requestUrl, String json);

		public void onReqFail(String requestUrl, Exception ex, int errorCode);
	}

	public void doGet(final String url) {
		if (getIsExecuting) {
			return;
		}
		getIsExecuting = true;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					HttpGet post = new HttpGet(url);
					List<BasicNameValuePair> entityParams = new ArrayList<BasicNameValuePair>();
					// client
					HttpParams httpParams = new BasicHttpParams();
					HttpProtocolParams.setContentCharset(httpParams, "UTF-8");
					ConnManagerParams.setTimeout(httpParams, CONN_TIME_OUT);
					HttpConnectionParams
							.setSoTimeout(httpParams, READ_TIME_OUT);
					HttpClient client = new DefaultHttpClient(httpParams);
					HttpResponse response = client.execute(post);

					final int code = response.getStatusLine().getStatusCode();
					if (code == 200) {
						final String json = EntityUtils.toString(
								response.getEntity(), "UTF-8");
						reqJson(url, json);
					}

				} catch (final Exception e) {
					e.printStackTrace();
					if (l != null) {
						handler.post(new Runnable() {

							@Override
							public void run() {
								l.onReqFail(url, e, 0);
							}
						});
					}
				} finally {
					getIsExecuting = false;
				}
			}
		}).start();
	}
}
