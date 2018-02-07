package com.baiyyang.file;

/**  
* @ClassName: MyFilter  
* @Description: 过滤原始文件中的不合法字符 
* @author baiyyang@163.com  
* @date 2017年5月8日 上午9:57:18  
*    
*/
public class MyFilter {
	
	public String filter(String content){
		StringBuffer out = new StringBuffer(); // Used to hold the output.
       char current; // Used to reference the current character.
       if (content == null || ("".equals(content)))
           return ""; // vacancy test.
       for (int i = 0; i < content.length(); i++) {
           current = content.charAt(i);
           if ((current == 0x9) || (current == 0xA) || (current == 0xD)
                  || ((current > 0x20) && (current <= 0xD7FF))
                  || ((current >= 0xE000) && (current <= 0xFFFD))
                  || ((current >= 0x10000) && (current <= 0x10FFFF))
                  || (current < 0x0) || (current == 32))
              out.append(current);
       }
       return out.toString().trim();
	}

	public static void main(String[] args) {
		String in = "Editor's note: The faces of some of 2016's greatest heroes were never seen but"
				+ " images of their selfless actions and at times, outright bravery, deeply touched the "
				+ "hearts of Chinese internet users.";
		MyFilter filter = new MyFilter();
		System.out.println(filter.filter(in));
	}
}
