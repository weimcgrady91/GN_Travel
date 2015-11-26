package com.gionee.gntravel.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class GNTravelProviderMetaData {
	public static final String AUTHORITY = "com.gionee.provider.TravelProvider";
	public static final String DATABASE_NAME = "gntravel.db";
	public static final int DATABASE_VERSION = 1;
	public static final String CITYS_TABLE_NAME = "citys";
	public static final String TRAVELLER_TABLE_NAME = "traveller";
	public static final String SMS_INTERCEPT_TABLE_NAME = "sms_intercept";
	public GNTravelProviderMetaData(){}
	
	public static final class Citys_TableMetaData implements BaseColumns{
		public Citys_TableMetaData(){}
		public static final String TABLE_NAME = "citys";
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/citys");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.gionee.citys";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.gionee.citys";
		public static final String CITY_NAME = "cityName";
		public static final String CITY_CODE = "cityCode";
		public static final String CITY_ENAME = "cityEName";
		public static final String CITY_AIRPORT = "airport";
		public static final String CITY_COUNTRY = "country";
		public static final String CITY_PROVINCE = "province";
		public static final String CITY_CITYNAMEJP = "cityNameJP";
		public static final String CITY_CITYNAMEPY = "cityNamePY";
		public static final String CITY_ISHOT = "isHot";
		public static final String CITY_ACTIVE = "cityActive";
	}
	
	
	public static final class Traveller_TableMetaData implements BaseColumns{
		public Traveller_TableMetaData(){}
		public static final String TABLE_NAME = "traveller";
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/traveller");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.gionee.traveller";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.gionee.traveller";
		public static final String TRAVELLER_NAME = "travellerName";
		public static final String TRAVELLER_CODE = "travellerCode";
		public static final String TRAVELLER_TYPE = "travellerType";
		public static final String TRAVELLER_TELPHONE = "travellerTelphone";

	}
	
	public static final class FlightCompany implements BaseColumns{
		public FlightCompany(){}
		public static final String TABLE_NAME = "flight_company";
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/filghtCompany");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.gionee.filghtCompany";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.gionee.filghtCompany";
		public static final String AIRLINEID = "airlineID";
		public static final String AIRLINECODE = "airlineCode";
		public static final String AIRLINENAME = "airlineName";
		public static final String AIRLINENAMESHORT = "airlineNameShort";
		public static final String AIRLINENAMEEN = "airlineNameEN";
		public static final String AIRLINENAMEPY = "airlineNamePY";
		public static final String AIRLINENAMEJP = "airlineNameJP";
		public static final String FIRSTLETTEREN = "firstLetterEN";
		public static final String FIRSTLETTERPY = "firstLetterPY";
		public static final String COUNTRYID = "countryID";
		public static final String COUNTRYNAME = "countryName";
		public static final String FLAG = "flag";
		public static final String WEIGHTFLAG = "weightFlag";
		public static final String HOTFLAG = "hotFlag";
	}
	public static final class FlightCity implements BaseColumns{
		public FlightCity(){}
		public static final String TABLE_NAME = "flight_city";
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/filghtCity");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.gionee.filghtCity";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.gionee.filghtCity";
		public static final String AIRPORTCITYDATAID = "airportCityDataId";
		public static final String CITYID = "cityID";
		public static final String CITYNAME = "cityName";
		public static final String CITYNAMEEN = "cityNameEN";
		public static final String CITYNAMEPY = "cityNamePY";
		public static final String CITYNAMEJP = "cityNameJP";
		public static final String CITYCODE = "cityCode";
		public static final String AIRPORTCODE = "airportCode";
		public static final String AIRPORTNAME = "airportName";
		public static final String FIRSTLETTER = "firstLetter";
		public static final String COUNTRYNAME = "countryName";
		public static final String FLAG = "flag";
		public static final String LATITUDE = "latitude";
		public static final String LONGITUDE = "longitude";
		public static final String WEIGHTFLAG = "weightFlag";
		public static final String HOTFLAG = "hotFlag";
	}
	public static final class FlightCraftype implements BaseColumns{
		public FlightCraftype(){}
		public static final String TABLE_NAME = "flight_craftype";
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/filghtCraftype");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.gionee.filghtCraftype";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.gionee.filghtCraftype";
		public static final String CRAFTTYPE = "CraftType";
		public static final String CTNAME = "CTName";
		public static final String WIDTHLEVEL ="WidthLevel";
		public static final String MINSEATS = "MinSeats";
		public static final String MAXSEATS = "MaxSeats";
		public static final String NOTE = "note";
		public static final String CRAFTKIND = "CraftKind";
		public static final String CRAFTTYPE_NAME = "crafttype_ename";
		public static final String HOTFLAG = "hotFlag";
	}
	
	public static final class SmsIntercept implements BaseColumns{
		public SmsIntercept(){}
		public static final String TABLE_NAME = "sms_intercept";
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/sms_intercept");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.gionee.sms_intercept";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.gionee.sms_intercept";
		public static final String INTERCEPT_SMS_ID = "intercept_sms_id";
	}
}
