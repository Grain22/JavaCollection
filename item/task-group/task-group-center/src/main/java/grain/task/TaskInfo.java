package grain.task;

import grain.node.Node;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author wulifu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class TaskInfo {
    public static final int waiting = 1;
    public static final int sent = 2;
    public static final int rendering = 3;
    public static final int completed = 4;
    public static final int canceled = 5;
    public static final int error = 6;
    int retryTimes;
    private int taskId;
    private String nodeId;
    private Task task;
    private int status;
    private long statusDate;
    private long sendDate;

    public void sendSuccess(Node node) {
        this.nodeId = node.getNodeId();
        this.status = sent;
        this.sendDate = System.currentTimeMillis();
        this.statusDate = System.currentTimeMillis();
    }

    public void retry(Node node) {
        retry(2, node);
    }

    public void retry(int maxTimeDefaultTwo, Node node) {
        this.nodeId = node.getNodeId();
        if (this.retryTimes > maxTimeDefaultTwo) {
            this.fail();
        } else {
            this.retryTimes++;
            this.statusDate = System.currentTimeMillis();
        }
    }

    public void fail() {
        this.status = error;
        this.statusDate = System.currentTimeMillis();
    }

    public void reFresh() {
        this.nodeId = null;
        this.status = waiting;
        this.statusDate = System.currentTimeMillis();
        this.retryTimes = 0;
    }
}
