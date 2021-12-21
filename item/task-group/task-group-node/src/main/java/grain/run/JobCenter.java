package grain.run;

import grain.configs.GlobalParams;
import grain.task.Task;
import lombok.extern.slf4j.Slf4j;
import org.grain.tools.thread.CustomThreadPool;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.*;


/**
 * @author wulifu
 */
@Component
@Slf4j
public class JobCenter {

    protected static ThreadFactory threadFactory = CustomThreadPool.createThreadFactory("task pool", false, Thread.NORM_PRIORITY);
    protected static ThreadPoolExecutor task_pool = null;
    protected static BlockingQueue<Runnable> jobs = new PriorityBlockingQueue<>(10240);
    protected final GlobalParams params;

    public JobCenter(GlobalParams params) {
        this.params = params;
    }

    public void init() {
        task_pool = new ThreadPoolExecutor(
                4,
                8,
                0,
                TimeUnit.MILLISECONDS,
                jobs,
                threadFactory,
                new ThreadPoolExecutor.AbortPolicy());
    }

    public ThreadPoolExecutor getExecutor() {
        if (Objects.isNull(task_pool)) {
            init();
        }
        return task_pool;
    }

    public boolean addTask(Task task) {
        getExecutor().execute(new Job(task, params.getNodeId(), params.getHostAddress(), params.getHostPort()));
        return true;
    }

    public void updateJob(int id, int priority) {
        Job next;
        Task task = null;
        Runnable remove = null;
        for (Runnable job : jobs) {
            next = (Job) job;
            if (next.getTask().getTaskId() == id) {
                remove = job;
                task = next.getTask();
                break;
            }
        }
        if (!Objects.isNull(remove) && !Objects.isNull(task)) {
            getExecutor().remove(remove);
            getExecutor().execute(new Job(task.setPriority(priority), params.getNodeId(), params.getHostAddress(), params.getHostPort()));
        }
    }

    public void cancelJob(int id) {
        Job next;
        Runnable remove = null;
        for (Runnable job : jobs) {
            next = (Job) job;
            if (next.getTask().getTaskId() == id) {
                remove = job;
                break;
            }
        }
        if (!Objects.isNull(remove)) {
            getExecutor().remove(remove);
        }
    }

}
