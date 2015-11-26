package com.gionee.gntravel.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

public class SmsBroadcastReceiver extends BroadcastReceiver {
	private Handler handler;
	private String patternCoder = "(?<!\\d)\\d{6}(?!\\d)";
	
	
	public SmsBroadcastReceiver(Handler handler) {
		super();
		this.handler = handler;
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		for (Object obj : objs) {
			byte[] pdu = (byte[]) obj;
			SmsMessage sms = SmsMessage.createFromPdu(pdu);
			// 短信的内容
			String message = sms.getMessageBody();
			// 短息的手机号。。+86开头？
			String from = sms.getOriginatingAddress();
			if (!TextUtils.isEmpty(from)) {
				String code = patternCode(message);
				if (!TextUtils.isEmpty(code)) {
					Message msg = Message.obtain();
					msg.what = FinalString.SMS_CODE_FINISH;
					msg.obj=code;
					handler.sendMessage(msg);
				}
			}
		}
	
	}
	private String patternCode(String patternContent) {
		if (TextUtils.isEmpty(patternContent)) {
			return null;
		}
		Pattern p = Pattern.compile(patternCoder);
		Matcher matcher = p.matcher(patternContent);
		if (matcher.find()) {
			return matcher.group();
		}
		return null;
	}
}
