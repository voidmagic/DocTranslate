package com.baiyyang.server.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import javax.xml.ws.WebServiceException;

import com.baiyyang.file.MyFilter;
import com.baiyyang.file.MyRegex;
import com.baiyyang.server.client.STMTWeb;
import com.baiyyang.server.client.STMTWebService;



/**
 * @author Chongde SHI
 *
 */
public class Test {
	
	private STMTWeb stmt;
	private String lang;
	private String domain;
	protected int thcount;
	
	/**  
	* @Fields regex : TODO 正则表达式  
	*/  
	private MyRegex regex = new MyRegex();

	class TransThread extends Thread{
    	public int id;

    	public String s;
    	public String t;
    	public TransThread(String lang, String domain, int id, String s){
    		thcount++;
    		this.id = id;
    		this.s = s;
    	}
        @Override
        public void run() {
        	this.t = stmt.getTranslation(lang, domain,"ABSTRACT", s);  
        	//System.out.println(this.t);
        	//Global.answers.add(this.t);
        	thcount--;
        }
    }    
	
	public Test(){
		stmt = new STMTWebService().getSTMTWebPort();
	}

	public Test(boolean b){

	}
	
	/**  
	* @Title: test  
	* @Description: TODO 机器翻译入口
	* @param @param lang
	* @param @param sent
	* @param @return
	* @param @throws Exception      
	* @return String     
	* @throws  
	*/
	public String test(String lang, String domain, String sent) throws Exception{
		sent = preprocess(sent);		
		MyFilter filter = new MyFilter();
		if(sent != null && !sent.trim().equals("")){
		
			String t = "";
			
			regex.getAns().clear();
			regex.regex(sent);
			List<String> ans = regex.getAns();
			for(int i = ans.size() - 1 ; i>=0 ; i--){
				
				if((!ans.get(i).equals(".")) && (regex.email(ans.get(i)) != true) && (regex.url(ans.get(i)) != true)){
					
					String legal = filter.filter(ans.get(i));
					//System.out.println("Legal: "+legal);
					t += stmt.getTranslation(lang, domain, "ABSTRACT", legal);
					
				}
				else {		
					t += ans.get(i);
				}
			}
			String[] con = t.split("\n");
			String content = "";
			for(int i=0;i<con.length;i++){
				content += con[i];
			} 
			if( content.length() > 0 ){
				if(lang.equals("EN2CN") && content.charAt(content.length() - 1) != '.'){
					content += "";
				}
				if(lang.equals("JP2CN") && content.charAt(content.length() - 1) != '。'){
					content += "。";
//					content += ".";
				}
			}
						
			return content;
		}
		
		return "";
	}
	
	public String preprocess(String s) {
		return s.replace("[", " [ ").replaceAll("]", " ] ");
	}
	public void test(String lang, String domain, Vector<String> lines){		
		//int count = 0;
		for(String s: lines){
			System.out.println(stmt.getTranslation(lang, domain,"ABSTRACT", s));
		}
	}
	public void testMultiThread(String lang, String domain, String content){
		int count = 0;		
		
		try{
			TransThread tt = new TransThread(lang, domain, count, content);
			count++;
			tt.start();
			tt.join();
			//System.out.println(thcount);
			while(thcount > 4){
				//
				Thread.currentThread().sleep(200);
				//System.out.println("waiting ...");
			}			
			
		}
		catch(Exception e){
			System.exit(-1);
			//e.printStackTrace();
		}
	}
	
	public  Vector<String> readFileByLines(String fileName) {
		Vector<String> lines = new Vector<String>();
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
            	lines.add(tempString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return lines;
    }
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Test test = new Test();
		//String english = "This is my Email baiyyang@163.com,and I think it's very useful website http://www.baidu.com.";
		String j = "\t        \r        \n";
		try {
			System.out.println(test.test("JP2CN", "UNKNOWN",j));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
