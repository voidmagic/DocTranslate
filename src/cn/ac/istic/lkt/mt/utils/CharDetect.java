/**
 * 
 */
package cn.ac.istic.lkt.mt.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Vector;

import org.mozilla.universalchardet.UniversalDetector;


/**
 * @author SHI Chongde
 *
 */
public class CharDetect {
	public static final String DEFAULT_ENCODING = "UTF-8";
	public static String detect(byte[] content) {
		
		UniversalDetector detector = new UniversalDetector(null);
		// 开始给一部分数据，让学习一下啊，官方建议是1000个byte左右（当然这1000个byte你得包含中文之类的）
		detector.handleData(content, 0, content.length);
		// 识别结束必须调用这个方法
		detector.dataEnd();
		// 神奇的时刻就在这个方法了，返回字符集编码。
		String encoding = detector.getDetectedCharset();
		if (encoding == null) {
	         encoding = DEFAULT_ENCODING;
	    }
		return encoding;
	}

	public static String detect(String filename) throws IOException {
		FileInputStream fis = new java.io.FileInputStream(filename);
		byte[] tmp = new byte[1024];
		int l;
		UniversalDetector detector = new UniversalDetector(null);
		while ((l = fis.read(tmp)) != -1) {  
	        if (!detector.isDone()) {  
	        	detector.handleData(tmp, 0, l);  
	        }  
	    } 
		detector.dataEnd();
		fis.close();
		String encoding = detector.getDetectedCharset();
		if (encoding == null) {
	         encoding = DEFAULT_ENCODING;
	    }
		return encoding;
	}
}
