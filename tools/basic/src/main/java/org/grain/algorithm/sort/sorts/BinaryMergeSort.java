package org.grain.algorithm.sort.sorts;

import org.grain.algorithm.sort.MergeSorting;

/**
 * @author wulifu
 */
public class BinaryMergeSort extends MergeSorting {
    @Override
    public void runSort(int[] array, int bucketCount) {
        sort(array, 0, array.length - 1);
    }

    private static void sort(int[] a, int left, int right) {
        if (left >= right) {
            return;
        }
        /* when left + right > 2^31-1
        mid = (left + right) / 2
        mid will be a negative number*/
        int mid = (left + right) >>> 1;
        sort(a, left, mid);
        sort(a, mid + 1, right);
        merge(a, left, mid, right);
    }

    private static void merge(int[] a, int left, int mid, int right) {
        int[] tmp = new int[a.length];
        int r1 = mid + 1;
        int tIndex = left;
        int cIndex = left;
        while (left <= mid && r1 <= right) {
            if (a[left] <= a[r1])
                tmp[tIndex++] = a[left++];
            else
                tmp[tIndex++] = a[r1++];
        }
        while (left <= mid) {
            tmp[tIndex++] = a[left++];
        }
        while (r1 <= right) {
            tmp[tIndex++] = a[r1++];
        }
        while (cIndex <= right) {
            a[cIndex] = tmp[cIndex];
            cIndex++;
        }
    }
}
