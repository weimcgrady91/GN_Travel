package com.gionee.gntravel.provider;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.gionee.gntravel.provider.GNTravelProviderMetaData.Citys_TableMetaData;
import com.gionee.gntravel.provider.GNTravelProviderMetaData.FlightCity;
import com.gionee.gntravel.provider.GNTravelProviderMetaData.FlightCompany;
import com.gionee.gntravel.provider.GNTravelProviderMetaData.FlightCraftype;
import com.gionee.gntravel.provider.GNTravelProviderMetaData.SmsIntercept;
import com.gionee.gntravel.provider.GNTravelProviderMetaData.Traveller_TableMetaData;


public class GNTravelProvider extends ContentProvider {
	public static final String TAG = "GN_TravelProvider";
	private DataBaseHelper mOpenHelper ;
	private static HashMap<String, String> sCitysProjectMap;
	static {
		sCitysProjectMap = new HashMap<String, String>();
		sCitysProjectMap.put(Citys_TableMetaData._ID, Citys_TableMetaData._ID);
		sCitysProjectMap.put(Citys_TableMetaData.CITY_NAME, Citys_TableMetaData.CITY_NAME);
		sCitysProjectMap.put(Citys_TableMetaData.CITY_CODE, Citys_TableMetaData.CITY_CODE);
		sCitysProjectMap.put(Citys_TableMetaData.CITY_ENAME, Citys_TableMetaData.CITY_ENAME);
		sCitysProjectMap.put(Citys_TableMetaData.CITY_AIRPORT, Citys_TableMetaData.CITY_AIRPORT);
		sCitysProjectMap.put(Citys_TableMetaData.CITY_COUNTRY, Citys_TableMetaData.CITY_COUNTRY);
		sCitysProjectMap.put(Citys_TableMetaData.CITY_PROVINCE, Citys_TableMetaData.CITY_PROVINCE);
		sCitysProjectMap.put(Citys_TableMetaData.CITY_CITYNAMEJP, Citys_TableMetaData.CITY_CITYNAMEJP);
		sCitysProjectMap.put(Citys_TableMetaData.CITY_CITYNAMEPY, Citys_TableMetaData.CITY_CITYNAMEPY);
		sCitysProjectMap.put(Citys_TableMetaData.CITY_ISHOT, Citys_TableMetaData.CITY_ISHOT);
		sCitysProjectMap.put(Citys_TableMetaData.CITY_ACTIVE, Citys_TableMetaData.CITY_ACTIVE);
	}
	private static HashMap<String, String> sTravellerProjectMap;
	static {
		sTravellerProjectMap = new HashMap<String, String>();
		sTravellerProjectMap.put(Traveller_TableMetaData.TRAVELLER_CODE, Traveller_TableMetaData.TRAVELLER_CODE);
		sTravellerProjectMap.put(Traveller_TableMetaData.TRAVELLER_NAME, Traveller_TableMetaData.TRAVELLER_NAME);
		sTravellerProjectMap.put(Traveller_TableMetaData.TRAVELLER_TYPE, Traveller_TableMetaData.TRAVELLER_TYPE);
		sTravellerProjectMap.put(Traveller_TableMetaData.TRAVELLER_TELPHONE, Traveller_TableMetaData.TRAVELLER_TELPHONE);
	}
	private static HashMap<String,String> sFlightCompany;
	static {
		sFlightCompany = new HashMap<String, String>();
		sFlightCompany.put(FlightCompany.AIRLINEID, FlightCompany.AIRLINEID);
		sFlightCompany.put(FlightCompany.AIRLINECODE, FlightCompany.AIRLINECODE);
		sFlightCompany.put(FlightCompany.AIRLINENAME, FlightCompany.AIRLINENAME);
		sFlightCompany.put(FlightCompany.AIRLINENAMESHORT, FlightCompany.AIRLINENAMESHORT);
		sFlightCompany.put(FlightCompany.AIRLINENAMEEN, FlightCompany.AIRLINENAMEEN);
		sFlightCompany.put(FlightCompany.AIRLINENAMEPY, FlightCompany.AIRLINENAMEPY);
		sFlightCompany.put(FlightCompany.AIRLINENAMEJP, FlightCompany.AIRLINENAMEJP);
		sFlightCompany.put(FlightCompany.FIRSTLETTEREN, FlightCompany.FIRSTLETTEREN);
		sFlightCompany.put(FlightCompany.FIRSTLETTERPY, FlightCompany.FIRSTLETTERPY);
		sFlightCompany.put(FlightCompany.COUNTRYNAME, FlightCompany.COUNTRYNAME);
		sFlightCompany.put(FlightCompany.FLAG, FlightCompany.FLAG);
		sFlightCompany.put(FlightCompany.WEIGHTFLAG, FlightCompany.WEIGHTFLAG);
		sFlightCompany.put(FlightCompany.HOTFLAG, FlightCompany.HOTFLAG);
	}
	private static HashMap<String,String> sFlightCity;
	static {
		sFlightCity = new HashMap<String, String>();
		sFlightCity.put(FlightCity.AIRPORTCITYDATAID, FlightCity.AIRPORTCITYDATAID);
		sFlightCity.put(FlightCity.CITYID, FlightCity.CITYID);
		sFlightCity.put(FlightCity.CITYNAME, FlightCity.CITYNAME);
		sFlightCity.put(FlightCity.CITYNAMEEN, FlightCity.CITYNAMEEN);
		sFlightCity.put(FlightCity.CITYNAMEPY, FlightCity.CITYNAMEPY);
		sFlightCity.put(FlightCity.CITYNAMEJP, FlightCity.CITYNAMEJP);
		sFlightCity.put(FlightCity.CITYCODE, FlightCity.CITYCODE);
		sFlightCity.put(FlightCity.AIRPORTCODE, FlightCity.AIRPORTCODE);
		sFlightCity.put(FlightCity.AIRPORTNAME, FlightCity.AIRPORTNAME);
		sFlightCity.put(FlightCity.FIRSTLETTER, FlightCity.FIRSTLETTER);
		sFlightCity.put(FlightCity.COUNTRYNAME, FlightCity.COUNTRYNAME);
		sFlightCity.put(FlightCity.FLAG, FlightCity.FLAG);
		sFlightCity.put(FlightCity.LATITUDE, FlightCity.LATITUDE);
		sFlightCity.put(FlightCity.LONGITUDE, FlightCity.LONGITUDE);
		sFlightCity.put(FlightCity.WEIGHTFLAG, FlightCity.WEIGHTFLAG);
		sFlightCity.put(FlightCity.HOTFLAG, FlightCity.HOTFLAG);
	}
	
