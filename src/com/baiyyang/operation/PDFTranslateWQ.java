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

        String language = this.srcLang;

        String source = global.getReadPath();
        String target = global.getCreatePath();

        final PDDocument document = PDDocument.load(new File(source));
        int num = document.getNumberOfPages();
        document.close();

        PDFTranslationWriter writer = new PDFTranslationWriter(source, target, test, this.language, this.domain);

        for (int i = 1; i <= num; ++i) {
            PDFTextLocationStripper stripper = new PDFTextLocationStripper(source, i, i, language);
            List<LineText> textWithRectangles = stripper.getTextWithRectangle();
            writer.drawTranslationWithWhiteBlock(textWithRectangles, i);

        }

        writer.close();
    }
}
