package org.grain.designpattern.template;

public class Test extends AbstractOperators {
    public static void run(String[] args) {
        AbstractOperators t = new Test();
        t.go();
    }

    @Override
    public void begin() {
        System.out.println("begin");
    }

    @Override
    public void then() {
        System.out.println("begin");
    }

    @Override
    public void end() {
        System.out.println("begin");
    }
}
