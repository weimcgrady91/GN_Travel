package com.gionee.gntravel;

import android.app.Application;

public class GNApplication extends Application {
//
//	public static BMapManager mBMapManager;
//	private boolean keyIsOk;
//
//	@Override
//	public void onCreate() {
//		super.onCreate();
//		if (mBMapManager == null) {
//			mBMapManager = new BMapManager(this);
//		}
//
//		if (!mBMapManager.init(this)) {
//			Toast.makeText(this, "BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
//		}
//	}
//
//	@Override
//	public void onTerminate() {
//		// TODO Auto-generated method stub
//		super.onTerminate();
//	}
//
//	/**************发生异常回调*******************/
//
//	@Override
//	public void onGetNetworkState(int iError) {
//		if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
//			Toast.makeText(this, "您的网络出错啦！", Toast.LENGTH_LONG).show();
//		} else if (iError == MKEvent.ERROR_NETWORK_DATA) {
//			Toast.makeText(this, "输入正确的检索条件！", Toast.LENGTH_LONG).show();
//		}
//	}
//
//	@Override
//	public void onGetPermissionState(int iError) {
//		// 非零值表示key验证未通过
//		if (iError != 0) {
//			// 授权Key错误：
//			Toast.makeText(this, "AndroidManifest.xml 文件输入正确的授权Key,并检查您的网络连接是否正常！error: " + iError, Toast.LENGTH_LONG)
//					.show();
//			keyIsOk = false;
//		} else {
//			keyIsOk = true;
//			Toast.makeText(this, "key认证成功", Toast.LENGTH_LONG).show();
//		}
//	}
}
