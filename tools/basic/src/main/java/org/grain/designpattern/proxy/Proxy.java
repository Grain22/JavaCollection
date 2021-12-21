package org.grain.designpattern.proxy;

/**
 * @author laowu
 * @version 5/6/2019 11:55 AM
 */
public class Proxy {
    public static void run(String[] args) {
        Image image = new ProxyImage("test");
        image.display();
        System.out.println("");
        image.display();
    }
}

