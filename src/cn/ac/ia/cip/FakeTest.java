package cn.ac.ia.cip;

import com.baiyyang.server.test.Test;

public class FakeTest extends Test {

    public FakeTest() {super(false);}

    public String test(String language, String domain, String source) {
        return source;
    }
}
