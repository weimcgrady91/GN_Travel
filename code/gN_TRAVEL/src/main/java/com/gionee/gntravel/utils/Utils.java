package com.gionee.gntravel.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gionee.gntravel.R;
/**
 * 
 * @author lijinbao
 *
 */
public class Utils {
	/*
	 * 截取出小时 和分钟
	 */

	public static String[] getTimes(String startTimeStr, String endTimeStr,
			int i) {
		String[] times = new String[2];

		String[] startTimeStrs = startTimeStr.split(":");
		String[] endTimeStrs = endTimeStr.split(":");

		long startTime = Long.parseLong(startTimeStrs[0]) * 60
				+ Long.parseLong(startTimeStrs[1]);
		long endTime = Long.parseLong(endTimeStrs[0]) * 60
				+ Long.parseLong(endTimeStrs[1]);

		long periodTime = endTime - startTime;

		periodTime = periodTime + i * 24 * 60;

		long periodHour = periodTime / 60;
		long periodMinute = periodTime % 60;
		times[0] = String.valueOf(periodHour);
		times[0] = "00" + times[0];
		times[0] = times[0].substring(times[0].length() - 2);
		times[1] = String.valueOf(periodMinute);
		times[1] = "00" + times[1];
		times[1] = times[1].substring(times[1].length() - 2);

		return times;
	}

	public static String getMonthAndDay(String time, Context context) {

		String[] timeArrays = time.split("-");
		String timeMonthAndDay = timeArrays[1]// from时间 年月
				+ context.getString(R.string.month)
				+ timeArrays[2]
				+ context.getString(R.string.day);

		return timeMonthAndDay;
	}

	/*
	 * 显示身份证断句
	 */
	public static String getKonggeCode(String code) {
		String code1 = code.substring(0, 6);
		String code2 = code.substring(6, 14);
		String code3 = code.substring(14, 18);
		code = code1 + " " + code2 + " " + code3;
		return code;
	}

	/**
	 * 手机号验证
	 * 
	 * @param str
	 * @return 验证通过返回true
	 */
	public static boolean isMobile(String str) {
		Pattern p = null;
		Matcher m = null;
		boolean b = false;
		p = Pattern.compile("^[1][3,4,5,8][0-9]{9}$"); // 验证手机号
		m = p.matcher(str);
		b = m.matches();
		return b;
	}

	/**
	 * 
	 * @param str
	 * @return 判断是否是中文
	 */
	public static boolean isChinese(String str) {
		Pattern p = null;
		Matcher m = null;
		boolean b = false;
		p = Pattern.compile("^[\u4e00-\u9fa5]+"); //
		m = p.matcher(str);
		b = m.matches();
		return b;
	}

	/**
	 * 密码验证
	 * 
	 * @param str
	 * @return 验证通过返回true
	 */
	public static boolean isPassword(String str) {
		Pattern p = null;
		Matcher m = null;
		boolean b = false;
		p = Pattern.compile("^[a-zA-Z0-9]+"); // 验证手机号
		m = p.matcher(str);
		b = m.matches();
		return b;
	}

	/**
	 * 截取身份证号上的字符串
	 * 
	 * @param card
	 *            格式为1980-05-25
	 * @return
	 */
	public static String getBirthdayStr(String card) {

		String code = card.substring(6, 14);

		String year = code.substring(0, 4);
		String month = code.substring(4, 6);
		String day = code.substring(6, 8);

		return year + "-" + month + "-" + day;
	}

