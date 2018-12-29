package cn.ac.ia.cip.utils;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;

import java.io.IOException;

public class FontTest {
    public static void main(String[] args) throws IOException {
        PdfFont font = PdfFontFactory.createFont();

        for (int i = 1; i < 50; i++) {
            float h = font.getAscent("hello world yABCDEFG.!!@#$%#@%", i) -font.getDescent("hello world", i);
            System.out.println(i + "\t" + h);
        }
    }
}
