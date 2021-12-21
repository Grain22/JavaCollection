package org.grain.net.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * @author wulifu
 */
public class Sender {
    public static void main(String[] args) throws IOException {
        byte[] data = "你好UDP".getBytes();
        InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
        DatagramPacket dp = new DatagramPacket(data, data.length, inetAddress, 6000);
        DatagramSocket ds = new DatagramSocket();
        ds.send(dp);
        ds.close();
    }
}
