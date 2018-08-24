package cn.ac.ia.cip;

import com.baiyyang.operation.PDFTranslateWQ;

import java.io.IOException;


public class Main {public static void main(String[] args) throws IOException {
    System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");

    process();
}

    private static void process() throws IOException {
        String filename = "zhoulei";
        String language = "EN2CN";

        String source = "example/" + filename + ".pdf";
        String target = "example/" + filename + "-output.pdf";

        PDFTranslateWQ pdfTranslateWQ = new PDFTranslateWQ(language, "", "");

        pdfTranslateWQ.translateFile(source, target, new FakeTest());
    }

}
