/**
 * 
 */
package cn.ac.istic.lkt.mt.doctrans.pdflayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.crypto.BadPasswordException;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

/**
 * @author Chongde SHI
 *
 */
public class PDFTextLayout extends PDFTextStripper{
	public static final String EN = "EN";
	public static final String CN = "CN";
	public static final String JP = "JP";
	public static final String ARTICLE = "ARTICLE";
	public static final String REPORT = "REPORT";

	private ArrayList<ArrayList<WordWithTextPositions>> newPageLines = null;

	public PDFTextLayout() throws IOException {
		super();

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String src = "D:/e5.pdf";
		String tgt = "D:/e5.copy.pdf";
		try {
			PDFTextLayout tl = new PDFTextLayout();
			tl.process(src, tgt, EN, ARTICLE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param src 输入文件地址
	 * @param textLayout 排版信息
	 * @return  
	 */
	public ArrayList<ArrayList<WordWithTextPositions>> getTextLayout(String src, String language, String docType) throws IOException{
		process(src, language, docType);
		return newPageLines;
	}

	/*
	 * 
	 */
	public int process(String src, String language, String docType) throws IOException{
			// pdfbox
			PDFParser pdfParser = new PDFParser(new RandomAccessFile(new File(src), "r"));
			pdfParser.parse();
			PDDocument pdDocument = new PDDocument(pdfParser.getDocument());
			if (pdDocument.isEncrypted()) {
				System.out.println("Document is encrypted.");
				pdDocument.close();
				return -1;
			}
			// tl.setSortByPosition(true);
			StringWriter sw = new StringWriter();
			writeText(pdDocument, sw);
			pdDocument.close();
			organize();
			// System.out.println(sw.toString());
			return 1;
		
	}

	/*
	 * 生成一个新的文档，给每个合并的分块添加红色外框，主要是debug用
	 */
	public void process(String src, String tgt, String language, String docType) {
		try {
			process(src, language, docType);
			// itext
			PdfReader reader = new PdfReader(src);
			PdfWriter writer = new PdfWriter(tgt);
			PdfDocument pdfDoc = new PdfDocument(reader, writer);

			for (ArrayList<WordWithTextPositions> lines : newPageLines) {
				for (WordWithTextPositions wtp : lines) {
					System.out.println(wtp);
				}
			}
			//drawLineBlocks(pdfDoc, pageLines);
			drawLineBlocks(pdfDoc, newPageLines);
			pdfDoc.close();
		} catch (IOException e) {
				e.printStackTrace();
		} 
	}

	public void drawLineBlocks(PdfDocument tgtDoc, ArrayList<ArrayList<WordWithTextPositions>> pageLines)
			throws IOException {
		String CFONT = "C:/windows/fonts/msyh.ttf";
		int fontsize = 12;
		PdfFont cfont = PdfFontFactory.createFont(CFONT, PdfEncodings.IDENTITY_H, true);
		int pageNum = 1;
		for (ArrayList<WordWithTextPositions> lines : pageLines) {
			PdfPage page = tgtDoc.getPage(pageNum);
			Rectangle r = page.getPageSize();
			float pageHeight = r.getHeight();
			PdfCanvas pdfCanvas = new PdfCanvas(page);
			pdfCanvas.setFontAndSize(cfont, fontsize);
			for (WordWithTextPositions wtp : lines) {
				// System.out.println(wtp.text);
				float x1 = wtp.getXstart();
				float y1 = pageHeight - wtp.getYstart() - wtp.getFontSizeInPt() * 0.2f;
				// float y1 = wtp.getYstart();
				float width = wtp.Width;
				float height = wtp.Height;

				pdfCanvas.setLineWidth(0.5f);
				pdfCanvas.setStrokeColor(ColorConstants.RED);
				Rectangle rectangle = new Rectangle(x1, y1, width, height);
				pdfCanvas.rectangle(rectangle);
				pdfCanvas.stroke();
			}
			pageNum += 1;
		}
	}

	protected void organize() {
		HashMap<Float, Integer> spaceFreq = new HashMap<Float, Integer>();
		HashMap<Float, Integer> continueSpaceFreq = new HashMap<Float, Integer>();
		WordWithTextPositions previous = null;
		// 先遍历和记录所有的行距；
		float previousSpace = 0f; // 前一行的行距
		for (ArrayList<WordWithTextPositions> page : pageLines) {
			for (WordWithTextPositions line : page) {
				float space = 0f;
				if (previous != null) {
					space = roundVal(line.getYstart() - previous.getYstart());
					if (spaceFreq.containsKey(space)) {
						spaceFreq.replace(space, spaceFreq.get(space) + 1);
					} else {
						spaceFreq.put(space, 1);
					}
					
					if (space == previousSpace) {
						if (continueSpaceFreq.containsKey(space)) {
							continueSpaceFreq.replace(space, continueSpaceFreq.get(space) + 1);
						} else {
							continueSpaceFreq.put(space, 1);
						}
					}
				}
				previous = line;
				previousSpace = space;
			}
		}
		
		HashSet<Float> validSpaceSet = new HashSet<Float>();
		List<Map.Entry<Float, Integer>> validSpaceList = new ArrayList<Map.Entry<Float, Integer>>(continueSpaceFreq.entrySet());
		for (Map.Entry<Float, Integer> entry : validSpaceList) {
			if (entry.getValue()>2 && entry.getKey() > 0) {
				validSpaceSet.add(entry.getKey());
			}
		}
		
		// 我们猜测行距出现次数最多的应该是正文
		List<Map.Entry<Float, Integer>> list = new ArrayList<Map.Entry<Float, Integer>>(spaceFreq.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Float, Integer>>() {
			// 降序排序
			public int compare(Entry<Float, Integer> o1, Entry<Float, Integer> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		for (Map.Entry<Float, Integer> entry : list) {
			// System.out.println(entry.getKey()+" "+ entry.getValue());
		}
		
		if (list.size() == 0) {
			return;
		}
		float basicSpace = list.get(0).getKey();

		// 再次遍历全文，把正文行进行合并；
		newPageLines = new ArrayList<ArrayList<WordWithTextPositions>>();
		previous = null;
		float basicSentLength = 0f;
		ArrayList<WordWithTextPositions> temp = new ArrayList<WordWithTextPositions>();
		for (ArrayList<WordWithTextPositions> page : pageLines) {
			ArrayList<WordWithTextPositions> newpage = new ArrayList<WordWithTextPositions>();

			for (int i = 0; i< page.size(); i++) {
				WordWithTextPositions line = page.get(i);
				if (previous == null) {
					//tempblock = new WordWithTextPositions(line.text, line.textPositions);
					previous = line;
					temp.add(line);
				} else {
					
					/*
					// 查看有没有同一行的，跳过同一行有两个语块的，一般是表格内的词汇
					while(i < page.size()-1) {
						WordWithTextPositions nextline = page.get(i+1);
						if (roundVal(line.getYstart()) == roundVal(nextline.getYstart())) {
							newpage.add(line);
							i++;	
						}else {
							break;
						}
					}*/
					
					float space = roundVal(line.getYstart() - previous.getYstart());
					if (space == basicSpace) { // 记录正文column的长度
						basicSentLength = basicSentLength > line.getWidth() ? basicSentLength : line.getWidth();
					}
					//PDFont fontp = previous.getTextPositions().get(previous.getTextPositions().size()-1).getFont();
					//PDFont fontc = line.getTextPositions().get(0).getFont();
					PDFont fontp = previous.getFont();
					PDFont fontc = line.getFont();
					float fontsizep = previous.getFontSizeInPt();
					float fontsizec = line.getFontSizeInPt();
					String currColor = line.getColorInfo();
					String previousColor = previous.getColorInfo();
					//System.out.println(fontp.getName()+" "+fontc.getName() +" ");
					// fontp.getFontDescriptor());
					//if (fontsizep==fontsizec && space/fontsizec < 2 && space > 0) {
					//fontp.getName().equals(fontc.getName())
					//if (space > fontsizec && (fontp.getName().equals(fontc.getName())|| fontp.getFontDescriptor().isItalic() ||
					//    fontc.getFontDescriptor().isItalic() )&& fontsizep == fontsizec && space / fontsizec < 1.8) {
					if ( (validSpaceSet.contains(space) ||  Math.abs(space-basicSpace) < 3) && ( space < fontsizep*2) &&           // 上下行间距合适
							(fontp.getName().equals(fontc.getName())|| line.fontFreq.size()>1 ||  previous.fontFreq.size()>1 ) &&  // 相同字体或者混合字体
							fontsizep == fontsizec &&                                                                              // 字体大小相同
							( Math.abs(line.getXstart()-previous.getXstart()) < 20 ||                                              // 左端对齐差的不多
									Math.abs(line.getXstart()+line.getWidth() -previous.getXstart()-previous.getWidth()) < 5  ||   // 右端对齐差的不多
							  (line.text.charAt(0) <= 122 && line.text.charAt(0) >=97) ) &&                                        //新的一行首字母小写
					        line.text.charAt(0) != '•' && 
					        currColor.equals(previousColor)  )                                     // 文字颜色
					{ 
						temp.add(line);
					} else {
						if (temp.size() > 0) {
							WordWithTextPositions block = combineLines(temp);
							if (block.getText().trim().length()>0) {
								newpage.add(block);
							}
						}
						temp = new ArrayList<WordWithTextPositions>();
						temp.add(line);
					}
					previous = line;
				}
			}
			if (temp.size() > 0) {
				WordWithTextPositions block = combineLines(temp);
				if (block.getText().trim().length()>0) {
					newpage.add(block);
				}
			}

			temp = new ArrayList<WordWithTextPositions>();
			newPageLines.add(newpage);
		}

	}

	private WordWithTextPositions combineLines(ArrayList<WordWithTextPositions> lines) {
		float maxWidth = 0f;
		WordWithTextPositions temp = null;
		for(WordWithTextPositions line : lines) {
			maxWidth = maxWidth > line.Width ? maxWidth : line.Width;
		}
		for(WordWithTextPositions line : lines) {
			
			if( temp == null) {
				temp = new WordWithTextPositions(line.text, line.textPositions);
				
			}else {
				temp.appendV(line, maxWidth);
			}
		}
		return temp;
	}
	
	protected float roundVal(Float yVal) {
		DecimalFormat rounded = new DecimalFormat("0.0'0'");
		float yValDub = new Float(rounded.format(yVal));
		return yValDub;
	}
}
