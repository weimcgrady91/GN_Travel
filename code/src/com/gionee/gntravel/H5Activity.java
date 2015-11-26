package com.gionee.gntravel;




import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.gionee.gntravel.utils.DownLoadUtils;

public class H5Activity extends Activity implements OnClickListener {
	private WebView webView;
	private String url;
	private String mTitle;
	private View mErrorFrame;
	private boolean mReturnHome;
	private WebBackForwardList mWebBackForwardList;
	private boolean mIsNeedRetunParent ;
	private ImageView loadingBar;
	private int mWindowWidth; 
	private int mAnimCurrentPosition;
	private boolean animloadFlag = false;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_h5pay);
		initViews();
		initUrl();
		loadUrl();
		
	}

	private void initUrl() {
		url = getIntent().getStringExtra("url");
		mReturnHome = getIntent().getBooleanExtra("returnHome", false);
		mIsNeedRetunParent = getIntent().getBooleanExtra("mIsNeedRetunParent", false);
	}

	private void loadUrl() {
		webView.loadUrl(url);
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
	
	private void initViews() {
		WindowManager wm = this.getWindowManager();
	    mWindowWidth = wm.getDefaultDisplay().getWidth();
	    
		loadingBar = (ImageView) findViewById(R.id.loadingBar);
		findViewById(R.id.tv_title).setOnClickListener(this);
		mErrorFrame = (View) findViewById(R.id.mErrorFrame);
		mErrorFrame.setOnClickListener(this);
		
		final TextView tv_title = (TextView) findViewById(R.id.tv_title);
		String title = getIntent().getStringExtra("title");
		if (!TextUtils.isEmpty(title)) {
			tv_title.setText(title);
		}
		
		webView = (WebView) findViewById(R.id.webView);
		mWebBackForwardList = webView.copyBackForwardList();
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setDomStorageEnabled(true);
		webView.setWebViewClient(new WebViewClient() {
			

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
		        if (url.startsWith("tel:")) { 
	                Intent intent = new Intent(Intent.ACTION_VIEW,
	                        Uri.parse(url)); 
	                startActivity(intent); 
	                return true;
	              }
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
				}
			}

		});
		// 屏蔽长按事件
		webView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				return true;
			}
		});
		webView.setDownloadListener(new DownloadListener() {
			
			@Override
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype, long contentLength) {
					dowanLoad(url, mimetype);
			}
		});
		activityLoadingAnimation(mWindowWidth-100);
		loadingBar.setVisibility(View.VISIBLE);
	}

	public void dowanLoad(String url, String mimetype) {
		DownLoadUtils.download(this, url);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			webView.goBack(); 
			return true;
		} else {
			if(mReturnHome) {
				finish();
				Intent intent = new Intent(this,H5Activity.class);
				intent.putExtra("url", getString(R.string.news_url));
				intent.putExtra("title", getString(R.string.widget_news));
				intent.putExtra("mIsNeedRetunParent", true);
				startActivity(intent);
				return true;
			} else {
				if(mIsNeedRetunParent) {
					returnParentActivity();
					return super.onKeyDown(keyCode, event);
				} else {
					return super.onKeyDown(keyCode, event);
				}

			}
		}
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title:
			if (webView.canGoBack()) {
				webView.goBack();
			} else {
				if(mReturnHome) {
					finish();
					Intent intent = new Intent(this,H5Activity.class);
					intent.putExtra("url", getString(R.string.news_url));
					intent.putExtra("title", getString(R.string.widget_news));
					intent.putExtra("mIsNeedRetunParent", true);
					startActivity(intent);
				} else {
					if(mIsNeedRetunParent) {
						returnParentActivity();
						finish();
					} else {
						finish();
					}
				}
				
			}
			break;
		case R.id.mErrorFrame:
			webView.reload(); 
			mErrorFrame.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}
	
	public void returnParentActivity() {
        Intent upIntent = NavUtils.getParentActivityIntent(this);  
        upIntent.putExtra("navigation", 1);
        if(upIntent != null) {
            TaskStackBuilder.create(this)  
            .addNextIntentWithParentStack(upIntent)  
            .startActivities();  
        }
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
}
