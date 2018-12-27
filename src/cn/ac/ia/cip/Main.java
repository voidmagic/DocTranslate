package cn.ac.ia.cip;

import com.baiyyang.operation.PDFTranslateWQ;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;


public class Main {

    public static void main(String[] args) throws IOException {
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
        java.util.logging.Logger.getLogger("org.apache.pdfbox").setLevel(java.util.logging.Level.OFF);

        File[] files = new File("example/38").listFiles();
        Arrays.sort(files);
        File file = files[2];
        String source = "example/38/" + file.getName();
        System.out.println(source);
        String target = "example/tmp.pdf";
        String language = "EN2CN";
        process(source, target, language);
    }

    private static void process(String source, String target, String language) throws IOException {


        PDFTranslateWQ pdfTranslateWQ = new PDFTranslateWQ(language, "", "", new FakeTest());

        pdfTranslateWQ.translateFile(source, target);
    }

}
