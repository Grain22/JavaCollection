package org.grain.designpattern.delegate.bus.service;

/**
 * @author laowu
 * @version 5/6/2019 2:04 PM
 */
public class EJBServiceImpl implements BusinessService {
    @Override
    public void doProcessing() {
        System.out.println("ejb");
    }
}
