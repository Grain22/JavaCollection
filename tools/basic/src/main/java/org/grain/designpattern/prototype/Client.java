package org.grain.designpattern.prototype;

/**
 * @author laowu
 * @version 5/7/2019 12:08 PM
 */
public class Client {
    private static int MAX_COUNT = 6;

    public static void run(String[] args) {
        int i = 0;
        Mail mail = new Mail(new AdvTemplate());
        mail.setTail("XX银行版权所有");
        Mail clone = mail.clone();
        System.out.println(clone.toString());
    }
}
