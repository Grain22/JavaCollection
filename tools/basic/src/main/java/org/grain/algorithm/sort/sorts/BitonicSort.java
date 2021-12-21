package org.grain.algorithm.sort.sorts;

import org.grain.algorithm.sort.ComparisonBasedSort;

import static org.apache.commons.lang3.ArrayUtils.swap;

/**
 * @author wulifu
 */
public class BitonicSort implements ComparisonBasedSort {
    private boolean direction = true;

    @Override
    public void runSort(int[] array, int bucketCount) {
        this.bitonicSort(array, 0, array.length, this.direction);
    }

    private static int greatestPowerOfTwoLessThan(int n) {
        int k = 1;
        while (k < n) {
            k = k << 1;
        }
        return k >> 1;
    }

    private void compare(int[] A, int i, int j, boolean dir) {
        int cmp = Integer.compare(A[i], A[j]);
        if (dir == (cmp == 1)) {
            swap(A, i, j, 1);
        }
    }

    private void bitonicMerge(int[] A, int lo, int n, boolean dir) {
        if (n > 1) {
            int m = BitonicSort.greatestPowerOfTwoLessThan(n);
            for (int i = lo; i < lo + n - m; i++) {
                this.compare(A, i, i + m, dir);
            }
            this.bitonicMerge(A, lo, m, dir);
            this.bitonicMerge(A, lo + m, n - m, dir);
        }
    }

    private void bitonicSort(int[] A, int lo, int n, boolean dir) {
        if (n > 1) {
            int m = n / 2;
            this.bitonicSort(A, lo, m, !dir);
            this.bitonicSort(A, lo + m, n - m, dir);
            this.bitonicMerge(A, lo, n, dir);
        }
    }

    public void changeDirection(String choice) throws Exception {
        if (choice.equals("forward")) this.direction = true;
        else if (choice.equals("backward")) this.direction = false;
        else throw new Exception("Invalid direction for Bitonic Sort!");
    }
}
