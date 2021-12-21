package org.grain.tools.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @author laowu
 * @version 5/7/2019 12:22 PM
 */
public class ObjectUtils {
    public static boolean isNull(Object o) {
        return o == null;
    }

    public static Stream<Long> range(final long start, long length, int step) {
        return Stream.generate(new Supplier<Long>() {
            private long next = start;
            @Override
            public Long get() {
                long nextTem = next;
                next += step;
                return nextTem;
            }
        }).limit(length);
    }

    @SuppressWarnings("unchecked")
    public static <T> T deepClone(T t) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(outputStream);
            oos.writeObject(t);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(inputStream);
            Object o = ois.readObject();
            if (o != null) {
                return (T) o;
            }
        } catch (Exception e) {
            System.out.println("克隆出错" + Arrays.toString(e.getStackTrace()));
        }
        return null;
    }
}