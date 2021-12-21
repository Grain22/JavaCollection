package org.grain.net.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * @author wulifu
 */
public class Receiver {
    public static void main(String[] args) throws IOException {
        DatagramSocket ds=new DatagramSocket(6000);
        //创建字节数组
        byte[] data=new byte[1024];
        //创建数据包对象，传递字节数组
        DatagramPacket dp=new DatagramPacket(data, data.length);
        //调用ds对象的方法receive传递数据包
        ds.receive(dp);
        //获取发送端的IP地址对象
        String ip=dp.getAddress().getHostAddress();
        //获取发送的端口号
        int port=dp.getPort();
        //获取接收到的字节数
        int length=dp.getLength();
        System.out.println(new String(data,0,length)+"...."+ip+":"+port);
        ds.close();
    }
}
