package com.github.imdmk.automessage;

import com.github.imdmk.automessage.util.StringUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringUtilTest {

    @Test
    void isIntegerTest() {
        String integer = "10";
        boolean excepted = true;

        assertEquals(excepted, StringUtil.isInteger(integer));
    }
}