	/*
	 * // 从SD卡中加载图片
	 */
	public static Bitmap loadImageFromSDCard(String path) {

		Bitmap pictureBitmap = null;
		FileInputStream fls = null;
		try {
			File file = new File(path);
			if (file.exists()) {

				fls = new FileInputStream(file);
				pictureBitmap = BitmapFactory.decodeStream(fls);

				if (fls != null) {
					fls.close();
					fls = null;
				}
				return pictureBitmap;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 
	 * @param context
	 * @return 返回手机号（默认是卡sim1）
	 */
	public static String getSIMCardInfo(Context context) {
		String mobile;
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		if (telephonyManager.getLine1Number() == null) {
			return "";
		} else if (telephonyManager.getLine1Number().startsWith("+86")) {
			// String mobile
		}

		return telephonyManager.getLine1Number();
	}

	/**
	 * 
	 * @param id
	 *            手机号
	 * 
	 * @return 代空格的手机号
	 */
	public static String getId(String id) {
		
		if (id != null && !"".equals(id)) {
			if (id.startsWith("0")){
				id=id.substring(1,id.length());
			}
			String first = id.substring(0, 3);
			String second = id.substring(3, 7);
			String third = id.substring(7, 11);
			id = first + " " + second + " " + third;
			System.out.println("first- " + first + "Senc  " + second
					+ " third  " + third);
			System.out.println(id);
			return id;
		}
		return "";

	}

	/**
	 * 
	 * @param context
	 *            1011 认证失败 1020 服务器内部错误 1031 发送超时 1104 帐号已冻结 1110 验证码验证类型不匹配
	 *            (登陆时返回状态)
	 * 
	 *            1010 信令格式错误 1020 服务器内部错误 1031 发送超时 1118 必须提供图形验证码 1042
	 *            下发短信验证码超出了频率 1110 验证码验证类型不匹配 1111 会话已过期 1119 业务会话已经使用（注册 获取短信）
	 * 
	 * 
	 *            1011 认证失败 1020 服务器内部错误 1031 发送超时 1110 验证码验证类型不匹配（修改密码）
	 * 
	 *            1010 信令格式错误 1020 服务器内部错误 1031 发送超时 1042 下发短信验证码超出了频率 1110
	 *            验证码类型不匹配 1101 验证码错误 1102 用户不存在 1114 验证码过期 （根据图形验证码获取短信验证码 忘记2）
	 * 
	 *            1011 认证失败 1020 服务器内部错误 1031 发送超时 1042 下发短信验证码超出了频率 1111
	 *            会话已过期（重新获取短信验证码 忘记3）
	 * 
	 *            1011 认证失败 1020 服务器内部错误 1031 发送超时 1110 验证码验证类型不匹配 1111 会话已过期
	 *            1101 验证码错误 1114 验证码过期 （提交短信验证码忘记4）
	 * 
	 *            1011 认证失1020 服务器内部错误 1031 发送超时 1111 会话已过期（重置用户密码，提交新密码 忘记密码5）
	 * 
	 */

	public static void MyToast(Context context, String result) {

		if ("1011".equals(result)) {
			Toast.makeText(context, R.string.renzhengshibai, Toast.LENGTH_LONG)
					.show();
		} else if ("1020".equals(result)) {
			Toast.makeText(context, R.string.service_error, Toast.LENGTH_LONG)
					.show();
		} else if ("1031".equals(result)) {
			Toast.makeText(context, R.string.sendtimeout, Toast.LENGTH_LONG)
					.show();
		} else if ("1104".equals(result)) {
			Toast.makeText(context, R.string.id_dongjie, Toast.LENGTH_LONG)
					.show();
		} else if ("1110".equals(result)) {
			Toast.makeText(context, R.string.yanzhengma_not_pipei,
					Toast.LENGTH_LONG).show();
		} else if ("1010".equals(result)) {
			Toast.makeText(context, R.string.style_error, Toast.LENGTH_LONG)
					.show();
		} else if ("1042".equals(result)) {
			Toast.makeText(context, R.string.send_chaochu_pinlv,
					Toast.LENGTH_LONG).show();

		} else if ("1100".equals(result)) {
			Toast.makeText(context, R.string.id_exists, Toast.LENGTH_LONG)
					.show();
		}

		else if ("1101".equals(result)) {
			Toast.makeText(context, R.string.yanzhengma_error,
					Toast.LENGTH_LONG).show();
		} else if ("1102".equals(result)) {
			Toast.makeText(context, R.string.id_not_exist, Toast.LENGTH_LONG)
					.show();
		} else if ("1114".equals(result)) {
			Toast.makeText(context, R.string.yanzhengma_guoqi,
					Toast.LENGTH_LONG).show();
		} else if ("1111".equals(result)) {
			Toast.makeText(context, R.string.huihuaguoqi, Toast.LENGTH_LONG)
					.show();
		} else if ("1117".equals(result)) {
			Toast.makeText(context, R.string.not_reset_reg, Toast.LENGTH_LONG)
					.show();
		} else if ("1118".equals(result)) {
			Toast.makeText(context, R.string.must_tuxing, Toast.LENGTH_LONG)
					.show();
		}
	}

	/**
	 * 点击返回键，提示一下是否返回
	 */
	public static Boolean showDialog(final Context context, EditText[] editTexts) {
		//如果有进一步需求传字符串数组
		final Dialog dialog;
		boolean flag = false;
		for (int i = 0; i < editTexts.length; i++) {
			if (editTexts[i].length() > 0) {
				flag = true;
				break;
			}
		}
		if (flag) {

			dialog = new Dialog(context, R.style.CustomProgressDialog);
			View viewDailog = LayoutInflater.from(context).inflate(
					R.layout.delete_dialog, null);
			RelativeLayout reDeLayout = (RelativeLayout) viewDailog
					.findViewById(R.id.re_dialog);
			ImageView imgCancel = (ImageView) viewDailog
					.findViewById(R.id.img_dialog_title_cha);
			TextView title = (TextView) viewDailog
					.findViewById(R.id.tv_dialog_title_name);
			title.setText(R.string.hint);
			TextView titleContent = (TextView) viewDailog
					.findViewById(R.id.tv_dialog_title);
			titleContent.setText(context.getString(R.string.hint_warn));
			Button btnCalcel = (Button) viewDailog
					.findViewById(R.id.btn_dialog_cancel);
			btnCalcel.setText(context.getString(R.string.cancel));
			Button btnOk = (Button) viewDailog
					.findViewById(R.id.btn_dialog_sure);
			btnOk.setText(context.getString(R.string.ok));
			btnCalcel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					dialog.dismiss();
				}
			});
			btnOk.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					dialog.dismiss();
					((Activity) context).finish();
				}
			});
			reDeLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					dialog.dismiss();
				}
			});
			imgCancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					dialog.dismiss();
				}
			});
			dialog.setContentView(viewDailog);
			dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			dialog.show();
			return true;
		} else {
			((Activity) context).finish();
			return false;
		}

	}

	/**
	 * 对姓名进行限制
	 */
	public static boolean limitName(Context context, String name) {
		if (TextUtils.isEmpty(name)) {
			Toast.makeText(context, R.string.passenger_name_null,
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (name.length() <= 1) {
			Toast.makeText(context, R.string.name_short, Toast.LENGTH_SHORT)
					.show();
			return false;
		} else if (name.length() > 8) {
			Toast.makeText(context, R.string.name_is_limit, Toast.LENGTH_SHORT)
					.show();
			return false;
		} else if (!Utils.isChinese(name)) {
			Toast.makeText(context, R.string.name_is_chinese,
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	/**
	 * 对手机号进行限制
	 */
	public static boolean limitMobile(Context context, String mobile) {
		if (TextUtils.isEmpty(mobile)) {
			Toast.makeText(context, R.string.mobile_null, Toast.LENGTH_SHORT)
					.show();
			return false;
		} else if (!Utils.isMobile(mobile)) {
			Toast.makeText(context, R.string.mobile_not_guize,
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	public static boolean limitCardId(Context context, String id) {
		if (TextUtils.isEmpty(id)) {
			Toast.makeText(context, R.string.id_null, Toast.LENGTH_SHORT)
					.show();
			return false;
		} else if (!IdCardUtil.IDCardValidate(id)) {
			Toast.makeText(context, R.string.id_error, Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		return true;
	}

	/**
	 * 关闭软键盘
	 * 
	 * @param context
	 */

	public static void closeSoftware(Context context) {

		try {
			InputMethodManager im = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(((Activity) context).getCurrentFocus()
					.getApplicationWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e) {
		}
	}

	/**
	 * 手机加空格
	 * 
	 * @param mEditText
	 */
	public static void mobileAddSpace(final int maxLen,
			final EditText mEditText) {
		mEditText.addTextChangedListener(new TextWatcher() {
			int beforeTextLength = 0;
			int onTextLength = 0;
			boolean isChanged = false;

			int location = 0;// 记录光标的位置
			private char[] tempChar;
			private StringBuffer buffer = new StringBuffer();
			int konggeNumberB = 0;

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				beforeTextLength = s.length();
				if (buffer.length() > 0) {
					buffer.delete(0, buffer.length());
				}
				konggeNumberB = 0;
				for (int i = 0; i < s.length(); i++) {
					if (s.charAt(i) == ' ') {
						konggeNumberB++;
					}
				}

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				onTextLength = s.length();
				buffer.append(s.toString());
				if (onTextLength == beforeTextLength || onTextLength <= 3
						|| isChanged) {
					isChanged = false;
					return;
				}
				isChanged = true;
				// 以下是限制最大长度是13的 手机为数代空格的
				Editable editable = mEditText.getText();
				int len = editable.length();

				if (len > maxLen) {
					int selEndIndex = Selection.getSelectionEnd(editable);
					String str = editable.toString();
					// 截取新字符串
					String newStr = str.substring(0, maxLen);
					mEditText.setText(newStr);
					editable = mEditText.getText();

					// 新字符串的长度
					int newLen = editable.length();
					// 旧光标位置超过字符串长度
					if (selEndIndex > newLen) {
						selEndIndex = editable.length();
					}
					// 设置新光标所在的位置
					Selection.setSelection(editable, selEndIndex);

				}

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (isChanged) {
					location = mEditText.getSelectionEnd();
					int index = 0;
					while (index < buffer.length()) {
						if (buffer.charAt(index) == ' ') {
							buffer.deleteCharAt(index);
						} else {
							index++;
						}
					}

					index = 0;
					int konggeNumberC = 0;
					while (index < buffer.length()) {
						if ((index == 3 || index == 8)) {
							buffer.insert(index, ' ');
							konggeNumberC++;
						}
						index++;
					}

					if (konggeNumberC > konggeNumberB) {
						location += (konggeNumberC - konggeNumberB);
					}

					tempChar = new char[buffer.length()];
					buffer.getChars(0, buffer.length(), tempChar, 0);
					String str = buffer.toString();
					if (location > str.length()) {
						location = str.length();
					} else if (location < 0) {
						location = 0;
					}

					mEditText.setText(str);
					Editable etable = mEditText.getText();
					Selection.setSelection(etable, location);
					isChanged = false;
				}
			}
		});
	}
}
