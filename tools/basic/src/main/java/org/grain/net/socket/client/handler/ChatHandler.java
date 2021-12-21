package org.grain.net.socket.client.handler;

import org.grain.net.socket.constants.Constants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author laowu
 */
public class ChatHandler {
    private Socket socket;

    public ChatHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            BufferedReader re = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
            String msg;
            while (true) {
                msg = re.readLine();
                if ("".equals(msg.trim())) {
                    continue;
                } else if (msg.equals(Constants.command + Constants.END)) {
                    break;
                }
                pw.println(msg);
            }
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
