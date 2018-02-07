package com.baiyyang.file;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**  
* @ClassName: MyImage  
* @Description: 处理图片，得到图片的宽度和高度 
* @author baiyyang@163.com  
* @date 2017年5月8日 上午9:59:03  
*    
*/
public class MyImage {
	
	/**  
	* @Fields width : 宽度  
	*/  
	private int width;
	/**  
	* @Fields height : 高度  
	*/  
	private int height;
	
	/**  
	* @Title: dealImage  
	* @Description: 得到图片的高度和宽度  
	* @param @param 图片路径
	* @return void     
	* @throws  
	*/
	public void dealImage(String path){
		File file = new File(path);
		BufferedImage bi = null;
		try {
			bi = ImageIO.read(file);
			if(null != bi){
				width = bi.getWidth();
				height = bi.getHeight();
			}
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		for(int i=0;i<=295;i++){
			MyImage image = new MyImage();
			String path = "F:\\MyEclipse2014\\PDFTranslate\\image\\" + String.valueOf(i) + ".jpg";
			image.dealImage(path);
			System.out.println("第" + i + "个：" + image.getWidth() + "----" + image.getHeight());
		}
	}
	
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}

	

}
