package org.grain.rmi.server;

import org.grain.rmi.constant.Constants;
import org.grain.rmi.constant.Handler;
import org.grain.rmi.constant.HandlerImpl;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * @author wulifu
 */
public class Server {
    /**
     * if method for remote does not extend <class>java.rmi.server.UnicastRemoteObject</class>
     * need sleep to hold daemon listen thread
     * <code>
     *      if (!(handler instanceof UnicastRemoteObject)) {
     *                 Thread.sleep(Long.MAX_VALUE);
     *      }
     * </code>
     */
    public static void run() {
        try {
            Handler handler = new HandlerImpl();
            Registry registry = LocateRegistry.createRegistry(Constants.port);
            registry.bind("handler", handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
