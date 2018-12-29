package cn.ac.ia.cip.utils;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;

import java.io.IOException;

public class FontTest {
    public static void main(String[] args) throws IOException {
        PdfDocument pdf = new PdfDocument(new PdfWriter("tmp1.pdf"));


        for (int i = 0; i < 10; i++) {
            PdfPage page = pdf.addNewPage();
            PdfCanvas pdfCanvas = new PdfCanvas(page);
            Rectangle rectangle = new Rectangle(36, 500, 500, 100);
            Text title = new Text("The");
            Paragraph p = new Paragraph().add(title);
            p.setRotationAngle(Math.PI / 8 * i);
            new Canvas(pdfCanvas, pdf, rectangle).add(p).close();
        }

        pdf.close();
    }
}
