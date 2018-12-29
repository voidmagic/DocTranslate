package cn.ac.ia.cip.writer;

import cn.ac.ia.cip.reader.LineText;
import com.baiyyang.server.test.Test;
import com.itextpdf.io.font.PdfEncodings;
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

    private Color whiteColor;
    private Color blackColor;

    public PDFTranslationWriter(String source, String target, Test test, String language, String domain) throws IOException {
        pdfDocument = new PdfDocument(new PdfReader(source), new PdfWriter(target));
        this.test = test;
        this.language = language;
        this.domain = domain;

        if (this.language.matches(".*?CN")) {
            this.font = PdfFontFactory.createFont("resources/simsun.ttc,1", PdfEncodings.IDENTITY_H, false);
        } else if (this.language.matches(".*?EN")) {
            this.font = PdfFontFactory.createFont();
        } else if (this.language.matches(".*?JP")) {
            // todo japanese font
            this.font = PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H", true);
        }

        this.whiteColor = new DeviceRgb(255,255,255);
        this.blackColor = new DeviceRgb(0,0,0);
    }

    public void drawTranslationWithWhiteBlock(List<LineText> textWithRectangles, int pageNumber) {
        PdfCanvas pdfCanvas = new PdfCanvas(pdfDocument.getPage(pageNumber));
        pdfCanvas.setFillColor(this.whiteColor);
        for (LineText textWithRectangle: textWithRectangles) {
            Rectangle rectangle = textWithRectangle.getRectangle();
            rectangle.setHeight(rectangle.getHeight() + 3);
            pdfCanvas.rectangle(rectangle);
        }
        pdfCanvas.fill();

        for (LineText textWithRectangle: textWithRectangles) {
            drawTextInRectangle(textWithRectangle, pdfCanvas, this.blackColor);
        }
    }

    public void drawTranslation(List<LineText> textWithRectangles, int pageNumber) {
        PdfCanvas pdfCanvas = new PdfCanvas(pdfDocument.getPage(pageNumber).setIgnorePageRotationForContent(true));
        for (LineText textWithRectangle: textWithRectangles) {
            drawTextInRectangle(textWithRectangle, pdfCanvas);
        }
    }

    public void drawTranslationWithBlackBox(List<LineText> textWithRectangles, int pageNumber) {
        PdfCanvas pdfCanvas = new PdfCanvas(pdfDocument.getPage(pageNumber));
        pdfCanvas.setStrokeColor(this.blackColor);
        for (LineText textWithRectangle: textWithRectangles) {
            drawTextInRectangle(textWithRectangle, pdfCanvas);
            pdfCanvas.rectangle(textWithRectangle.getRectangle());
        }
        pdfCanvas.stroke();
    }

    private void drawTextInRectangle(LineText textWithRectangle, PdfCanvas pdfCanvas) {
        int rgb = textWithRectangle.getTextColor();
        Color color = new DeviceRgb(rgb >> 16 & 0xff,rgb >> 8 & 0xff, rgb & 0xff);
        drawTextInRectangle(textWithRectangle, pdfCanvas, color);
    }

    private void drawTextInRectangle(LineText textWithRectangle, PdfCanvas pdfCanvas, Color color) {
        Rectangle rectangle = textWithRectangle.getRectangle();
        Text translationText = getTranslationText(this.language, this.domain, textWithRectangle.getText());
        float fontSize = calculateFontSize(rectangle, translationText.getText());
        translationText = translationText.setFontSize(fontSize);
        pdfCanvas.setFillColor(color);
        Paragraph p = new Paragraph(translationText).setMultipliedLeading(1);
        if (textWithRectangle.getDirection() != 0) {
            p.setRotationAngle(textWithRectangle.getDirection() / 180 * Math.PI);
            float x = pdfDocument.getPage(1).getPageSize().getHeight() - rectangle.getX();
            float y = pdfDocument.getPage(1).getPageSize().getHeight() - rectangle.getY();
            rectangle.setX(y - fontSize * 2);
            rectangle.setY(x - fontSize * 2);
        }
        new Canvas(pdfCanvas, pdfDocument, rectangle).add(p).close();
    }

    private Text getTranslationText(String lang, String domain, String source) {
        String result;
        try {
            result = this.test.test(lang, domain, source);
        } catch (Exception e) {
            result = "翻译出错！";
        }

        return new Text(result).setFont(this.font);
    }

    private float calculateFontSize(Rectangle rectangle, String text) {
        return calculateFontSizeWithScale(rectangle, text);
    }

    private float calculateFontSizeWithScale(Rectangle rectangle, String text) {
        // 计算字体大小
        float scale = (float) 1.5;
        // empty content
        if (text.length() < 1) return 1;

        // 边界
        float maxHeight = (rectangle.getHeight() - 5);
        float maxWidth = (float) (rectangle.getWidth() * 0.9);

        float actualFontSize = 50;
        while (true) {
            float actualWidth = this.font.getWidth(text, actualFontSize);
            int lineCapacity = (int) (maxHeight / actualFontSize / scale);
            if (lineCapacity * maxWidth > actualWidth)
                break;

            actualFontSize -= 0.1;
        }

        return actualFontSize;
    }


    public void close() {
        try {
            pdfDocument.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
