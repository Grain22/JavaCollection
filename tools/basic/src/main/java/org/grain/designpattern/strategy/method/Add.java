package org.grain.designpattern.strategy.method;

import org.grain.designpattern.strategy.Strategy;

/**
 * @author laowu
 * @version 5/6/2019 2:19 PM
 */
public class Add implements Strategy {
    @Override
    public int run(int a, int b) {
        return a + b;
    }
}
