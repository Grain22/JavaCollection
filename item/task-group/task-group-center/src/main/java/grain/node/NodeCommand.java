package grain.node;

import com.alibaba.fastjson.JSON;
import grain.Msg;
import grain.Strings;
import grain.task.TaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Objects;

import static grain.utils.RequestUtils.getUrl;
import static grain.utils.RequestUtils.sendPost;

/**
 * @author wulifu
 */
@Slf4j
public class NodeCommand {
    public static boolean checkAvailable(Node node) {
        try {
            /* 发送请求 获取节点标签 (id)*/
            String s = sendPost(getUrl(node.nodeHost, node.nodePort, "/api/" + Strings.NODE + "/" + Strings.AVAILABLE), null);
            if (node.getNodeId().equals(Msg.parse(s).getData())) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }

    }

    public static boolean sendTask(Node node, TaskInfo a) {
        try {
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("json", JSON.toJSONString(a.getTask()));
            String s = sendPost(getUrl(node.nodeHost, node.nodePort, "/api/" + Strings.NODE + "/" + Strings.ADD_TASK), map);
            if (Msg.parse(s).getCode() == Msg.code_success) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean cancel(Node node, TaskInfo task) {
        try {
            if (Objects.isNull(node)) {
                log.info("失效节点,任务自动放弃");
                return true;
            }
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("json", JSON.toJSONString(task.getTaskId()));
            String s = sendPost(getUrl(node.nodeHost, node.nodePort, "/api/" + Strings.NODE + "/" + Strings.CANCEL_TASK), map);
            log.info(s);
            if (Msg.parse(s).getCode() == Msg.code_success) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
