package cn.ac.ia.cip;

import com.baiyyang.operation.PDFTranslateWQ;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;


public class Main {

    public static void main(String[] args) throws IOException {
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");

        File[] files = new File("example/38").listFiles();
        Arrays.sort(files);


//        String source = "example/38/" + "英文-分栏式科技论文1-pdf1.4 .pdf";
//        System.out.println(source);
//        String target = "example/res/" + "英文-分栏式科技论文1-pdf1.4 .pdf";
//        String language;
//        if ("英文-分栏式科技论文1-pdf1.4".startsWith("中文")) {
//            language = "EN2CN";
//        } else {
//            language = "EN2EN";
//        }
//        process(source, target, language);
//
        for (int i = 1; i < files.length; ++i) {
            File file = files[i];
            String source = "example/38/" + file.getName();
            System.out.println(source);
            String target = "example/res/" + file.getName();
            String language;
            if (file.getName().startsWith("中文")) {
                language = "EN2CN";
            } else {
                language = "EN2EN";
            }
            process(source, target, language);
        }
    }

    private static void process(String source, String target, String language) throws IOException {


        PDFTranslateWQ pdfTranslateWQ = new PDFTranslateWQ(language, "", "", new FakeTest());

        pdfTranslateWQ.translateFile(source, target);
    }

}
