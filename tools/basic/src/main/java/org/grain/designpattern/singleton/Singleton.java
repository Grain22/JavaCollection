package org.grain.designpattern.singleton;

public class Singleton {
    private static Singleton instance = null;

    private Singleton() {
    }

    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    Singleton t = new Singleton();
                    instance = t;
                }
            }
        }
        return instance;
    }
}
