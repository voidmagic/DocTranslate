package com.baiyyang.savepdf;

import java.util.ArrayList;
import java.util.List;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
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

/**
 * @ClassName: SavePDF
 * @Description: TODO 生成pdf类
 * @author baiyyang@163.com
 * @date 2017年5月8日 上午10:57:20
 * 
 */
public class SavePDF {

	/**
	 * @Fields document : iText中的Document
	 */

	/**
	 * @Fields savePath : TODO 生成的pdf保存的路径
	 */
	private String savePath;

	/**
	 * @Fields myImage : TODO 判断图像的大小类
	 */
	//private MyImage myImage = new MyImage();

	/**
	 * @Title: exportPdf @Description: TODO 生成pdf @param @param
	 *         textContent @param @param pageImages @return void @throws
	 */
	/*
	public void exportPdf(List<List<String>> textContent, List<PageImages> pageImages) {

		try {
			PdfWriter writer = new PdfWriter(savePath);
			PdfDocument document = new PdfDocument(writer);
			try {
				this.setFooter(writer);
			} catch (IOException e) {
				e.printStackTrace();
			}


			// String[] texts = (String[]) textContent.toArray(new
			// String[textContent.size()]);
			PageImages[] images = (PageImages[]) pageImages.toArray(new PageImages[pageImages.size()]);

			for (int i = 0; i < textContent.size(); i++) {
				List<String> pageTexts = textContent.get(i);

				for (String text : pageTexts) {
					addText("          " + text);
					addText(" ");
				}

				int count = 0;
				for (String image : images[i].getImagesPath()) {
					myImage.dealImage(image);
					int width = myImage.getWidth();
					int heigth = myImage.getHeight();
					if (width >= 300 && heigth >= 300) {
						addImage(image, width, heigth);
						count++;
						if (count >= 4) {
							break;
						}
					}
				}
				document.addNewPage();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} finally {
			if (document != null) {
				// 第五步：关闭文档。
				document.close();
			}
		}

	}
	*/

	public void createpdf(String src, List<List<String>> textContent, List<List<String>> readContent,
			List<List<Rectangle>> locations) {
		// step 1
		int threshold = 20;
		
		
		try {
			// step 3
			// document = new PdfDocument(reader);
			// step 4
			PdfDocument stamper = new PdfDocument(new PdfReader(src), new PdfWriter(savePath));
			String CFONT = "C:/windows/fonts/msyh.ttf";
			PdfFont cfont = PdfFontFactory.createFont(CFONT, PdfEncodings.IDENTITY_H, true);
			//PdfFont cfont = PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H", false);
			
			
			for (int page = 0; page < textContent.size(); page++) {
				List<PdfCleanUpLocation> cleanUpLocations = new ArrayList<PdfCleanUpLocation>();
				
				List<Rectangle> rectangles = locations.get(page);
				List<String> Content = readContent.get(page);
				List<String> pageContent = textContent.get(page);
				
				List<Rectangle> new_rec = new ArrayList<Rectangle>();
				List<String> ftext = new ArrayList<String>();
				List<String> etext = new ArrayList<String>();
				for (int la = 0; la < rectangles.size(); la++) {
					if (Content.get(la).length() > threshold) {
						new_rec.add(rectangles.get(la));
						ftext.add(Content.get(la));
						etext.add(pageContent.get(la));
						cleanUpLocations.add(new PdfCleanUpLocation(page + 1, rectangles.get(la), DeviceRgb.WHITE));	
					}
				}
				//System.out.println(page);
				PdfCleanUpTool cleaner = new PdfCleanUpTool(stamper, cleanUpLocations);
				cleaner.cleanUp();
				
				PdfPage pdfPage = stamper.getPage(page+1);
				PdfCanvas pdfCanvas = new PdfCanvas(pdfPage);
				pdfCanvas.setFontAndSize(cfont, 12);
				for (int lb = 0; lb < new_rec.size(); lb++) {
					Rectangle rec = new_rec.get(lb);
					//pdfCanvas.rectangle(rec);
					//pdfCanvas.stroke();
					
					Canvas canvas = new Canvas(pdfCanvas, stamper, rec);
					Text text = new Text(etext.get(lb)).setFont(cfont).setFontSize(10);
					Paragraph p = new Paragraph().add(text);
					p.setMarginTop(0);
					p.setFirstLineIndent(16);
					p.setMultipliedLeading(1.05f);
					canvas.add(p);
					canvas.close();
				}
				//PdfContentByte cb = stamper.getOverContent(pages + 1);

				//List<Rectangle> pagerectangles = locations.get(page);
				//List<String> pageContent = textContent.get(page);
				
				
				//List<String> readtexts = readContent.get(page);
				/*
				for (int lc = 0; lc < rectangles.size(); lc++) {
					if (readtexts.get(lc).length() > threshold) {
						//ColumnText text = new ColumnText(cb);
						
						//text.setSimpleColumn(rectangles.get(lc));
						// 设置字体大小格式
						PdfFont bfChinese = PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H", false);
						//PdfFont headFont = new PdfFont(bfChinese, 10);

						Paragraph p = new Paragraph(pageContent.get(lc)).setFont( bfChinese).setFontSize(10);
						text.addElement(p);
						text.go();
						//System.out.println(p.toString());
						//System.out.println("--------------------------------");
					}
				}*/
			}

			stamper.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();;
			// e.printStackTrace();
		} 
	}

