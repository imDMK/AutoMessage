package com.github.imdmk.automessage;

import com.github.imdmk.automessage.util.CollectionUtil;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CollectionUtilTest {

    @Test
    void testSelect() {
        List<String> strings = List.of("DMK", "AutoMessage", "ExampleString");

        int position = 1;

        String exceptedString = "AutoMessage";
        Optional<String> resultOptional = CollectionUtil.select(strings, position);

        assertTrue(resultOptional.isPresent());

        String result = resultOptional.get();

        assertEquals(exceptedString, result);
    }

    @Test
    void testRandom() {
        List<String> strings = List.of("DMK", "AutoMessage");

        Optional<String> randomString = CollectionUtil.getRandom(strings);

        assertTrue(randomString.isPresent());
    }
}
