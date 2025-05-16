package com.github.imdmk.automessage.util;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utility class for working with collections in a safe and performant way.
 */
public final class CollectionUtil {

    private CollectionUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Returns an element at the given position in the collection if it exists.
     * Efficient for lists; for other types, falls back to iteration.
     *
     * @param collection The source collection.
     * @param position   Index of the element to retrieve.
     * @param <T>        Element type.
     * @return Optional of the selected element.
     */
    public static <T> Optional<T> select(@NotNull final Collection<T> collection, int position) {
        return getByIndex(collection, position);
    }

    /**
     * Returns a random element from the collection.
     * Efficient and safe for any collection type.
     *
     * @param collection The source collection.
     * @param <T>        Element type.
     * @return Optional of a randomly selected element.
     */
    @NotNull
    public static <T> Optional<T> getRandom(@NotNull final Collection<T> collection) {
        int size = collection.size();
        if (size == 0) {
            return Optional.empty();
        }
        int index = ThreadLocalRandom.current().nextInt(size);
        return getByIndex(collection, index);
    }

    /**
     * Retrieves an element at the specified index from the collection.
     * Optimized for lists; falls back to manual iteration otherwise.
     *
     * @param collection The collection to access.
     * @param index      The target index.
     * @param <T>        Element type.
     * @return Optional containing the element, or empty if index is invalid.
     */
    @NotNull
    private static <T> Optional<T> getByIndex(@NotNull final Collection<T> collection, int index) {
        if (index < 0 || index >= collection.size()) {
            return Optional.empty();
        }

        if (collection instanceof List) {
            return Optional.ofNullable(((List<T>) collection).get(index));
        }

        Iterator<T> iterator = collection.iterator();
        for (int i = 0; i < index; i++) {
            iterator.next(); // safe due to range check above
        }

        return Optional.ofNullable(iterator.next());
    }

}
