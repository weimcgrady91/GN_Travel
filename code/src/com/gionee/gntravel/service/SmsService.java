package com.gionee.gntravel.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

public class SmsService extends Service {

	private String content;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
//		if(intent != null) {
//			Bundle bundle = intent.getExtras();
//			content = bundle.getString("content");
//			parseSms();
////			upLoadOrder();
//		}
		stopSelf();
		return START_REDELIVER_INTENT;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	
	public void upLoadOrder() {
		
	}

	public void parseSms() {
		
	}
	
	public String getSmsInPhone()   
	{   
	    final String SMS_URI_ALL   = "content://sms/";     
	    final String SMS_URI_INBOX = "content://sms/inbox";   
	    final String SMS_URI_SEND  = "content://sms/sent";   
	    final String SMS_URI_DRAFT = "content://sms/draft";   
	       
	    StringBuilder smsBuilder = new StringBuilder();   
	       
	    try{   
	        ContentResolver cr = getContentResolver();   
	        String[] projection = new String[]{"_id", "address", "person",    
	                "body", "date", "type"};   
	        Uri uri = Uri.parse(SMS_URI_ALL);   
	        Cursor cur = cr.query(uri, projection, null, null, "date desc");   
	  
	        if (cur.moveToFirst()) {   
	            String name;    
	            String phoneNumber;          
	            String smsbody;   
	            String date;   
	            String type;   
	            
	            int nameColumn = cur.getColumnIndex("person");   
	            int phoneNumberColumn = cur.getColumnIndex("address");   
	            int smsbodyColumn = cur.getColumnIndex("body");   
	            int dateColumn = cur.getColumnIndex("date");   
	            int typeColumn = cur.getColumnIndex("type");   
	            
	            do{   
	                name = cur.getString(nameColumn);                
	                phoneNumber = cur.getString(phoneNumberColumn);   
	                smsbody = cur.getString(smsbodyColumn);   
	                   
	                SimpleDateFormat dateFormat = new SimpleDateFormat(   
	                        "yyyy-MM-dd hh:mm:ss");   
	                Date d = new Date(Long.parseLong(cur.getString(dateColumn)));   
	                date = dateFormat.format(d);   
	                   
	                int typeId = cur.getInt(typeColumn);   
	                if(typeId == 1){   
	                    type = "接收";   
	                } else if(typeId == 2){   
	                    type = "发送";   
	                } else {   
	                    type = "";   
	                }   
	                
	                smsBuilder.append("[");   
	                smsBuilder.append(name+",");   
	                smsBuilder.append(phoneNumber+",");   
	                smsBuilder.append(smsbody+",");   
	                smsBuilder.append(date+",");   
	                smsBuilder.append(type);   
	                smsBuilder.append("] ");   
	                
	                if(smsbody == null) smsbody = "";     
	            }while(cur.moveToNext());   
	        } else {   
	            smsBuilder.append("no result!");   
	        }   
	            
	        smsBuilder.append("getSmsInPhone has executed!");   
	    } catch(SQLiteException ex) {   
	        Log.d("SQLiteException in getSmsInPhone", ex.getMessage());   
	    }   
	    return smsBuilder.toString();   
	}
	

	
}
