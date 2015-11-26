package com.gionee.gntravel.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.util.Log;

import com.gionee.gntravel.entity.FlightSimple4Sms;
import com.gionee.gntravel.entity.SaveTripFromSms;
import com.gionee.gntravel.entity.SmsEntity;
import com.gionee.gntravel.provider.GNTravelProviderMetaData.SmsIntercept;

public class SmsUtils {
	private Context context;
	private final String SMS_URI_ALL = "content://sms/";  
	private SimpleDateFormat dateFormat ;
	private final Uri uri = SmsIntercept.CONTENT_URI;
	public SmsUtils(Context context) {
		this.context = context;
		dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss",Locale.getDefault()); 
	}
	
	public ArrayList<SmsEntity> getSmsInPhone()   
	{   
		ArrayList<SmsEntity> smsList = new ArrayList<SmsEntity>();
		
	    StringBuilder smsBuilder = new StringBuilder();   
	    try{   
	        ContentResolver cr = context.getContentResolver();   
	        String[] projection = new String[]{"_id", "address", "person",    
	                "body", "date", "type"};   
	        Uri uri = Uri.parse(SMS_URI_ALL);   
	        Cursor cur = cr.query(uri, projection, "address=?", new String[]{"+8618710087516"}, "date desc");   
	  
	        if (cur.moveToFirst()) { 
	        	int _id;
	            String phoneNumber;          
	            String smsbody;   
	            Date date; 
	            int _idColumn = cur.getColumnIndex("_id");
	            int phoneNumberColumn = cur.getColumnIndex("address");   
	            int smsbodyColumn = cur.getColumnIndex("body");   
	            int dateColumn = cur.getColumnIndex("date");   
	            
	            do{   
	            	_id = cur.getInt(_idColumn);
	                phoneNumber = cur.getString(phoneNumberColumn);   
	                smsbody = cur.getString(smsbodyColumn);   
	                date = new Date(Long.parseLong(cur.getString(dateColumn)));   
	                SmsEntity smsEntity = new SmsEntity();
	                smsEntity.setPhoneNumber(phoneNumber);
	                smsEntity.setSmsbody(smsbody);
	                smsEntity.setDate(date);
	                smsEntity.set_id(_id);
	                smsList.add(smsEntity);
	            }while(cur.moveToNext());   
	        } else {   
	        }   
	            
	        smsBuilder.append("getSmsInPhone has executed!");   
	    } catch(SQLiteException ex) {   
	        Log.d("SQLiteException in getSmsInPhone", ex.getMessage());   
	    }   
	    return smsList;  
	} 
	
	private FlightSimple4Sms parseFlightSms(String sms) {
		String orderId ;
		String takeOffDate;
		String takeOffTime;
		String arriveTime;
		String flightNum;
		String dportCity;
		String arriveCity;
		//"[携程]订单123456789已出票:12月12日06:35-06:50 CZ6412北京-上海.总金额510元[携程]."
		String text = sms;
		int maoHaoFirstPosition = text.indexOf(":");
		int juHaoFirstPosition = text.indexOf(".");
		String text0 = text.substring(0,maoHaoFirstPosition);
		System.out.println(text0);
		int hanziDan = text0.indexOf("单");
		int hanziYi = text0.indexOf("已");
		orderId = text0.substring(hanziDan+1, hanziYi);
		text0 =  text.substring(maoHaoFirstPosition+1, juHaoFirstPosition);
		String[] content = text0.split(" ");
		takeOffDate = content[0].substring(0,content[0].indexOf("日") + 1);
		String allTime = content[0].substring(content[0].indexOf("日") + 1);
		String[] times = allTime.split("-");
		takeOffTime = times[0];
		arriveTime = times[1];
		text0 = content[1];
		int firstHanziPosition = -1;
		for(int index=0;index<text0.length()-1;index++) {
			String word = text0.substring(index, index+1);
			if(word.compareTo("\u4e00")>0 && word.compareTo("\u9fa5")<0) {
				firstHanziPosition = index;
				break;
			}
		}
		flightNum = text0.substring(0, firstHanziPosition);
		String[] citys = text0.substring(firstHanziPosition).split("-");
		dportCity = citys[0];
		arriveCity = citys[1];
		
//		System.out.println("订单号:"+orderId+","+"出发日期:"+takeOffDate+","+"出发时间:"+takeOffTime+","+"到达时间:"+arriveTime
//				+","+"航班号:"+flightNum+","+"出发城市:"+dportCity+","+"到达城市:"+arriveCity);
		FlightSimple4Sms flightSimple = new FlightSimple4Sms();
		flightSimple.setOrderId(orderId);
		flightSimple.setTakeOffDate(takeOffDate);
		flightSimple.setTakeOffTime(takeOffTime);
		flightSimple.setArriveTime(arriveTime);
		flightSimple.setFlightNum(flightNum);
		flightSimple.setDportCity(dportCity);
		flightSimple.setArriveCity(arriveCity);
		return flightSimple;
	}
	
