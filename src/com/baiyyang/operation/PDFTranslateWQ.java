package com.baiyyang.operation;

import cn.ac.ia.cip.reader.LineText;
import cn.ac.ia.cip.reader.PDFTextLocationStripper;
import cn.ac.ia.cip.writer.PDFTranslationWriter;
import com.baiyyang.global.Global;
import com.baiyyang.server.test.Test;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PDFTranslateWQ {

    private Test test;
    private String language;
    private String srcLang;
    private String domain;

    public PDFTranslateWQ(String language, String domain, String docType) {
        this.domain = domain;
        this.language = language;

        String[] lang = language.split("2");
        assert(lang.length==2);
        srcLang = lang[0];

        test = new Test();
    }

    public void translate(Global global, String userAbsolutePath) throws IOException{

        String source = global.getReadPath();
        String target = global.getCreatePath();

        translateFile(source, target, this.test);
    }

    public void translateFile(String source, String target, Test test) throws IOException {

        final PDDocument document = PDDocument.load(new File(source));
        int num = document.getNumberOfPages();
        document.close();

        PDFTranslationWriter writer = new PDFTranslationWriter(source, target, test, this.language, this.domain);
        List<List<LineText>> allPageTextWithRectangles = new ArrayList<>();
        for (int i = 1; i <= num; ++i) {
            PDFTextLocationStripper stripper = new PDFTextLocationStripper(source, i, i, this.srcLang);
            List<LineText> textWithRectangles = stripper.getTextWithRectangle();
            allPageTextWithRectangles.add(textWithRectangles);
        }

        for (int i = 1; i <= num; ++i) {
            writer.drawTranslationWithWhiteBlock(allPageTextWithRectangles.get(i-1), i);
        }


        writer.close();
    }
}
