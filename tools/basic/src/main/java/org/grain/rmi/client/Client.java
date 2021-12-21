package org.grain.rmi.client;

import org.grain.rmi.constant.Constants;
import org.grain.rmi.constant.Handler;
import org.grain.tools.thread.GlobalIdGenerator;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * @author laowu
 * @date 2019/6/21 18:48
 */
public class Client {
    public static void run(String[] args) {
        try {
            Handler handler = (Handler) Naming.lookup("rmi://localhost:" + Constants.port + "/handler");
            for (int i = 0; i < 1000; i++) {
                int finalI = i;
                new Thread(() -> {
                    try {
                        System.out.println(GlobalIdGenerator.analyzeId(Long.parseLong(handler.getMsg(finalI, finalI))));;
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
