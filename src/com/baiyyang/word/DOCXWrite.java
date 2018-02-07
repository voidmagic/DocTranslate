package com.baiyyang.word;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

/**  
* @ClassName: DOCXWrite  
* @Description: TODO  docx生成类
* @author baiyyang@163.com  
* @date 2017年5月8日 上午11:10:02  
*    
*/
public class DOCXWrite {

	/**  
	* @Title: writeDOCX  
	* @Description: TODO  docx生成方法
	* @param @param contents
	* @param @param filepath
	* @param @return      
	* @return boolean     
	* @throws  
	*/
	public boolean writeDOCX(List<String> contents, String filepath) {

		try {
			// 新建一个文档
			XWPFDocument doc = new XWPFDocument();
			for (String content : contents) {
				// 创建一个段落
				XWPFParagraph para = doc.createParagraph();

				// 一个XWPFRun代表具有相同属性的一个区域。
				XWPFRun run = para.createRun();
				// run.setBold(true); //加粗
				run.setFontSize(17);
				run.setText(content);
			}
			OutputStream os = new FileOutputStream(filepath);

			// 把doc输出到输出流
			doc.write(os);
			this.close(os);
			
			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**  
	* @Title: close  
	* @Description: TODO  关闭输出流
	* @param @param os      
	* @return void     
	* @throws  
	*/
	private void close(OutputStream os) {
		if (os != null) {
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		DOCXWrite write = new DOCXWrite();
		List<String> contents = new ArrayList<>();
		contents.add("123443563217656789765467");
		contents.add("这是一个生成的docx文件");
		write.writeDOCX(contents ,
				"C:\\Users\\白洋洋5066\\Desktop\\docx.docx");
	}

}
