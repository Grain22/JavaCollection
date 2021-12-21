package org.grain.designpattern.abstractfactory;

interface food {
}

interface produce {
    /**
     * get food
     *
     * @return
     */
    food get();
}

class A implements food {
}

class B implements food {
}

class FactoryForA implements produce {
    @Override
    public food get() {
        return new A();
    }
}

class FactoryForB implements produce {
    @Override
    public food get() {
        return new B();
    }
}

/**
 * @author laowu
 */
public class AbstractFactory {
    public void clientCode(String name) {
        food x = new FactoryForA().get();
        x = new FactoryForB().get();
    }
}