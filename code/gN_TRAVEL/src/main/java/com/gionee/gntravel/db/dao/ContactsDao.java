package com.gionee.gntravel.db.dao;

import android.database.sqlite.SQLiteDatabase;

import com.gionee.gntravel.entity.ContentBean;

/**
 * 增删改查联系人（飞机联系人，酒店联系人，或者是入住人）
 * 
 * @author lijinbao
 * 
 */
public interface ContactsDao {
	/**
	 * 查询上次缓存的联系人数据
	 * 
	 * @param db
	 * @param userId
	 * @param type
	 *            是酒店的联系人还是飞机的联系人或者是入住人
	 * @return
	 */
	public ContentBean findContactsByUserId(SQLiteDatabase db, String userId,
			String type);

	/**
	 * 删除某个帐号下的联系人缓存(这个是在常用联系人里面删除用这个)
	 * 
	 * @param db
	 * @param userId
	 * @param id
	 */
	public void delectContactsByUserId(SQLiteDatabase db, String userId,
			String id);

	/**
	 * 删除某个帐号下的联系人还是入住人缓存(根据类型 是联系人 还是入住人)
	 * 
	 * @param db
	 * @param userId
	 * @param type
	 */
	public void delectContactsByType(SQLiteDatabase db, String userId,
			String type);

	/**
	 * 插入某个联系人
	 * 
	 * @param db
	 * @param userId
	 * @param name
	 * @param code
	 * @param id
	 * @param type
	 * @return
	 */
	public long insertContactsByUserId(SQLiteDatabase db, String userId,
			String name, String code, String id, String type);

	/**
	 * 修改某个联系人
	 * 
	 * @param db
	 * @param userId
	 * @param id
	 * @param name
	 * @param code
	 */
	public void updateContactsByUserIdandId(SQLiteDatabase db, String userId,
			String id, String name, String code);

}
