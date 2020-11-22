package me.rainstorm.innodb.common;

import java.util.Iterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author traceless
 */
public class StreamUtil {
    public static <T> Stream<T> of(Iterator<T> iterator) {
        return of(iterator, false);
    }

    public static <T> Stream<T> of(Iterator<T> iterator, boolean parallel) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), parallel);
    }

    public static <T> Stream<T> of(Iterator<T> iterator, int size) {
        return of(iterator, size, false);
    }

    public static <T> Stream<T> of(Iterator<T> iterator, int size, boolean parallel) {
        return StreamSupport.stream(Spliterators.spliterator(iterator, size, 0), parallel);
    }
}
