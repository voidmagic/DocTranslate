package com.baiyyang.operation;

import cn.ac.ia.cip.PdfTextProcess;
import cn.ac.ia.cip.reader.LineText;
import cn.ac.ia.cip.reader.PDFTextLocationStripper;
import cn.ac.ia.cip.writer.PDFTranslationWriter;
import com.baiyyang.global.Global;
import com.baiyyang.server.test.Test;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
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

    static {
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.ERROR);
    }

    public PDFTranslateWQ(String language, String domain, String docType) {
        this.domain = domain;
        this.language = language;

        String[] lang = language.split("2");
        assert(lang.length==2);
        srcLang = lang[0];

        test = new Test();
    }

    public PDFTranslateWQ(String language, String domain, String docType, Test test) {
        this.domain = domain;
        this.language = language;

        String[] lang = language.split("2");
        assert(lang.length==2);
        srcLang = lang[0];

        this.test = test;
    }

    public void translate(Global global, String userAbsolutePath) throws IOException{

        String source = global.getReadPath();
        String target = global.getCreatePath();

        translateFile(source, target);
    }

    public void translateFile(String source, String target) throws IOException {

        final PDDocument document = PDDocument.load(new File(source));
        int num = document.getNumberOfPages();
        document.close();

        List<List<LineText>> allPageTextWithRectangles = new ArrayList<>();
        for (int i = 1; i <= num; ++i) {
            PDFTextLocationStripper stripper = new PDFTextLocationStripper(source, i, i, this.srcLang);
            List<LineText> textWithRectangles = stripper.getTextWithRectangle();
            allPageTextWithRectangles.add(textWithRectangles);
        }

        String tmp = "tmp.pdf";
        PdfTextProcess pdfTextProcess = new PdfTextProcess();
        pdfTextProcess.removePDFText(source, tmp);


        PDFTranslationWriter writer = new PDFTranslationWriter(tmp, target, test, this.language, this.domain);
        for (int i = 1; i <= num; ++i) {
            writer.drawTranslation(allPageTextWithRectangles.get(i-1), i);
        }

        writer.close();

        File file = new File(tmp);
        boolean result = file.delete();

    }
}
