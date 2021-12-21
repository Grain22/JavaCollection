package org.grain.algorithm.sort.sorts;

import org.grain.algorithm.sort.ComparisonBasedSort;
import org.grain.algorithm.sort.Sort;

import static java.lang.Integer.compare;
import static org.apache.commons.lang3.ArrayUtils.swap;

/**
 * @author wulifu
 */
final public class BadSort implements ComparisonBasedSort, Sort {
    @Override
    public void runSort(int[] array, int bucketCount) {
        int currentLen = array.length;
        for (int i = 0; i < currentLen; i++) {
            int shortest = i;
            for (int j = i; j < currentLen; j++) {
                boolean isShortest = true;
                for (int k = j + 1; k < currentLen; k++) {
                    if (compare(array[j], array[k]) == 1) {
                        isShortest = false;
                        break;
                    }
                }
                if (isShortest) {
                    shortest = j;
                    break;
                }
            }
            swap(array, i, shortest);
        }
    }
}
