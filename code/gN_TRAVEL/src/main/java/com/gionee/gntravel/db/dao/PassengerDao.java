package com.gionee.gntravel.db.dao;

import java.util.ArrayList;

import android.database.sqlite.SQLiteDatabase;

import com.gionee.gntravel.entity.ContentBean;
/**
 * 增删改查登机人
 * @author lijinbao
 *
 */
public interface PassengerDao {
	/**
	 * 查询上次缓存的登机人数据
	 * @param db
	 * @param userId
	 * @return
	 */
	public ArrayList<ContentBean> findPassengersByUserId(SQLiteDatabase db, String userId);
	/**
	 * 删除某个帐号下的所有登机人缓存
	 * @param db
	 * @param userId
	 */
	public void  delectPassengersByUserId(SQLiteDatabase db, String userId);
	/**
	 * 删除某个帐号下的一条登机人缓存
	 * @param db
	 * @param id
	 */
	public void  delectPassengersById(SQLiteDatabase db, String id);
	/**
	 * 插入某个登机人
	 * @param db
	 * @param userId
	 * @param id
	 * @param name
	 * @param code
	 * @return
	 */
	public long  insertPassengersByUserId(SQLiteDatabase db,String userId, String id,String name,String code);
	/**
	 * 修改
	 * @param db
	 * @param userId
	 * @param id
	 * @param name
	 * @param code
	 */
	public void  updatePassengersByUserIdandId(SQLiteDatabase db,String userId, String id,String name,String code);
	
}
