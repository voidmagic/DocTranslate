package cn.ac.ia.cip;

import cn.ac.ia.cip.utils.PdfTextProcess;
import cn.ac.ia.cip.utils.Varifier;
import cn.ac.ia.cip.reader.LineText;
import cn.ac.ia.cip.reader.PDFTextLocationStripper;
import cn.ac.ia.cip.writer.PDFTranslationWriter;
import com.baiyyang.global.Global;
import com.baiyyang.server.test.Test;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PDFTranslateImpl {

    private Test test;
    private String language;
    private String srcLang;
    private String domain;

    public PDFTranslateImpl(String language, String domain, String docType) {
        this.domain = domain;
        this.language = language;

        String[] lang = language.split("2");
        assert(lang.length==2);
        srcLang = lang[0];

        test = new Test();
    }

    public PDFTranslateImpl(String language, String domain, String docType, Test test) {
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


        Varifier varifier = new Varifier();
        List<Integer> illegalPages = varifier.illegalPages(source);

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
            if (illegalPages.contains(i-1)) {
                writer.drawTranslationWithWhiteBlock(allPageTextWithRectangles.get(i - 1), i);
            } else {
                writer.drawTranslation(allPageTextWithRectangles.get(i - 1), i);
            }
        }

        writer.close();

        File file = new File(tmp);
        boolean result = file.delete();
    }
}