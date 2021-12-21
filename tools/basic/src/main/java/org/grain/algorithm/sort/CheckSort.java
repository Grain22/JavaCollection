package org.grain.algorithm.sort;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.security.SecureRandom;

/**
 * @author wulifu
 */
public class CheckSort {
    public static void main(String[] args) throws IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        int length = 999999;
        int random = 99999;
        SecureRandom secureRandom = new SecureRandom();
        int[] ints = new int[length];
        for (int i = 0; i < ints.length; i++) {
            ints[i] = secureRandom.nextInt(random);
        }
/*new BinaryQuickSort().runSort(ints,0);
        check(ints);*/
        doAll(ints);
    }

    private static void doAll(int[] ints) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        ScanResult scan = new ClassGraph()
                .verbose()
                .enableAllInfo()
                .acceptPackages("grain.algorithm.sort")
                .scan();
        ClassInfoList sortFiles = scan.getAllClasses();
        for (io.github.classgraph.ClassInfo sortFile : sortFiles) {
            int[] tem = new int[ints.length];
            Class<?> sortClass = Class.forName(sortFile.getName());
            if (Modifier.isAbstract(sortClass.getModifiers())) {
                continue;
            }
            if (!Sort.class.isAssignableFrom(sortClass)) {
                continue;
            }
            System.arraycopy(ints, 0, tem, 0, ints.length);
            Constructor<?> newSort = sortClass.getConstructor();
            Sort sort = (Sort) newSort.newInstance();
            long begin = System.currentTimeMillis();
            System.out.println("sort : " + sort.getClass().getName());
            System.out.println("sort begin " + begin);
            sort.runSort(tem, 128);
            System.out.println("sort end " + (System.currentTimeMillis() - begin));
            check(tem);
            System.out.println();
        }
    }

    private static boolean check(int[] tem) {
        for (int i = 0; i < tem.length - 1; i++) {
            if (tem[i] > tem[i + 1]) {
                System.out.println("fail");
                return false;
            }
        }
        System.out.println("success");
        return true;
    }
}
