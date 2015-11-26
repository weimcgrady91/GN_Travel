package com.gionee.push.assist;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * Author: wangxin <br/>
 * Date: 12-7-4 <br/>
 */

public class InfoSendThread extends Thread {
    private WeakReference<Context> mContextRef;
    private String mRid;

    public InfoSendThread(Context context, String rid) {
        mContextRef = new WeakReference<Context>(context);
        mRid = rid;
    }

    @Override
    public void run() {
        Context context = mContextRef.get();
        if (context == null) {
            return;
        }

        sendInfoToAps(context, mRid);

        EmptyService.cancelKeep(context);
    }

    private void sendInfoToAps(Context context, String rid) {
        List<NameValuePair> postParam = new ArrayList<NameValuePair>(2);
        String apsServer = "http://push.gionee.com/aps/AcceptRid";

        postParam.add(new BasicNameValuePair("rid", rid));
        postParam.add(new BasicNameValuePair("packagename", context.getPackageName()));
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        postParam.add(new BasicNameValuePair("imei", imei));

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(apsServer);
        try {
            HttpEntity httpEntity = new UrlEncodedFormEntity(postParam, "UTF-8");
            httpPost.setEntity(httpEntity);

            HttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(response.getEntity());
                System.out.println(result);
            } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_FORBIDDEN) {
                String result = EntityUtils.toString(response.getEntity());
                System.out.println(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
