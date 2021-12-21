package org.grain.net.socket.server.handler;

import lombok.extern.slf4j.Slf4j;
import org.grain.tools.data.ByteUtils;

import java.net.Socket;
import java.security.SecureRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author laowu
 */
@Slf4j
public class BytesServerHandler implements Runnable {
    public static final ExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    Socket socket;
    int i;
    boolean intercepted = false;

    public BytesServerHandler(Socket s, int i) {
        socket = s;
        this.i = i;
    }

    public void intercepted() {
        this.intercepted = true;
    }

    @Override
    public void run() {
        try {
            byte[] header = new byte[10];
            log.info("read length {}", socket.getInputStream().read(header));
            byte[] data = ByteUtils.getData(header, 0, 4);
            int i = ByteUtils.bytesToInt(data);
            byte[] body = new byte[i - 10];
            log.info("read body length {}", socket.getInputStream().read(body));
            log.info(new String(body));
            byte[] bytes = "{\"returnCode\":0}".getBytes();
            byte[] bytes1 = new byte[10 + bytes.length];
            byte[] bytes2 = ByteUtils.intToBytes(bytes.length + 10);
            System.arraycopy(bytes2, 0, bytes1, 0, bytes2.length);
            System.arraycopy(bytes, 0, bytes1, 10, bytes.length);
            Thread.sleep(new SecureRandom().nextInt(10) + i);
            socket.getOutputStream().write(bytes1);
            executorService.submit(new BytesServerHandler(socket, i));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
