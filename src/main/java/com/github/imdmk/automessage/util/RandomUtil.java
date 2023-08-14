package com.github.imdmk.automessage.util;

import java.util.Collection;
import java.util.Optional;
import java.util.Random;

public final class RandomUtil {

    private static final Random RANDOM = new Random();

    private RandomUtil() {
        throw new UnsupportedOperationException("This is utility class.");
    }

    public static <T> Optional<T> getRandom(Collection<T> collection) {
        return collection.stream()
                .skip(RANDOM.nextInt(collection.size()))
                .findAny();
    }
}
