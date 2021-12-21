package grain.node;

import grain.task.TaskTable;
import lombok.Data;
import tools.data.DateUtils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author wulifu
 */
@Data
public class NodeTable {

    public static final List<Node> NODES = new CopyOnWriteArrayList<>();

    public static List<Node> sortByPriority() {
        List<Node> nodes = NODES.stream().filter(a -> a.nodeAvailable).collect(Collectors.toList());
        nodes.sort((o1, o2) -> {
            if (TaskTable.inQueueCount(o1) == TaskTable.inQueueCount(o2)) {
                return o1.getNodePriority() - o2.getNodePriority();
            }
            return TaskTable.inQueueCount(o1) - TaskTable.inQueueCount(o2);
        });
        return nodes;
    }

    public static List<Node> available() {
        return NODES.stream().filter(a -> a.nodeAvailable).collect(Collectors.toList());
    }

    public static boolean isEmpty() {
        return available().isEmpty();
    }

    public static Node firstOne() {
        if (isEmpty()) {
            throw new RuntimeException("可用节点表为空");
        }
        return sortByPriority().get(0);
    }

    public static Node secondOne() {
        if (isEmpty()) {
            throw new RuntimeException("可用节点表为空");
        }
        List<Node> nodes = sortByPriority();
        if (nodes.size() > 1) {
            return nodes.get(1);
        }
        return nodes.get(0);
    }

    public static List<String> disable() {
        List<String> collect;
        synchronized (NODES) {
            collect = NODES.stream().filter(a -> !a.nodeAvailable).map(Node::getNodeId).collect(Collectors.toList());
            NODES.removeIf(next -> collect.contains(next.getNodeId()));
        }
        return collect;
    }

    public static String register(String ipAddress, String port) {
        String timeStringLong = DateUtils.getTimeStringLong();
        Node node = new Node(Node.getIdByMd5(ipAddress, port, timeStringLong), ipAddress, port, timeStringLong, System.currentTimeMillis(), 0, 5, true);
        NODES.add(node);
        return node.getNodeId();
    }

    public static Node find(String nodeId) {
        for (Node node : NODES) {
            if (node.getNodeId().equals(nodeId)) {
                return node;
            }
        }
        return null;
    }
}
