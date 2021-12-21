package grain.controller;

import com.alibaba.fastjson.JSON;
import grain.Msg;
import grain.Strings;
import grain.configs.GlobalParams;
import grain.run.JobCenter;
import grain.task.Task;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wulifu
 */
@RestController
@RequestMapping(Strings.NODE)
public class NodeCont {

    protected final JobCenter jobCenter;
    protected final GlobalParams params;

    @Lazy
    public NodeCont(JobCenter jobCenter, GlobalParams params) {
        this.jobCenter = jobCenter;
        this.params = params;
    }


    @PostMapping(Strings.ADD_TASK)
    public Msg addTask(String json) {
        try {
            jobCenter.addTask(JSON.parseObject(json, Task.class));
            return Msg.success();
        } catch (Exception e) {
            return Msg.error(Msg.code_task_info_error, e.getMessage());
        }
    }

    @PostMapping(Strings.CANCEL_TASK)
    public Msg cancel(String json) {
        jobCenter.cancelJob(Integer.parseInt(json));
        return Msg.success();
    }

    @PostMapping(Strings.AVAILABLE)
    public Msg available() {
        return Msg.success(params.getNodeId());
    }
}
