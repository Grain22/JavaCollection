package org.grain.algorithm.sort.sorts;

import org.grain.algorithm.sort.BinaryQuickSorting;

import java.util.Random;

import static org.apache.commons.lang3.ArrayUtils.swap;

/**
 * @author wulifu
 */
public class BinaryQuickSort extends BinaryQuickSorting {
    @Override
    public void runSort(int[] array, int bucketCount) {
        new FirstValue().quickSort(array,0,array.length-1);
    }

    public void runFirst(int[] array, int bucketCount){
        new FirstValue().quickSort(array, 0, array.length - 1);
    }
    public void runRandom(int[] array, int bucketCount){
        new RandomValue().quickSort(array, 0, array.length - 1);
    }

    private abstract static class QuickSort{
        public static void exchange(int[] element, int firstIndex, int secondIndex) {
            swap(element, firstIndex, secondIndex);
        }
        public static int findCenter(int[] element, int begin, int end) {
            int i = begin - 1;
            int endValue = element[end];
            for (int j = begin; j < end; j++) {
                if (element[j] < endValue) {
                    i++;
                    exchange(element, i, j);
                }
            }
            exchange(element, i + 1, end);
            return i + 1;
        }

        /**
         * 排序入口
         * @param element 数组
         * @param begin begin
         * @param end end
         */
        abstract void quickSort(int[] element, int begin, int end);
    }
    private static class FirstValue extends QuickSort{
        @Override
        void quickSort(int[] element, int begin, int end) {
            if (end - begin == 1) {
                if (element[end] < element[begin]) {
                    exchange(element, begin, end);
                }
            } else if (begin < end) {
                int center = findCenter(element, begin, end);
                quickSort(element, begin, center - 1);
                quickSort(element, center + 1, end);
            }
        }
    }
    private static class RandomValue extends QuickSort{

        public static int findCenterRandom(int[] element,int begin,int end){
            //不用currentTimeMillis的原因是：当多线程调用时，由于CPU速率很快，因此currentTimeMillis很可能相等，使得随机数结果也会相等
            //nanoTime()返回最准确的可用系统计时器的当前值,以毫微秒为单位。此方法只能用于测量已过的时间,与系统或钟表时间的其他任何时间概念无关。
            long seed=System.nanoTime();
            Random rd=new Random(seed);
            int point=(rd.nextInt(end)%(end-begin+1))+begin;
            exchange(element,point,end);
            return findCenter(element,begin,end);
        }

        @Override
        void quickSort(int[] element, int begin, int end) {
            if (end - begin == 1) {
                if (element[end] < element[begin]) {
                    exchange(element, begin, end);
                }
            } else if (begin < end) {
                int center = findCenterRandom(element, begin, end);
                quickSort(element, begin, center - 1);
                quickSort(element, center + 1, end);
            }
        }
    }
}
