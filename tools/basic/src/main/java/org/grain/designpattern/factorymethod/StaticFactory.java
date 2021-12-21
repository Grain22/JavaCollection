package org.grain.designpattern.factorymethod;

interface Food {
}

class A implements Food {
}

class B implements Food {
}

class C implements Food {
}

/**
 * @author laowu
 */
public class StaticFactory {
    private StaticFactory() {
    }

    public static Food getA() {
        return new A();
    }

    public static Food getB() {
        return new B();
    }

    public static Food getC() {
        return new C();
    }
}

class Client {
    public void get(String name) {
        Food x = null;
        if ("A".equals(name)) {
            x = StaticFactory.getA();
        } else if ("B".equals(name)) {
            x = StaticFactory.getB();
        } else {
            x = StaticFactory.getC();
        }
    }
}