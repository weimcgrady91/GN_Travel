package com.gionee.gntravel;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ctrip.openapi.java.utils.ImageUtil;
import com.gionee.gntravel.adapter.GuestRoomListAdapter;
import com.gionee.gntravel.adapter.GuestRoomListAdapter.onMySelectDateListner;
import com.gionee.gntravel.entity.GuestRoom;
import com.gionee.gntravel.entity.Hotel;
import com.gionee.gntravel.entity.HotelDetail;
import com.gionee.gntravel.entity.HotelImage;
import com.gionee.gntravel.utils.DateSelectUtils;
import com.gionee.gntravel.utils.DateUtils;
import com.gionee.gntravel.utils.FinalString;
import com.gionee.gntravel.utils.GNConnUtil;
import com.gionee.gntravel.utils.GNConnUtil.GNConnListener;
import com.gionee.gntravel.utils.JSONUtil;
import com.gionee.gntravel.widget.MyScrollView;
import com.gionee.gntravel.widget.MyViewPager;
import com.weiqun.customcalendar.CustomCalendar;

public class HotelDetailActivity extends DetailsActivity implements GNConnListener {
	String TAG = "HotelDetailActivity";
	private LinearLayout hotel_detail_ll_room;
	GuestRoomListAdapter adapter = null;
	private MyViewPager hotel_detail_vp;
	private LinearLayout hotel_detail_ll_dots;
	private String url;
	private TextView hotel_detail_tv_name;
	private TextView hotel_detail_tv_commrate;
	private TextView hotel_detail_tv_commnum;
	private TextView hotel_detail_tv_address;
	private String address = "";
	private List<GuestRoom> guestRoomsList = new ArrayList<GuestRoom>();;
	private List<HotelImage> images = new ArrayList<HotelImage>();
	private Button hotel_detail_bt_update_indate;
	private Button hotel_detail_bt_update_leavedate;
	private CustomCalendar picker_cakendar;;
	private String inOrLeave;
	private TextView hotel_detail_tv_leavedate;
	private TextView hotel_detail_tv_indate;
	private ImageView hotel_detail_iv_desc;
	private ImageView hoteldetail_pb_img;
	private ImageView hoteldetail_pb_roomlv;
	private PopupWindow mPopupWindow;
	private View popupView;
	private LinearLayout activity_hotel_details_ll_map;
	private HotelDetail detail;
	private Hotel hotel;
	private LinearLayout hotel_detail_ll_desc;
	private ImageView hotel_detail_vp_prepage;
	private ImageView hotel_detail_vp_nextpage;
	private int mPosition;
	private String currentDate;
	private String hotelName;
	private Animation out_anim;
	private String hotelCode;
	private boolean bookable = true;
	private LinearLayout activity_hotel_detail_ll_choseDate;
	private FrameLayout activity_hotel_detail_fl_room;
	private static final int INITCALENDAR = 0x1000;
	private myHandler handler = new myHandler();
	private boolean isDismissing;
	private boolean desc_bt_clickable = true;
	private String hotelId;
	private MyScrollView view;
	private Date leaveDate;
	private Date inDate;
	private RelativeLayout rl_failed;
	private ImageView iv_failed;
	private TextView btn_refresh;
	private LinearLayout ll_hoteldetail;
	private String indate_Str;
	private boolean editClickable = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViews();
		initData();
		setListner();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		desc_bt_clickable = true;
		super.onResume();
	}

	private class myHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case INITCALENDAR:
				initCalendar();
				break;

			default:
				break;
			}
		}
	}

	private void initCalendar() {
		popupView = this.getLayoutInflater().inflate(R.layout.window_calendar, null);
		picker_cakendar = (CustomCalendar) popupView.findViewById(R.id.picker_cakendar);
		DateSelectUtils.initCalendar(picker_cakendar);
		picker_cakendar
				.setOnDateSelectedListener(new com.weiqun.customcalendar.CustomCalendar.OnDateSelectedListener() {
					@Override
					public void onDateUnselected(Date date) {

					}

					@Override
					public void onDateSelected(Date date) {
						try {
							if (isIn()) {
								setInTime(date);// 设置了in time自动设置leave time
							}
							if (isLeave()) {
								setLeaveTime(date);
							}
							popwindowDismiss();
						} catch (Exception ex) {
						}
					}

				});
	}

	private void findViews() {
		activity_hotel_details_ll_map = (LinearLayout) findViewById(R.id.activity_hotel_details_ll_map);
		hotel_detail_ll_room = (LinearLayout) findViewById(R.id.hotel_detail_ll_room);
		hotel_detail_vp = (MyViewPager) findViewById(R.id.hotel_detail_vp);
		hotel_detail_ll_dots = (LinearLayout) findViewById(R.id.hotel_detail_ll_dots);
		ll_hoteldetail = (LinearLayout) findViewById(R.id.ll_hoteldetail);
		hotel_detail_tv_name = (TextView) findViewById(R.id.hotel_detail_tv_name);
		hotel_detail_tv_commrate = (TextView) findViewById(R.id.hotel_detail_tv_commrate);
		hotel_detail_tv_commnum = (TextView) findViewById(R.id.hotel_detail_tv_commnum);
		hotel_detail_tv_address = (TextView) findViewById(R.id.hotel_detail_tv_address);
		hotel_detail_bt_update_indate = (Button) findViewById(R.id.hotel_detail_bt_update_indate);
		hotel_detail_bt_update_leavedate = (Button) findViewById(R.id.hotel_detail_bt_update_leavedate);
		rl_failed = (RelativeLayout) findViewById(R.id.rl_failed);
		iv_failed = (ImageView) findViewById(R.id.iv_failed);
		rl_failed.setOnClickListener(this);
		btn_refresh = (TextView) findViewById(R.id.btn_refresh);
		setEditClickable_false();
		hotel_detail_tv_indate = (TextView) findViewById(R.id.hotel_detail_tv_indate);
		hotel_detail_tv_leavedate = (TextView) findViewById(R.id.hotel_detail_tv_leavedate);
		hotel_detail_iv_desc = (ImageView) findViewById(R.id.hotel_detail_iv_desc);
		hotel_detail_ll_desc = (LinearLayout) findViewById(R.id.activity_hotel_detail_ll_desc);
		hoteldetail_pb_img = (ImageView) findViewById(R.id.hoteldetail_pb_img);
		hoteldetail_pb_roomlv = (ImageView) findViewById(R.id.hoteldetail_pb_roomlv);
		hotel_detail_vp_prepage = (ImageView) findViewById(R.id.activity_hotel_detail_vp_prepage);
		hotel_detail_vp_nextpage = (ImageView) findViewById(R.id.activity_hotel_detail_vp_nextpage);
		activity_hotel_detail_ll_choseDate = (LinearLayout) findViewById(R.id.activity_hotel_detail_ll_choseDate);
		activity_hotel_detail_fl_room = (FrameLayout) findViewById(R.id.activity_hotel_detail_fl_room);
		view = (MyScrollView) findViewById(R.id.activity_hotel_detail_sv);
		hotel_detail_vp.setMyScrollView(view);
	}

	private void hideBook() {
		activity_hotel_detail_ll_choseDate.setVisibility(View.GONE);
		activity_hotel_detail_fl_room.setBackgroundResource(R.drawable.shurukuang);
	}

	private void showPb() {
		hoteldetail_pb_img.setVisibility(View.VISIBLE);
		AnimationDrawable animationDrawable = (AnimationDrawable) hoteldetail_pb_img.getBackground();
		animationDrawable.start();
		hoteldetail_pb_roomlv.setVisibility(View.VISIBLE);
		AnimationDrawable animationDrawable_room = (AnimationDrawable) hoteldetail_pb_roomlv.getBackground();
		animationDrawable_room.start();
	}

	private void dissmissPb() {
		AnimationDrawable animationDrawable = (AnimationDrawable) hoteldetail_pb_img.getBackground();
		animationDrawable.stop();
		AnimationDrawable animationDrawable_room = (AnimationDrawable) hoteldetail_pb_roomlv.getBackground();
		animationDrawable_room.stop();
		hoteldetail_pb_img.setVisibility(View.GONE);
		hoteldetail_pb_roomlv.setVisibility(View.GONE);
	}

	private void setListner() {
		hotel_detail_ll_desc.setOnClickListener(this);
		hotel_detail_iv_desc.setOnClickListener(this);
		hotel_detail_bt_update_indate.setOnClickListener(this);
		hotel_detail_bt_update_leavedate.setOnClickListener(this);
		activity_hotel_details_ll_map.setOnClickListener(this);
		hotel_detail_tv_commnum.setOnClickListener(this);
		hotel_detail_vp_nextpage.setOnClickListener(this);
		hotel_detail_vp_prepage.setOnClickListener(this);
	}

	private void updateVpAdapter(final List<HotelImage> hotelimglist, final int batchSize) {
		final List<List<HotelImage>> batches = new ArrayList<List<HotelImage>>();
		int index = -1;
		for (int i = 0; i < hotelimglist.size(); i++) {
			List<HotelImage> innerList = null;
			if (i % batchSize == 0) {
				innerList = new ArrayList<HotelImage>();
				batches.add(innerList);
				index++;
			} else {
				innerList = batches.get(index);
			}
			innerList.add(hotelimglist.get(i));
		}

		PagerAdapter vp_adapter = hotel_detail_vp.getAdapter();
		if (vp_adapter == null) {
			vp_adapter = new PagerAdapter() {

				@Override
				public boolean isViewFromObject(View arg0, Object arg1) {
					return arg0 == arg1;
				}

				@Override
				public int getCount() {
					return batches.size();
				}

				@Override
				public void destroyItem(ViewGroup container, int position, Object object) {
					hotel_detail_vp.removeView((View) object);
				}

				@Override
				public Object instantiateItem(ViewGroup container, final int position) {
					ViewGroup layout = (ViewGroup) LayoutInflater.from(HotelDetailActivity.this).inflate(
							R.layout.item_hoteldetail_vp, null);

					List<HotelImage> innerList = batches.get(position);
					int i = 0;
					for (final HotelImage hotelImage : innerList) {
						ImageView iv = (ImageView) layout.getChildAt(i);
						iv.setScaleType(ScaleType.FIT_XY);
						iv.setVisibility(View.VISIBLE);
						String imgurl = hotelImage.getUrl();
						if (((TravelApplication) getApplication()).isShowImgWithWifiState()) {
							ConnectivityManager connManager = (ConnectivityManager) getApplication().getSystemService(
									Context.CONNECTIVITY_SERVICE);
							NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
							if (networkInfo.isConnected()) {
								if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
									ImageUtil.getInstance(HotelDetailActivity.this).display(imgurl, iv, null);
								}
							}
						} else {
							ImageUtil.getInstance(HotelDetailActivity.this).display(imgurl, iv, null);
						}
						final int k = batchSize * position + i;
						iv.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								Intent i = new Intent(getApplicationContext(), HotelBigImageActivity.class);
								i.putExtra("index", k);
								i.putExtra("json", JSONUtil.getInstance().toJSON(hotelimglist));
								startActivity(i);
							}
						});
						i++;
					}

					hotel_detail_vp.addView(layout);
					return layout;
				}

			};
			hotel_detail_vp.setAdapter(vp_adapter);
		}
		// 更新dots
		hotel_detail_ll_dots.removeAllViews();
		if (batches != null && batches.size() > 1) {
			hotel_detail_vp_prepage.setVisibility(View.VISIBLE);
			hotel_detail_vp_nextpage.setVisibility(View.VISIBLE);
		} else {
			hotel_detail_vp_prepage.setVisibility(View.GONE);
			hotel_detail_vp_nextpage.setVisibility(View.GONE);
		}

		for (int i = 0; i < batches.size(); i++) {
			View dot = LayoutInflater.from(this).inflate(R.layout.item_vp_dot, null);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			// lp.weight = 1;
			lp.gravity = Gravity.CENTER;
			dot.setLayoutParams(lp);
			hotel_detail_ll_dots.addView(dot);
		}
		// 设置跳转dot的指示
		hotel_detail_vp.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				mPosition = position;
				// 选择了的
				selectDot(hotel_detail_ll_dots, mPosition);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		if (hotelimglist.size() > 0) {
			hotel_detail_vp.setCurrentItem(hotelimglist.size() - 1);
			hotel_detail_vp.setCurrentItem(0);
		}
		vp_adapter.notifyDataSetChanged();
		selectDot(hotel_detail_ll_dots, 0);
	}

	protected void selectDot(LinearLayout hotel_detail_ll_dots, int toBeSelected) {
		if (hotel_detail_ll_dots.getChildCount() <= 1) {
			hotel_detail_ll_dots.setVisibility(View.INVISIBLE);
		} else {
			hotel_detail_ll_dots.setVisibility(View.VISIBLE);
		}
		for (int i = 0; i < hotel_detail_ll_dots.getChildCount(); i++) {
			LinearLayout dot = (LinearLayout) hotel_detail_ll_dots.getChildAt(i);

			ImageView iv = (ImageView) dot.findViewById(R.id.item_vp_dot_iv);
			if (i == toBeSelected) {
				iv.setImageResource(R.drawable.item_hoteldetail_vp_dot_selected);
			} else {
				iv.setImageResource(R.drawable.item_hoteldetail_vp_dot_normal);
			}
		}
	}

	private void initData() {
		url = getString(R.string.gionee_host) + FinalString.HOTEL_ACTION + "hotelDetail.action";
		Intent i = this.getIntent();
		hotel = (Hotel) i.getSerializableExtra("hotelInfo");
		currentDate = i.getStringExtra("currentDate");
		bookable = i.getBooleanExtra("bookable", true);
		if (TextUtils.isEmpty(currentDate)) {
			Date d = Calendar.getInstance().getTime();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			currentDate = sdf.format(d);
		}
		Date d_in = DateUtils.StringToDate(currentDate, "yyyy/MM/dd");
		setInTime(d_in);// 设置了in，自动设置了leave

		address = hotel.getAddressLine();
		hotel_detail_tv_address.setText(address);
		int lineCount = hotel_detail_tv_address.getLineCount();
		hotelCode = hotel.getHotelCode();
		hotelId = hotel.getId();
		hotelName = hotel.getHotelName();
		hotel_detail_tv_name.setText(hotelName);
		String commRate = hotel.getCommRate() + "分";
		hotel_detail_tv_commrate.setText(commRate);
		if (!bookable) {
			hideBook();
		}
		indate_Str = DateUtils.dateToStr(d_in, "yyyy-M-d");
		loadHotelDetail();

		Message msg = Message.obtain();
		msg.what = INITCALENDAR;
		handler.sendMessageDelayed(msg, 500);
	}

	private void loadHotelDetail() {
		url = url + "?hotelId=" + hotelId + "&hotelCode=" + hotelCode + "&date=" + indate_Str;
		GNConnUtil connUtil = new GNConnUtil();
		connUtil.setGNConnListener(this);
		showPb();
		connUtil.doGet(url);
		rl_failed.setVisibility(View.GONE);
		ll_hoteldetail.setVisibility(View.VISIBLE);
	}

	public HotelDetailActivity() {
		super(R.layout.activity_hotel_details);
	}

	@Override
	protected void payment() {
	}

	@Override
	protected String curstomTitle() {
		return getString(R.string.hotel_detail);
	}

	/**
	 * 请求成功回调
	 * 
	 * @Author: yangxy
	 * @Version: V1.0
	 * @Create Date: 2014-11-10
	 * @param requestUrl
	 *            ，请求的url
	 * @param json
	 *            ，后台返回的数据
	 */

	@Override
	public void onReqSuc(String requestUrl, String json) {
		detail = JSONUtil.getInstance().fromJSON(HotelDetail.class, json);
		guestRoomsList = detail.getGuestRooms();
		for (GuestRoom guestRoom : guestRoomsList) {
			guestRoom.setHotelCode(hotelCode);
		}
		images = detail.getImages();
		adapter = new GuestRoomListAdapter(guestRoomsList, this, hotelName, bookable);
		adapter.setonSelectDateListner(new onMySelectDateListner() {

			@Override
			public void selectDate(GuestRoom guestRoom, String hotelName) {
				gotoHotelOrder(guestRoom, hotelName);
			}
		});
		// ,getStrDateFormater().format(inDate),getStrDateFormater().format(leaveDate)
		setRoomAdapter(adapter);
		updateVpAdapter(images, 3);
		dissmissPb();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				setEditClickable_true();
			}
		}, 500);
	}

	private void setRoomAdapter(GuestRoomListAdapter adapter) {
		for (int i = 0; i < adapter.getCount(); i++) {
			hotel_detail_ll_room.addView(adapter.getView(i, null, null));
		}
	}

	private void gotoHotelOrder(GuestRoom guestRoom, String hotelName) {
		String indate_str = getStrDateFormater().format(inDate);
		String leavedate_str = getStrDateFormater().format(leaveDate);
		TravelApplication app = (TravelApplication) this.getApplication();
		if (app.isLoginState()) {
			Intent i = new Intent(this, HotelOrderActivity.class);
			Bundle b = new Bundle();
			b.putSerializable("room", guestRoom);
			b.putString("indate", indate_str);
			b.putString("leavedate", leavedate_str);
			b.putString("hotelName", hotelName);
			i.putExtras(b);
			startActivity(i);
		} else {
			Bundle bundle = new Bundle();
			bundle.putString("target", "com.gionee.gntravel.HotelOrderActivity");
			bundle.putSerializable("room", guestRoom);
			bundle.putString("indate", indate_str);
			bundle.putString("leavedate", leavedate_str);
			bundle.putString("hotelName", hotelName);
			Intent intent = new Intent();
			intent.setClass(this, LoginActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
			finish();
		}
	}

	/**
	 * 请求失败时回调
	 * 
	 * @Author: yangxy
	 * @Version: V1.0
	 * @Create Date: 2014-11-10
	 * @param requestUrl
	 *            请求的url
	 * @param ex
	 *            后台返回的异常信息
	 * @param errorCode
	 *            ，后台返回的错误代码
	 */

	@Override
	public void onReqFail(String requestUrl, Exception ex, int errorCode) {
		setEditClickable_true();
		dissmissPb();
		if (ex instanceof IOException) {
			showNetErrorMsg();
		}
	}

	public void showNetErrorMsg() {
		ll_hoteldetail.setVisibility(View.GONE);
		iv_failed.setImageResource(R.drawable.wifi);
		btn_refresh.setText(getResources().getString(R.string.net_refresh));
		rl_failed.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.hotel_detail_bt_update_indate:
			setEditClickable_false();
			if (!editClickable) {
				return;
			}
			editClickable = false;
			inOrLeave = "in";
			showCalendar();
			break;
		case R.id.hotel_detail_tv_commnum:
			Intent commIntent = new Intent();
			commIntent.putExtra("hotelId", hotel.getHotelCode());
			commIntent.setClass(this, HotelCommentActivity.class);
			commIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(commIntent);
			break;
		case R.id.hotel_detail_bt_update_leavedate:
			setEditClickable_false();
			if (!editClickable) {
				return;
			}
			editClickable = false;
			inOrLeave = "leave";
			showCalendar();
			break;
		case R.id.activity_hotel_detail_ll_desc:
			if (desc_bt_clickable) {
				Intent i = new Intent();
				i.setClass(this, HotelDescActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				i.putExtra("hotelCode", hotelCode);
				startActivity(i);
				desc_bt_clickable = false;
			}
			break;
		case R.id.activity_hotel_details_ll_map:
			Intent detailMapIntent = new Intent();
			detailMapIntent.setClass(this, HotelMapDetailActivity.class);
			detailMapIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			detailMapIntent.putExtra("hotelDetail", hotel);
			startActivity(detailMapIntent);
			break;
		case R.id.activity_hotel_detail_vp_prepage:
			if (mPosition > 0) {
				mPosition = mPosition - 1;
				hotel_detail_vp.setCurrentItem(mPosition);
				selectDot(hotel_detail_ll_dots, mPosition);
			}
			break;
		case R.id.activity_hotel_detail_vp_nextpage:
			int count = hotel_detail_ll_dots.getChildCount();
			if (mPosition < count - 1) {
				mPosition = mPosition + 1;
				hotel_detail_vp.setCurrentItem(mPosition);
				selectDot(hotel_detail_ll_dots, mPosition);
			}
			break;
		case R.id.rl_failed:
			loadHotelDetail();
			break;
		default:
			super.onClick(v);
		}
	}

	private void setEditClickable_false() {
		hotel_detail_bt_update_leavedate.setClickable(false);
		hotel_detail_bt_update_indate.setClickable(false);
	}

	private void showCalendar() {
		Date date = null;
		if (isIn()) {
			date = (Date) hotel_detail_tv_indate.getTag();
		} else {
			date = (Date) hotel_detail_tv_leavedate.getTag();
		}
		picker_cakendar.selectDate(date);
		mPopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				isDismissing = false;
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						setEditClickable_true();
					}
				}, 1000);
			}
		});
		mPopupWindow.setTouchable(true);
		mPopupWindow.setOutsideTouchable(true);
		if (popupView != null) {
			popupView.setFocusable(true);
			popupView.setFocusableInTouchMode(true);
			popupView.setOnKeyListener(new View.OnKeyListener() {
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK) {
						popwindowDismiss();
					}
					return false;
				}
			});
			popupView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mPopupWindow.isShowing()) {
						popwindowDismiss();
					}
				}
			});
		} else {
			return;
		}
		if (mPopupWindow.getContentView().getParent() != null) {
			return;
		}
		mPopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
		mPopupWindow.update();
		Animation amn = AnimationUtils.loadAnimation(this, R.anim.menu_bottombar_in);
		amn.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// 放在animation里面是因为animation开始了，view就布局完毕了，可以遍历。
				if (isIn()) {
					DateSelectUtils.setCalendarCellText(picker_cakendar, "入住");
				} else {
					DateSelectUtils.setCalendarCellText(picker_cakendar, "离店");
				}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub

			}
		});

		popupView.findViewById(R.id.window_filter_animPart).startAnimation(amn);

	}

	private void setEditClickable_true() {
		editClickable = true;
		hotel_detail_bt_update_indate.setClickable(true);
		hotel_detail_bt_update_leavedate.setClickable(true);
	}

	private void setInTime(Date date) {
		inDate = date;
		setTime(hotel_detail_tv_indate, date);
		if (leaveDate != null && !inDate.before(leaveDate)) {
		}

		// 如果入住时间等于或者晚于离店时间，那么离店时间+1天
		if (leaveDate == null || date.after(leaveDate) || (!date.after(leaveDate) && !date.before(leaveDate))) {
			// 离店时间自动加1天
			Calendar cld = Calendar.getInstance();
			cld.clear();
			cld.setTime(date);
			cld.add(Calendar.DAY_OF_YEAR, 1);
			leaveDate = cld.getTime();
		}

		setTime(hotel_detail_tv_leavedate, leaveDate);
		String dateStr = DateUtils.dateToStr(date);
		TravelApplication app = (TravelApplication) getApplication();
		app.replaceTripName(dateStr);
	}

	private void setLeaveTime(Date date) {
		leaveDate = date;
		if (leaveDate.before(inDate) || leaveDate.getTime() == inDate.getTime()) {
			Toast.makeText(getApplicationContext(), "离店时间必须晚于入住时间", Toast.LENGTH_SHORT).show();
			// 选择回原来的离店时间
			picker_cakendar.selectDate(leaveDate);
		} else {
			setTime(hotel_detail_tv_leavedate, date);
		}
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}

	private SimpleDateFormat getFormater() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日", Locale.getDefault());
		return sdf;
	}

	private SimpleDateFormat getStrDateFormater() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		return sdf;
	}

	private boolean isLeave() {
		return inOrLeave.equals("leave");
	}

	private boolean isIn() {
		return inOrLeave.equals("in");
	}

	private void popwindowDismiss() {
		if (!isDismissing) {
			out_anim = AnimationUtils.loadAnimation(this, R.anim.menu_bottombar_out);
			popupView.findViewById(R.id.window_filter_animPart).startAnimation(out_anim);
			out_anim.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					mPopupWindow.dismiss();
				}
			});
		} else {
			return;
		}
		isDismissing = true;
	}

	private void setTime(TextView tv, Date date) {
		tv.setText(getFormater().format(date));
		tv.setTag(date);
	}
}
