package com.gionee.gntravel.utils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

public class ValidateUtil {

	public static void w(Object o) {
		System.out.println(o);
	}

	/**
	 * 邮箱验证：
	 */
	public static boolean isValidEmail(String email) {

		String regEmail = "^(?:\\w+\\.{1})*\\w+@(\\w+\\.)*\\w+$";
		Pattern pat = Pattern.compile(regEmail);
		Matcher mat = pat.matcher(email);

		if (mat.find()) {
			w("合法邮箱");
			return true;
		}
		w("邮箱格式错误！");
		return false;
	}

	/**
	 * ip 地址的验证
	 * 
	 */
	public static boolean isValidIp(String strIp) {

		String reIp = "\\b((\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])(\\b|\\.)){4}";
		Pattern com = Pattern.compile(reIp);
		Matcher mat = com.matcher(strIp);
		if (mat.find()) {
			w("IP地址格式正确");
			return true;
		}

		w("Ip地址格式错误");
		return false;
	}
	/**
	 * ip 地址的验证
	 * 
	 */
	public static boolean isValidPhone(String strPhone) {
		String regPhone = "(((\\(\\d{3}\\))|(\\d{3}\\-))?1[0-9]\\d{9})|(^(0[0-9]{2,3}\\-)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?$)";
		Pattern com = Pattern.compile(regPhone);
		Matcher mat = com.matcher(strPhone);
		if (mat.find()) {
			w("手机号码格式正确");
			return true;
		}
		w("手机号码格式错误");
		return false;
	}

	/**
	 * 验证日期
	 * 
	 */
	public static boolean isValidDate(String sDate) {
		String reDate = "(?:[1-9]|0[1-9]|[12]\\d|3[0-1])(\\/|\\-)(?:[1-9]|0[1-9]|1[0-2])(\\/|\\-)(?:19|20\\d\\d)";
		Pattern com = Pattern.compile(reDate);
		Matcher mat = com.matcher(sDate);
		if (mat.find()) {

			w("日期格式正确");
			return true;
		}

		w("日期格式错误");

		return false;
	}

	/**
	 * 验证信用卡号
	 * 
	 */
	public static boolean isValidCard(String sCard) {
		return true;
//		String reCard = "^(\\d{13}(?:\\d{3})?)$";
//		Pattern com = Pattern.compile(reCard);
//		Matcher mat = com.matcher(sCard);
//		
//		
//		if (mat.find()) {
//			w(reCard);
//			w("格式正确");
//
//			// 判断是否合法
//			boolean luhn = isLuhn(sCard);
//
//			if (luhn) {
//				w("卡号是合法的");
//				return true;
//			} else {
//				w("卡号不合法");
//				return false;
//			}
//
//		}
//		w("格式不正确");
//
//		return false;
	}
	
	/**
	 * luhn算法
	 * 
	 */
	
         public static boolean isLuhn(String strNum){
        	 
        	 int oddSum=0;
        	 int evenSum=0;
        	 boolean isOdd=true;
        	 
        	 for (int i=strNum.length()-1;i>=0;i--){
        		 char cNum=strNum.charAt(i);
        		
        		 int   num=Integer.parseInt(cNum+"");
        		 
        		 System.out.print("第"+i+"个"+"是"+"\t"+num+"\n");
        	 
        		 
        		if(isOdd){
        			 oddSum+=num;
        		 }else{
        			 num=num*2;
        			 if(num>9){
        				 num=num%10+1;
        			 }
        			 evenSum=evenSum+num;
        		 }
        		 isOdd=!isOdd;
        	 }
        	 
        		  return ((evenSum+oddSum)%10==0);
         }

     	public static boolean isValidIdCard(String idCard) {
     		// String reCard = "^(\\d{13}(?:\\d{3})?)$";
     		// 身份证号码为15位或者18位，15位时全为数字，18位前17位为数字，最后一位是校验位，可能为数字或字符X
     		String regCard = "/(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)/";

     		Pattern com = Pattern.compile(regCard);
     		Matcher mat = com.matcher(idCard);

     		if (mat.find()) {
     			return true;
     		}
     		return false;
     	}

	/**
	 * 测试
	 * 
	 */

	public static void main(String args[]) {
		 String email = "qing.qingbyqing@gmail.vip.com";//邮箱测试
		 isValidEmail(email);
		  String strIp="1.10.111.255";//IP地址测试
		 isValidIp(strIp);
		  
		  String sDate="03/03/1911"; 
		 isValidDate(sDate);//日期测试
		

		String strNum = "4432123456788881";
//		w(isValidCard(strNum));//信用卡测试
	}
}
