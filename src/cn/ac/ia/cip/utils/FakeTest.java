package cn.ac.ia.cip.utils;

import cn.ac.ia.cip.utils.mt.STMTWeb;
import cn.ac.ia.cip.utils.mt.STMTWebService;
import com.baiyyang.server.test.Test;

import java.util.Random;

public class FakeTest extends Test {
    private static int scale = 4;
    private STMTWeb stmt;
    public FakeTest() {
        super(false);
        stmt = new STMTWebService().getSTMTWebPort();
    }

    public String test(String language, String domain, String source) {
        if (Config.testMt) {
            return stmt.getTranslation(language, domain, "ABSTRACT", source);
        } else {
            return source;
        }
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
