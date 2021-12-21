package grain.command;

import grain.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static grain.utils.RequestUtils.getUrl;
import static grain.utils.RequestUtils.sendPost;

/**
 * @author wulifu
 */
@Slf4j
public class Command {

    public static String register(String host, String port, String localPort) {
        try {
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("port", localPort);
            String s = sendPost(getUrl(host, port, "/api/" + Strings.CENTER + "/" + Strings.REGISTER), map);
            return s;
        } catch (Exception e) {
            log.info("请求失败");
            throw new RuntimeException("请求失败");
        }
    }

    public static void success(Integer taskId, String host, String port, String serialId) {
        try {
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("taskId", taskId.toString());
            map.add("nodeId", serialId);
            String s = sendPost(getUrl(host, port, "/api/" + Strings.CENTER + "/" + Strings.TASK_COMPLETE), map);
            log.info(s);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }
}

