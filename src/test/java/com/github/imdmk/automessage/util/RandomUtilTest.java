package com.github.imdmk.automessage.util;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RandomUtilTest {

    @Test
    void testRandom() {
        List<String> strings = List.of("DMK", "AutoMessage");

        Optional<String> randomString = RandomUtil.getRandom(strings);

        assertTrue(randomString.isPresent());
    }
}
