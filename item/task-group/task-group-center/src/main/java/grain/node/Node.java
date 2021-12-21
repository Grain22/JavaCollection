package grain.node;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import tools.data.DigestUtil;

/**
 * @author wulifu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Node {

    public static final int normal = 0;
    public static final int wait = 2;
    public static final int drop = 3;
    public static final int uncheck = -1;
    public static final int priority_top = 9;
    public static final int priority_bottom = 0;
    /**
     * 节点标签
     */
    String nodeId;
    String nodeHost;
    String nodePort;
    String nodeRegisterDateTime;
    long nodeLastCheckDateTime;
    int nodeRetryTimes = 0;
    int nodePriority = 5;
    boolean nodeAvailable = true;

    public static String getIdByMd5(String nodeHost, String nodePort, String nodeRegisterDateTime) {
        return DigestUtil.md5Digests(nodeHost + nodePort + nodeRegisterDateTime);
    }

    public void setNodeId() {
        this.nodeId = DigestUtil.md5Digests(nodeHost + nodePort + nodeRegisterDateTime);
    }

    public void increasePriority() {
        if (this.nodePriority < priority_top) {
            this.nodePriority++;
        }
    }

    public void decreasePriority() {
        if (this.nodePriority > priority_bottom) {
            this.nodePriority--;
        }
    }

    public void nodeWait() {
        this.nodePriority = priority_bottom;
        this.nodeRetryTimes++;
    }

    public void unBelieve() {
        this.nodeAvailable = false;
        this.nodePriority = priority_bottom;
        this.setNodeRetryTimes(Integer.MAX_VALUE);
    }

    public void refreshCheckDate() {
        this.nodeAvailable = true;
        this.nodeLastCheckDateTime = System.currentTimeMillis();
        this.setNodeRetryTimes(0);
    }
}