	/*
	public void createpdf(String src, List<List<String>> textContent, List<List<Rectangle>> locations)
			throws IOException, DocumentException {
		// step 1
		PdfDocument document = new PdfDocument();

		PdfReader reader = new PdfReader(src);
		// step 3
		document.open();
		// step 4
		PdfDocument stamper = new PdfDocument(reader, new PdfWriter(savePath));

		List<PdfCleanUpLocation> cleanUpLocations = new ArrayList<PdfCleanUpLocation>();
		for (int page = 0; page < textContent.size(); page++) {
			List<Rectangle> rectangles = locations.get(page);
			for (int la = 0; la < rectangles.size(); la++) {
				cleanUpLocations.add(new PdfCleanUpLocation(page + 1, rectangles.get(la), BaseColor.WHITE));
			}
			PdfCleanUpProcessor cleaner = new PdfCleanUpProcessor(cleanUpLocations, stamper);
			cleaner.cleanUp();
		}

		for (int pages = 0; pages < textContent.size(); pages++) {

			PdfContentByte cb = stamper.getOverContent(pages + 1);

			List<Rectangle> pagerectangles = locations.get(pages);
			List<String> pageContent = textContent.get(pages);
			for (int lc = 0; lc < pagerectangles.size(); lc++) {
				ColumnText text = new ColumnText(cb);
				text.setSimpleColumn(pagerectangles.get(lc));
				// 设置字体大小格式
				BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
				PdfFont headFont = new PdfFont(bfChinese, 10, PdfFont.NORMAL);

				Paragraph p = new Paragraph(pageContent.get(lc), headFont);
				text.addElement(p);
				text.go();
				//System.out.println(p.toString());
				//System.out.println("--------------------------------");
			}
		}
		stamper.close();
		reader.close();
		document.close();
	}

	public void savePdf(List<List<String>> textContent, List<PageImages> pageImages, List<List<Rectangle>> locations)
			throws DocumentException, IOException {
		// step 1
		PdfDocument document = new PdfDocument();
		//System.out.println(document.getPageSize());
		// step 2
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(savePath));

		// step 3
		document.open();
		// step 4
		PdfFont font2 = new PdfFont(PdfFont.FontFamily.TIMES_ROMAN, 8);

		for (int page = 0; page < textContent.size(); page++) {

			PdfContentByte cb = writer.getDirectContent();

			List<Rectangle> pagerectangles = locations.get(page);
			List<String> pageContent = textContent.get(page);
			for (int lc = 0; lc < pagerectangles.size(); lc++) {
				ColumnText text = new ColumnText(cb);
				text.setSimpleColumn(pagerectangles.get(lc));
				// 设置字体大小格式
				BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
				PdfFont headFont = new PdfFont(bfChinese, 10, PdfFont.NORMAL);

				Paragraph p = new Paragraph(pageContent.get(lc), headFont);
				text.addElement(p);
				text.go();
				//System.out.println(p.toString());
				//System.out.println("--------------------------------");
			}
			document.newPage();
		}

		document.close();
	}
*/
	/**
	 * @Title: addText @Description: TODO 添加文本 @param @param textContent @return
	 *         void @throws
	 */
	/*
	public void addText(String textContent) {

		// 设置字体大小格式
		PdfFont bfChinese;
		try {
			// 设置中文字体
			bfChinese =  PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H", false);
			Paragraph p = new Paragraph(textContent).setFont(bfChinese);

			// String page = "第" + String.valueOf(document.getPageNumber()) +
			// "页";

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}*/

	/**
	 * @Title: addImage @Description: TODO 添加单个图片 @param @param
	 *         imageContent @param @param width @param @param height @return
	 *         void @throws
	 */
	/*
	public void addImage(String imageContent, int width, int height) {
		Image img;

		try {
			img = Image.getInstance(imageContent);
			img.setAlignment(Image.LEFT);
			// 设置宽度和高度
			img.scaleAbsolute(300, 150);

			document.add(img);

		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
*/
	/**
	 * @Title: setFooter @Description: TODO 设置页脚 @param @param
	 *         writer @param @throws DocumentException @param @throws
	 *         IOException @return void @throws
	 */
	/*
	private void setFooter(PdfWriter writer) throws DocumentException, IOException {
		// HeaderFooter headerFooter = new HeaderFooter(this);
		// 更改事件，瞬间变身 第几页/共几页 模式。
		PDFPages headerFooter = new PDFPages();// 就是上面那个类
		writer.setBoxSize("art", PageSize.A4);
		writer.setPageEvent(headerFooter);
	}

	public PdfDocument getDocument() {
		return document;
	}

	public void setDocument(PdfDocument document) {
		this.document = document;
	}
*/
	public String getSavePath() {
		return savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	/*
	public class RedBorder extends PdfPageEventHelper {
		protected Rectangle[] rectangles;

		public RedBorder(List<Rectangle> RECTANGLES) {
			rectangles = new Rectangle[RECTANGLES.size()];
			for (int i = 0; i < RECTANGLES.size(); i++) {
				rectangles[i] = new Rectangle(RECTANGLES.get(i));
				rectangles[i].setBorder(Rectangle.BOX);
				rectangles[i].setBorderWidth(1);
				rectangles[i].setBorderColor(BaseColor.RED);
			}
		}

		@Override
		public void onEndPage(PdfWriter writer, PdfDocument document) {
			PdfContentByte canvas = writer.getDirectContent();
			for (Rectangle rectangle : rectangles) {
				canvas.rectangle(rectangle);
			}
		}
	}
	*/
}
