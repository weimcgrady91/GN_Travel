package com.gionee.gntravel.entity;


public class BaseBean {
	public static final int CODE_NORMAL = 0;
	public static final int CODE_PARAMS_ERROR = 1;
	public static final int CODE_SERVER_ERROR = 2;
	public static final int CODE_LOCAL_ERROR = 3;//客户端本身请求错误，比如，连接超时、读取超时，404等

	// {content:"",errorCode:0,errorString:""}
	private String content;// json 正常时候需要解析的内容
	private int errorCode;// 0：正常，否则就是错误
	private String errorMsg;// 错误消息，错误时候需要读取的内容

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

}
