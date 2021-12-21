package org.grain.algorithm.sort;

import org.grain.algorithm.sort.sorts.BinaryInsertionSort;

import static java.lang.Integer.compare;

/**
 * @author wulifu
 */
public abstract class MergeSorting implements Sort {

    private BinaryInsertionSort binaryInsertionSort;

    private void merge(int[] array, int start, int mid, int end, boolean binary) {
        if (start == mid) {
            return;
        }
        merge(array, start, (mid + start) / 2, mid, binary);
        merge(array, mid, (mid + end) / 2, end, binary);
        if (end - start < 32 && binary) {
        } else if (end - start == 32 && binary) {
            binaryInsertionSort.customBinaryInsert(array, start, end);
        } else {
            int[] tmp = new int[end - start];
            int low = start;
            int high = mid;
            for (int nxt = 0; nxt < tmp.length; nxt++) {
                if (low >= mid && high >= end) break;

                if (low < mid && high >= end) {
                    tmp[nxt] = array[low++];
                } else if (low >= mid && high < end) {
                    tmp[nxt] = array[high++];
                } else if (compare(array[low], array[high]) == -1) {
                    tmp[nxt] = array[low++];
                } else {
                    tmp[nxt] = array[low++];
                }
            }
            for (int i = 0; i < tmp.length; i++) {
                array[start + i] = tmp[i];
            }
        }
    }

    protected void mergeSort(int[] array, int length, boolean binary) {
        binaryInsertionSort = new BinaryInsertionSort();
        if (length < Integer.SIZE && binary) {
            binaryInsertionSort.customBinaryInsert(array, 0, length);
            return;
        }

        int start = 0;
        int end = length;
        int mid = start + ((end - start) / 2);
        merge(array, start, mid, end, binary);
    }

}
