package com.gionee.gntravel.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.util.Log;

public class HttpConnectionUtil {
	public static enum HttpMethod {
		GET, POST
	}

//	public String asyncConnect(final String url, final HttpMethod method,
//			final HttpConnCallback callback) {
//		asyncConnect(url, null, method, callback);
//	}

	public String syncConnect(final String url, final HttpMethod method,
			final HttpConnCallback callback) {
		return syncConnect(url, null, method, callback);
	}

//	public String asyncConnect(final String url,
//			final Map<String, String> params, final HttpMethod method,
//			final HttpConnCallback callback) {
//		Looper.prepare();
//		Handler handler = new Handler();
//		Runnable runnable = new Runnable() {
//			public void run() {
//				return syncConnect(url, params, method, callback);
//			}
//		};
//		handler.post(runnable);
//	}

	public String syncConnect(final String url, final Map<String, String> params,
			final HttpMethod method, final HttpConnCallback callback) {
		String json = null;
		BufferedReader reader = null;

		try {
			HttpClient client = new DefaultHttpClient();
			HttpUriRequest request = getRequest(url, params, method);
			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				reader = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent()));
				StringBuilder sb = new StringBuilder();
				for (String s = reader.readLine(); s != null; s = reader
						.readLine()) {
					sb.append(s);
				}
				json = sb.toString();
			}
			return json;
		} catch (ClientProtocolException e) {
			Log.e("HttpConnectionUtil", e.getMessage(), e);
		} catch (IOException e) {
			Log.e("HttpConnectionUtil", e.getMessage(), e);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				// ignore me ^o^
			}
		}
		return json;
//		callback.execute(json);
	}

	private HttpUriRequest getRequest(String url, Map<String, String> params,
			HttpMethod method) {
		if (method.equals(HttpMethod.POST)) {
			List<BasicNameValuePair> postData = new ArrayList<BasicNameValuePair>(); 
            for (Map.Entry<String, String> entry : params.entrySet()) {
                postData.add(new BasicNameValuePair(entry.getKey(), 
                        entry.getValue())); 
            }
			try {
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
						postData, HTTP.UTF_8);
				HttpPost request = new HttpPost(url);
				request.setEntity(entity);
				return request;
			} catch (UnsupportedEncodingException e) {
				// Should not come here, ignore me.
				throw new java.lang.RuntimeException(e.getMessage(), e);
			}
		} else {
			if (url.indexOf("?") < 0) {
				url += "?";
			}
			if (params != null) {
				for (String name : params.keySet()) {
					url += "&" + name + "="
							+ URLEncoder.encode(params.get(name));
				}
			}
			HttpGet request = new HttpGet(url);
			return request;
		}
	}
}