	private static HashMap<String,String> sFlightCrafType;
	static {
		sFlightCrafType = new HashMap<String, String>();
		sFlightCrafType.put(FlightCraftype.CRAFTTYPE, FlightCraftype.CRAFTTYPE);
		sFlightCrafType.put(FlightCraftype.CTNAME, FlightCraftype.CTNAME);
		sFlightCrafType.put(FlightCraftype.WIDTHLEVEL, FlightCraftype.WIDTHLEVEL);
		sFlightCrafType.put(FlightCraftype.MINSEATS, FlightCraftype.MINSEATS);
		sFlightCrafType.put(FlightCraftype.MAXSEATS, FlightCraftype.MAXSEATS);
		sFlightCrafType.put(FlightCraftype.NOTE, FlightCraftype.NOTE);
		sFlightCrafType.put(FlightCraftype.CRAFTKIND, FlightCraftype.CRAFTKIND);
		sFlightCrafType.put(FlightCraftype.CRAFTTYPE_NAME, FlightCraftype.CRAFTTYPE_NAME);
		sFlightCrafType.put(FlightCraftype.HOTFLAG, FlightCraftype.HOTFLAG);
	}
	
	private static HashMap<String,String> sInterceptSms;
	static {
		sInterceptSms = new HashMap<String, String>();
		sInterceptSms.put(SmsIntercept.INTERCEPT_SMS_ID, SmsIntercept.INTERCEPT_SMS_ID);
	}
	
