package org.grain.algorithm.simple;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author laowu
 * @apiNote 常用算法记录
 */
public class SortUtils {

    final static int two = 2;

    /**
     * 冒泡 n2
     *
     * @param list
     * @return
     */
    public static List<Integer> bubble(List<Integer> list) {
        int tem = 0;
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = 0; j < list.size() - i - 1; j++) {
                if (list.get(j) > list.get(j + 1)) {
                    tem = list.get(j + 1);
                    list.set(j + 1, list.get(j));
                    list.set(j, tem);
                }
            }
        }
        return list;
    }

    /**
     * 快速排序 n log n
     *
     * @param list
     * @return
     */
    public static List<Integer> fast(List<Integer> list) {
        if (list.size() < two) {
            return list;
        } else {
            Integer integer = list.get(0);
            List<Integer> equal = list.stream().filter(a -> a.equals(integer)).collect(Collectors.toList());
            List<Integer> small = list.stream().filter(a -> a < integer).collect(Collectors.toList());
            List<Integer> bigger = list.stream().filter(a -> a > integer).collect(Collectors.toList());
            small = fast(small);
            bigger = fast(bigger);
            small.addAll(equal);
            small.addAll(bigger);
            return small;
        }
    }

}
