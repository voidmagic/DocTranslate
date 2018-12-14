package cn.ac.ia.cip;

import com.baiyyang.operation.PDFTranslateWQ;

import java.io.File;
import java.io.IOException;


public class Main {

    public static void main(String[] args) throws IOException {
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
        java.util.logging.Logger.getLogger("org.apache.pdfbox").setLevel(java.util.logging.Level.OFF);

        String source = "example/38/" + "中文-ppt式图表文本1-pdf1.5.pdf";
        String target = "example/tmp.pdf";
        String language = "CN2EN";
        process(source, target, language);
    }

    private static void process(String source, String target, String language) throws IOException {


        PDFTranslateWQ pdfTranslateWQ = new PDFTranslateWQ(language, "", "", new FakeTest());

        pdfTranslateWQ.translateFile(source, target);
    }

}
