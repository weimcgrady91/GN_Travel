package com.gionee.gntravel.utils;

import java.util.ArrayList;

import com.gionee.gntravel.entity.City;

public class HisToryUtils {
	public static ArrayList<City> histroy=new ArrayList<City>();
	
	public static ArrayList<City>  getHisToryList(City city   ){
		ArrayList<City> list=new ArrayList<City>();
		
			if(list.size()>4){
			list.remove(1);
		}
		list.add(city);
		return list;
	}

}