	private ArrayList<Integer> insertList = new ArrayList<Integer>();
	
	private void saveInsert_Id(int intercept_id) {
		insertList.add(intercept_id);
	}
	
	public void insertDB() {
		for(int index=0;index<insertList.size();index++) {
			ContentValues values = new ContentValues();
			values.put(SmsIntercept.INTERCEPT_SMS_ID, insertList.get(index));
			context.getContentResolver().insert(uri, values);
		}
	}
	
	private void createTrip(HashMap<String,ArrayList<SmsEntity>> map, ArrayList<SmsEntity> smsList) {
		ArrayList<SmsEntity> newSmsList = filterSmsList(smsList);// Remove already intercept sms_id;
		if(newSmsList.size() == 0) {
			return;
		}
		
		for(int index=0;index<newSmsList.size();index++) {
			SmsEntity smsEntity = newSmsList.get(index);
			String tripName = DateUtils.dateDelYear(smsEntity.getDate());
			if(!map.containsKey(tripName)) {
				ArrayList<SmsEntity> list = new ArrayList<SmsEntity>();
				list.add(smsEntity);
				map.put(tripName, list);
			} else {
				map.get(tripName).add(smsEntity);
			}
			saveInsert_Id(smsEntity.get_id());
		}
	}
	
	private ArrayList<SmsEntity> filterSmsList(ArrayList<SmsEntity> smsList) {
		ArrayList<SmsEntity> interceptList = new ArrayList<SmsEntity>();
		ArrayList<Integer> exitsList = new ArrayList<Integer>();
		Cursor cursor = context.getContentResolver().query(uri, new String[]{SmsIntercept.INTERCEPT_SMS_ID}, null, null, null);
		while(cursor.moveToNext()) {
			exitsList.add(cursor.getInt(0));
		}
		for(int index=0;index<smsList.size();index++) {
			SmsEntity smsEntity = smsList.get(index);
			if(!exitsList.contains(smsEntity.get_id())) {
				interceptList.add(smsEntity);
			}
		}
		return interceptList;
	}
	
	public String processDate(HashMap<String,ArrayList<SmsEntity>> map,ArrayList<SmsEntity> smsList) {
		createTrip(map, smsList);
		ArrayList<SaveTripFromSms> tripList = parseSms(map);
		if(tripList.size() == 0) {
			return null;
		}
		return JSONUtil.getInstance().toJSON(tripList);
	}
	
	private ArrayList<SaveTripFromSms> parseSms(HashMap<String,ArrayList<SmsEntity>> map) {
		ArrayList<SaveTripFromSms> tripList = new ArrayList<SaveTripFromSms>();
		if(map.size() == 0) {
			return tripList;
		}
		for(Map.Entry<String, ArrayList<SmsEntity>> entity : map.entrySet()) {
			SaveTripFromSms saveTripFormSms = new SaveTripFromSms();
			String tripName = entity.getKey();
			saveTripFormSms.setTripName(tripName);
			saveTripFormSms.setFlights(new ArrayList<FlightSimple4Sms>());
			ArrayList<SmsEntity> valuesList = entity.getValue();
			for(int index=0;index<valuesList.size();index++) {
				SmsEntity smsEntity = valuesList.get(index);
				switch(getSMSType(smsEntity)) {
				case FLIGHT:
					FlightSimple4Sms newFlightSimple4Sms = parseFlightSms(smsEntity.getSmsbody());
					newFlightSimple4Sms.setCreateTime(DateUtils.dateToTimeWithSpit_T(smsEntity.getDate()));
					saveTripFormSms.getFlights().add(newFlightSimple4Sms);
					break;
				case HOTEL:
					break;
				case UNKNOW:
					break;
				default:
					break;
				}
			}
			tripList.add(saveTripFormSms);
		}
		return tripList;
	}

	private SmsType getSMSType(SmsEntity smsEntity) {
		if(smsEntity.getSmsbody().startsWith("[携程]")) {
			return SmsType.FLIGHT;
		}
		if(smsEntity.getSmsbody().startsWith("确认:")) {
			return SmsType.HOTEL;
		}
		return SmsType.UNKNOW;
	}
	
	private enum SmsType {
		FLIGHT,HOTEL,UNKNOW
	}
}
