package org.grain.algorithm.sort;

/**
 * @author wulifu
 */
public  interface Sort {
    /**
     * bucketCount will be zero for comparison-based sorts
     * @param array src
     * @param bucketCount 数组桶大小
     */
    void runSort(int[] array, int bucketCount);
}