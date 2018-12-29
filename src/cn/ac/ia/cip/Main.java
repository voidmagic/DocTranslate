package cn.ac.ia.cip;

import cn.ac.ia.cip.utils.FakeTest;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;


public class Main {

    public static void main(String[] args) throws IOException {
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");

        File[] files = new File("example/38").listFiles();
        Arrays.sort(files);

//
        String source = "example/38/" + "中文-ppt式图表文本2-pdf1.5.pdf";
        System.out.println(source);
        String target = "example/res/" + "中文-ppt式图表文本2-pdf1.5.pdf";
        String language;
        language = "CN2EN";
        process(source, target, language);

//        for (int i = 1; i < files.length; ++i) {
//            File file = files[i];
//            String source = "example/38/" + file.getName();
//            System.out.println(source);
//            String target = "example/res/" + file.getName();
//            String language;
//            if (file.getName().startsWith("英文")) {
//                language = "EN2CN";
//            } else {
//                language = "CN2EN";
//            }
//            process(source, target, language);
//        }
    }

    private static void process(String source, String target, String language) throws IOException {
        PDFTranslateImpl pdfTranslate = new PDFTranslateImpl(language, "", "", new FakeTest());
        pdfTranslate.translateFile(source, target);
    }

}
