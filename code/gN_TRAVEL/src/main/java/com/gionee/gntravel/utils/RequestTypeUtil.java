package com.gionee.gntravel.utils;

public enum RequestTypeUtil {
	OTA_FLIGHTSEARCH("OTA_FlightSearch") {

		@Override
		public String getUrl() {
			String url = "http://{API_Url}/Flight/DomesticFlight/OTA_FlightSearch.asmx";
			return url.replace("{API_Url}", HOST);
		}
		
	},
	OTA_FLTSAVEORDER("OTA_FltSaveOrder") {

		@Override
		public String getUrl() {
			String url = "http://{API_Url}/Flight/DomesticFlight/OTA_FltSaveOrder.asmx";
			return url.replace("{API_Url}", HOST);
		}
		
	},
	OTA_FLTCANCELORDER("OTA_FltCancelOrder") {

		@Override
		public String getUrl() {
			String url = "http://{API_Url}/Flight/DomesticFlight/OTA_FltCancelOrder.asmx";
			return url.replace("{API_Url}", HOST);
		}
		
	},
	OTA_FLTORDERLIST("OTA_FltOrderList") {

		@Override
		public String getUrl() {
			String url = "http://{API_Url}/Flight/DomesticFlight/OTA_FltOrderList.asmx";
			return url.replace("{API_Url}", HOST);
		}
		
	},
	OTA_FLTVIEWORDER("OTA_FltViewOrder") {

		@Override
		public String getUrl() {
			String url = "http://{API_Url}/Flight/DomesticFlight/OTA_FltViewOrder.asmx";
			return url.replace("{API_Url}", HOST);
		}
		
	},
	OTA_GETSTATUSCHANGEDORDERS("OTA_GetStatusChangedOrders") {

		@Override
		public String getUrl() {
			String url = "http://{API_Url}/Flight/DomesticFlight/OTA_GetStatusChangedOrders.asmx";
			return url.replace("{API_Url}", HOST);
		}
		
	};
	
	public String requestType;
	
	RequestTypeUtil(String requestType) {
		this.requestType = requestType;
	}
	private static final String HOST = "openapi.ctrip.com";
	public abstract String getUrl();
}
