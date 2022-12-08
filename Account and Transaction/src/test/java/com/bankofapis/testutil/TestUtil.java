package com.bankofapis.testutil;

import java.net.URL;

public class TestUtil {
    public static URL loadTestData(String dataFileName) {
        return TestUtil.class.getResource("/test_data/" + dataFileName);
    }
}
