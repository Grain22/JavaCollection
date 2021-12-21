package grain;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableList;
import grain.node.Node;
import grain.node.NodeCommand;
import grain.node.NodeTable;
import grain.task.Task;
import grain.task.TaskInfo;
import grain.task.TaskTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wulifu
 */
@Slf4j
@Component
@EnableAsync
@EnableScheduling
public class NodeManager {

    private static final long OLD = 1000L * 60 * 60 * 24 * 30;
    private static int id = 0;
    private static long timeOut = 1000L * 60 * 30;

    private static synchronized int getId() {
        return id++;
    }

    public static void increaseTimeOutLimit() {
        timeOut *= 1.05;
        log.info("超时时间变更为 {}", timeOut);
    }

    public static String register(String ipAddress, String port) {
        return NodeTable.register(ipAddress, port);
    }

    @Async
    @Scheduled(fixedDelay = 1000 * 60)
    public void tasks() {
        nodes();
        taskCollect();
        taskDistribute();
        taskOutTimeCheck();
    }

    public void nodes() {
        nodeAvailableCheck();
        nodePriorityCheck();
    }

    /**
     * 任务收集
     * 定时 每 分钟
     * 加入任务表
     * 状态等待中
     */
    public void taskCollect() {
        int hundred = 100;
        ArrayList<Task> tasks = new ArrayList<>();
        for (int i = 0; i < new Random().nextInt(hundred); i++) {
            try {
                SecureRandom instanceStrong = SecureRandom.getInstanceStrong();
                int wait = instanceStrong.nextInt(100);
                tasks.add(new Task().setTaskId(getId()).setTaskLong(wait * 10000).setAddedDate(LocalDateTime.now().toString()).setPriority(instanceStrong.nextInt(9)));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        TaskTable.addTasks(tasks);
        log.info("任务收集 数量 {}", tasks.size());
    }

    public void taskDistribute() {
        /*
         * 新加入任务收集
         */
        List<TaskInfo> taskInfos = TaskTable.waitingTask();
        if (taskInfos.isEmpty()) {
            log.info("无新任务");
            return;
        }
        /*
         * 任务排序
         * 排序方式 优先级 > 加入时间
         */
        TaskTable.sortPriorityDate(taskInfos);
        /*
         * 节点任务分配
         */
        distributeTasks(taskInfos);
    }

    /**
     * 任务超时
     */
    public void taskOutTimeCheck() {
        /*所有超时任务*/
        List<TaskInfo> outTimeTasks = TaskTable.outTime(timeOut);
        if (outTimeTasks.isEmpty()) {
            log.info("无超时任务");
            return;
        }
        /*超时任务节点归并*/
        Map<String, List<TaskInfo>> map = new HashMap<>(2);
        outTimeTasks.forEach(a -> map.merge(a.getNodeId(), ImmutableList.of(a), (ov, nv) -> {
            ov.addAll(nv);
            return ov;
        }));
        /*可用节点*/
        List<Node> available = NodeTable.available();
        /*超时任务节点 匹配 可用任务节点*/
        List<String> outTimeTasksNodeAvailable = map.keySet().stream().filter(a -> available.stream().map(Node::getNodeId).collect(Collectors.toList()).contains(a)).collect(Collectors.toList());
        if (outTimeTasksNodeAvailable.size() == available.size()) {
            /*所有可用节点都有超时任务*/
            NodeManager.increaseTimeOutLimit();
        } else {
            /*不存在超时任务节点*/
            List<Node> collect = available.stream().filter(a -> !outTimeTasksNodeAvailable.contains(a.getNodeId())).collect(Collectors.toList());
            for (Node node : collect) {
                node.increasePriority();
            }
            /*超时任务重分配*/
            for (TaskInfo outTimeTask : outTimeTasks) {
                NodeCommand.cancel(NodeTable.find(outTimeTask.getNodeId()), outTimeTask);
                distributeTask(outTimeTask);
            }
        }
    }

    @Async
    @Scheduled(cron = "0 0 0 * * *")
    public void taskDataRemove() {
        log.info("过期任务信息清理");
        TaskTable.cleanOld(OLD);
    }

    /**
     * 节点优先度
     * 据情况上调或下调优先级
     */
    public void nodePriorityCheck() {
        if (NodeTable.isEmpty()) {
            log.info("无节点");
            return;
        }
        if (TaskTable.collectByNode().isEmpty()) {
            log.info("无任务");
            return;
        }
        /*平均节点任务数量*/
        int average = TaskTable.nodeAverage();
        for (Node node : NodeTable.available()) {
            /*节点队列中数量*/
            int i = TaskTable.inQueueCount(node);
            if (i < average - 2) {
                /*比平均还少两个以上,上调优先度*/
                node.increasePriority();
            } else if (i > average + 2) {
                /*比平均还多两个以上,下调优先度*/
                node.decreasePriority();
            }
        }
    }

    /**
     * 节点检查
     */
    public void nodeAvailableCheck() {
        NodeTable.NODES.forEach(this::checkNodeCanReach);
        List<String> disable = NodeTable.disable();
        log.info("节点检查 丢失节点数 {}", disable.size());
        if (!disable.isEmpty()) {
            TaskTable.disableNodeTask(disable);
        }
    }

    private void distributeTasks(List<TaskInfo> taskInfos) {
        if (NodeTable.isEmpty()) {
            /*无节点 跳过分配 继续等待*/
            log.info("当前无节点 等待下个任务周期");
            return;
        }
        taskInfos.forEach(this::distributeTask);
    }

    private void distributeTask(TaskInfo taskInfo) {
        Node firstOne = NodeTable.firstOne();
        Node secondOne = NodeTable.secondOne();
        log.info("任务 {} 发送流程", taskInfo.getTaskId());
        if (NodeCommand.sendTask(firstOne, taskInfo)) {
            log.info("任务 {} 发送到 {} 成功", taskInfo.getTaskId(), firstOne.getNodeHost() + " " + firstOne.getNodeId());
            taskInfo.sendSuccess(firstOne);
        } else if (firstOne.getNodeId().equals(secondOne.getNodeId())) {
            log.info("任务 {} 节点唯一 再次尝试 {}", taskInfo.getTaskId(), firstOne.getNodeHost() + " " + firstOne.getNodeId());
            reDistributeTask(taskInfo, firstOne);
        } else {
            log.info("任务 {} 尝试顺位节点 {}", taskInfo.getTaskId(), JSON.toJSONString(secondOne));
            reDistributeTask(taskInfo, secondOne);
            /*节点下调置信度*/
            firstOne.decreasePriority();
        }
    }

    private void reDistributeTask(TaskInfo taskInfo, Node node) {
        taskInfo.retry(node);
        if (NodeCommand.sendTask(node, taskInfo)) {
            log.info("任务 {} 发送到 {} 成功", taskInfo.getTaskId(), node.getNodeHost() + " " + node.getNodeId());
            taskInfo.sendSuccess(node);
        } else {
            log.info("任务 {} 发送到 {} 失败", taskInfo.getTaskId(), node.getNodeHost() + " " + node.getNodeId());
            taskInfo.fail();
        }
    }

    private void checkNodeCanReach(Node node) {
        int trustTryTimes = 2;
        if (NodeCommand.checkAvailable(node)) {
            node.refreshCheckDate();
        } else if (node.getNodeRetryTimes() < trustTryTimes) {
            log.info("节点暂时失联 {}", node.getNodeHost());
            node.nodeWait();
        } else {
            log.info("节点不可达 {}", node.getNodeHost());
            node.unBelieve();
        }
    }
}
