package org.grain.net.socket.server.handler;

import org.grain.net.socket.server.Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author laowu
 */
public class ChatServerHandler implements Runnable {
    Socket socket;
    private BufferedReader br;
    private PrintWriter pw;
    private String msg;

    public ChatServerHandler(Socket s) {
        socket = s;
    }

    @Override
    public void run() {
        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            msg = socket.getInetAddress() + " " + Server.clients.size();
            sendMsg();
            byte[] byteData = new byte[99];
            while ((msg = br.readLine()) != null) {
                msg = new StringBuilder().append(socket.getInetAddress()).append(" ").append(msg).toString();
                sendMsg();
            }
        } catch (Exception ex) {

        }
    }

    public void sendMsg() {
        try {
            System.out.println(msg);
            for (int i = Server.clients.size() - 1; i >= 0; i--) {
                pw = new PrintWriter(Server.clients.get(i).getOutputStream(), true);
                pw.println(msg);
                pw.flush();
            }
        } catch (Exception ex) {
        }
    }
}
