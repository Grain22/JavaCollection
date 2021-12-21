package org.grain.designpattern.proxy;

/**
 * @author laowu
 * @version 5/6/2019 11:58 AM
 */
public class RealImage implements Image {

    private final String fileName;

    RealImage(String fileName) {
        this.fileName = fileName;
        loadFromDisk(fileName);
    }

    @Override
    public void display() {
        System.out.println("Displaying " + fileName);
    }

    private void loadFromDisk(String fileName) {
        System.out.println("Loading " + fileName);
    }
}
