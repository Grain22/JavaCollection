package org.grain.designpattern.template;

/**
 * @author laowu
 * @version 5/7/2019 2:31 PM
 */
public abstract class AbstractOperators {
    public void go() {
        begin();
        then();
        end();
    }

    public abstract void begin();

    public abstract void then();

    public abstract void end();
}
