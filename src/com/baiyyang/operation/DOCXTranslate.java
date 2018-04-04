package com.baiyyang.operation;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.poifs.filesystem.OfficeXmlFileException;

import com.baiyyang.global.Global;
import com.baiyyang.server.test.Test;
import com.baiyyang.word.DOCXParser;
import com.baiyyang.word.DOCXWrite;

/**  
* @ClassName: DOCXTranslate  
* @Description: 处理docx的类  
* @author baiyyang@163.com  
* @date 2017年5月8日 上午10:16:16  
*    
*/
public class DOCXTranslate {
	
	/**  
	* @Fields parser : docx文件解析类
	*/  
	private DOCXParser parser = new DOCXParser();
	/**  
	* @Fields write : docx文件生成类
	*/  
	private DOCXWrite write = new DOCXWrite();
	
	/**  
	* @Fields test : 机器翻译类
	*/  
	private Test test = new Test();
	
	/**  
	* @Title: translate  
	* @Description: docx文档翻译
	* @param @param global
	* @param @return      
	* @return boolean     
	* @throws  
	*/
	public boolean translate(Global global, String language, String domain) throws OfficeXmlFileException{
		
		List<String> contents = new ArrayList<>();
		contents = parser.parser(global.getReadPath());
		
		for(String sent : contents){			
			try {
				global.getDocxAnswers().add("        " + test.test(language, domain, sent));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				global.getDocxAnswers().add("        " + "翻译出错啦！！！！");
			}
		}
	
		//生成新的docx文件
		boolean ans = write.writeDOCX(global.getDocxAnswers(), global.getCreatePath());
		
		global.getDocxAnswers().clear();
		
		return ans;	
	}
}
