package com.baiyyang.main;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.xml.ws.WebServiceException;

import cn.ac.ia.cip.PDFTranslateImpl;
import com.baiyyang.operation.*;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;

import com.baiyyang.file.MyDelete;
import com.baiyyang.global.Global;

/**
 * @ClassName: Translate
 * @Description: 翻译项目的入口程序
 * @author baiyyang@163.com
 * @date 2017年5月8日 上午9:48:24
 * 
 */
public class Translate {

	/**
	 * @Fields DOCFormat : 存放生成的doc的模板文件
	 */
	private String DOCFormat = "";

	
	/**
	 * @Fields global : 全局变量
	 */
	private Global global = null;

	public Translate(){
		DOCFormat = "DOCFormat.doc";
		global = new Global();
	}

	/**
	 * @Title: fileTranslate @Description: API调用函数 @param @param filePath
	 * 
	 *         原始文件路径 @param @param createPath 生成文件路径 @param @param
	 *         createFileName 生成文件名 @param @param language
	 *         原文件语言 @param @return @return boolean 返回结果状态 @throws
	 */
	public void fileTranslate(String srcFile, String tgtFile, String language, String domain, String docType) throws WebServiceException, IOException{
		// 获取文件名
		String fileName = "";
		String userAbsolutePath = Global.WORKD;
		File originalFile = new File(srcFile);
		fileName = originalFile.getName();

		// 选取一个读取文件，并进行编码转换(Linux)

		// 判断当前操作系统是否为 Windows
		if (System.getProperty("os.name").indexOf("Windows") != -1) {
			global.setReadPath(srcFile.trim());
		}
		// Linux
		else {
			try {
				global.setReadPath(new String(srcFile.trim().getBytes(), "UTF-8"));
			} catch (UnsupportedEncodingException uee) {
				uee.printStackTrace();
			}
		}


		// 翻译pdf
		if (fileName.endsWith(".pdf")) {

			// 存放图片文件的路径
			File image = new File(Global.FILED+"/"+fileName);
			if (image.exists()) {
				new MyDelete().deleteAll(image);
			}
			if (!image.exists()) {
				image.mkdirs();
			}

			global.setImagePath(userAbsolutePath + image.getPath());

			// 创建pdf文件
			File pdfFile = new File(tgtFile);
			if (pdfFile.exists())
				pdfFile.delete();
			if (!pdfFile.exists()) {
				try {
					pdfFile.createNewFile();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}

			// 编码转换
			if (System.getProperty("os.name").indexOf("Windows") != -1) {
				global.setCreatePath(pdfFile.getPath());
			}
			// Linux
			else {
				try {
					global.setCreatePath(new String(pdfFile.getPath().getBytes(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

			PDFTranslateImpl translate = new PDFTranslateImpl(language, domain, docType);
			
			translate.translate(global, userAbsolutePath);
			
			
		}
		// txt翻译
		else if (fileName.endsWith(".txt")) {
			// 创建txt文件
			File txtFile = new File(tgtFile);

			if (txtFile.exists())
				txtFile.delete();

			if (!txtFile.exists()) {
				try {
					txtFile.createNewFile();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}

			// 编码转换
			if (System.getProperty("os.name").indexOf("Windows") != -1) {
				global.setCreatePath(txtFile.getPath());
			}
			// Linux
			else {
				try {
					global.setCreatePath(new String(txtFile.getPath().getBytes(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

			TXTTranslate translate = new TXTTranslate();

			translate.translate(global, language, domain);
		}

		// doc翻译
		else if (fileName.endsWith("doc")) {
			// 创建doc文件
			File docFile = new File(tgtFile);
			if (docFile.exists())
				docFile.delete();

			String encodePath = tgtFile;

			// 编码转换
			if (System.getProperty("os.name").indexOf("Windows") != -1) {
				global.setCreatePath(encodePath);
			}
			// Linux
			else {
				try {
					global.setCreatePath(new String(encodePath.getBytes(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

			global.setDocFormat(DOCFormat);

			try {
				DOCTranslate translate = new DOCTranslate();
				translate.translate(global, language, domain );
			}catch (OfficeXmlFileException e) {
				DOCXTranslate translate = new DOCXTranslate();
				translate.translate(global, language, domain );
			}
		}

		// docx翻译
		else if (fileName.endsWith(".docx")) {
			// 创建docx文件

			File docFile = new File(tgtFile);
			if (docFile.exists())
				docFile.delete();

			String encodePath = tgtFile;

			// 编码转换
			if (System.getProperty("os.name").indexOf("Windows") != -1) {
				global.setCreatePath(encodePath);
			}
			// Linux
			else {
				try {
					global.setCreatePath(new String(encodePath.getBytes(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

			try {
			DOCXTranslate translate = new DOCXTranslate();
			translate.translate(global, language, domain);
			}catch (OfficeXmlFileException e) {
				DOCTranslate translate = new DOCTranslate();
				translate.translate(global, language, domain );
			}
		}
	}

	/**
	 * @Title: main @Description: 测试方法 @param @param args @return void @throws
	 */
	public static void main(String[] args) {
		
	}

}
