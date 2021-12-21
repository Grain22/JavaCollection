package org.grain.algorithm.sort;

import static java.lang.Integer.compare;

/**
 * @author wulifu
 */
public abstract class BinaryInsertionSorting implements Sort {
    protected void binaryInsertSort(int[] array, int start, int end) {
        for (int i = start; i < end; i++) {
            int num = array[i];
            int lo = start, hi = i;
            while (lo < hi) {
                /*avoid int overflow!*/
                int mid = lo + ((hi - lo) / 2);
                /*do NOT move equal elements to right of inserted element; this maintains stability!*/
                if (compare(num, array[mid]) < 0) {
                    hi = mid;
                } else {
                    lo = mid + 1;
                }
            }
            // item has to go into position lo
            int j = i - 1;
            while (j >= lo) {
                array[j + 1] = array[j];
                /*write(array, j + 1, array[j]);*/
                j--;
            }
            array[lo] = num;
            /*Writes.write(array, lo, num, writeSleep, true, false);*/
        }
    }
}
