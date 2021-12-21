package org.grain.net.socket.client.handler;

import java.net.Socket;
import java.util.Random;

/**
 * @author laowu
 */
public class HeartHandler implements Runnable {
    private Socket socket;

    public HeartHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            while (true) {
                byte[] bytes = new byte[20];
                for (int i = 0; i < bytes.length; i++) {
                    bytes[i] = (byte) (new Random().nextInt());
                }
                for (byte aByte : bytes) {
                    System.out.print(aByte);
                }
                socket.getOutputStream().write(bytes);
                socket.getOutputStream().flush();
                byte[] read = new byte[5];
                socket.getInputStream().read(read);
                for (byte aByte : read) {
                    System.out.print(aByte);
                }
                System.out.println("");
                Thread.sleep(5000);
                //socket.close();
            }
        } catch (Exception ignore) {

        }
    }
}
