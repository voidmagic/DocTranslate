package com.baiyyang.operation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.poifs.filesystem.OfficeXmlFileException;

import com.baiyyang.global.Global;
import com.baiyyang.server.test.Test;
import com.baiyyang.word.DOCParser;
import com.baiyyang.word.DOCWrite;

/**  
* @ClassName: DOCTranslate  
* @Description: 对doc文件进行翻译
* @author baiyyang@163.com  
* @date 2017年5月8日 上午10:11:46  
*    
*/
public class DOCTranslate {
	/**  
	* @Fields parser : 解析doc类
	*/  
	private DOCParser parser = new DOCParser();
	/**  
	* @Fields write : 生成doc类  
	*/  
	private DOCWrite write = new DOCWrite();
	
	/**  
	* @Fields test : 机器翻译类
	*/  
	private Test test = new Test();
		
	/**  
	* @Title: translate  
	* @Description: 对doc文件进行翻译
	* @param @param global
	* @param @return      
	* @return boolean     
	* @throws  
	*/
	public boolean translate(Global global, String language, String domain) throws OfficeXmlFileException{
		
		List<String> contents = new ArrayList<>();
		contents = parser.parser(global.getReadPath());
		
		//翻译过程
		for(String sent : contents){			
			try {
				global.getDocAnswers().add(test.test(language, domain,  sent));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				global.getDocAnswers().add("翻译出错啦！！！");
			}
		}
		
		String answer = "";
		for(String s : global.getDocAnswers()){
			answer += "    " + s + "\r\n";
		}
		//替换文件
		Map<String, String> map=new HashMap<String, String>();
		map.put("content", answer);						
		boolean ans = write.writeDoc(map, global.getCreatePath() , global.getDocFormat());
		
		global.getDocAnswers().clear();
		
		return ans;
		
	}					

}
