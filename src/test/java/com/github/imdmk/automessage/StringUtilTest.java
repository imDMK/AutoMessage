package com.github.imdmk.automessage;

import com.github.imdmk.automessage.util.StringUtil;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringUtilTest {

    static List<Object[]> integers() {
        return List.of(new Object[][] {
                { false, "AutoMessage" },
                { true, "50" }
        });
    }

    @ParameterizedTest
    @MethodSource("integers")
    void integerTest(boolean excepted, String string) {
        boolean result = StringUtil.isInteger(string);
        assertEquals(excepted, result);
    }
}