	private static final UriMatcher sUriMatcher;
	private static final int INCOMING_CITY_COLLECTION_URI_INDICATOR = 1;
	private static final int INCOMING_SINGLE_CITY_URI_INDICATOR = 2;
	private static final int INCOMING_AIRPORT_COLLECTION_URI_INDICATOR = 3;
	private static final int INCOMING_SINGLE_AIRPORT_URI_INDICATOR = 4;
	private static final int INCOMING_TRAVELLER_COLLECTION_URI_INDICATOR = 5;
	private static final int INCOMING_SINGLE_TRAVELLER_URI_INDICATOR = 6;
	private static final int INCOMING_FLIGHTCITY_COLLECTION_URI_INDICATOR = 7;
	private static final int INCOMING_SINGLE_FLIGHTCITY_URI_INDICATOR = 8;
	private static final int INCOMING_FLIGHTCRAFTYPE_COLLECTION_URI_INDICATOR = 9;
	private static final int INCOMING_SINGLE_FLIGHTCRAFTYPE_URI_INDICATOR = 10;
	private static final int INCOMING_INTERCEPT_SMS_COLLECTION_URI_INDICATOR = 11;
	private static final int INCOMING_SINGLE_INTERCEPT_SMS_URI_INDICATOR = 12;
	
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(GNTravelProviderMetaData.AUTHORITY, "citys", INCOMING_CITY_COLLECTION_URI_INDICATOR);
		sUriMatcher.addURI(GNTravelProviderMetaData.AUTHORITY, "citys/#", INCOMING_SINGLE_CITY_URI_INDICATOR);
		sUriMatcher.addURI(GNTravelProviderMetaData.AUTHORITY, "traveller", INCOMING_TRAVELLER_COLLECTION_URI_INDICATOR);
		sUriMatcher.addURI(GNTravelProviderMetaData.AUTHORITY, "traveller/#", INCOMING_SINGLE_TRAVELLER_URI_INDICATOR);
		sUriMatcher.addURI(GNTravelProviderMetaData.AUTHORITY, "filghtCompany", INCOMING_AIRPORT_COLLECTION_URI_INDICATOR);
		sUriMatcher.addURI(GNTravelProviderMetaData.AUTHORITY, "filghtCompany/#", INCOMING_SINGLE_AIRPORT_URI_INDICATOR);
		sUriMatcher.addURI(GNTravelProviderMetaData.AUTHORITY, "filghtCity", INCOMING_FLIGHTCITY_COLLECTION_URI_INDICATOR);
		sUriMatcher.addURI(GNTravelProviderMetaData.AUTHORITY, "filghtCity/#", INCOMING_SINGLE_FLIGHTCITY_URI_INDICATOR);
		sUriMatcher.addURI(GNTravelProviderMetaData.AUTHORITY, "filghtCraftype", INCOMING_FLIGHTCRAFTYPE_COLLECTION_URI_INDICATOR);
		sUriMatcher.addURI(GNTravelProviderMetaData.AUTHORITY, "filghtCraftype/#", INCOMING_SINGLE_FLIGHTCRAFTYPE_URI_INDICATOR);
		sUriMatcher.addURI(GNTravelProviderMetaData.AUTHORITY, "sms_intercept", INCOMING_INTERCEPT_SMS_COLLECTION_URI_INDICATOR);
		sUriMatcher.addURI(GNTravelProviderMetaData.AUTHORITY, "sms_intercept/#", INCOMING_SINGLE_INTERCEPT_SMS_URI_INDICATOR);
	}
	
	
	private class DataBaseHelper extends SQLiteOpenHelper {
		
		public DataBaseHelper(Context context) {
			super(context, GNTravelProviderMetaData.DATABASE_NAME, null, GNTravelProviderMetaData.DATABASE_VERSION);
		}
		public DataBaseHelper(Context context, String name,
				CursorFactory factory, int version,
				DatabaseErrorHandler errorHandler) {
			super(context, name, factory, version, errorHandler);
		}

		public DataBaseHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + GNTravelProviderMetaData.CITYS_TABLE_NAME + "("
					+ Citys_TableMetaData._ID + " INTEGER PRIMARY KEY, " 
					+ Citys_TableMetaData.CITY_NAME + " TEXT, "
					+ Citys_TableMetaData.CITY_CODE + " TEXT, "
					+ Citys_TableMetaData.CITY_ENAME + " TEXT, "
					+ Citys_TableMetaData.CITY_AIRPORT + " TEXT, "
					+ Citys_TableMetaData.CITY_COUNTRY + " TEXT, "
					+ Citys_TableMetaData.CITY_PROVINCE + " TEXT, "
					+ Citys_TableMetaData.CITY_CITYNAMEJP + " TEXT, "
					+ Citys_TableMetaData.CITY_CITYNAMEPY + " TEXT, "
					+ Citys_TableMetaData.CITY_ISHOT + " INTEGER, "
					+ Citys_TableMetaData.CITY_ACTIVE + " ); ");
			db.execSQL("CREATE TABLE " + GNTravelProviderMetaData.TRAVELLER_TABLE_NAME + "("
					+ Traveller_TableMetaData._ID + " INTEGER PRIMARY KEY, " 
					+ Traveller_TableMetaData.TRAVELLER_CODE + " TEXT, "
					+ Traveller_TableMetaData.TRAVELLER_NAME + " TEXT, "
					+ Traveller_TableMetaData.TRAVELLER_TELPHONE + " TEXT, "
					+ Traveller_TableMetaData.TRAVELLER_TYPE + " TEXT ); ");
			System.out.println("创建表成功");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXITS " + GNTravelProviderMetaData.CITYS_TABLE_NAME);
			db.execSQL("DROP TABLE IF EXITS " + GNTravelProviderMetaData.TRAVELLER_TABLE_NAME);
			onCreate(db);
		}
		
	}
	
	private void create_travller(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS " + GNTravelProviderMetaData.TRAVELLER_TABLE_NAME + "("
				+ Traveller_TableMetaData._ID + " INTEGER PRIMARY KEY, " 
				+ Traveller_TableMetaData.TRAVELLER_CODE + " TEXT, "
				+ Traveller_TableMetaData.TRAVELLER_NAME + " TEXT, "
				+ Traveller_TableMetaData.TRAVELLER_TELPHONE + " TEXT, "
				+ Traveller_TableMetaData.TRAVELLER_TYPE + " TEXT ); ");
	}
	
	private void create_sms_intercept(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS " + GNTravelProviderMetaData.SMS_INTERCEPT_TABLE_NAME + "("
				+ SmsIntercept._ID + " INTEGER PRIMARY KEY, " 
				+ SmsIntercept.INTERCEPT_SMS_ID + " INTEGER NOT NULL); " );
	}
	
	@Override
	public boolean onCreate() {
		mOpenHelper = new DataBaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		
		
		
		switch(sUriMatcher.match(uri)) {
		case INCOMING_CITY_COLLECTION_URI_INDICATOR :
			qb.setTables(Citys_TableMetaData.TABLE_NAME);
			qb.setProjectionMap(sCitysProjectMap);
			break;
		case INCOMING_SINGLE_CITY_URI_INDICATOR : 
			qb.setTables(Citys_TableMetaData.TABLE_NAME);
			qb.setProjectionMap(sCitysProjectMap);
			qb.appendWhere(Citys_TableMetaData._ID + "=" + uri.getPathSegments().get(1));
			break;
		case INCOMING_TRAVELLER_COLLECTION_URI_INDICATOR :
			create_travller(db);
			qb.setTables(Traveller_TableMetaData.TABLE_NAME);
			qb.setProjectionMap(sTravellerProjectMap);
			break;
		case INCOMING_SINGLE_TRAVELLER_URI_INDICATOR : 
			create_travller(db);
			qb.setTables(Traveller_TableMetaData.TABLE_NAME);
			qb.setProjectionMap(sTravellerProjectMap);
			qb.appendWhere(Traveller_TableMetaData._ID + "=" + uri.getPathSegments().get(1));
			break;
		case INCOMING_AIRPORT_COLLECTION_URI_INDICATOR:
			qb.setTables(FlightCompany.TABLE_NAME);
			qb.setProjectionMap(sFlightCompany);
			break;
		case INCOMING_FLIGHTCITY_COLLECTION_URI_INDICATOR:
			qb.setTables(FlightCity.TABLE_NAME);
			qb.setProjectionMap(sFlightCity);
			break;
		case INCOMING_FLIGHTCRAFTYPE_COLLECTION_URI_INDICATOR:
			qb.setTables(FlightCraftype.TABLE_NAME);
			qb.setProjectionMap(sFlightCrafType);
			break;
		case INCOMING_INTERCEPT_SMS_COLLECTION_URI_INDICATOR:
			create_sms_intercept(db);
			qb.setTables(SmsIntercept.TABLE_NAME);
			qb.setProjectionMap(sInterceptSms);
			break;
		default :
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public String getType(Uri uri) {
		switch(sUriMatcher.match(uri)) {
		case INCOMING_CITY_COLLECTION_URI_INDICATOR : return Citys_TableMetaData.CONTENT_TYPE;
		case INCOMING_SINGLE_CITY_URI_INDICATOR : return Citys_TableMetaData.CONTENT_ITEM_TYPE;
		case INCOMING_TRAVELLER_COLLECTION_URI_INDICATOR : return Traveller_TableMetaData.CONTENT_TYPE;
		case INCOMING_SINGLE_TRAVELLER_URI_INDICATOR : return Traveller_TableMetaData.CONTENT_ITEM_TYPE;
		case INCOMING_INTERCEPT_SMS_COLLECTION_URI_INDICATOR: return SmsIntercept.CONTENT_ITEM_TYPE;
		case INCOMING_SINGLE_INTERCEPT_SMS_URI_INDICATOR: return SmsIntercept.CONTENT_ITEM_TYPE;
		default : throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initiaValues) {
//		if(sUriMatcher.match(uri) != INCOMING_CITY_COLLECTION_URI_INDICATOR&&sUriMatcher.match(uri) != INCOMING_TRAVELLER_COLLECTION_URI_INDICATOR) {
//			throw new IllegalArgumentException("Unknown URI " + uri);
//		}
		ContentValues values;
		if(initiaValues != null) {
			values = new ContentValues(initiaValues);
		} else {
			values = new ContentValues();
		}
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		switch (sUriMatcher.match(uri)) {
		case INCOMING_SINGLE_CITY_URI_INDICATOR:
		case INCOMING_CITY_COLLECTION_URI_INDICATOR:
			System.out.println("插入Citys");
			long rowId = db.insert(Citys_TableMetaData.TABLE_NAME, Citys_TableMetaData.CITY_NAME, values);
			if(rowId > 0) {
				Uri insertCityUri = ContentUris.withAppendedId(uri, rowId);
				getContext().getContentResolver().notifyChange(insertCityUri, null);
				return insertCityUri;

		}
		case INCOMING_TRAVELLER_COLLECTION_URI_INDICATOR:
		case INCOMING_SINGLE_TRAVELLER_URI_INDICATOR:
			System.out.println("插入travell");
			long rowId2 = db.insert(Traveller_TableMetaData.TABLE_NAME, Traveller_TableMetaData.TRAVELLER_NAME, values);
			if(rowId2 > 0) {
				Uri insertTravellUri = ContentUris.withAppendedId(uri, rowId2);
				getContext().getContentResolver().notifyChange(insertTravellUri, null);
				return insertTravellUri;
		}
		case INCOMING_INTERCEPT_SMS_COLLECTION_URI_INDICATOR:
			long rowId3 = db.insert(SmsIntercept.TABLE_NAME, SmsIntercept.TABLE_NAME, values);
			if(rowId3 > 0) {
				Uri insertIntercepSmstUri = ContentUris.withAppendedId(uri, rowId3);
				getContext().getContentResolver().notifyChange(insertIntercepSmstUri, null);
				return insertIntercepSmstUri;
			}
		}
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
	    SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
				SQLiteDatabase db=mOpenHelper.getWritableDatabase();
				int num=0;
				num=db.delete(Traveller_TableMetaData.TABLE_NAME, selection, selectionArgs);
				getContext().getContentResolver().notifyChange(uri, null);
			    return num;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int num = 0;
		switch (sUriMatcher.match(uri)) {
		case INCOMING_CITY_COLLECTION_URI_INDICATOR :
			num = db.update(Citys_TableMetaData.TABLE_NAME, values,selection, selectionArgs);
			getContext().getContentResolver().notifyChange(uri, null);
		    return num;
		case INCOMING_TRAVELLER_COLLECTION_URI_INDICATOR :
			num = db.update(Traveller_TableMetaData.TABLE_NAME, values,selection, selectionArgs);
			getContext().getContentResolver().notifyChange(uri, null);
		    return num;

		default :
			throw new SQLException("Failed to update row  " + uri);
		}
		
		


	}
	
}
