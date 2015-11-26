package com.gionee.gntravel;

import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.util.EncodingUtils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctrip.openapi.java.utils.ConfigData;
import com.ctrip.openapi.java.utils.SignatureUtils;
import com.gionee.gntravel.customview.CustomProgressDialog;
import com.gionee.gntravel.utils.DateUtils;
import com.gionee.gntravel.utils.FinalString;
import com.youju.statistics.YouJuAgent;

public class H5PayOrder extends Activity implements OnClickListener {
	private static final String TAG = "H5PayOrder";
	final Handler myHandler = new Handler();
	private ImageView loadingBar;
	private int mWindowWidth; 
	private View mErrorFrame;
	private WebView webView;
	private String tempOrderID;
	private String timeStamp;
	private String signature = "";
	private String returnUrl;
	private String SHOW_URL;
	private static final String DESCRIPTION = "submitorder";
	private CustomProgressDialog progressDialog;
	private String mUserName;
	private TravelApplication app;
	private int mAnimCurrentPosition;
	private boolean animloadFlag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_h5pay);
		YouJuAgent.onEvent(this, getString(R.string.youju_getflightorder));
		initView();
		processData();
		loadUrl();
	}
	
	private void initView() {
		WindowManager wm = this.getWindowManager();
	    mWindowWidth = wm.getDefaultDisplay().getWidth();
	    
		loadingBar = (ImageView) findViewById(R.id.loadingBar);
		mErrorFrame = (View) findViewById(R.id.mErrorFrame);
		mErrorFrame.setOnClickListener(this);
		final TextView tv_title = (TextView) findViewById(R.id.tv_title);
		SHOW_URL = getResources().getString(R.string.gionee_host) + FinalString.FLIGHT_SHOW_URL;
		webView = (WebView) findViewById(R.id.webView);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setLightTouchEnabled(true);
		final JavaScriptInterface myJavaScriptInterface = new JavaScriptInterface(
				this);
		webView.addJavascriptInterface(myJavaScriptInterface, "AndroidFunction");
		webView.getSettings().setDefaultTextEncodingName("UTF-8");
		webView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return super.shouldOverrideUrlLoading(view, url);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				mErrorFrame.setVisibility(View.VISIBLE);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				loadingBar.getLayoutParams().width=0;
				loadingBar.requestLayout();
				loadingBar.setVisibility(View.VISIBLE);
				activityLoadingAnimation(mWindowWidth-100);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				loadingBar.setVisibility(View.GONE);
				ViewWrapper wrapper = new ViewWrapper(loadingBar);
				wrapper.setWidth(0);
			}
			
			
		});
		webView.setWebChromeClient(new WebChromeClient() {

			public void onProgressChanged(WebView view, int progress) {
				
				if(animloadFlag) {
					ViewWrapper wrapper = new ViewWrapper(loadingBar);
					ObjectAnimator animator = ObjectAnimator.ofInt(wrapper, "width", progress + mAnimCurrentPosition);
					animator.start();
				}

				if (progress == 100) {
					animloadFlag = false;
//					loadingBar.setVisibility(View.GONE);
//					ViewWrapper wrapper = new ViewWrapper(loadingBar);
//					wrapper.setWidth(0);
				}
			}

		});
		webView.setOnLongClickListener(new OnLongClickListener() {  
            
	          @Override  
	          public boolean onLongClick(View v) {  
	              return true;  
	          }  
	      }); 
		
		tv_title.setText(getString(R.string.pay_title));
		tv_title.setOnClickListener(this);

	}

	private void processData() {
		app = (TravelApplication) getApplication();
		mUserName = app.getU();
		Intent intent = getIntent();
		tempOrderID = intent.getStringExtra("tempOrderID");
		timeStamp = SignatureUtils.GetTimeStamp() + "";
		try {
//			signature = SignatureUtils.CalculationSignature(timeStamp,
//					ConfigData.AllianceId, ConfigData.SecretKey,
//					ConfigData.SId, "mobilepayentry");
			signature = SignatureUtils.CalculationSignature(timeStamp,
					ConfigData.AllianceId, ConfigData.SecretKey,
					ConfigData.SId, "paymententry");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		

		returnUrl = getString(R.string.gionee_host)
				+ "/GioneeTrip/tripAction_payResult.action?u="+mUserName+"-"+app.getTripName();

	}

	private void loadUrl() {
		String url = processUrl();
//		startProgressDialog();
		
		String postData = "ReturnUrl="+returnUrl+"&Description="+DESCRIPTION+"&ShowUrl="+SHOW_URL
				+"&PaymentDescription="+DESCRIPTION+"&OrderID="+tempOrderID+"&OrderType=1&Language=ZH&OrderSummary=gioneeOrder";	
		webView.postUrl(url, EncodingUtils.getBytes(postData, "UTF-8"));
		loadingBar.getLayoutParams().width=0;
		loadingBar.requestLayout();
		loadingBar.setVisibility(View.VISIBLE);
		activityLoadingAnimation(mWindowWidth-100);
	}

	private String processUrl() {
		StringBuilder url = new StringBuilder();

		
//		url.append("http://openapi.ctrip.com/Flight/MobilePayEntry.aspx?AllianceID=");
//		url.append(ConfigData.AllianceId);
//		url.append("&SID=");
//		url.append(ConfigData.SId);
//		url.append("&TimeStamp=");
//		url.append(timeStamp);
//		url.append("&Signature=");
//		url.append(signature);
//		url.append("&RequestType=mobilepayentry");
		
		
		url.append("http://openapi.ctrip.com/Flight/PaymentEntry.aspx?AllianceID=");
		url.append(ConfigData.AllianceId);
		url.append("&SID=");
		url.append(ConfigData.SId);
		url.append("&TimeStamp=");
		url.append(timeStamp);
		url.append("&Signature=");
		url.append(signature);
		url.append("&RequestType=paymententry");
		
		
//		url.append("&ReturnUrl=");
//		url.append(returnUrl);
//		url.append("&Description=");
//		url.append(DESCRIPTION);
//		url.append("&ShowUrl=");
//		url.append(SHOW_URL);
//		url.append("&PaymentDescription=");
//		url.append(DESCRIPTION);
//		url.append("&OrderID=");
//		url.append(tempOrderID);
//		url.append("&OrderType=1&Language=ZH");
//		url.append("&OrderSummary=test");
		return url.toString();
	}

	private void startProgressDialog() {
		if (progressDialog == null) {
			progressDialog = CustomProgressDialog.createDialog(this,R.layout.customprogressdialog);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setCancelable(false);
		}
		progressDialog.show();
	}

	private void stopProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	@Override
	protected void onDestroy() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title:
			finish();
			break;
		default:
			break;
		}
	}

	public class JavaScriptInterface {
		Context mContext;

		JavaScriptInterface(Context c) {
			mContext = c;
		}

		public void goToHotel() {
			Intent intent = new Intent(H5PayOrder.this, ListOfMessageActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			String takeOffTime = app.getTakeOffDate();
			Date returnDate = DateUtils.StringToDate(takeOffTime,"yyyy-MM-dd");
			Calendar c = Calendar.getInstance();
			c.setTime(returnDate);
			c.add(Calendar.DAY_OF_YEAR, +1);
			String flightReturnDate = DateUtils.dateToStr(c.getTime());
			intent.putExtra("TakeOffTime", takeOffTime);
			intent.putExtra("departCity", app.getDepartcity());
			intent.putExtra("arriveCity", app.getArrivecity());
			intent.putExtra("flightReturnDate", flightReturnDate);
			intent.putExtra("showHotelList", true);
			startActivity(intent);
			YouJuAgent.onEvent(H5PayOrder.this, getString(R.string.youju_flight_pay_order_succ));
			finish();
			
		}

		public void goToReturnFlgith() {
			
			Intent intent = new Intent(H5PayOrder.this, ListOfMessageActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			String takeOffTime = app.getTakeOffDate();
			Date returnDate = DateUtils.StringToDate(takeOffTime,"yyyy-MM-dd");
			Calendar c = Calendar.getInstance();
			c.setTime(returnDate);
			c.add(Calendar.DAY_OF_YEAR, +1);
			String flightReturnDate = DateUtils.dateToStr(c.getTime());
			
			intent.putExtra("TakeOffTime", takeOffTime);
			intent.putExtra("departCity", app.getArrivecity());
			intent.putExtra("arriveCity", app.getDepartcity());
			intent.putExtra("flightReturnDate", flightReturnDate);
			startActivity(intent);
			YouJuAgent.onEvent(H5PayOrder.this, getString(R.string.youju_flight_pay_order_succ));
			finish();
		}

		public void tripFinish() {
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), MainTabActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			YouJuAgent.onEvent(H5PayOrder.this, getString(R.string.youju_flight_pay_order_succ));
			finish();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		sendRefershBroadcast();
	}
	
	public void sendRefershBroadcast() {
		Intent intent = new Intent(FinalString.REFRESH_TRIP_ACTION);
		sendBroadcast(intent);
	}
	
    private static class ViewWrapper {
        private View mTarget;

        public ViewWrapper(View target) {
            mTarget = target;
        }

        public int getWidth() {
            return mTarget.getLayoutParams().width;
        }

        public void setWidth(int width) {
            mTarget.getLayoutParams().width = width;
            mTarget.requestLayout();
        }
    }
	private void activityLoadingAnimation(int targetWidth) {

		ViewWrapper wrapper = new ViewWrapper(loadingBar);
		ObjectAnimator animator = ObjectAnimator.ofInt(wrapper, "width", targetWidth);
		animator.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				mAnimCurrentPosition = (Integer)animation.getAnimatedValue(); 
			}
		});
		animator.setDuration(600);
		animator.addListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator animation) {
				
			}
			
			@Override
			public void onAnimationRepeat(Animator animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				animloadFlag = true;
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
				
			}
		});
		animator.start();
	}
}

//
//returnUrl =
//getString(R.string.gionee_host)+"/GioneeTrip/tripAction_payResult.action?tel=18710087516&tripName=2014/12/12北京到上海"
//+"&TransactionID=1234567&Amount=470&CurrencyCode=CNY&OrderID=1058600866&Status=null";
