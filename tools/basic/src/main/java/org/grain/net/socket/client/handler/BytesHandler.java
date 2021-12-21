package org.grain.net.socket.client.handler;

import java.net.Socket;

/**
 * @author laowu
 */
public class BytesHandler implements Runnable {
    private Socket socket;

    public BytesHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            while (true) {
                byte[] bytes = new byte[99];
                for (int i = 0; i < bytes.length; i++) {
                    bytes[i] = '1';
                }
                socket.getOutputStream().write(bytes);
                byte[] read = new byte[10];
                socket.getInputStream().read(read);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
