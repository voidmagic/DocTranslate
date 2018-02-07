package com.baiyyang.file;

import java.io.File;

/**  
* @ClassName: MyRename  
* @Description: 对文件中提取出来的图片后缀同意图换成jpg格式
* @author baiyyang@163.com  
* @date 2017年5月8日 上午10:04:33  
*    
*/
public class MyRename {
	
	/**  
	* @Title: rename  
	* @Description: 将该目录下的所有文件更改为格式to  
	* @param @param path
	* @param @param to      
	* @return void     
	* @throws  
	*/
	public void rename(String path , String to){
		File root = new File(path);
		File[] files = root.listFiles();
		if(files != null){
			for(File file : files){
				String name = file.getName();
				if(!name.endsWith(to)){
					file.renameTo(new File(file.getParent() + "/" + name.substring(0 , name.indexOf(".")) + to));
				}
			}
		}
	}
	
	public static void main(String[] args) {
		MyRename myRename = new MyRename();
		myRename.rename("" , ".jpg");
	}

}
