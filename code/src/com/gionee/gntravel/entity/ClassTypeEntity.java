package com.gionee.gntravel.entity;

public class ClassTypeEntity extends FlightSearchEntityBase{
	public static String[] strKeys = new String[]{
		"不限",
		"公务舱",
		"头等舱",
		"经济舱"};
	public static boolean[] booleanValues = new boolean[]{
		true,
		false,
		false,
		false};
	public ClassTypeEntity() {
		super(strKeys, booleanValues);
	}
	public ClassTypeEntity(String[] strKeys , boolean[] booleanValues) {
		super(strKeys,booleanValues);
	}
	
	@Override
	public String getTagName() {
		// TODO Auto-generated method stub
		return getClass().getSimpleName();
	}
}
