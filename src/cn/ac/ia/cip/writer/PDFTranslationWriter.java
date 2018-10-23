package cn.ac.ia.cip.writer;

import cn.ac.ia.cip.reader.LineText;
import com.baiyyang.server.test.Test;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;

import java.io.IOException;
import java.util.List;

public class PDFTranslationWriter {

    private PdfDocument pdfDocument;
    private Test test;
    private String language;
    private String domain;
    private PdfFont font;

    public PDFTranslationWriter(String source, String target, Test test, String language, String domain) throws IOException {
        pdfDocument = new PdfDocument(new PdfReader(source), new PdfWriter(target));

        this.test = test;
        this.language = language;
        this.domain = domain;

        if (this.language.matches(".*?CN")) {
            this.font = PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H", true);
        } else if (this.language.matches(".*?EN")) {
            this.font = PdfFontFactory.createFont();
        } else if (this.language.matches(".*?JP")) {
            // todo japanese font
            this.font = PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H", true);
        }

        this.font = PdfFontFactory.createFont();
    }

    public void drawWhiteBlock(List<LineText> textWithRectangles, int pageNumber) {
        Color whiteColor = new DeviceRgb(255,255,255);
        PdfCanvas canvas = new PdfCanvas(pdfDocument.getPage(pageNumber));
        canvas.setFillColor(whiteColor);
        for (LineText textWithRectangle: textWithRectangles) {
            Rectangle rectangle = textWithRectangle.getRectangle();
            canvas.rectangle(rectangle);
        }
        canvas.fill();
    }

    public void drawBlackBlock(List<LineText> textWithRectangles, int pageNumber) {
        Color blackColor = new DeviceRgb(0,0,0);
        PdfCanvas canvas = new PdfCanvas(pdfDocument.getPage(pageNumber));
        canvas.setStrokeColor(blackColor);
        for (LineText textWithRectangle: textWithRectangles) {
            Rectangle rectangle = textWithRectangle.getRectangle();
            canvas.rectangle(rectangle);
        }
        canvas.stroke();
    }

    public void drawTranslationWithWhiteBlock(List<LineText> textWithRectangles, int pageNumber) {
        Color whiteColor = new DeviceRgb(255,255,255);
        Color blackColor = new DeviceRgb(0,0,0);
        PdfCanvas pdfCanvas = new PdfCanvas(pdfDocument.getPage(pageNumber));

        for (LineText textWithRectangle: textWithRectangles) {

            pdfCanvas.setFillColor(whiteColor);
            Rectangle rectangle = textWithRectangle.getRectangle();
            rectangle.setHeight(rectangle.getHeight() + 3);
            pdfCanvas.rectangle(rectangle);
            pdfCanvas.fill();

            Text translationText = getTranslationText(this.language, this.domain, textWithRectangle.getText());

            float fontSize = calculateFontSize(rectangle, translationText.getText());
            translationText = translationText.setFontSize(fontSize);

            pdfCanvas.setFillColor(blackColor);
            Paragraph p = new Paragraph(translationText).setMultipliedLeading(1);
            new Canvas(pdfCanvas, pdfDocument, textWithRectangle.getRectangle()).add(p).close();
        }
    }

    public void drawTranslation(List<LineText> textWithRectangles, int pageNumber) {
//        Color blackColor = new DeviceRgb(0,0,0);
        PdfCanvas pdfCanvas = new PdfCanvas(pdfDocument.getPage(pageNumber));

        for (LineText textWithRectangle: textWithRectangles) {

            Rectangle rectangle = textWithRectangle.getRectangle();

            Text translationText = getTranslationText(this.language, this.domain, textWithRectangle.getText());

            float fontSize = calculateFontSize(rectangle, translationText.getText());
            translationText = translationText.setFontSize(fontSize);

            int rgb = textWithRectangle.getTextColor();
            Color color = new DeviceRgb(rgb >> 16 & 0xff,rgb >> 8 & 0xff, rgb & 0xff);
            pdfCanvas.setFillColor(color);
            Paragraph p = new Paragraph(translationText).setMultipliedLeading(1);
            new Canvas(pdfCanvas, pdfDocument, textWithRectangle.getRectangle()).add(p).close();
        }
    }

    private Text getTranslationText(String lang, String domain, String source) {
        String result;
        try {
            result = this.test.test(lang, domain, source);
        } catch (Exception e) {
            result = "翻译出错！";
//            e.printStackTrace();
        }

        return new Text(result).setFont(this.font);
    }

    private float calculateFontSize(Rectangle rectangle, String text) {

        if (this.language.matches(".*?CN") || this.language.matches(".*?JP")) {
            return calculateFontSizeWithScale(rectangle, text, (float) 1.4);
        } else {
            return calculateFontSizeWithScale(rectangle, text, (float) 1.8);
        }
    }

    private float calculateFontSizeWithScale(Rectangle rectangle, String text, float lineScale) {
        // 计算中文日语等方块字的大小
        if (text.length() < 1) text = text + ".";
        float unitWidth = this.font.getWidth(text, 5);
        float unitHeight = this.font.getAscent(text, 5) - this.font.getDescent(text, 5);

        if (unitHeight < 1 && unitWidth < 1) unitHeight = unitWidth = 1;
        else if (unitHeight < 1 || unitWidth < 1) unitHeight = unitWidth = Math.max(unitHeight, unitWidth);

        float unitArea = unitHeight * unitWidth;
        float totalArea = rectangle.getHeight() * rectangle.getWidth();
        float scale = totalArea / unitArea;
        float actualFontSize = (float) (Math.sqrt(scale) * 5);
        while (true) {
            float actualWidth = this.font.getWidth(text, actualFontSize);
            float actualHeight = (this.font.getAscent(text, actualFontSize) - this.font.getDescent(text, actualFontSize)) * lineScale;
            int lineCapacity = (int) (rectangle.getHeight() / actualHeight);

            if (lineCapacity * rectangle.getWidth() > actualWidth)
                break;

            actualFontSize -= 1;
        }

        return 10;
    }


    public void close() {
        try {
            pdfDocument.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
