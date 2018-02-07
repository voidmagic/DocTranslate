package com.baiyyang.savepdf;

import java.util.ArrayList;
import java.util.List;

/**  
* @ClassName: PageImages  
* @Description: 记录每一页的图片的路径  
* @author baiyyang@163.com  
* @date 2017年5月8日 上午10:54:09  
*    
*/
public class PageImages {

	/**  
	* @Fields imagesPath : 存放图片路径
	*/  
	private List<String> imagesPath = new ArrayList<>();
	
	public List<String> getImagesPath() {
		return imagesPath;
	}
	public void setImagesPath(List<String> imagesPath) {
		this.imagesPath = imagesPath;
	}
	
	
}
