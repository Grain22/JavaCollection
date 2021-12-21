package org.grain.designpattern.proxy;

/**
 * @author laowu
 * @version 5/6/2019 11:59 AM
 */
public class ProxyImage implements Image {

    private RealImage realImage;
    private final String fileName;

    ProxyImage(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void display() {
        if (realImage == null) {
            realImage = new RealImage(fileName);
        }
        realImage.display();
    }
}