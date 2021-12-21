package org.grain.algorithm.sort.sorts;

import org.grain.algorithm.sort.ComparisonBasedSort;

import static java.lang.Integer.compare;
import static org.apache.commons.lang3.ArrayUtils.swap;

/**
 * @author wulifu
 */
public class BinaryGnomeSort implements ComparisonBasedSort {
    @Override
    public void runSort(int[] array, int bucketCount) {
        int length = array.length;
        for (int i = 1; i < length; i++) {
            int num = array[i];

            int lo = 0, hi = i;
            while (lo < hi) {
                int mid = lo + ((hi - lo) / 2);
                /*do NOT shift equal elements past each other; this maintains stability!*/
                if (compare(num, array[mid]) < 0) {
                    hi = mid;
                }
                else {
                    lo = mid + 1;
                }
            }
            // item has to go into position lo
            int j = i;
            while (j > lo) {
                swap(array, j, j - 1);
                j--;
            }
        }
    }
}
