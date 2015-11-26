package com.gionee.gntravel.entity;


public class CraftTypeEntity extends FlightSearchEntityBase{

	public static String[] strKeys = new String[]{
			"不限",
			"大型机",
			"中型机",
			"小型机"};
	public static boolean[] booleanValues = new boolean[]{
			true,
			false,
			false,
			false};
	public CraftTypeEntity() {
		super(strKeys, booleanValues);
	}
	public CraftTypeEntity(String[] strKeys , boolean[] booleanValues) {
		super(strKeys,booleanValues);
	}
	
	@Override
	public String getTagName() {
		// TODO Auto-generated method stub
		return getClass().getSimpleName();
	}
	
}
