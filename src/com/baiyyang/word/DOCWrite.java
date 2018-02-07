package com.baiyyang.word;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.FieldsDocumentPart;
import org.apache.poi.hwpf.usermodel.Field;
import org.apache.poi.hwpf.usermodel.Fields;
import org.apache.poi.hwpf.usermodel.Range;

import com.baiyyang.global.Global;

/**  
* @ClassName: DOCWrite  
* @Description: TODO doc文档生成类  
* @author baiyyang@163.com  
* @date 2017年5月8日 上午11:04:31  
*    
*/
public class DOCWrite {
	
	//private URL base = this.getClass().getResource("");

	/**  
	* @Title: writeDoc  
	* @Description: TODO doc文档生成方法  
	* @param @param map 生成doc需要的替换内容映射 
	* @param @param filepath 生成路径
	* @param @param docFormatPath doc文档生成的格式
	* @param @return      
	* @return boolean     
	* @throws  
	*/
	public boolean writeDoc(Map<String, String> map , String filepath ,String docFormatPath) {
		try {
			//读取word模板
			//String fileDir = new File(base.getFile(),"../../../../../doc/").getCanonicalPath();						
			
			FileInputStream in = new FileInputStream(new File(docFormatPath));
			HWPFDocument hdt = new HWPFDocument(in);
			Fields fields = hdt.getFields();
			Iterator<Field> it = fields.getFields(FieldsDocumentPart.MAIN).iterator();
			while(it.hasNext()){
				System.out.println(it.next().getType());
			}
			
			//读取word文本内容
			Range range = hdt.getRange();
			//System.out.println(range.text());
			//替换文本内容
			for (Map.Entry<String,String> entry:map.entrySet()) {
				range.replaceText(entry.getKey(),entry.getValue());
			}    
			ByteArrayOutputStream ostream = new ByteArrayOutputStream();			
			FileOutputStream out = new FileOutputStream(filepath,true);
			hdt.write(ostream);
            //输出字节流
			out.write(ostream.toByteArray());
			out.close();
			ostream.close();
			
			return true;
					
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}


}
