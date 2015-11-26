package com.gionee.gntravel.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;

public class GdUtil {
	private Activity mContext;
	public GdUtil(Activity context){
		mContext = context;
		
	}

	private static HashMap<String, String> map = new HashMap<String, String>();

	public  Map<String, String> getCityCodeMap() {
		if (map.size() == 0) {
			BufferedReader reader;
			try {
				reader = new BufferedReader(new InputStreamReader(mContext.getResources().getAssets().open("gdcityCode.txt")));
				String line = null;
				while ((line = reader.readLine()) != null) {
					if(line.trim().length()==0){
						continue;
					}
					String[] data = line.split(",");
					map.put(data[1], data[0]);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		System.out.println(map.size());
		return map;
	}
}
