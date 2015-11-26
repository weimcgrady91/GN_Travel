package com.gionee.gntravel;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.TextView;
/**
 * 关于我们
 * @author lijinbao
 *
 */
public class AboutActivity extends Activity implements OnClickListener {
	private WebView webView;
	private TextView tvVersionName;
	private TextView tvTitle;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		setupView();
	}

	private void setupView() {
		tvTitle=(TextView) findViewById(R.id.tv_title);
		tvTitle.setText(getString(R.string.about_us));
		tvTitle.setOnClickListener(this);
		webView = (WebView) findViewById(R.id.wv);
		//webview 获取本地html
		webView.loadUrl("file:///android_asset/software.html");
		tvVersionName = (TextView) findViewById(R.id.tv_version_name);
		tvVersionName.setText(getAppVersioInfo());
	}

	private String getAppVersioInfo() {// 获取版本号和版本名字

		try {

			String pkName = this.getPackageName();

			String versionName = this.getPackageManager().getPackageInfo(

			pkName, 0).versionName;
			return versionName;

		} catch (Exception e) {
		}
		return "";

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title:
			this.finish();
			break;

		default:
			break;
		}

	}
}
