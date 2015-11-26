package com.gionee.gntravel.utils;

import java.util.HashMap;

import android.content.Context;

import com.gionee.gntravel.R;
public class WeatherTypeUtil {
	public Context context;
	public HashMap<String, Integer> tianqi_iv_map;
	public WeatherTypeUtil() {
		
	}
	public WeatherTypeUtil(Context context) {
		this.context = context;
		tianqi_iv_map = new HashMap<String, Integer>();
		tianqi_iv_map.put(context.getResources().getString(R.string.weather_qing), R.drawable.yangguang_b);
		tianqi_iv_map.put(context.getResources().getString(R.string.weather_baoxue), R.drawable.baoxue_b);
		tianqi_iv_map.put(context.getResources().getString(R.string.weather_baoydaodabaoy), R.drawable.baoydaodabaoy_b);//
		tianqi_iv_map.put(context.getResources().getString(R.string.weather_baoyu), R.drawable.baoyu_b);
		tianqi_iv_map.put(context.getResources().getString(R.string.weather_dabaoydaotedabaoy), R.drawable.dabaoydaotedabaoy_b);//
		tianqi_iv_map.put(context.getResources().getString(R.string.weather_dabaoyu), R.drawable.dabaoyu_b);
		tianqi_iv_map.put(context.getResources().getString(R.string.weather_dadaobaox), R.drawable.dadaobx_b);
		tianqi_iv_map.put(context.getResources().getString(R.string.weather_dadaobaoy), R.drawable.dadaoby_b);
		tianqi_iv_map.put(context.getResources().getString(R.string.weather_daxue), R.drawable.daxue_b);
		tianqi_iv_map.put(context.getResources().getString(R.string.weather_dayu), R.drawable.dayu_b);
		tianqi_iv_map.put(context.getResources().getString(R.string.weather_dongyu), R.drawable.dongyu_b);//
		tianqi_iv_map.put(context.getResources().getString(R.string.weather_duoyun), R.drawable.duoyun_b);
		tianqi_iv_map.put(context.getResources().getString(R.string.weather_fuchen), R.drawable.fuchen_b);
		tianqi_iv_map.put(context.getResources().getString(R.string.weather_leizhenyu), R.drawable.leizhenyu_b);
		tianqi_iv_map.put(context.getResources().getString(R.string.weather_leizhy_bingbao), R.drawable.leizhy_bingbao_b);
		tianqi_iv_map.put(context.getResources().getString(R.string.weather_mai), R.drawable.mai_b);
		tianqi_iv_map.put(context.getResources().getString(R.string.weather_qiangshachenbao), R.drawable.qiangshachenbao_b);
		tianqi_iv_map.put(context.getResources().getString(R.string.weather_shachenbao), R.drawable.shachenbao_b);
		tianqi_iv_map.put(context.getResources().getString(R.string.weather_tedabaoy), R.drawable.tedabaoyu_b);
		tianqi_iv_map.put(context.getResources().getString(R.string.weather_wu), R.drawable.wu_b);
		tianqi_iv_map.put(context.getResources().getString(R.string.weather_xiaodaozhx), R.drawable.xiaodaozhx_b);
		tianqi_iv_map.put(context.getResources().getString(R.string.weather_xiaodaozhy), R.drawable.xiaodaozhy_b);
		tianqi_iv_map.put(context.getResources().getString(R.string.weather_xiaoxue), R.drawable.xiaoxue_b);
		tianqi_iv_map.put(context.getResources().getString(R.string.weather_xiaoyu), R.drawable.xiaoyu_b);
		tianqi_iv_map.put(context.getResources().getString(R.string.weather_yangsha), R.drawable.yangsha_b);
		tianqi_iv_map.put(context.getResources().getString(R.string.weather_yin), R.drawable.yin_b);
		tianqi_iv_map.put(context.getResources().getString(R.string.weather_yujiaxue), R.drawable.yujiaxue_b);
		tianqi_iv_map.put(context.getResources().getString(R.string.weather_zhdaodax), R.drawable.zhongdaodx_b);
		tianqi_iv_map.put(context.getResources().getString(R.string.weather_zhongxue), R.drawable.zhongxue_b);
		tianqi_iv_map.put(context.getResources().getString(R.string.weather_zhenxue), R.drawable.zhenxue_b);
		tianqi_iv_map.put(context.getResources().getString(R.string.weather_zhenyu), R.drawable.zhenyu_b);
		tianqi_iv_map.put(context.getResources().getString(R.string.weather_zhongdaoday), R.drawable.zhongdaoday_b);
		tianqi_iv_map.put(context.getResources().getString(R.string.weather_zhongyu), R.drawable.zhongyu_b);
	}
	
	public int getWeatherTypeImage(String weatherType) {
		if(tianqi_iv_map.get(weatherType) != null) {
			return tianqi_iv_map.get(weatherType);
		} else {
			return -1;
		}
	}
}
