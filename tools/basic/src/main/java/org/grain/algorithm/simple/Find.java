package org.grain.algorithm.simple;

public class Find {
    /**
     * 二分查找 log n
     *
     * @return
     */
    public static boolean halfHalf(int[] ints, int begin, int end, int find) {
        System.out.println("in");
        int mid = (begin + end) / 2;
        if (ints[mid] == find) {
            return true;
        } else if (Math.abs(begin - end) == 1) {
            return ints[begin] == find || ints[end] == find;
        } else if (ints[mid] > find) {
            return halfHalf(ints, begin, mid, find);
        } else if (ints[mid] < find) {
            return halfHalf(ints, mid, end, find);
        }
        return false;
    }
}
