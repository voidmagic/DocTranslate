package cn.ac.ia.cip.writer;

import cn.ac.ia.cip.reader.LineText;
import com.baiyyang.server.test.Test;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;

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
            this.font = PdfFontFactory.createFont("resources/simsun.ttc,1", PdfEncodings.IDENTITY_H, false);
        } else if (this.language.matches(".*?EN")) {
            this.font = PdfFontFactory.createFont();
        } else if (this.language.matches(".*?JP")) {
            // todo japanese font
            this.font = PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H", true);
        }

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
        PdfCanvas pdfCanvas = new PdfCanvas(pdfDocument.getPage(pageNumber).setIgnorePageRotationForContent(true));
        for (LineText textWithRectangle: textWithRectangles) {

            Rectangle rectangle = textWithRectangle.getRectangle();

            Text translationText = getTranslationText(this.language, this.domain, textWithRectangle.getText());

            float fontSize = calculateFontSize(rectangle, translationText.getText());
            translationText = translationText.setFontSize(fontSize);

            int rgb = textWithRectangle.getTextColor();
            Color color = new DeviceRgb(rgb >> 16 & 0xff,rgb >> 8 & 0xff, rgb & 0xff);
            pdfCanvas.setFillColor(color);
            Paragraph p = new Paragraph(translationText).setMultipliedLeading(1);

            new Canvas(pdfCanvas, pdfDocument, textWithRectangle.getRectangle())
                    .add(p)
                    .close();
        }
    }

    public void drawTranslationWithBlackBox(List<LineText> textWithRectangles, int pageNumber) {
        Color blackColor = new DeviceRgb(0,0,0);
        PdfCanvas pdfCanvas = new PdfCanvas(pdfDocument.getPage(pageNumber));
        pdfCanvas.setStrokeColor(blackColor);
        for (LineText textWithRectangle: textWithRectangles) {

            Rectangle rectangle = textWithRectangle.getRectangle();

            Text translationText = getTranslationText(this.language, this.domain, textWithRectangle.getText());

            float fontSize = calculateFontSize(rectangle, translationText.getText());
            translationText = translationText.setFontSize(fontSize);

            int rgb = textWithRectangle.getTextColor();
            Color color = new DeviceRgb(rgb >> 16 & 0xff,rgb >> 8 & 0xff, rgb & 0xff);
            pdfCanvas.setFillColor(color);
            Paragraph p = new Paragraph(translationText).setMultipliedLeading(1);
            new Canvas(pdfCanvas, pdfDocument, rectangle).add(p).close();
            pdfCanvas.rectangle(rectangle);
        }
        pdfCanvas.stroke();
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
        float scale;
        if (this.language.matches(".*?CN") || this.language.matches(".*?JP")) {
            scale = (float) 1.2;
        } else {
            scale = (float) 1.4;
        }

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
