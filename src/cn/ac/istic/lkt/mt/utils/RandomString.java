/**
 * 
 */
package cn.ac.istic.lkt.mt.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @author Chongde SHI
 *
 */
public class RandomString {

	public static String get(int length){
		//String base = "abcdefghijklmnopqrstuvwxyz0123456789";
		String base = "0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}
	
	public static String getTimeString(int length) {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		return "["+df.format(new Date())+"."+get(length)+"]";// new Date()为获取当前系统时间
	}
}
