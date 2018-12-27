package cn.ac.ia.cip;

import com.baiyyang.server.test.Test;

import java.util.Random;

public class FakeTest extends Test {
    private static int scale = 4;

    public FakeTest() {super(false);}

    public String test(String language, String domain, String source) {
//        int len = source.length();
//        if (language.split("2")[1].equals("EN"))
//            return randomChar(len*scale);
//        else
//            return randomChs(len/scale+1);
        return source;
    }

    private String randomChar(int len) {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < len; ++i) {
            int r = random.nextInt(52);
            if (r < 26) {
                stringBuilder.append((char) (r + 65));
            } else {
                stringBuilder.append((char) (r + 71));
            }
        }
        return stringBuilder.toString();
    }

    private String randomChs(int len) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < len; ++i) {
            stringBuilder.append((char) (0x4e00 + (int) (Math.random() * (0x6fa5 - 0x4e00 + 1))));
        }
        return stringBuilder.toString();
    }
}
