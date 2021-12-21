package org.grain.designpattern.delegate.client;

import org.grain.designpattern.delegate.bus.delegate.Delegate;

/**
 * @author laowu
 * @version 5/6/2019 2:12 PM
 */
public class Client {
    private Delegate delegate;

    public Client(Delegate delegate) {
        this.delegate = delegate;
    }

    public void doTask() {
        delegate.doTask();
    }
}
