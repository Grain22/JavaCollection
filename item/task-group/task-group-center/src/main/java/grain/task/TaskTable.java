package grain.task;

import grain.node.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author wulifu
 */
public class TaskTable {
    private static final List<TaskInfo> TASK_INFOS = new CopyOnWriteArrayList<>();

    public static List<TaskInfo> inQueue() {
        synchronized (TASK_INFOS) {
            return TASK_INFOS.stream().filter(a -> a.getStatus() == TaskInfo.sent).collect(Collectors.toList());
        }
    }

    public static int inQueueCount() {
        return inQueue().size();
    }

    public static List<TaskInfo> inQueue(Node node) {
        synchronized (TASK_INFOS) {
            return TASK_INFOS.stream()
                    .filter(a -> a.getStatus() == TaskInfo.sent)
                    .filter(taskInfo -> taskInfo.getNodeId().equals(node.getNodeId())).collect(Collectors.toList());
        }
    }

    public static int inQueueCount(Node node) {
        return inQueue(node).size();
    }

    public static void addTasks(ArrayList<Task> tasks) {
        tasks.forEach(TaskTable::addTask);
    }

    public static void addTask(Task task) {
        synchronized (TASK_INFOS) {
            TASK_INFOS.add(new TaskInfo().setStatus(TaskInfo.waiting).setTask(task).setTaskId(task.taskId));
        }
    }

    public static List<TaskInfo> waitingTask() {
        synchronized (TASK_INFOS) {
            return TASK_INFOS.stream().filter(a -> a.getStatus() == TaskInfo.waiting).collect(Collectors.toList());
        }
    }

    public static void sortPriorityDate(List<TaskInfo> list) {
        list.sort((o1, o2) -> {
            if (o1.getTask().getPriority() == o2.getTask().getPriority()) {
                return o1.getTask().getAddedDate().compareTo(o2.getTask().getAddedDate());
            }
            return o1.getTask().getPriority() - o2.getTask().getPriority();
        });
    }

    public static List<TaskInfo> disableNodeTask(List<String> disable) {
        List<TaskInfo> result = new ArrayList<>();
        for (TaskInfo taskInfo : TASK_INFOS) {
            if (disable.contains(taskInfo.getNodeId())) {
                taskInfo.reFresh();
                result.add(taskInfo);
            }
        }
        return result;
    }

    public static Map<String, List<TaskInfo>> collectByNode() {
        Map<String, List<TaskInfo>> map = new HashMap<>(2);
        for (TaskInfo a : TASK_INFOS) {
            if (a.getStatus() == TaskInfo.sent) {
                if (map.containsKey(a.getNodeId())) {
                    List<TaskInfo> taskInfos = map.get(a.getNodeId());
                    taskInfos.add(a);
                    map.put(a.getNodeId(), taskInfos);
                } else {
                    List<TaskInfo> taskInfos = new ArrayList<>();
                    taskInfos.add(a);
                    map.put(a.getNodeId(), taskInfos);
                }
            }
        }
        return map;
    }

    public static int nodeAverage() {
        Map<String, List<TaskInfo>> map = collectByNode();
        if (map.isEmpty()) {
            return 0;
        }
        int taskSize = map.values().stream().map(List::size).reduce(0, Integer::sum);
        int nodeSize = map.keySet().size();
        return taskSize / nodeSize;
    }

    public static List<TaskInfo> outTime(long timeOut) {
        List<TaskInfo> taskInfos = inQueue();
        List<TaskInfo> outTimeTasks = taskInfos.stream().filter(a -> a.getSendDate() + timeOut < System.currentTimeMillis()).collect(Collectors.toList());
        return outTimeTasks;
    }

    public static void cleanOld(long old) {
        TASK_INFOS.removeIf(a -> a.getStatus() == TaskInfo.completed && (a.getStatusDate() + old) < System.currentTimeMillis());
    }

    public static boolean complete(String taskId, String nodeId) {
        for (TaskInfo taskInfo : TASK_INFOS) {
            if (taskInfo.getTaskId() == Integer.parseInt(taskId)) {
                if (nodeId.equals(taskInfo.getNodeId())) {
                    taskInfo.setStatus(TaskInfo.completed)
                            .setStatusDate(System.currentTimeMillis());
                    return true;
                }
                break;
            }
        }
        return false;
    }
}
