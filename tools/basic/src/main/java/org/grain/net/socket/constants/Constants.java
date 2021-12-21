package org.grain.net.socket.constants;

import org.grain.tools.thread.CustomThreadPool;

import java.util.concurrent.ExecutorService;

/**
 * @author laowu
 */
public class Constants {
    public static final String END = "end";
    public static int POINT = 10000;
    public static String command = "///";
    public static int port = 9999;
    public static String host = "127.0.0.1";
    public static ExecutorService clients;

    static {
        clients = CustomThreadPool.createThreadPool("socket_clients",32,32,false,3000);
    }
}
