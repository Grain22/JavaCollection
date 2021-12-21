package grain.run;

import grain.command.Command;
import grain.task.Task;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wulifu
 */
@Slf4j
@Data
public class Job implements Runnable, Comparable<Job> {

    private final Task task;
    private final String serialId;
    private final String host;
    private final String port;


    public Job(Task task, String serialId, String hostAddress, String hostPort) {
        this.task = task;
        this.serialId = serialId;
        this.host = hostAddress;
        this.port = hostPort;
    }

    @Override
    public void run() {
        RunTask.runTask(this.task);
        Command.success(task.getTaskId(), host, port, serialId);
    }

    @Override
    public int compareTo(Job o) {
        if (this.task.getPriority() == o.task.getPriority()) {
            return task.getAddedDate().compareTo(o.task.getAddedDate());
        }
        return o.task.getPriority() - this.task.getPriority();
    }
}
