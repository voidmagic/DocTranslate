package com.baiyyang.word;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hwpf.extractor.WordExtractor; 

/**  
* @ClassName: DOCParser  
* @Description: TODO doc文档解析类  
* @author baiyyang@163.com  
* @date 2017年5月8日 上午11:03:56  
*    
*/
public class DOCParser {
	
	 /**  
	* @Title: parser  
	* @Description: TODO doc文档解析方法  
	* @param @param filename
	* @param @return      
	* @return List<String>     
	* @throws  
	*/
	public List<String> parser(String filename) {  
		 
		 List<String> content = new ArrayList<>();
		 
         try {  
            //word 2003： 图片不会被读取  
            InputStream is = new FileInputStream(new File(filename));  
            WordExtractor ex = new WordExtractor(is);  
            
            //获取doc文件的页数
            //int count = ex.getSummaryInformation().getPageCount();	     
            
            //分段显示内容
            String[] paragraphs = ex.getParagraphText();
            for(int i=0;i<paragraphs.length;i++){
            	content.add(paragraphs[i]);            	
            }
            ex.close();
            
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return content;
    }  
}
