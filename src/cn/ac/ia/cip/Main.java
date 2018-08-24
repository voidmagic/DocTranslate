package cn.ac.ia.cip;

import cn.ac.ia.cip.reader.LineText;
import cn.ac.ia.cip.reader.PDFTextLocationStripper;
import cn.ac.ia.cip.writer.PDFTranslationWriter;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class Main {public static void main(String[] args) throws IOException {
    System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");

    process();
}

    private static void process() throws IOException {
        String filename = "zhoulei";
        String language = PDFTextLocationStripper.EN;

        String source = "example/" + filename + ".pdf";
        String target = "example/" + filename + "-output.pdf";

        final PDDocument document = PDDocument.load(new File(source));
        int num = document.getNumberOfPages();
        document.close();

        PDFTranslationWriter writer = new PDFTranslationWriter(source, target, new FakeTest(), "EN2CN", "");

        for (int i = 1; i <= num; ++i) {
            PDFTextLocationStripper stripper = new PDFTextLocationStripper(source, i, i, language);
            List<LineText> textWithRectangles = stripper.getTextWithRectangle();
            writer.drawTranslationWithWhiteBlock(textWithRectangles, i);
            writer.drawBlackBlock(textWithRectangles, i);
        }
        writer.close();
    }
}
