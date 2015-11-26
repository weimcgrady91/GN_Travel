package com.gionee.gntravel.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.gionee.gntravel.utils.HttpConnUtil4Gionee.HttpMethod;

public class ConnUtil {
	
	public String doInBackground(Object... params) {
		long startTime = System.currentTimeMillis();
		long endTime = 0;
		String result = null;
		HttpUriRequest request = null;
		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
		if (params[1] == null) {
			String url = params[0].toString();
			HttpGet req = new HttpGet(url);
			request = req;

			try {
				HttpResponse httpResponse = client.execute(request);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					result = EntityUtils.toString(httpResponse.getEntity());
				}
				return result;
			} catch (Exception e) {
				return null;
			}

		}				
		if ((HttpMethod)params[2] == HttpMethod.POST) {
			List<BasicNameValuePair> postData = new ArrayList<BasicNameValuePair>();
			for (Map.Entry<String, String> entry : ((HashMap<String, String>) params[1])
					.entrySet()) {
				postData.add(new BasicNameValuePair(entry.getKey(), entry
						.getValue()));
			}
			try {
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
						postData, HTTP.UTF_8);
				HttpPost req = new HttpPost(params[0].toString());
				req.setEntity(entity);
				request = req;
			} catch (UnsupportedEncodingException e) {
				Log.getStackTraceString(e);
			}
		} else if ((HttpMethod)params[2] == HttpMethod.GET) {
			String url = params[0].toString();
			if (url.indexOf("?") < 0) {
				url += "?";
			}
			if (params != null) {
				boolean firstParams = true;
				for (String name : ((HashMap<String, String>) params[1])
						.keySet()) {
					if(firstParams) {
						firstParams = false;
						url += name
								+ "="
								+ ((HashMap<String, String>) params[1])
										.get(name);
					} else {
						url += "&"
								+ name
								+ "="
								+ ((HashMap<String, String>) params[1])
										.get(name);
					}
				}
			}
			HttpGet req = new HttpGet(url);
			request = req;
		}

		try {
			HttpResponse httpResponse = client.execute(request);
			
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils.toString(httpResponse.getEntity());
				return result;
				
			}
			return null;
		} catch (Exception e) {
			endTime = System.currentTimeMillis();
			return null;
		}
	}
}
