package org.grain.net.socket.server;

import org.grain.net.socket.server.handler.ByteTemHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author laowu
 * @version 5/10/2019 11:12 AM
 */
public class Server {

    public static List<Socket> clients = new ArrayList<>();
    ServerSocket server;
    String[] args;
    boolean forNew;

    public Server(String[] args, boolean forNew) {
        this.args = args;
        this.forNew = forNew;
        try {
            init();
            listen();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void init() throws IOException {
        server = new ServerSocket(Integer.parseInt(args[0]));
//        server.setSoTimeout(1000);
    }

    private void listen() throws IOException {
        while (!Thread.currentThread().isInterrupted()) {
            Socket socket = server.accept();
            clients.add(socket);
            new Thread(new ByteTemHandler(socket,Integer.valueOf(args[1]))).start();
//            new Thread(new BytesServerHandler(socket, Integer.parseInt(args[1]))).start();
        }
    }
}