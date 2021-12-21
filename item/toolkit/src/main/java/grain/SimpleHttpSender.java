package grain;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class SimpleHttpSender {
    public static void main(String[] args) throws IOException {
        Map<String, String> map = new HashMap<>();
        for (String arg : args) {
            String[] split = arg.split("=");
            if (split.length == 2) {
                map.put(split[0], split[1]);
            }
        }
        File file = new File(map.get("file"));
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String s = bufferedReader.readLine();
        Unirest.config().verifySsl(false);
        HttpResponse<String> response = Unirest.post(map.get("url"))
                .header("Authorization", "SignWithMd5 appkey=3000007438,timestamp=123456789,signature=3e5253dcda8b0687def851f2324e23cb")
                .header("Content-Type", "application/json")
                .body(s)
                .asString();
        bufferedReader.close();
        System.out.println(response.getBody());
    }
}
