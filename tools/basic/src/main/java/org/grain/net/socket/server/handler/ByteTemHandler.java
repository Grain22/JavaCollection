package org.grain.net.socket.server.handler;

import lombok.extern.slf4j.Slf4j;

import java.net.Socket;
import java.security.SecureRandom;

/**
 * @author wulifu
 */
@Slf4j
public class ByteTemHandler implements Runnable {
    Socket socket;
    int readLength;
    boolean intercept = false;

    public ByteTemHandler(Socket s, int readLength) {
        socket = s;
        this.readLength = readLength;
    }
    public void intercept(){
        this.intercept = true;
    }

    @Override
    public void run() {
        try {
            while (!intercept) {
                byte[] header = new byte[readLength];
                log.info("read {}",socket.getInputStream().read(header));
                StringBuilder sb = new StringBuilder("");
                for (byte b : header) {
                    sb.append(b).append(" ");
                }
                log.info(sb.toString());
                byte[] bytes1 = new byte[10];
                System.arraycopy(header, 0, bytes1, 0, bytes1.length);
                bytes1[8] = 48;
                Thread.sleep(new SecureRandom().nextInt(10) + 50);
                socket.getOutputStream().write(bytes1);
            }
        } catch (Exception e) {
            System.out.println(socket.getLocalPort() + "" + e.getMessage());
        }
    }
}
