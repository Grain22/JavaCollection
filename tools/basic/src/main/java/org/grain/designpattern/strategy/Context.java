package org.grain.designpattern.strategy;

/**
 * @author laowu
 */
public class Context {
    private Strategy strategy;

    Context(Strategy strategy) {
        this.strategy = strategy;
    }

    public int execute(int a, int b) {
        return strategy.run(a, b);
    }
}
