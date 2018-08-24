/**
 * 
 */
package cn.ac.istic.lkt.mt.doctrans.net;

import java.io.IOException;

import com.baiyyang.global.Global;
import com.baiyyang.main.Translate;

import cn.ac.istic.lkt.mt.doctrans.pdflayout.PDFTextLayout;

/**
 * @author SHI Chongde
 *
 */
public class FileTranslateMain {
	public static void TextEN2CN() {
		Translate translate = new Translate();
		String fi = "D:/e5.pdf";
		String fo = "D:/a.result.pdf";
		String fl = "D:/a.layout.pdf";

		try {
			
			PDFTextLayout tl = new PDFTextLayout();
			//tl.process(fi, fl, PDFTextLayout.EN, PDFTextLayout.ARTICLE);
			translate.fileTranslate(fi, fo, "EN2CN", "INFO", "REPORT");
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

	
	public static void TextCN2EN() {
		Translate translate = new Translate();
		String fi = "D:/patent.pdf";
		String fo = "D:/a.result.pdf";
		String fl = "D:/a.layout.pdf";

		try {
			PDFTextLayout tl = new PDFTextLayout();
			tl.process(fi, fl, PDFTextLayout.CN, PDFTextLayout.ARTICLE);
			translate.fileTranslate(fi, fo, "CN2EN", "SOCI", "REPORT");
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Global.WORKD=System.getProperty("user.dir");
		Global.prepareDir();
		FileTranslateMain.TextEN2CN();
		//FileTranslateMain.TextCN2EN();
	}

}
