package org.grain.algorithm.sort;

import java.util.LinkedList;
import java.util.Queue;

import static org.apache.commons.lang3.ArrayUtils.swap;

/**
 * @author wulifu
 */
public abstract class BinaryQuickSorting implements Sort {

    public static class Task {
        public int p;
        public int r;
        public int bit;

        public Task(int p, int r, int bit)
        {
            this.p = p;
            this.r = r;
            this.bit = bit;
        }
    }

    public void binaryQuickSortRecursive(int[] array, int p, int r, int bit)
    {
        if (p < r && bit >= 0)
        {
            int q = partition(array, p, r, bit);
            binaryQuickSortRecursive(array, p, q, bit-1);
            binaryQuickSortRecursive(array, q+1, r, bit-1);
        }
    }

    public void binaryQuickSort(int[] array, int p, int r, int bit)
    {
        Queue<Task> tasks = new LinkedList<>();
        tasks.add(new Task(p, r, bit));

        while (tasks.isEmpty() == false)
        {
            Task task = tasks.remove();
            if (task.p < task.r && task.bit >= 0)
            {
                int q = partition(array, task.p, task.r, task.bit);
                tasks.add(new Task(task.p, q, task.bit-1));
                tasks.add(new Task(q+1, task.r, task.bit-1));
            }
        }
    }

    public int partition(int[] array, int p, int r, int bit)
    {
        int i = p - 1;
        int j = r + 1;

        while (true) {
            // Left is not set
            i++;
            while(i < r && !getBit(array[i], bit)) {
                i++;
            }
            // Right is set
            j--;
            while(j > p && getBit(array[j], bit)) {
                j--;
            }
            // If i is less than j, we swap, otherwise we are done
            if (i < j)
                swap(array, i, j, 1);
            else
                return j;
        }
    }
    public boolean getBit(int n, int k) {
        // Find boolean value of bit k in n
        return ((n >> k) & 1) == 1;
    }
}
