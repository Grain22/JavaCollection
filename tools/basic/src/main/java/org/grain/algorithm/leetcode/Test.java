package org.grain.algorithm.leetcode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author wulifu
 * @date 2020/8/25 16:54
 */
public class Test {
    static Set<Character> num = new HashSet<>(Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9'));
    static Set<Character> sign = new HashSet<>(Arrays.asList('+', '-'));

    private static void print(int[][] intSet) {
        for (int[] intS : intSet) {
            for (int anInt : intS) {
                System.out.printf("%9d", anInt);
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println(System.currentTimeMillis());
        List<String> list = Arrays.asList("  005047e+6  "
                , " 0.1 "
                , "abc"
                , "1 a"
                , "2e10"
                , " -90e3 "
                , " 1e"
                , "e3"
                , " 6e-1"
                , " 99e2.5 "
                , "53.5e93"
                , " --6 "
                , "-+3"
                , "95a54e53");
        System.out.println();
        System.out.println(System.currentTimeMillis());
    }


}
