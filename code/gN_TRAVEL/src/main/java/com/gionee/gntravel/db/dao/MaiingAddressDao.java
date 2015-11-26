package com.gionee.gntravel.db.dao;

import android.database.sqlite.SQLiteDatabase;

import com.gionee.gntravel.entity.AddressEntity;
import com.gionee.gntravel.entity.ContentBean;

/**
 * 增删改查联系人（飞机联系人，酒店联系人，或者是入住人）
 * 
 * @author lijinbao
 * 
 */
public interface MaiingAddressDao {
	/**
	 * 查询上次缓存的联系人数据
	 * 
	 * @param db
	 * @param userId
	 * @return
	 */
	public AddressEntity findMailAddressByUserId(SQLiteDatabase db, String userId);

	/**
	 * 删除某个帐号下邮寄地址信息
	 * 
	 * @param db
	 * @param userId
	 */
	public void delectMailAddressByUserId(SQLiteDatabase db, String userId);

	
/**
 * 
 * @param db
 * @param userId
 * @param name
 * @param mobile
 * @param id
 * @param address
 * @param mailCode
 * @return
 */

	public long insertMailAddressByUserId(SQLiteDatabase db, String userId,
			String name, String mobile, String id, String address,String mailCode);



}
