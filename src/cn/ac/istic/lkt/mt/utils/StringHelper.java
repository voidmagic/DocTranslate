/**
 * 
 */
package cn.ac.istic.lkt.mt.utils;

/**
 * @author SHI Chongde
 *
 */
public class StringHelper {
	public static boolean isUpCase(char c) {
		return c >= 65 && c <=90;
	}
	
	public static boolean isLowerCase(char c) {
		return c >=97 && c <=122;
	}
	
	public static boolean isEnglish(char c) {
		return (c >= 65 && c <=90) || (c >=97 && c <=122);
	}
	
	public static boolean isAscii(String s) {
		for (char c: s.toCharArray()) {
			if (c > 255 || c < 0) {
				return false;
			}
		}
		return true;
	}
}
