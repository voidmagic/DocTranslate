package com.baiyyang.file;

import java.io.File;

/**  
* @ClassName: MyDelete  
* @Description: 删除文件工具类
* @author baiyyang@163.com  
* @date 2017年5月8日 上午9:56:07  
*    
*/
public class MyDelete {
	
	/**  
	* @Title: deleteAll  
	* @Description: 删除某一路径下的所有文件
	* @param @param path      传入的文件路径
	* @return void     
	* @throws  
	*/
	public void deleteAll(File path) {
		if (!path.exists())   //路径不存在
			return;
		if (path.isFile()) {  //是文件
			path.delete();
			return;
		}else{
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				deleteAll(files[i]);
			}
			System.out.println(path);
			System.out.println(path.delete());; 
		}
		
    }

}
