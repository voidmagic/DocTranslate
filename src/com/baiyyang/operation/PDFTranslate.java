package com.baiyyang.operation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.baiyyang.global.Global;
import com.baiyyang.server.test.Test;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.pdfcleanup.PdfCleanUpLocation;
import com.itextpdf.pdfcleanup.PdfCleanUpTool;

import cn.ac.istic.lkt.mt.doctrans.pdflayout.PDFTextLayout;
import cn.ac.istic.lkt.mt.doctrans.pdflayout.WordWithTextPositions;
import cn.ac.istic.lkt.mt.utils.StringHelper;


/**
 * @ClassName: PDFTranslate
 * @Description: PDF文件翻译类
 * @author baiyyang@163.com
 * @date 2017年5月8日 上午10:18:32
 * 
 */
public class PDFTranslate {

	/**
	 * @Fields test : 机器翻译类
	 */
	private Test test = null ;
	private PdfFont cFont = null;
	private PdfFont cBoldFont = null;
	private String language = null;
	private String srcLang = null;
	private String tgtLang = null;
	private String domain = null;
	private String docType = null;
	private PdfDocument pdfDoc = null;
	

	
	private static boolean drawBlock = false;
	
	public PDFTranslate(String language, String domain, String docType) {
		this.domain = domain;
		this.language = language;
		this.docType = docType;
		
		String[] lang = language.split("2");
		assert(lang.length==2);
		srcLang = lang[0];
		tgtLang = lang[1];
		
		// 使用的字体
		test = new Test();
		try {
			String cFontFile = Global.WORKD+"\\resources\\msyh.ttf";
			String cBoldFontFile = Global.WORKD+"\\resources\\msyhbd.ttf";
			cFont = PdfFontFactory.createFont(cFontFile, PdfEncodings.IDENTITY_H, true);
			cBoldFont = PdfFontFactory.createFont(cBoldFontFile, PdfEncodings.IDENTITY_H, true);
		}catch (IOException e) { // 使用内嵌字体
			try {
				cFont = PdfFontFactory.createFont(StandardFonts.HELVETICA, PdfEncodings.IDENTITY_H, true);
				cBoldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD, PdfEncodings.IDENTITY_H, true);
			}catch(IOException ee){
				ee.printStackTrace();
			}
			
		}
	}
	
	/**
	 * @throws IOException 
	 * @Title: translate @Description: pdf文件翻译 @param @param
	 *         global @param @return @return boolean @throws
	 */
	public void translate(Global global, String userAbsolutePath) throws IOException{
		ArrayList<ArrayList<WordWithTextPositions>> textLayout;
		ArrayList<ArrayList<WordWithTextPositions>> filteredTP;
		PdfReader reader = null;
		PdfWriter writer = null; 
		try {
			// 分析layout
			PDFTextLayout tl = new PDFTextLayout();
			textLayout = tl.getTextLayout(global.getReadPath(), srcLang, docType);
			filteredTP = filtAndTranslate(textLayout);
			reader = new PdfReader(global.getReadPath());
			writer = new PdfWriter(global.getCreatePath());
		}catch(IOException e) {
			e.printStackTrace();
			return;
			//
		}
		
		// 先尝试stamper模式, 在原来的文件基础上清理文字区域，填入翻译后的内容; 
		// stamper模式有可能碰到区域无法清理的情况，产生Exception，这样的话就不使用老文件，直接生成新文件
		try { 
			PdfDocument writeDoc = new PdfDocument(reader, writer);
			int pageNum = textLayout.size();
			//清理 原有文字
			for (int pageid = 0; pageid < pageNum; pageid++) {
				ArrayList<WordWithTextPositions> textAreas = filteredTP.get(pageid);
				List<Rectangle> cleanArea = new ArrayList<Rectangle>();
				List<PdfCleanUpLocation> cleanUpLocations = new ArrayList<PdfCleanUpLocation>();

				PdfPage pdfPage = writeDoc.getPage(pageid + 1);
				float pageHeight = pdfPage.getPageSize().getHeight();
				// 擦除 翻译
				for (int i = 0; i < textAreas.size(); i++) {
					WordWithTextPositions ta = textAreas.get(i);
					Rectangle rec = getArea(ta, pageHeight);
					cleanArea.add(rec);
					cleanUpLocations.add(new PdfCleanUpLocation(pageid + 1, rec, DeviceRgb.WHITE));
				}
				PdfCleanUpTool cleaner = new PdfCleanUpTool(writeDoc, cleanUpLocations);
				cleaner.cleanUp();
			}
			pdfDoc = writeDoc;
		}catch (com.itextpdf.io.IOException e) { 
			e.printStackTrace();
			PdfDocument readDoc = new PdfDocument(reader);
			PdfDocument writeDoc = new PdfDocument(writer);
			for(int i = 0 ; i < filteredTP.size(); i++) {
				writeDoc.addNewPage(new PageSize(readDoc.getPage(i+1).getPageSize()));
			}
			pdfDoc = writeDoc;
		}finally {
			int pageNum = pdfDoc.getNumberOfPages();
			for (int j = 0; j < pageNum; j++) {
				ArrayList<WordWithTextPositions> filteredTextAreas = filteredTP.get(j);
				PdfPage pdfPage = pdfDoc.getPage(j+1);
				float pageHeight = pdfPage.getPageSize().getHeight();
				PdfCanvas pdfCanvas = new PdfCanvas(pdfPage);
				for (int i = 0; i < filteredTextAreas.size(); i++) {
					WordWithTextPositions ta = filteredTextAreas.get(i);
					float fontsize = ta.getFontSizeInPt();
					Rectangle rec = getArea(ta, pageHeight);

					if (drawBlock) {
						pdfCanvas.setLineWidth(0.5f);
						pdfCanvas.setStrokeColor(ColorConstants.RED);
						pdfCanvas.rectangle(rec);
						pdfCanvas.stroke();
					}
					
					float transFontSize = fontsize;
					if (tgtLang.equals("EN")){
						transFontSize = fontsize - 2;
					}
					String srcFontName = ta.getFont().getName().toLowerCase();
					PdfFont tgtFont = cFont;
					if (srcFontName.contains("bold")) {
						tgtFont = cBoldFont;
					}
					Canvas canvas = new Canvas(pdfCanvas, pdfDoc, rec);
					String tr = translate(ta);
					Text text = new Text(tr).setFont(tgtFont).setFontSize(transFontSize);
					System.out.println("render: " + text.getText());
					Paragraph p = new Paragraph().add(text);
					p.setMarginTop(0);
					//p.setFirstLineIndent(10);
					p.setMultipliedLeading(1.05f);
					canvas.add(p);
					canvas.close();

				}
			}
			pdfDoc.close();
		}
	}
	public Rectangle getArea(WordWithTextPositions tp, float pageHeight) {
		return new Rectangle(tp.getXstart(), pageHeight - tp.getYstart() - tp.getFontSizeInPt() * 0.2f, tp.getWidth(), tp.getHeight());
	}
	
	public ArrayList<ArrayList<WordWithTextPositions>> filtAndTranslate(ArrayList<ArrayList<WordWithTextPositions>> textLayout){
		ArrayList<ArrayList<WordWithTextPositions>> filteredPosition = new ArrayList<ArrayList<WordWithTextPositions>>();
		for(ArrayList<WordWithTextPositions> pagePosition: textLayout) {
			ArrayList<WordWithTextPositions> newPagePosition = new ArrayList<WordWithTextPositions>();
			for (WordWithTextPositions tp : pagePosition) {
				String text = tp.getText().trim();
				
				// 跳过数字
				if (text.length() == 0 || StringUtils.isNumeric(text)) {
					continue;
				}
				
				// 目标语言英文，原文中英文的部分不翻译
				if (tgtLang.equals(Global.EN) && StringHelper.isAscii(text)) {
					continue;
				}
				
				/*
				if (srcLang.equals(EN) && text.equals("Reference") && tp.getFont().getName().toLowerCase().contains("bold")) {
					filteredPosition.add(newPagePosition);
					return filteredPosition;
				}
				if (srcLang.equals(CN) && text.replace("\\s+","").equals("参考文献") && tp.getFont().getName().toLowerCase().contains("bold")) {
					filteredPosition.add(newPagePosition);
					return filteredPosition;
				}
				*/
				
				if (srcLang.equals(Global.EN) && text.split("\\s+").length <= 3) {
					;
				}
							
				//tp.setTrans(translate(tp));
				newPagePosition.add(tp);
				
			}
			filteredPosition.add(newPagePosition);
		}
		return filteredPosition;
	}
	
	// 需要针对文本内容做一些预处理，进行一些预判
	public String translate(WordWithTextPositions ta) {
		String answer = null;
		try {

			String[] sents = ta.getText().replace("•", "\n•").split("\n");
			StringBuilder sb = new StringBuilder();
			for (String s : sents) {
				s = s.trim();
				if (s.length() == 0)
					continue;
				String t = test.test(language, domain, s);
				if (s.charAt(0) == '•' && t.trim().length() > 0) {
					t = " •  " + t;
				}
				System.out.println("[Translation]:" + t);
				if (t.trim().length() > 0) {
					sb.append(t + "\n");
				}
			}
			answer = sb.toString().trim();
		} catch (Exception e) {
			e.printStackTrace();
			answer = "翻译出错啦！！！";
		}
		return answer;
	}

}
