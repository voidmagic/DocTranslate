package com.baiyyang.global;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.baiyyang.savepdf.PageImages;

import cn.ac.istic.lkt.mt.doctrans.net.SocketServer;

/**
 * @ClassName: Global
 * @Description: 存储翻译结果和图片信息
 * @author baiyyang@163.com
 * @date 2017年5月8日 上午10:06:40
 * 
 */
public class Global {
	private static Logger logger = Logger.getLogger(Global.class);

	public static final String EN="EN";
	public static final String CN="CN";
	public static final String JP="JP";
	
	public static final String REPORT="REPORT";
	public static final String ARTICLE="ARTICLE";
	public static final String PPT="PPT";
	
	public static String WORKD  = null;
	public static String FILED  = null  ;
	public static String TGTD  = null;

	
	public Global() {
	}
	
	public static void prepareDir(){
		if (WORKD==null){
			logger.error("Working Dir is not set properlly.");
			System.exit(-1);
		}
		FILED = WORKD+"\\files\\";

		File tff = new File(FILED);
		if (!tff.exists())
			tff.mkdir();

	}


	/**
	 * @Fields answers : pdf存放翻译结果
	 */
	private List<List<String>> answers = new ArrayList<>();

	/**
	 * @Fields txtAnswers : txt 存放翻译结果
	 */
	private List<String> txtAnswers = new ArrayList<>();

	/**
	 * @Fields docAnswers : 存放doc翻译结果
	 */
	private List<String> docAnswers = new ArrayList<>();

	/**
	 * @Fields docxAnswers : 存放docx翻译结果
	 */
	private List<String> docxAnswers = new ArrayList<>();

	/**
	 * @Fields pageImages : 存放图片信息
	 */
	private List<PageImages> pageImages = new ArrayList<>();

	/**
	 * @Fields readPath : 原始文件路径
	 */
	private String readPath = "";

	/**
	 * @Fields createPath : 生成文件路径
	 */
	private String createPath = "";

	/**
	 * @Fields imagePath : 图片存放路径
	 */
	private String imagePath = "";

	/**
	 * @Fields docFormat : 生成doc模板
	 */
	private String docFormat = "";

	/**
	 * @Fields createFileName : 记录生成文件名
	 */
	private String createFileName;

	public List<List<String>> getAnswers() {
		return answers;
	}

	public void setAnswers(List<List<String>> answers) {
		this.answers = answers;
	}

	public List<String> getTxtAnswers() {
		return txtAnswers;
	}

	public void setTxtAnswers(List<String> txtAnswers) {
		this.txtAnswers = txtAnswers;
	}

	public List<String> getDocAnswers() {
		return docAnswers;
	}

	public void setDocAnswers(List<String> docAnswers) {
		this.docAnswers = docAnswers;
	}

	public List<String> getDocxAnswers() {
		return docxAnswers;
	}

	public void setDocxAnswers(List<String> docxAnswers) {
		this.docxAnswers = docxAnswers;
	}

	public List<PageImages> getPageImages() {
		return pageImages;
	}

	public void setPageImages(List<PageImages> pageImages) {
		this.pageImages = pageImages;
	}

	public String getReadPath() {
		return readPath;
	}

	public void setReadPath(String readPath) {
		this.readPath = readPath;
	}

	public String getCreatePath() {
		return createPath;
	}

	public void setCreatePath(String createPath) {
		this.createPath = createPath;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getDocFormat() {
		return docFormat;
	}

	public void setDocFormat(String docFormat) {
		this.docFormat = docFormat;
	}

	public String getCreateFileName() {
		return createFileName;
	}

	public void setCreateFileName(String createFileName) {
		this.createFileName = createFileName;
	}

	public static String absDonePath(String filename) {
		return (Global.FILED + filename + ".DONE").replace("/", "\\");
	}

	public static String absErrorPath(String filename) {
		return (Global.FILED + filename + ".ERROR").replace("/", "\\");
	}
	
	public static String absPath(String filename) {
		return (Global.FILED + filename).replace("/", "\\");
	}

	public static String absTransPath(String filename) {
		int p = filename.lastIndexOf(".");
		return (Global.FILED + filename.substring(0, p) + "_trans." + filename.substring(p + 1)).replace("/", "\\");
	}

	public static void deleteFiles(String filename) {
		int p = filename.lastIndexOf(".");
		String stem = filename.substring(0, p);
		File[] path = new File(Global.FILED).listFiles();
		for (File f : path) {
			String fname = f.getName();
			if (fname.equals(filename+".DONE") || fname.equals(filename+".ERROR")) {
				f.delete();
			}
			if (f.getName().contains(stem)) {
				String ext = filename.substring(p);
				if (ext.equals(".ocr") || ext.equals(".tif")) {
					f.delete();
				}
			}
		}
	}
}
