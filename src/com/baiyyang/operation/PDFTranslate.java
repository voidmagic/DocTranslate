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
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;
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
			//String cFontFile = Global.WORKD+"\\resources\\msyh.ttf";
			String cFontFile = "C:/WINDOWS/Fonts/SIMYOU.TTF";    
			String cBoldFontFile = "C:/WINDOWS/Fonts/msyhbd.ttf";
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
		// 20180329 - try、catch部分代码实际上已经不再起作用，使用新的策略，直接在原文档之上贴上新的底色为白色的文字层，代码见finally部分
		try { 
			PdfDocument writeDoc = new PdfDocument(reader, writer);
			pdfDoc = writeDoc;
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
				//cleaner.cleanUp();
			}
			
		//}catch (com.itextpdf.io.IOException| java.nio.charset.UnsupportedCharsetException | javax.imageio.IIOException | java.lang.RuntimeException e ) { 
		}catch (java.lang.RuntimeException e ) { 
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
				float pageWidth = pdfPage.getPageSize().getWidth();
				PdfCanvas pdfCanvas = new PdfCanvas(pdfPage);
				for (int i = 0; i < filteredTextAreas.size(); i++) {
					WordWithTextPositions ta = filteredTextAreas.get(i);
					float srcFontsize = ta.getFontSizeInPt();
					Rectangle rec = getArea(ta, pageHeight);
					
					// 覆盖的时候有的下边界覆盖不住，稍作微调
					rec.setY(rec.getY()-1);
					rec.setHeight(rec.getHeight()+1);
					
					String srcFontName = ta.getFont().getName().toLowerCase();
					PdfFont tgtFont = cFont;
					
					if (srcFontName.contains("bold")) {
						tgtFont = cBoldFont;
					}
					
					String tr = translate(ta);
					float tgtAscent = tgtFont.getAscent(tr, srcFontsize);
					float tgtDescent = tgtFont.getDescent(tr, srcFontsize);
					float tgtFontSize = srcFontsize - 1 ;
					
					//System.out.println("SRC: "+ta.getText());
					//System.out.println("TGT: "+tr);
					
					pdfCanvas.setColor(ColorConstants.WHITE, true);
					pdfCanvas.setStrokeColor(ColorConstants.WHITE);
					pdfCanvas.setLineWidth(0.5f);
					if (drawBlock) {
						pdfCanvas.setStrokeColor(ColorConstants.RED);
					}
					pdfCanvas.setStrokeColor(ColorConstants.RED);
					pdfCanvas.rectangle(rec);
					
					pdfCanvas.setColor(ColorConstants.BLACK, true);
					
					
					Text text = new Text(tr);
					
					float textLength = tgtFont.getWidth(tr, tgtFontSize);
					float textHeight = tgtAscent + tgtDescent;
					float neededLines = (float)Math.ceil(textLength/rec.getWidth());
					float canvasLines = (float)Math.floor(rec.getHeight()/tgtFontSize);
					//System.err.println("[SRC] "+ta.getText()+" [textWidth] "+ textLength + " [LineWidth] "+ rec.getWidth());
					if (canvasLines == 1) {
						while(textLength > rec.getWidth() && rec.getHeight() < (textHeight+1) ) {
							tgtFontSize -= 1;
							textLength = tgtFont.getWidth(tr, tgtFontSize);
						}
					}else if( this.language.equals("CN2EN")) {
						while (neededLines > canvasLines+1 ) {
							tgtFontSize -= 1;
							textLength = tgtFont.getWidth(tr, tgtFontSize);
							neededLines = (float)Math.ceil(textLength/rec.getWidth());
							canvasLines = (float)Math.floor(rec.getHeight()/tgtFontSize);
						}
						
					}else  {
						tgtFontSize += 1;
						while (neededLines > canvasLines ) {
							tgtFontSize -= 1;
							textLength = tgtFont.getWidth(tr, tgtFontSize);
							neededLines = (float)Math.ceil(textLength/rec.getWidth());
							canvasLines = (float)Math.floor(rec.getHeight()/tgtFontSize);
						}
					}
					
					text.setFont(tgtFont).setFontSize(tgtFontSize);
					
					rec.setY(rec.getY()-tgtDescent);
					Canvas canvas = new Canvas(pdfCanvas, pdfDoc, rec);
					
					float centerPosition = rec.getX()+rec.getWidth()/2;
					
					
					Paragraph p = new Paragraph().add(text);
					if (Math.abs(centerPosition-pageWidth/2) < 30 && canvasLines < 2 && (rec.getWidth()< pageWidth/2 || ta.getFont().getName().toLowerCase().contains("bold"))) {
						p.setTextAlignment(TextAlignment.CENTER);
					}
					p.setMarginTop(0);
					//p.setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f));
					//p.setFirstLineIndent(10);
					
					p.setMultipliedLeading(1.0f);
					p.setBackgroundColor(ColorConstants.WHITE);
					canvas.setBackgroundColor(ColorConstants.WHITE);
					
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
				
				if (text.equals("•")) {
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
				//System.out.println("[Translation]:" + t);
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
