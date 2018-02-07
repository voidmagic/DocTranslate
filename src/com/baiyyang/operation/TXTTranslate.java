package com.baiyyang.operation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;

import com.baiyyang.global.Global;
import com.baiyyang.server.test.Test;

/**  
* @ClassName: TXTTranslate  
* @Description: txt文件翻译类
* @author baiyyang@163.com  
* @date 2017年5月8日 上午10:19:45  
*    
*/
public class TXTTranslate {
	
	/**  
	* @Fields test : 机器翻译类
	*/  
	private Test test = new Test();
	
	/**  
	* @Title: translate  
	* @Description: txt翻译方法
	* @param @param global
	* @param @return      
	* @return boolean     
	* @throws  
	*/
	public boolean translate(Global global, String language, String domain){
		//读取txt文件内容
		File file = new File(global.getReadPath());
		InputStreamReader reader = null;
		BufferedReader bf = null;
		try {
			reader = new InputStreamReader(new FileInputStream(file),"UTF-8");
			bf = new BufferedReader(reader);
			String line = "";
			String paragraph = "";			
			while((line = bf.readLine()) != null){				
				
				//根据最后一个字母判断改行是否为一个完整的句子
				int paragraphFlag = 0;
				for(int i = line.length() - 1 ; i >= 0 ; i--){
					if(line.charAt(i) == ' '){
						continue;
					}
					else {
						if(line.charAt(i) == '.' || line.charAt(i) == '。'){
							paragraphFlag = 1;
							paragraph += line;
						}
						else {
							paragraphFlag = 0;
						}
						break;
					}
				}
				
				//此行表示一个段落
				if(paragraphFlag == 1){
					String[] texts = paragraph.split("\r\n");
					String sent = "";
					for(int j = 0 ; j<texts.length ; j++){
						sent += texts[j];
					}
					
					//Global.txtAnswers.add(test.test(Global.language , sent));
					
					try {
						//Global.txtAnswers.add(test.test(Global.language, sent));
						global.getTxtAnswers().add(test.test(language, domain, sent));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						global.getTxtAnswers().add("翻译出错啦！！！");
					}
					
					//System.out.println(sent);
					
					paragraph = "";
				}
				//此行不是一个段落
				else {
					paragraph += line + " ";
				}										
			}
			reader.close();
							
			if(!paragraph.equals("")){
				
				String[] texts = paragraph.split("\r\n");
				String sent = "";
				for(int j = 0 ; j<texts.length ; j++){
					sent += texts[j];
				}
				
				try {
					global.getTxtAnswers().add(test.test(language, domain, sent));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					global.getTxtAnswers().add("翻译出错啦！！！");
				}
			}
			
			//写入到txt中
			File txtFile = new File(global.getCreatePath());
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(txtFile,true), "UTF-8");
			BufferedWriter bw = new BufferedWriter(writer);
			try {
				for(String content : global.getTxtAnswers()){
					bw.write(content + "\r\n");
				}
				bw.close();
				
				//清除多余内存
				global.getTxtAnswers().clear();
				
				return true;
				
			} catch (Exception e) {
				// TODO: handle exception
				return false;
			}			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
		finally{
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
