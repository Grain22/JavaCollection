package grain;


import org.grain.net.socket.server.Server;

import java.util.HashMap;
import java.util.Map;

public class Bootstrap {
    public static void main(String[] args) {
        System.out.println("use sleep"/* + args[0]*/);
        new Thread(() -> new Server(new String[]{"9002", "100"}, false)).start();
        new Thread(() -> new Server(new String[]{"9003", "99"}, false)).start();
//        Thread all = new Thread(() -> new Server(new String[]{"9004", args[0]}, true));
//        all.start();
    }

    public static void run(String[] args) {
        Map<String, String> map = new HashMap<>(2);
        for (String arg : args) {
            String[] split = arg.split("=");
            if (split.length == 2) {
                map.put(split[0], split[1]);
            }
        }
        FileScanner.scanner(map.get("file"), map.get("regex"), map.get("target"));
    }
}
