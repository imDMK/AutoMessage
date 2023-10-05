package com.github.imdmk.automessage.util;

import java.util.Collection;
import java.util.Optional;
import java.util.Random;

public class CollectionUtil {

    private static final Random RANDOM = new Random();

    public static <T> Optional<T> select(Collection<T> collection, int position) {
        return collection.stream()
                .skip(position)
                .findFirst();
    }

    public static <T> Optional<T> getRandom(Collection<T> collection) {
        return collection.stream()
                .skip(RANDOM.nextInt(collection.size()))
                .findAny();
    }
}
