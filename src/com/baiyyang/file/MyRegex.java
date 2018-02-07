package com.baiyyang.file;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**  
* @ClassName: MyRegex  
* @Description: 处理原始文件中的URL和Email数据
* @author baiyyang@163.com  
* @date 2017年5月8日 上午10:02:00  
*    
*/
public class MyRegex {

	/**  
	* @Fields ans : 返回处理后的结果
	*/  
	private List<String> ans = new ArrayList<>();
	
	/**  
	* @Title: regexEmail  
	* @Description: 匹配email，将字符串分割成3段，email前一段，email，email后一段 
	* @param @param email
	* @param @return      
	* @return List<String>     
	* @throws  
	*/
	public List<String> regexEmail(String email){
		
		List<String> groups = new ArrayList<String>();
		
		Pattern pattern = Pattern.compile("^(.*)( (-|(\\w)|(\\.))+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+)(.*)$");
        Matcher matcher = pattern.matcher(email);
        
        if(matcher.find()){  
            int start = matcher.start(2);
            int end = matcher.end(2);
            
            groups.add(email.substring(0 , start));
            groups.add(email.substring(start, end));
            groups.add(email.substring(end , email.length()));
        }      
        
        return groups;
	}
	
	
	/**  
	* @Title: regexURL  
	* @Description: 匹配URL，分割成URL前一段，URL，URL后一段  
	* @param @param url
	* @param @return      
	* @return List<String>     
	* @throws  
	*/
	public List<String> regexURL(String url){
		
		List<String> groups = new ArrayList<String>();
		
		Pattern pattern = Pattern.compile("^(.*)((https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|])(.*)$");
        Matcher matcher = pattern.matcher(url);
        
        if(matcher.find()){  
            int start = matcher.start(2);
            int end = matcher.end(2);
            
            groups.add(url.substring(0 , start));
            groups.add(url.substring(start, end));
            groups.add(url.substring(end , url.length()));
        }     
        
		return groups;
	}
	
	/**  
	* @Title: email  
	* @Description: 判断是否是email  
	* @param @param email
	* @param @return      
	* @return boolean     
	* @throws  
	*/
	public boolean email(String email){
		Pattern pattern = Pattern.compile("^(-|(\\w)|(\\.))+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
		Matcher matcher = pattern.matcher(email.trim());
		if(matcher.find()){
			return true;
		}
		else {
			return false;
		}
	}
	
	/**  
	* @Title: url  
	* @Description: 判断是否是url 
	* @param @param url
	* @param @return      
	* @return boolean     
	* @throws  
	*/
	public boolean url(String url){
		Pattern pattern = Pattern.compile("^(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]$");
		Matcher matcher = pattern.matcher(url.trim());
		if(matcher.find()){
			return true;
		}
		else {
			return false;
		}
	}
	
	
	/**  
	* @Title: regex  
	* @Description: 判断email和URL并对其分割
	* @param @param text      
	* @return void     
	* @throws  
	*/
	public void regex(String text){
		
		List<String> temp = new ArrayList<>();
		
		//啥都没有
		if(regexEmail(text).size() == 0 && regexURL(text).size() == 0){
			ans.add(text);
		}
		
		//只有email
		else if (regexEmail(text).size() > 0  && regexURL(text).size() == 0) {
			temp = regexEmail(text);
			ans.add(temp.get(2));
			ans.add(temp.get(1));
			regex(temp.get(0));
		}
		
		//只有url
		else if (regexEmail(text).size() == 0 && regexURL(text).size() > 0){
			temp = regexURL(text);
			ans.add(temp.get(2));
			ans.add(temp.get(1));
			regex(temp.get(0));
		}
		
		//email和url都有
		else {
			temp = regexEmail(text);
			regex(temp.get(2));
			ans.add(temp.get(1));
			regex(temp.get(0));
			
		}
		
	}
	
	
	public List<String> getAns() {
		return ans;
	}


	public void setAns(List<String> ans) {
		this.ans = ans;
	}


	public static void main(String[] args) {  
	    
        MyRegex regex = new MyRegex();
        String email = "bayyang@163.com";
        System.out.println(regex.email(email));
        
        /*String url = "It's a useful bayyang@163.com website that http://http://www.cnblogs.com/speeding/p/5097790.html .you can learn http://www.baidu.com abundant konwledge.";
        List<String> ans1 = regex.regexURL(url);
        for(String string : ans1){
        	System.out.println(string);
        }*/
        
        /*String text = "This is my email bayyang@163.com,and if you have something wrong ,please connect me in time."
        		+ "It's a useful bayyang@163.com website that http://www.baidu.com and http://www.cnblogs.com/speeding/p/5097790.html ."
        		+ "you can learn http://www.baidu.com abundant konwledge.";
        regex.regex(text);
        List<String> ans = regex.getAns();
        for(int i = ans.size() - 1 ; i>=0 ; i--){
        	System.out.println(ans.get(i));
        }*/
        
    }  
	
}
