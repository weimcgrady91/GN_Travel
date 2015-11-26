package com.gionee.gntravel.entity;

import java.util.ArrayList;


public class TakeOffEntity extends FlightSearchEntityBase{
	public static String[] strKeys = new String[]{
			"不限",
			"上午 00:00-12:00",
			"中午 12:00-14:00",
			"下午 14:00-18:00",
			"晚上 18:00-24:00"
	};
	
	public static boolean[] booleanValues = new boolean[]{
			true,
			false,
			false,
			false,
			false
	};
	
	public static ArrayList<int[]> limtTime = new ArrayList<int[]>();
	static {
		limtTime.add(new int[]{-1,-1});
		limtTime.add(new int[]{0,12});
		limtTime.add(new int[]{12,14});
		limtTime.add(new int[]{14,18});
		limtTime.add(new int[]{18,24});
	}
	public TakeOffEntity() {
		super(strKeys, booleanValues);
	}
	public TakeOffEntity(String[] strKeys , boolean[] booleanValues) {
		super(strKeys,booleanValues);
	}
	
	@Override
	public String getTagName() {
		// TODO Auto-generated method stub
		return getClass().getSimpleName();
	}
}
