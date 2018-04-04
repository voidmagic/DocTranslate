package com.baiyyang.word;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

/**  
* @ClassName: DOCXParser  
* @Description: TODO 解析docx文档 类
* @author baiyyang@163.com  
* @date 2017年5月8日 上午11:07:07  
*    
*/
public class DOCXParser {
	
	/**  
	* @Title: parser  
	* @Description: TODO 解析docx文件  
	* @param @param filepath
	* @param @return      
	* @return List<String>     
	* @throws  
	*/
	public List<String> parser(String filepath) throws OfficeXmlFileException{
	    
		List<String> contents = new ArrayList<>();
		try {
			OPCPackage opcPackage = POIXMLDocument.openPackage(filepath);
			/*POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);
			String text2007 = extractor.getText();
			System.out.println(text2007);*/
			
			XWPFDocument document = new XWPFDocument(opcPackage);
			List<XWPFParagraph> paragraphs = document.getParagraphs();
			int count = 1;
			for(XWPFParagraph paragraph : paragraphs){
				contents.add(paragraph.getText());
				//System.out.println("第" + count + "段：" + paragraph.getText());
				count++;
			}
			document.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return contents;
	}
	public static void main(String[] args) {
		DOCXParser parser = new DOCXParser();
		parser.parser("");
	}
}
