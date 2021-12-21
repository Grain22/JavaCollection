package org.grain.web.server;

import java.util.Properties;

/**
 * @author laowu
 * @version 5/30/2019 11:05 AM
 */
public class Server {

    public static void run() {
        Properties properties = null;
        try {
            properties.load(Server.class.getClassLoader().getResourceAsStream("/config.xml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
