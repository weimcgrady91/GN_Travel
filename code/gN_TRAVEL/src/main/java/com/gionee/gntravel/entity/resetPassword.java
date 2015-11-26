package com.gionee.gntravel.entity;

import java.io.Serializable;
/**
 * 
 * @author lijinbao
 * 重置密码 entity
 */
public class resetPassword implements Serializable{
	/**
	 *  * tn:必选项，用户注册时电话号码
	 * vid:必选项,通过refreshGVCImage刷新验证码获取的vid
	 * vtx:必选项,通过refreshGVCImage刷新验证码获取的vda解码，转成图片验证码
	 * vty:必选项,验证码类型,图片的是vtext.
	 * aty:可选项，验证类型，0或者不传表示sms
	 */
	private String tn;
	private String vid;

}
